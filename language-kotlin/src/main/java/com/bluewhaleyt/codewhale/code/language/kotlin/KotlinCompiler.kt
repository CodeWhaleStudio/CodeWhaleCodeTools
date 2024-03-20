package com.bluewhaleyt.codewhale.code.language.kotlin

import android.content.Context
import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter
import com.bluewhaleyt.codewhale.code.core.compiler.Compiler
import com.bluewhaleyt.codewhale.code.core.compiler.CompilerCache
import com.bluewhaleyt.codewhale.code.core.Language
import com.bluewhaleyt.codewhale.code.language.java.compiler.task.D8Task
import com.bluewhaleyt.codewhale.code.language.java.compiler.task.JarTask
import com.bluewhaleyt.codewhale.code.language.java.compiler.task.JavaCompileTask
import com.bluewhaleyt.codewhale.code.language.java.compiler.utils.JavaCompilerUtils
import com.bluewhaleyt.codewhale.code.language.kotlin.task.KotlinCompileTask

class KotlinCompiler(
    private val context: Context,
    override val reporter: CompileReporter = CompileReporter(),
    val project: KotlinProject,
    val options: KotlinCompileOptions = KotlinCompileOptions()
) : Compiler<KotlinCompileOptions>(reporter, options) {

    val language = Language.Kotlin
    private var compilationResult: KotlinCompilationResult = KotlinCompilationResult()

    private val utils = JavaCompilerUtils(
        context, reporter, project, options
    )

    init {
        initializeCache(project)
    }

    fun listClasses(): List<String> {
        compileKotlin()
        startCompileJava()
        return utils.classes
    }

    fun startCompileJava() {
        compileJava()
        compileD8()
        if (options.generateJar) compileJar()
        utils.output = ""
        utils.checkClasses(
            className = options.className,
            compilationResult = compilationResult,
            onOutput = {
                utils.output = it
            },
            onClasses = {
                utils.classes = it
            }
        )
    }

    override fun compile(): KotlinCompilationResult {
        try {
            listClasses()
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