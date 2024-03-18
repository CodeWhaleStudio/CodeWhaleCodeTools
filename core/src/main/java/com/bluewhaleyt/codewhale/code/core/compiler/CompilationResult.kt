package com.bluewhaleyt.codewhale.code.core.compiler

abstract class CompilationResult(
    open var output: String? = null,
    open var error: Throwable? = null,
)