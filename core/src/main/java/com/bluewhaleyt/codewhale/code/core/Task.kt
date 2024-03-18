package com.bluewhaleyt.codewhale.code.core

import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter

interface Task {
    fun execute(reporter: CompileReporter)
}