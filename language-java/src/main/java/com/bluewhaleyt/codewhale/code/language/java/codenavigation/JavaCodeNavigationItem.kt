package com.bluewhaleyt.codewhale.code.language.java.codenavigation

enum class JavaCodeNavigationItemKind {
    Class, Method, Field
}

data class JavaCodeNavigationItem(
    val name: String,
    val modifier: String,
    val startPosition: Int,
    val endPosition: Int,
    val kind: JavaCodeNavigationItemKind,
    val depth: Int = 0
)