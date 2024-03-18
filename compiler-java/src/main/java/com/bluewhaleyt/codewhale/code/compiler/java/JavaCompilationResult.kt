package com.bluewhaleyt.codewhale.code.compiler.java

import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult

data class JavaCompilationResult(
    override var output: String? = null,
    override var error: Throwable? = null,
) : CompilationResult()