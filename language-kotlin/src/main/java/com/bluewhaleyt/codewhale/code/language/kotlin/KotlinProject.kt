package com.bluewhaleyt.codewhale.code.language.kotlin

import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaProject
import java.io.File

data class KotlinProject(
    override var rootDir: File,
    override var projectDir: File,
) : JavaProject(rootDir, projectDir) {

    override val srcDir
        get() = File(projectDir, "src/main/kotlin")

}