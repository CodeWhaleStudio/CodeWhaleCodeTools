package com.bluewhaleyt.codewhale.code.compiler.kotlin.task

import com.bluewhaleyt.codewhale.code.compiler.core.CompileReporter
import com.bluewhaleyt.codewhale.code.compiler.core.Task
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject
import com.bluewhaleyt.codewhale.code.compiler.kotlin.KotlinCompileOptions
import com.bluewhaleyt.codewhale.code.compiler.kotlin.KotlinProject
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.incremental.isJavaFile
import org.jetbrains.kotlin.incremental.makeJvmIncrementally
import java.io.File

internal class KotlinCompileTask(
    val project: KotlinProject,
    val options: KotlinCompileOptions
) : Task {

    val args: K2JVMCompilerArguments by lazy {
        K2JVMCompilerArguments().apply {
            noReflect = true
            noStdlib = true
            noJdk = true
            newInference = true
            useFirLT = true
            useFirIC = true
        }
    }

    override fun execute(reporter: CompileReporter) {
        val kotlinFiles = project.getSourceFiles(project.srcDir, "kt")
        if (kotlinFiles.isEmpty()) {
            reporter.reportInfo("No Kotlin files found. Skipping compilation.")
            return
        }

        reporter.reportInfo("Compiling with Kotlin version ${options.languageVersion}, JVM Target version ${options.jvmTarget}...")

        val size = kotlinFiles.size
        reporter.reportInfo("Compiling $size Kotlin ${if (size == 1) "file" else "files"}...")

        val kotlinHomeDir = project.binDir.resolve("kotlin").apply { mkdirs() }
        val classOutput = project.binDir.resolve("classes").apply { mkdirs() }
        val classpathFiles = collectClasspathFiles()

        args.apply {
            classpath = (project.systemClasspath + classpathFiles)
                .joinToString(separator = File.pathSeparator) { it.absolutePath }
            kotlinHome = kotlinHomeDir.absolutePath
            destination = classOutput.absolutePath
            javaSourceRoots =
                kotlinFiles.filter { it.isJavaFile() }
                    .map { it.absolutePath }.toTypedArray()
            moduleName = project.name
            languageVersion = options.languageVersion
            apiVersion = options.apiVersion
            jvmTarget = options.jvmTarget
            script = false
        }

        val collector = createMessageCollector(reporter)
        makeJvmIncrementally(
            cachesDir = kotlinHomeDir,
            sourceRoots = listOf(project.srcDir),
            args = args,
            messageCollector = collector
        )
    }

    private fun collectClasspathFiles(): List<File> {
        return project.libsDir.walk().filter(File::isFile).toList()
    }

    private fun createMessageCollector(reporter: CompileReporter): MessageCollector =
        object : MessageCollector {
            private var hasErrors: Boolean = false
            override fun clear() {}
            override fun hasErrors() = hasErrors
            override fun report(
                severity: CompilerMessageSeverity,
                message: String,
                location: CompilerMessageSourceLocation?
            ) {
                val diagnostic = CompilationDiagnostic(message, location)
                when (severity) {
                    CompilerMessageSeverity.ERROR, CompilerMessageSeverity.EXCEPTION -> {
                        hasErrors = true
                        reporter.reportError(diagnostic.toString())
                    }
                    CompilerMessageSeverity.WARNING, CompilerMessageSeverity.STRONG_WARNING -> reporter.reportWarning(
                        diagnostic.toString()
                    )
                    CompilerMessageSeverity.INFO -> reporter.reportInfo(diagnostic.toString())
                    CompilerMessageSeverity.LOGGING -> reporter.reportLogging(diagnostic.toString())
                    CompilerMessageSeverity.OUTPUT -> reporter.reportOutput(diagnostic.toString())
                }
            }
        }

    data class CompilationDiagnostic(
        val message: String,
        val location: CompilerMessageSourceLocation?
    ) {
        override fun toString() =
            location?.toString()?.substringAfter("src/main/").orEmpty() + " " + message
    }

}