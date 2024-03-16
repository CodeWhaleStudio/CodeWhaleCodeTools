package com.bluewhaleyt.codewhale.code.compiler.sass

import com.bluewhaleyt.codewhale.code.compiler.core.CompileOptions
import java.io.File

data class SassCompileOptions(
    val file: File,
) : CompileOptions()