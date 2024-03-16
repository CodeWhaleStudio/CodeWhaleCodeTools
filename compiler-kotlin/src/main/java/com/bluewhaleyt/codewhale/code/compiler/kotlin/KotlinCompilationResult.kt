package com.bluewhaleyt.codewhale.code.compiler.kotlin

import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult

data class KotlinCompilationResult(
    override var output: String? = null,
    override var error: Throwable? = null
) : CompilationResult()