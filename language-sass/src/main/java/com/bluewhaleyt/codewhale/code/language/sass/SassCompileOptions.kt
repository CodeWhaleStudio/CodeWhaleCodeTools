package com.bluewhaleyt.codewhale.code.language.sass

import com.bluewhaleyt.codewhale.code.core.compiler.CompileOptions
import java.io.File

data class SassCompileOptions(
    val file: File,
) : CompileOptions()