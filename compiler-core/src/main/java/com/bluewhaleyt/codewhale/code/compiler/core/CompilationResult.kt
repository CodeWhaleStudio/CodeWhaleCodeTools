package com.bluewhaleyt.codewhale.code.compiler.core

abstract class CompilationResult(
    var output: String? = null,
    var error: Throwable? = null
)