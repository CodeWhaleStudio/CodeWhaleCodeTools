package com.bluewhaleyt.codewhale.code.language.kotlin

import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi
import com.bluewhaleyt.codewhale.language.compiler.java.JavaProject
import java.io.File

@ExperimentalCompilerApi
@OptIn(ExperimentalCompilerApi::class)
data class KotlinProject(
    override var rootDir: File,
    override var projectDir: File,
) : JavaProject(rootDir, projectDir) {

    override val srcDir
        get() = File(projectDir, "src/main/kotlin")

}