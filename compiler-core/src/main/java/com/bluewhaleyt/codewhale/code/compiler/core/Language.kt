package com.bluewhaleyt.codewhale.code.compiler.core

enum class Language(
    val hasStandardInput: Boolean = false
) {
    Sass,
    Java(hasStandardInput = true),
    Kotlin(hasStandardInput = true)
}