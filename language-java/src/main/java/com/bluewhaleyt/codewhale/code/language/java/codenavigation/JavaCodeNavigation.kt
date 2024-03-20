package com.bluewhaleyt.codewhale.code.language.java.codenavigation

import com.bluewhaleyt.codewhale.code.core.codenavigation.CodeNavigation
import com.intellij.core.JavaCoreApplicationEnvironment
import com.intellij.core.JavaCoreProjectEnvironment
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.extensions.impl.ExtensionsAreaImpl
import com.intellij.openapi.vfs.impl.VirtualFileManagerImpl
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiElementFinder
import com.intellij.psi.PsiField
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.augment.PsiAugmentProvider
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import java.io.File

class JavaCodeNavigation(
    val file: File?,
) : CodeNavigation {

    private val env = JavaCoreProjectEnvironment(
        {}, JavaCoreApplicationEnvironment {}
    )

    private val psiFileFactory by lazy {
        PsiFileFactory.getInstance(env.project)
    }

    init {
        registerExtensions(env.project.extensionArea)
    }

    fun getSymbols(): List<JavaCodeNavigationItem> {
        var symbols = mutableListOf<JavaCodeNavigationItem>()
        val psiJavaFile = psiFileFactory.createFileFromText(
            file?.name.toString(),
            JavaLanguage.INSTANCE,
            file?.readText().toString()
        ) as PsiJavaFile

        if (psiJavaFile.classes.isEmpty()) return emptyList()

        psiJavaFile.classes.forEachIndexed { index, psiClass ->
            symbols = extract(psiClass).toMutableList()
        }

        return symbols
    }

    private fun extract(
        psiClass: PsiClass,
        depth: Int = 0
    ): List<JavaCodeNavigationItem> {
        var depth = depth
        val navigationItems = mutableListOf<JavaCodeNavigationItem>()
        val name = buildString {
            append(psiClass.name)
            if (psiClass.superClass != null) {
                append(" : ")
                append(psiClass.superClass?.name)
            }
            if (psiClass.implementsList != null && psiClass.implementsList!!.referenceElements.isNotEmpty()) {
                append(" implements ")
                append(psiClass.implementsList?.referenceElements?.joinToString(", ") { it.text })
            }
        }
        val item = JavaCodeNavigationItem(
            name = name,
            modifier = psiClass.modifierList!!.text,
            startPosition = psiClass.textOffset,
            endPosition = psiClass.textOffset + psiClass.textLength,
            kind = JavaCodeNavigationItemKind.Class,
            depth = depth
        )
        if (depth == 0) navigationItems.add(item)
        depth++
        psiClass.children.forEachIndexed { index, child ->
            when (child) {
                is PsiMethod -> {
                    val modifiers = child.modifierList
                    val parameters = child.parameterList
                    val returnType = child.returnTypeElement?.text ?: "void"

                    val methodName = child.name + "(" + parameters.parameters.joinToString(", ") {
                        it.typeElement?.text ?: "void"
                    } + ") : $returnType"

                    val startPosition = child.textOffset
                    val endPosition = startPosition + child.textLength

                    val item = JavaCodeNavigationItem(
                        name = methodName,
                        modifier = modifiers.text,
                        startPosition = startPosition,
                        endPosition = endPosition,
                        kind = JavaCodeNavigationItemKind.Method,
                        depth = depth
                    )
                    navigationItems.add(item)
                }
                is PsiField -> {
                    val modifiers = child.modifierList ?: return@forEachIndexed
                    val startPosition = child.textOffset
                    val type = child.typeElement?.text ?: "void"
                    val fieldName = child.name + " : $type"

                    val item = JavaCodeNavigationItem(
                        name = fieldName,
                        modifier = modifiers.text,
                        startPosition = startPosition,
                        endPosition = startPosition + name.length,
                        kind = JavaCodeNavigationItemKind.Field,
                        depth = depth
                    )
                    navigationItems.add(item)
                }
                is PsiClass -> {
                    val modifiers = child.modifierList
                    val innerClassName = buildString {
                        append(psiClass.name)
                        if (psiClass.superClass != null) {
                            append(" : ")
                            append(psiClass.superClass?.name)
                        }
                        if (psiClass.implementsList != null && psiClass.implementsList!!.referenceElements.isNotEmpty()) {
                            append(" implements ")
                            append(psiClass.implementsList?.referenceElements?.joinToString(", ") { it.text })
                        }
                    }
                    val startPosition = child.textOffset
                    val endPosition = startPosition + child.textLength

                    val item = JavaCodeNavigationItem(
                        name = innerClassName,
                        modifier = modifiers!!.text,
                        startPosition = startPosition,
                        endPosition = endPosition,
                        kind = JavaCodeNavigationItemKind.Class,
                        depth = depth
                    )
                    navigationItems.add(item)
                    navigationItems.addAll(
                        extract(child, depth + 1)
                    )
                }
            }
        }
        return navigationItems
    }

    @Suppress("DEPRECATION")
    private fun registerExtensions(extensionArea: ExtensionsAreaImpl) {
        if (!extensionArea.hasExtensionPoint("com.intellij.virtualFileManagerListener")) {
            extensionArea.registerExtensionPoint(
                "com.intellij.virtualFileManagerListener",
                VirtualFileManagerImpl::class.java.name,
                ExtensionPoint.Kind.INTERFACE
            )
        }
        if (extensionArea.hasExtensionPoint("com.intellij.java.elementFinder").not()) {
            extensionArea.registerExtensionPoint(
                "com.intellij.java.elementFinder",
                PsiElementFinder::class.java.name,
                ExtensionPoint.Kind.INTERFACE
            )
        }
        val rootArea = Extensions.getRootArea()
        if (rootArea.hasExtensionPoint("com.intellij.treeCopyHandler").not()) {
            rootArea.registerExtensionPoint(
                "com.intellij.treeCopyHandler",
                TreeCopyHandler::class.java.name,
                ExtensionPoint.Kind.INTERFACE
            )
        }
        if (rootArea.hasExtensionPoint("com.intellij.codeStyleManager").not()) {
            rootArea.registerExtensionPoint(
                "com.intellij.codeStyleManager",
                CodeStyleManager::class.java.name,
                ExtensionPoint.Kind.INTERFACE
            )
        }
        if (rootArea.hasExtensionPoint("com.intellij.psiElementFactory").not()) {
            rootArea.registerExtensionPoint(
                "com.intellij.psiElementFactory",
                PsiElementFactory::class.java.name,
                ExtensionPoint.Kind.INTERFACE
            )
        }
        if (rootArea.hasExtensionPoint("com.intellij.lang.psiAugmentProvider").not()) {
            rootArea.registerExtensionPoint(
                "com.intellij.lang.psiAugmentProvider",
                PsiAugmentProvider::class.java.name,
                ExtensionPoint.Kind.INTERFACE
            )
        }
        if (rootArea.hasExtensionPoint("com.intellij.psiElementFinder").not()) {
            rootArea.registerExtensionPoint(
                "com.intellij.psiElementFinder",
                PsiElementFinder::class.java.name,
                ExtensionPoint.Kind.INTERFACE
            )
        }
    }

}