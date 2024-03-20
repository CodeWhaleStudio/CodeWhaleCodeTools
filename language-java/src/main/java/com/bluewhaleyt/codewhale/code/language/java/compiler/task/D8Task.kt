package com.bluewhaleyt.codewhale.code.language.java.compiler.task

import com.android.tools.r8.D8
import com.android.tools.r8.D8Command
import com.android.tools.r8.OutputMode
import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter
import com.bluewhaleyt.codewhale.code.core.Task
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaProject

class D8Task(
    val project: JavaProject,
    val options: JavaCompileOptions
) : Task {
    override fun execute(reporter: CompileReporter) {
        val classes = project.getClassFiles(
            project.binDir.resolve("classes")
        )
        if (classes.isEmpty()) {
            reporter.reportError("No classes found to compile.")
            return
        }
        D8.run(
            D8Command.builder()
                .setMinApiLevel(options.minApiLevel)
                .setMode(options.mode)
                .addClasspathFiles(project.systemClasspath.map { it.toPath() })
                .addProgramFiles(classes)
                .setOutput(project.binDir.toPath(), OutputMode.DexIndexed)
                .build()
        )
    }
}