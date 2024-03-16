package com.bluewhaleyt.codewhale.code.compiler.sass

import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult

data class SassCompilationResult(
    var css: String? = null
) : CompilationResult()