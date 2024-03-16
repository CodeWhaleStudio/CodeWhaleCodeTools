package com.bluewhaleyt.codewhale.code.compiler.sass

import com.bluewhaleyt.codewhale.code.compiler.core.CompilerOptions
import java.io.File

data class SassCompilerOptions(
    val file: File,
) : CompilerOptions()