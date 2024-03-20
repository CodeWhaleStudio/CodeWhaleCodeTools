package com.bluewhaleyt.codewhale.code.language.java.compiler

import android.os.Build
import com.android.tools.r8.CompilationMode
import com.bluewhaleyt.codewhale.code.core.compiler.CompileOptions
import java.io.InputStream

open class JavaCompileOptions(
    var sourceVersion: String = "8",
    var targetVersion: String = sourceVersion,
    var generateJar: Boolean = false,
    var flags: String? = null,
    var minApiLevel: Int = Build.VERSION_CODES.O,
    var mode: CompilationMode = CompilationMode.DEBUG,
    var inputStream: InputStream? = null,
    open var className: String = "Main"
) : CompileOptions()