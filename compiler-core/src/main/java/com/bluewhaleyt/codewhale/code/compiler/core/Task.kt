package com.bluewhaleyt.codewhale.code.compiler.core

interface Task {
    fun execute(reporter: CompileReporter)
}