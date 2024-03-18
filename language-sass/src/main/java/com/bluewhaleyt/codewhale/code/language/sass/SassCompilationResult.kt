package com.bluewhaleyt.codewhale.code.language.sass

import com.bluewhaleyt.codewhale.code.core.compiler.CompilationResult

data class SassCompilationResult(
    override var output: String? = null,
    override var error: Throwable? = null,
    var css: String? = null
) : CompilationResult()