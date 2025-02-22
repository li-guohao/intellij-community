// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.startup.importSettings.importer

import com.intellij.ide.startup.importSettings.chooser.ui.BaseSettingPane
import com.intellij.ide.startup.importSettings.chooser.ui.createSettingPane
import com.intellij.ide.startup.importSettings.data.ActionsDataProvider
import com.intellij.ide.startup.importSettings.data.IconProductSize
import com.intellij.ide.startup.importSettings.data.ImportItem
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.UnscaledGaps
import com.intellij.ui.util.maximumWidth
import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import java.awt.Color
import javax.swing.Action
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
import javax.swing.border.LineBorder

open class SettingDialog(private val provider: ActionsDataProvider<*>, val product: ImportItem) : DialogWrapper(null) {
  open val configurable = true
  protected val settingPanes = mutableListOf<BaseSettingPane>()

  private val pane = JPanel(HorizontalLayout(0)).apply {
    add(panel {
      row {
        text("Import<br>Settings From").apply {
          this.component.font = JBFont.h1()
        }.align(AlignY.TOP).customize(UnscaledGaps(20, 0, 17, 0))
      }
      panel {
        provider.getProductIcon(product.id, IconProductSize.MIDDLE)?.let { icn ->
          row {
            icon(icn).align(AlignY.TOP).customize(UnscaledGaps(0, 0, 0, 8))
            panel {
              row {
                text(provider.getText(product)).customize(UnscaledGaps(0, 0, 2, 0))
              }

              provider.getComment(product)?.let { addTxt ->
                row {
                  comment(addTxt).customize(UnscaledGaps(0))
                }
              }
            }
          }
        }
      }.align(AlignY.TOP)
    }.apply {
      preferredSize = JBDimension(200, 110)
      border = JBUI.Borders.emptyLeft(5)
    })

    val productService = provider.productService

    val listPane = JPanel().apply {
      layout = BoxLayout(this, BoxLayout.Y_AXIS)
      productService.getSettings(product.id).forEach {
        val st = createSettingPane(it, configurable)
        settingPanes.add(st)
        add(st.component().apply {
          maximumWidth = 420
        })
      }
    }

    add(
      panel {
        row {
          cell(JBScrollPane(listPane).apply {
            horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_NEVER
            preferredSize = JBDimension(420, 374)
            border = JBUI.Borders.empty(16, 0, 12,0)
          })
        }
      }.apply {
        isOpaque = false
       // preferredSize = JBDimension(420, 374)
        background = Color.RED
        border = LineBorder(Color.BLACK)
      }
    )
  }.apply {
    // preferredSize = JBDimension(640, 410)
  }


  init {
    init()
  }


  override fun createCenterPanel(): JComponent {
    return pane
  }

  override fun getOKAction(): Action {
    return super.getOKAction().apply {
      putValue(Action.NAME, "Import Settings")
    }
  }

  override fun getCancelAction(): Action {
    return super.getCancelAction().apply {
      putValue(Action.NAME, "Back")
    }
  }
}