package com.bluewhaleyt.codewhale.code.language.java.compiler

import com.bluewhaleyt.codewhale.code.core.compiler.CompilationResult

data class JavaCompilationResult(
    override var output: String? = null,
    override var error: Throwable? = null,
) : CompilationResult()