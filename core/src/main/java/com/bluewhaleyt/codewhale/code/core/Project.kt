package com.bluewhaleyt.codewhale.code.core

import java.io.File
import java.io.Serializable

abstract class Project(
    open val projectDir: File
) : Serializable {
    val name: String
        get() = projectDir.name

    fun delete() {
        if (projectDir.isDirectory && projectDir.name == name) {
            projectDir.deleteRecursively()
        } else {
            throw IllegalStateException("Failed to delete directory: ${projectDir.absolutePath}")
        }
    }
}