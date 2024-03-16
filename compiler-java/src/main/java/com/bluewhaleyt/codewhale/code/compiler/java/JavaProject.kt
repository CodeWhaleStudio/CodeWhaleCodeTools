package com.bluewhaleyt.codewhale.code.compiler.java

import android.content.Context
import com.bluewhaleyt.codewhale.code.compiler.core.Project
import java.io.File

data class JavaProject(
    var dataDir: File,
    override var projectDir: File,
) : Project(projectDir) {

    val srcDir
        get() = File(projectDir, "src/main/java")
    val buildDir
        get() = File(projectDir, "build")
    val cacheDir
        get() = File(buildDir, "cache")
    val binDir
        get() = File(buildDir, "bin")
    val libsDir
        get() = File(projectDir, "libs")

    val projectsDir
        get() = dataDir.resolve("projects")
    val classpathDir
        get() = dataDir.resolve("classpath")

    var args = listOf<String>()
        get() {
            val file = cacheDir.resolve("args.txt")
            if (file.exists()) return file.readLines().toMutableList()
            return listOf()
        }
        set(value) {
            val file = cacheDir.resolve("args.txt")
            file.writeText(value.joinToString("\n"))
            field = value
        }

    val sourceFiles
        get() = srcDir.walkTopDown()
            .filter { it.isFile && it.extension.lowercase() == "java" }
            .toList()

    val classpath: List<File>
        get() {
            val classpath = mutableListOf(File(binDir, "classes"))
            if (libsDir.exists() && libsDir.isDirectory) {
                classpath += libsDir.walk().filter { it.extension == "jar" }.toList()
            }
            return classpath
        }

    val systemClasspath: List<File>
        get() = classpathDir.listFiles()?.toList() ?: emptyList()

    fun getClassFiles(rootDir: File) =
        rootDir.walk()
            .filter { it.extension == "class" }
            .map { it.toPath() }
            .toList()

}