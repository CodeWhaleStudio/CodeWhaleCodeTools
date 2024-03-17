package com.bluewhaleyt.codewhale.code.compiler.java.utils

import android.content.Context
import android.os.Build
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult
import com.bluewhaleyt.codewhale.code.compiler.core.CompileReporter
import com.bluewhaleyt.codewhale.code.compiler.core.Compiler
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompilationResult
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompiler
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject
import com.bluewhaleyt.codewhale.code.compiler.java.MultipleDexClassLoader
import com.bluewhaleyt.codewhale.code.compiler.java.task.D8Task
import com.bluewhaleyt.codewhale.code.compiler.java.task.JarTask
import com.bluewhaleyt.codewhale.code.compiler.java.task.JavaCompileTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    fun checkClasses(
        onOutput: (String) -> Unit
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
//        withContext(Dispatchers.IO) {
            bis.close()
//        }
        val classes = dexFile.classes.map {
            it.type.substring(1, it.type.length - 1)
        }
        if (classes.isEmpty()) {
            reporter.reportError("No classes found")
            return
        }
        println("Found ${classes.size} classes")
        println("Available classes:")
        classes.forEach {
            println("  $it")
        }
        val index = classes.firstOrNull { it.endsWith("Main") }
            ?: classes.firstOrNull { it.endsWith("MainKt") } ?: classes.first()

        runClass(
            className = index,
            onOutput = onOutput
        )
    }

    private fun runClass(
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
        System.setIn(options.inputStream)

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
                    e.printStackTrace()
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