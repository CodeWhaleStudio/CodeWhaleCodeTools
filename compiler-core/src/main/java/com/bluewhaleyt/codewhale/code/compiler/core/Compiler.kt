package com.bluewhaleyt.codewhale.code.compiler.core

abstract class Compiler<T>(
    open val reporter: CompileReporter,
    options: CompilerOptions,
) {
    open fun compile(): CompilationResult? = null
}