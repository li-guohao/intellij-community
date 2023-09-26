// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.kotlin.idea.fir.resolve

import org.jetbrains.kotlin.analysis.api.KtAnalysisSession
import org.jetbrains.kotlin.analysis.api.symbols.AdditionalKDocResolutionProvider
import org.jetbrains.kotlin.analysis.api.symbols.KtSymbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement

object AdditionalKDocResolutionProviderForTest: AdditionalKDocResolutionProvider {
    context(KtAnalysisSession)
    override fun resolveKdocFqName(fqName: FqName, contextElement: KtElement): Collection<KtSymbol> =
        contextElement.containingKtFile.declarations.filter { it.name == fqName.shortName().asString() }.map { it.getSymbol() }
}