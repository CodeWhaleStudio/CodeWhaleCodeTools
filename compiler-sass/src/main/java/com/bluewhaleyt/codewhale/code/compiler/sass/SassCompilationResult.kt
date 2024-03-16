package com.bluewhaleyt.codewhale.code.compiler.sass

import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult

data class SassCompilationResult(
    override var output: String? = null,
    override var error: Throwable? = null,
    var css: String? = null
) : CompilationResult()