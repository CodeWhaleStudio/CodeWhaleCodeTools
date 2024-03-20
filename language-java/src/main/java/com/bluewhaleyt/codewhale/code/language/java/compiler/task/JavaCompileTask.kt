package com.bluewhaleyt.codewhale.code.language.java.compiler.task

import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter
import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi
import com.bluewhaleyt.codewhale.code.core.Task
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaProject
import com.sun.tools.javac.api.JavacTool
import java.io.Writer
import java.nio.file.Files
import java.util.Locale
import javax.tools.Diagnostic
import javax.tools.DiagnosticCollector
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject
import javax.tools.StandardLocation

@OptIn(ExperimentalCompilerApi::class)
class JavaCompileTask(
    val project: JavaProject,
    val options: JavaCompileOptions
) : Task {

    private val diagnostics = DiagnosticCollector<JavaFileObject>()
    private val tool = JavacTool.create()
    private val fileManager = tool.getStandardFileManager(diagnostics, null, null)

    override fun execute(reporter: CompileReporter) {
        val output = project.binDir.resolve("classes")
        try {
            Files.createDirectories(output.toPath())
        } catch (e: Exception) {
            reporter.reportWarning(e.stackTraceToString())
        }
        val javaFiles = project.getSourceFiles(project.srcDir, "java")
        if (javaFiles.isEmpty()) {
            reporter.reportInfo("No Java files found. Skipping compilation.")
            return
        }
        if (options.sourceVersion != options.targetVersion) {
            reporter.reportWarning("Source version and Target version are different.")
            reporter.reportInfo("Compiling with Java version ${options.sourceVersion} (Source version: ${options.sourceVersion}, Target version: ${options.targetVersion})...")
        } else {
            reporter.reportInfo("Compiling with Java version ${options.sourceVersion}...")
        }
        val size = javaFiles.size
        reporter.reportInfo("Compiling $size Java ${if (size == 1) "file" else "files"}...")

        val javaFileObjects = javaFiles.map { file ->
            object : SimpleJavaFileObject(file.toURI(), JavaFileObject.Kind.SOURCE) {
                override fun getCharContent(ignoreEncodingErrors: Boolean): CharSequence {
                    return file.readText()
                }
            }
        }

        fileManager.use { fileManager ->
            fileManager.apply {
                setLocation(StandardLocation.CLASS_OUTPUT, listOf(output))
                setLocation(StandardLocation.PLATFORM_CLASS_PATH, project.systemClasspath)
                setLocation(StandardLocation.CLASS_PATH, project.classpath)
                setLocation(StandardLocation.SOURCE_PATH, javaFiles)
            }
            val flags = options.flags ?: ""
            val compileOptions = listOf(
                "-proc:none",
                "-source",
                options.sourceVersion,
                "-target",
                options.targetVersion
            ) + if (flags.isNotEmpty()) flags.split(" ").toList() else listOf()

            val task = tool.getTask(
                object : Writer() {
                    private val sb = StringBuilder()
                    override fun close() {
                        flush()
                    }
                    override fun flush() {
//                        reporter.reportInfo(sb.toString())
                        sb.clear()
                    }
                    override fun write(cbuf: CharArray?, off: Int, len: Int) {
                        sb.appendRange(cbuf!!, off, off + len)
//                        reporter.reportInfo(sb.toString())
                    }
                },
                fileManager,
                diagnostics,
                compileOptions,
                null,
                javaFileObjects
            )

            task.call()

            diagnostics.diagnostics.forEachIndexed { index, diagnostic ->
                val message = StringBuilder()
                diagnostic.source?.apply {
                    message.append("$name:${diagnostic.lineNumber}: ")
                }
                message.append(diagnostic.getMessage(Locale.getDefault()))
                when (diagnostic.kind) {
                    Diagnostic.Kind.ERROR,
                    Diagnostic.Kind.OTHER -> reporter.reportError(message.toString())
                    Diagnostic.Kind.NOTE,
                    Diagnostic.Kind.WARNING,
                    Diagnostic.Kind.MANDATORY_WARNING -> reporter.reportWarning(message.toString())
                    else -> reporter.reportInfo(message.toString())
                }
            }
        }
    }

}