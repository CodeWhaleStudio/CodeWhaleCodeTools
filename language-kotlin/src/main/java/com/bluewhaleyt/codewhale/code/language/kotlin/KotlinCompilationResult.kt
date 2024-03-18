package com.bluewhaleyt.codewhale.code.language.kotlin

import com.bluewhaleyt.codewhale.code.core.compiler.CompilationResult
import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi

@ExperimentalCompilerApi
data class KotlinCompilationResult(
    override var output: String? = null,
    override var error: Throwable? = null
) : CompilationResult()