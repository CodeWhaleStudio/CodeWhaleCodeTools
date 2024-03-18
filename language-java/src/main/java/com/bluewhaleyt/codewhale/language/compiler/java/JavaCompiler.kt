package com.bluewhaleyt.codewhale.language.compiler.java

import android.content.Context
import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter
import com.bluewhaleyt.codewhale.code.core.compiler.Compiler
import com.bluewhaleyt.codewhale.code.core.compiler.CompilerCache
import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi
import com.bluewhaleyt.codewhale.code.core.Language
import com.bluewhaleyt.codewhale.language.compiler.java.task.D8Task
import com.bluewhaleyt.codewhale.language.compiler.java.task.JarTask
import com.bluewhaleyt.codewhale.language.compiler.java.task.JavaCompileTask
import com.bluewhaleyt.codewhale.language.compiler.java.utils.JavaCompilerUtils

@ExperimentalCompilerApi
class JavaCompiler(
    private val context: Context,
    override val reporter: CompileReporter = CompileReporter(),
    val project: JavaProject,
    val options: JavaCompileOptions = JavaCompileOptions()
) : Compiler<JavaCompileOptions>(reporter, options) {

    val language = Language.Java
    private var compilationResult: JavaCompilationResult = JavaCompilationResult()

    private val utils = JavaCompilerUtils(
        context, reporter, project, options
    )

    init {
        initializeCache(project)
    }

    fun listClasses(): List<String> {
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

    override fun compile(): JavaCompilationResult {
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