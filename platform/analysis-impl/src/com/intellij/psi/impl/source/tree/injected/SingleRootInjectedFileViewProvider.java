// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package com.intellij.psi.impl.source.tree.injected;

import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.lang.Language;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class SingleRootInjectedFileViewProvider extends SingleRootFileViewProvider implements InjectedFileViewProvider {
  static final ThreadLocal<Boolean> disabledTemporarily = ThreadLocal.withInitial(() -> false);
  static final Key<Language> LANGUAGE_FOR_INJECTED_COPY_KEY = Key.create("LANGUAGE_FOR_INJECTED_COPY_KEY");
  private final Object myLock = new Object();
  private final DocumentWindowImpl myDocumentWindow;
  private boolean myPatchingLeaves;

  SingleRootInjectedFileViewProvider(@NotNull PsiManager psiManager,
                                     @NotNull VirtualFileWindow virtualFile,
                                     @NotNull DocumentWindowImpl documentWindow,
                                     @NotNull Language language) {
    super(psiManager, (VirtualFile)virtualFile, true, language);
    myDocumentWindow = documentWindow;
  }

  @Override
  public Object getLock() {
    return myLock;
  }

  @Override
  public boolean getPatchingLeaves() {
    return myPatchingLeaves;
  }

  @Override
  public FileViewProvider clone() {
    return cloneImpl();
  }

  @Override
  public void rootChanged(@NotNull PsiFile psiFile) {
    super.rootChanged(psiFile);
    rootChangedImpl(psiFile);
  }

  @Override
  public boolean isEventSystemEnabled() {
    return isEventSystemEnabledImpl();
  }

  @Override
  public boolean isPhysical() {
    return isPhysicalImpl();
  }

  @Override
  @NotNull
  public DocumentWindowImpl getDocument() {
    return myDocumentWindow;
  }

  @NonNls
  @Override
  public String toString() {
    return "Single root injected file '"+getVirtualFile().getName()+"' " + (isValid() ? "" : " invalid") + (isPhysical() ? "" : " nonphysical");
  }

  public void doNotInterruptMeWhileImPatchingLeaves(@NotNull Runnable runnable) {
    myPatchingLeaves = true;
    try {
      runnable.run();
    }
    finally {
      myPatchingLeaves = false;
    }
  }
}
