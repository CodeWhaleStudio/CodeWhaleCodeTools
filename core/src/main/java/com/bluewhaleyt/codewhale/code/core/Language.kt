package com.bluewhaleyt.codewhale.code.core

enum class Language(
    val fileExtensions: Array<String>,
    val keywords: Array<String> = emptyArray<String>(),
    val hasStandardInput: Boolean = false
) {
    Sass(
        fileExtensions = arrayOf("sass", "scss", "css")
    ),
    Java(
        fileExtensions = arrayOf("jav", "java"),
        keywords = arrayOf(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false", "null"
        ),
        hasStandardInput = true
    ),
    Kotlin(
        fileExtensions = arrayOf("kt"),
        hasStandardInput = true
    )
}