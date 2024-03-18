package com.bluewhaleyt.codewhale.language.compiler.java

import com.bluewhaleyt.codewhale.code.core.compiler.CompilationResult
import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi

@ExperimentalCompilerApi
data class JavaCompilationResult(
    override var output: String? = null,
    override var error: Throwable? = null,
) : CompilationResult()