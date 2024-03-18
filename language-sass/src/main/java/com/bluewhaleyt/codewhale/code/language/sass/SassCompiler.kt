package com.bluewhaleyt.codewhale.code.language.sass

import android.content.Context
import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter
import com.bluewhaleyt.codewhale.code.core.compiler.Compiler
import com.bluewhaleyt.codewhale.code.core.Language
import de.larsgrefer.sass.embedded.android.AndroidSassCompilerFactory

class SassCompiler(
    private val context: Context,
    override val reporter: CompileReporter = CompileReporter(),
    val options: SassCompileOptions,
) : Compiler<SassCompileOptions>(reporter, options) {

    val language = Language.Sass

    private val compiler = AndroidSassCompilerFactory.bundled(context)

    override fun compile(): SassCompilationResult {
        val compilationResult = SassCompilationResult()
        try {
            if (options.file.isFile) {
                when (options.file.extension.lowercase()) {
                    "css", "scss", "sass" -> reporter.reportInfo("Compiling ${options.file.name}...")
                    else -> reporter.reportError("Invalid file type, file must be in either css, sass or scss format.")
                }
                val compilationSuccess = compiler.compileFile(options.file)
                compilationResult.apply {
                    output = null
                    css = compilationSuccess.css
                }
                reporter.reportSuccess("Compiled successfully, no output is printed.")
            }
        } catch (e: Throwable) {
            compilationResult.error = e
            reporter.reportError("Compilation failed.")
        }
        return compilationResult
    }

}