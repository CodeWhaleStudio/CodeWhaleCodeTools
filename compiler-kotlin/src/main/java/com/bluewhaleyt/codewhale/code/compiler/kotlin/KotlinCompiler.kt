package com.bluewhaleyt.codewhale.code.compiler.kotlin

import android.content.Context
import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult
import com.bluewhaleyt.codewhale.code.compiler.core.CompileReporter
import com.bluewhaleyt.codewhale.code.compiler.core.Compiler
import com.bluewhaleyt.codewhale.code.compiler.core.ExperimentalCompilerApi
import com.bluewhaleyt.codewhale.code.compiler.core.Language
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompilationResult
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject
import com.bluewhaleyt.codewhale.code.compiler.java.task.D8Task
import com.bluewhaleyt.codewhale.code.compiler.java.task.JarTask
import com.bluewhaleyt.codewhale.code.compiler.java.task.JavaCompileTask
import com.bluewhaleyt.codewhale.code.compiler.java.utils.JavaCompilerUtils
import com.bluewhaleyt.codewhale.code.compiler.kotlin.task.KotlinCompileTask

class KotlinCompiler(
    private val context: Context,
    override val reporter: CompileReporter,
    val project: KotlinProject,
    val options: KotlinCompileOptions = KotlinCompileOptions()
) : Compiler<KotlinCompileOptions>(reporter, options) {

    val language = Language.Kotlin

    private val utils = JavaCompilerUtils(
        context, reporter, project, options
    )

    init {
        initializeCache(project)
    }

    override fun compile(): KotlinCompilationResult {
        val compilationResult = KotlinCompilationResult()
        try {
            compileKotlin()
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

    private fun compileKotlin() {
        compileTask<KotlinCompileTask>("Compiling Kotlin...")
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

    private fun initializeCache(project: KotlinProject) {
        CompilerCache.saveCache(JavaCompileTask(project, options))
        CompilerCache.saveCache(KotlinCompileTask(project, options))
        CompilerCache.saveCache(D8Task(project, options))
        CompilerCache.saveCache(JarTask(project, options))
    }

}