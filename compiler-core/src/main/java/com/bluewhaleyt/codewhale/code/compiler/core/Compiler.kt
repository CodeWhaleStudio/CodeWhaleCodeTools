package com.bluewhaleyt.codewhale.code.compiler.core

abstract class Compiler<T>(
    reporter: CompileReporter,
    options: CompilerOptions,
) {
    open fun compile(): CompilationResult? = null
}