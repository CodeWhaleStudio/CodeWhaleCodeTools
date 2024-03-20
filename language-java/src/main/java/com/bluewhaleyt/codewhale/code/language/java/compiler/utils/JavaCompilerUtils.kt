package com.bluewhaleyt.codewhale.code.language.java.compiler.utils

import android.content.Context
import android.os.Build
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.bluewhaleyt.codewhale.code.core.compiler.CompilationResult
import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaProject
import com.bluewhaleyt.codewhale.code.language.java.compiler.MultipleDexClassLoader
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import java.lang.reflect.Modifier

// basically you won't need to use it
class JavaCompilerUtils(
    val context: Context,
    val reporter: CompileReporter,
    val project: JavaProject,
    val options: JavaCompileOptions
) {

    var isRunning = false
    var output = ""
    var classes: List<String> = emptyList()

    fun checkClasses(
        className: String,
        compilationResult: CompilationResult,
        onOutput: (String) -> Unit,
        onClasses: (List<String>) -> Unit
    ) {
        val dex = project.binDir.resolve("classes.dex")
        if (!dex.exists()) {
            reporter.reportError("classes.dex not found")
            return
        }
        val bis = dex.inputStream().buffered()
        val dexFile = DexBackedDexFile.fromInputStream(
            Opcodes.forApi(33), bis
        )
        bis.close()
        val classes = dexFile.classes.map {
            it.type.substring(1, it.type.length - 1)
        }
        if (classes.isEmpty()) {
            reporter.reportError("No classes found")
            return
        }
        reporter.reportInfo("Found ${classes.size} classes, compiling $className...")
//        reporter.reportLogging("Available classes:")
//        classes.forEach {
//            reporter.reportLogging("  $it")
//        }
//        val index = classes.firstOrNull { it.endsWith("Main") }
//            ?: classes.firstOrNull { it.endsWith("MainKt") } ?: classes.first()

        onClasses(classes)

        runClass(
            compilationResult = compilationResult,
            className = className,
            onOutput = onOutput
        )
    }

    private fun runClass(
        compilationResult: CompilationResult,
        className: String,
        onOutput: (String) -> Unit,
    ) = with(Dispatchers.IO) {
        val systemOut = PrintStream(object : OutputStream() {
            override fun write(b: Int) {
                output += b.toChar().toString()
                onOutput(output)
            }
        })
        System.setOut(systemOut)
        System.setErr(systemOut)

        options.inputStream?.let {
            System.setIn(it)
        }

        val loader = MultipleDexClassLoader(
            classLoader = javaClass.classLoader!!
        )
        loader.loadDex(
            makeDexReadOnlyIfNeeded(
                context = context,
                dexFile = project.binDir.resolve("classes.dex")
            )
        )
        project.buildDir.resolve("libs")
            .listFiles()?.filter { it.extension.lowercase() == "dex" }
            ?.forEach {
                loader.loadDex(makeDexReadOnlyIfNeeded(context, it))
            }
        runCatching {
            loader.loader.loadClass(className)
        }.onSuccess { clazz ->
            isRunning = true
            System.setProperty("project.dir", project.projectDir.absolutePath)
            if (clazz.declaredMethods.any {
                    it.name == "main" && it.parameterCount == 1 && it.parameterTypes[0] == Array<String>::class.java
                }) {
                val method = clazz.getDeclaredMethod("main", Array<String>::class.java)
                try {
                    if (Modifier.isStatic(method.modifiers)) {
                        method.invoke(null, project.args.toTypedArray())
                    } else if (Modifier.isPublic(method.modifiers)) {
                        method.invoke(
                            clazz.getDeclaredConstructor().newInstance(),
                            project.args.toTypedArray()
                        )
                    } else {
                        System.err.println("Main method is not public or static")
                    }
                } catch (e: Throwable) {
                    compilationResult.error = e
                }
            } else {
                System.err.println("No main method found")
            }
        }.onFailure { e ->
            System.err.println("Error loading class: ${e.message}")
        }.also {
            systemOut.close()
            options.inputStream?.close()
            isRunning = false
        }
    }

    private fun makeDexReadOnlyIfNeeded(
        context: Context,
        dexFile: File
    ): File {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return dexFile
        }
        val target = context.cacheDir.resolve(dexFile.name)
        if (target.exists()) {
            target.delete()
        }
        target.createNewFile()
        dexFile.inputStream().buffered().use {
            target.writeBytes(it.readBytes())
        }
        target.setReadOnly() // This is required for Android 14+
        return target
    }

}