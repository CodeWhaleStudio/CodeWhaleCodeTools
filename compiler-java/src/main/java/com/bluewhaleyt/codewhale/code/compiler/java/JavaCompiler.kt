package com.bluewhaleyt.codewhale.code.compiler.java

import android.content.Context
import android.os.Build
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult
import com.bluewhaleyt.codewhale.code.compiler.core.CompileReporter
import com.bluewhaleyt.codewhale.code.compiler.core.Compiler
import com.bluewhaleyt.codewhale.code.compiler.core.Language
import com.bluewhaleyt.codewhale.code.compiler.java.task.D8Task
import com.bluewhaleyt.codewhale.code.compiler.java.task.JarTask
import com.bluewhaleyt.codewhale.code.compiler.java.task.JavaCompileTask
import com.bluewhaleyt.codewhale.code.compiler.java.utils.JavaCompilerUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import java.lang.reflect.Modifier

class JavaCompiler(
    private val context: Context,
    override val reporter: CompileReporter,
    val project: JavaProject,
    val options: JavaCompileOptions = JavaCompileOptions()
) : Compiler<JavaCompileOptions>(reporter, options) {

    val language = Language.Java

    private val utils = JavaCompilerUtils(
        context, reporter, project, options
    )

    init {
        initializeCache(project)
    }

    override fun compile(): JavaCompilationResult {
        val compilationResult = JavaCompilationResult()
        try {
            compileJava()
            compileD8()
            if (options.generateJar) compileJar()
            utils.output = ""
            utils.checkClasses(
                className = options.className,
                compilationResult = compilationResult,
                onOutput = {
                    utils.output = it
                }
            )
            compilationResult.output = utils.output
            reporter.reportSuccess("Build completed successfully.")
        } catch (e: Throwable) {
            compilationResult.error = e
            reporter.reportError("Compilation failed.")
        }
        return compilationResult
    }

    private fun compileJava() {
        compileTask<JavaCompileTask>("Compiling Java...")
    }

    private fun compileJar() {
        compileTask<JarTask>("Compiling JAR...")
    }

    private fun compileD8() {
        compileTask<D8Task>("Converting class files into Dex format...")
    }

    private fun initializeCache(project: JavaProject) {
        CompilerCache.saveCache(JavaCompileTask(project, options))
        CompilerCache.saveCache(D8Task(project, options))
        CompilerCache.saveCache(JarTask(project, options))
    }

}