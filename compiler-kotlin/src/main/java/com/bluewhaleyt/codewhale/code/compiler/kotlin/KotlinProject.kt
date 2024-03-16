package com.bluewhaleyt.codewhale.code.compiler.kotlin

import com.bluewhaleyt.codewhale.code.compiler.core.Project
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject
import java.io.File

data class KotlinProject(
    override var dataDir: File,
    override var projectDir: File,
) : JavaProject(dataDir, projectDir) {

    override val srcDir
        get() = File(projectDir, "src/main/kotlin")

}