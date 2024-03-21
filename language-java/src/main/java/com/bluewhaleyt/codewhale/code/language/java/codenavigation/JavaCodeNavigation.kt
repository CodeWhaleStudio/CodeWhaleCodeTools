package com.bluewhaleyt.codewhale.code.language.java.codenavigation

import com.bluewhaleyt.codewhale.code.core.codenavigation.CodeNavigation
import com.intellij.core.JavaCoreApplicationEnvironment
import com.intellij.core.JavaCoreProjectEnvironment
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.extensions.impl.ExtensionsAreaImpl
import com.intellij.openapi.vfs.impl.VirtualFileManagerImpl
import com.intellij.psi.JavaModuleSystem
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDeclarationStatement
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiElementFinder
import com.intellij.psi.PsiField
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiLocalVariable
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiVariable
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

    fun getSymbols(): List<JavaCodeNavigationSymbol> {
        var symbols = mutableListOf<JavaCodeNavigationSymbol>()
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
    ): List<JavaCodeNavigationSymbol> {
        val navigationItems = mutableListOf<JavaCodeNavigationSymbol>()
        val item = extractClass(psiClass, depth)
        if (depth == 0) navigationItems.add(item)

        psiClass.children.forEach { child ->
            when (child) {
                is PsiMethod -> {
                    val startPosition = child.textOffset
                    val endPosition = startPosition + child.textLength

                    val methodItem = JavaCodeNavigationSymbol(
                        name = child.name,
                        modifiers = child.modifierList.text,
                        kind = JavaCodeNavigationSymbolKind.Method,
                        startPosition = startPosition,
                        endPosition = endPosition,
                        javadocComment = child.docComment?.text,
                        type = child.returnTypeElement?.text ?: "void",
                        parameters = child.parameterList.parameters.map { it.text },
                        throws = child.throwsList.referenceElements.map { it.text },
                        depth = depth + 1
                    )
                    navigationItems.add(methodItem)

                    child.body?.let { methodBody ->
                        methodBody.children.forEach { statement ->
                            if (statement is PsiDeclarationStatement) {
                                statement.declaredElements.forEach { element ->
                                    if (element is PsiLocalVariable) {
                                        val variableStartPosition = element.textOffset
                                        val variableEndPosition = variableStartPosition + element.textLength

                                        val variableItem = JavaCodeNavigationSymbol(
                                            name = element.name,
                                            modifiers = element.modifierList?.text,
                                            kind = JavaCodeNavigationSymbolKind.Variable,
                                            startPosition = variableStartPosition,
                                            endPosition = variableEndPosition,
                                            type = element.typeElement.text ?: "void",
                                            depth = depth + 2
                                        )
                                        navigationItems.add(variableItem)
                                    }
                                }
                            }
                        }
                    }
                }
                is PsiField -> {
                    val startPosition = child.textOffset
                    val endPosition = startPosition + child.textLength

                    val fieldItem = JavaCodeNavigationSymbol(
                        name = child.name,
                        modifiers = child.modifierList?.text,
                        kind = JavaCodeNavigationSymbolKind.Field,
                        startPosition = startPosition,
                        endPosition = endPosition,
                        javadocComment = child.docComment?.text,
                        type = child.typeElement?.text ?: "void",
                        depth = depth + 1
                    )
                    navigationItems.add(fieldItem)
                }
                is PsiClass -> {
                    val childItem = extractClass(child, depth + 1)
                    navigationItems.add(childItem)
                    navigationItems.addAll(extract(child, depth + 1))
                }
            }
        }
        return navigationItems
    }

    private fun extractClass(
        psiClass: PsiClass,
        depth: Int
    ): JavaCodeNavigationSymbol {
        val item = JavaCodeNavigationSymbol(
            kind = JavaCodeNavigationSymbolKind.Class
        )
        if (psiClass.extendsList != null && psiClass.extendsList!!.referenceElements.isNotEmpty()) {
            item.extends = psiClass.extendsList?.referenceElements?.map { it.text }
        }
        if (psiClass.implementsList != null && psiClass.implementsList!!.referenceElements.isNotEmpty()) {
            item.implements = psiClass.implementsList?.referenceElements?.map { it.text }
        }
        item.apply {
            name = psiClass.name
            modifiers = psiClass.modifierList?.text
            startPosition = psiClass.textOffset
            endPosition = psiClass.textOffset + psiClass.textLength
            javadocComment = psiClass.docComment?.text
            this.depth = depth
        }
        return item
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
        if (rootArea.hasExtensionPoint("com.intellij.javaModuleSystem").not()) {
            rootArea.registerExtensionPoint(
                "com.intellij.javaModuleSystem",
                JavaModuleSystem::class.java.name,
                ExtensionPoint.Kind.INTERFACE
            )
        }
    }

}