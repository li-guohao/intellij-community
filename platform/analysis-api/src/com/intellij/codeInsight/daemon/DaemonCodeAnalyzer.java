// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.codeInsight.daemon;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Manages the background highlighting and auto-import for files displayed in editors.
 */
public abstract class DaemonCodeAnalyzer {
  public static DaemonCodeAnalyzer getInstance(Project project) {
    return project.getService(DaemonCodeAnalyzer.class);
  }

  public abstract void settingsChanged();

  public abstract void setUpdateByTimerEnabled(boolean value);
  public abstract void disableUpdateByTimer(@NotNull Disposable parentDisposable);

  public abstract boolean isHighlightingAvailable(@NotNull PsiFile file);

  public abstract void setImportHintsEnabled(@NotNull PsiFile file, boolean value);
  public abstract void resetImportHintsEnabledForProject();
  public abstract void setHighlightingEnabled(@NotNull PsiFile file, boolean value);
  public abstract boolean isImportHintsEnabled(@NotNull PsiFile file);
  public abstract boolean isAutohintsAvailable(@NotNull PsiFile file);

  /**
   * Force re-highlighting for all files.
   */
  public abstract void restart();

  /**
   * Force re-highlighting for a specific file.
   * @param file the file to rehighlight.
   */
  public abstract void restart(@NotNull PsiFile file);

  public abstract void autoImportReferenceAtCursor(@NotNull Editor editor, @NotNull PsiFile file);

  public static final Topic<DaemonListener> DAEMON_EVENT_TOPIC = new Topic<>(DaemonListener.class, Topic.BroadcastDirection.NONE, true);

  /**
   * Listener for various events during daemon lifecycle.
   * You register your own listener implementaion by adding lines like these into your plugin.xml:
   * <pre>
   * {@code
   *  <projectListeners>
   *     <listener class="your.own.class.name" topic="com.intellij.codeInsight.daemon.DaemonCodeAnalyzer$DaemonListener"/>
   *  </projectListeners>
   * }
   * </pre>
   */
  public interface DaemonListener {
    /**
     * Fired when the background code analysis is being scheduled for the specified set of files.
     * @param fileEditors The list of files that will be analyzed during the current execution of the daemon.
     */
    default void daemonStarting(@NotNull Collection<? extends @NotNull FileEditor> fileEditors) {
    }

    /**
     * @see DaemonListener#daemonFinished(Collection)
     */
    default void daemonFinished() {
    }

    /**
     * Fired when the background code analysis is stopped.
     * Analysis can be stopped because either it was completed, or it was canceled, for various reasons.
     * If it was canceled, the {@link DaemonListener#daemonCancelEventOccurred(String)} is called at least once
     * between {@link DaemonListener#daemonStarting(Collection)} and this method invocations.
     * @param fileEditors The list of files analyzed during the current execution of the daemon.
     */
    default void daemonFinished(@NotNull Collection<? extends @NotNull FileEditor> fileEditors) {
      daemonFinished();
    }

    /**
     * Fired when the daemon is canceled because of user tries to type something into the document or other reasons.
     * Please don't do anything remotely expensive in your listener implementation because it's called in the background thread under the read action,
     * and if it's not fast enough, it could slow down the highlighting process and hurt overall responsiveness.
     */
    default void daemonCanceled(@NotNull String reason, @NotNull Collection<? extends @NotNull FileEditor> fileEditors) {
      daemonCancelEventOccurred(reason);
    }

    /**
     * @see DaemonListener#daemonCanceled(String, Collection)
     */
    default void daemonCancelEventOccurred(@NotNull String reason) {
    }

    /**
     * Internal class for reporting annotator-related statistics
     */
    @ApiStatus.Internal
    class AnnotatorStatistics {
      /** the annotator this statistics is generated for */
      public final Annotator annotator;
      /** timestamp (in {@link System#nanoTime} sense) of the {@link #annotator} creation in {@link com.intellij.codeInsight.daemon.impl.DefaultHighlightVisitor} */
      public long annotatorStartStamp;
      /** timestamp (in {@link System#nanoTime} sense) of the first call to {@link com.intellij.lang.annotation.AnnotationHolder#newAnnotation} by this annotator in this annotation session (or {@code 0} if there were no annotations produced)*/
      public long firstAnnotationStamp;
      /** the annotation passed to the first call to {@link com.intellij.lang.annotation.AnnotationHolder#newAnnotation} by this annotator in this annotation session (or {@code null} if there were no annotations produced)*/
      public Annotation firstAnnotation;
      /** timestamp (in {@link System#nanoTime} sense) of the last call to {@link com.intellij.lang.annotation.AnnotationHolder#newAnnotation} by this annotator in this annotation session (or {@code 0} if there were no annotations produced)*/
      public long lastAnnotationStamp;
      /** the annotation passed to the last call to {@link com.intellij.lang.annotation.AnnotationHolder#newAnnotation} by this annotator in this annotation session (or {@code null} if there were no annotations produced)*/
      public Annotation lastAnnotation;
      /** timestamp (in {@link System#nanoTime} sense) of the finish of the {@link com.intellij.codeInsight.daemon.impl.DefaultHighlightVisitor#analyze} method */
      public long annotatorFinishStamp;

      AnnotatorStatistics(@NotNull Annotator annotator) {
        this.annotator = annotator;
      }
    }

    @ApiStatus.Internal
    default void daemonAnnotatorStatisticsGenerated(@NotNull AnnotationSession session,
                                                    @NotNull Collection<? extends AnnotatorStatistics> statistics,
                                                    @NotNull PsiFile file) {
    }
  }
}
