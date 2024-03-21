package com.bluewhaleyt.codewhale.code.language.java.codenavigation

enum class JavaCodeNavigationSymbolKind {
    Class, Method, Field
}

data class JavaCodeNavigationSymbol(
    var name: String? = null,
    var modifiers: String? = null,
    var startPosition: Int = 0,
    var endPosition: Int = 0,
    var kind: JavaCodeNavigationSymbolKind,
    var depth: Int = 0,
    var javadocComment: String? = null,

    var extends: List<String>? = null,
    var implements: List<String>? = null,

    var type: String? = null,
    var parameters: List<String>? = null,
)