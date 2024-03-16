package com.bluewhaleyt.codewhale.code.compiler.java.task

import com.bluewhaleyt.codewhale.code.compiler.core.CompileReporter
import com.bluewhaleyt.codewhale.code.compiler.core.Task
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject
import java.io.File
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

internal class JarTask(
    val project: JavaProject,
    val options: JavaCompileOptions
) : Task {
    override fun execute(reporter: CompileReporter) {
        val dir = project.binDir.resolve("classes")
        reporter.reportInfo("Creating JAR file from directory: ${dir.absolutePath}")
        val jarFile = File(project.binDir, "classes.jar")
        if (jarFile.exists()) {
            jarFile.delete()
        }
        JarOutputStream(jarFile.outputStream()).use { jar ->
            dir.walkTopDown().filter { it.isFile && it.extension.lowercase() == "class" }
                .forEach { classFile ->
                    val entryName = classFile.relativeTo(dir)
                        .path.replace("\\", "/")
                    jar.putNextEntry(ZipEntry(entryName))
                    classFile.inputStream().buffered().use { input ->
                        input.copyTo(jar)
                    }
                    jar.closeEntry()
                    reporter.reportInfo("JAR file created: ${jarFile.absolutePath}")
                }
        }
    }
}