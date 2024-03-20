package com.bluewhaleyt.codewhale.code.language.java.compiler.task

import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter
import com.bluewhaleyt.codewhale.code.core.Task
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaProject
import java.io.File
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class JarTask(
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
                    reporter.reportInfo("JAR file created: ${jarFile.absolutePath} for ${classFile.name}")
                }
        }
    }
}