package com.bluewhaleyt.codewhale.code.compiler.core

abstract class CompilationResult(
    open var output: String? = null,
    open var error: Throwable? = null
)