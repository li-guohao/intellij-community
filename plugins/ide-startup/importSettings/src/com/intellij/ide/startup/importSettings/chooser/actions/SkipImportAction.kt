// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.startup.importSettings.chooser.actions

import com.intellij.ide.startup.importSettings.chooser.ui.JButtonAction_
import com.intellij.ide.ui.laf.darcula.ui.OnboardingDialogButtons
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.JButton

class SkipImportAction : JButtonAction_("Skip Import") {
  init {
    templatePresentation.text = "Skip Import"
    templatePresentation.icon = null
  }

  override fun displayTextInToolbar(): Boolean {
    return true
  }

  override fun actionPerformed(e: AnActionEvent) {

  }


  override fun createButton(): JButton {
    return OnboardingDialogButtons.createHoveredLinkButton()
  }

}

interface LinkAction