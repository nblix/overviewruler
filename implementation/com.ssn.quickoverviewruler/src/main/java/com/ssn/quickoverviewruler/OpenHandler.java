/**
 * Copyright (C) 2015 Jann Schneider
 *   
 *    This file is part of the QuickoverviewRuler Feature.
 *
 *   this is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The QuickoverviewRuler feature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public License
 * along with the QuickoverviewRuler Feature.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ssn.quickoverviewruler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.JavaElementProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.Workbench;

public class OpenHandler extends AbstractHandler {

  public Object execute(ExecutionEvent arg0) {
    IEditorPart editor = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    if (editor instanceof JavaEditor) {
      showQuickAnnotations((JavaEditor) editor);
    }
    return null;
  }

  private void showQuickAnnotations(final JavaEditor editor) {
    InformationPresenter presenter = new InformationPresenter(new IInformationControlCreator() {
      public IInformationControl createInformationControl(Shell parent) {
        int shellStyle = SWT.RESIZE;
        int treeStyle = SWT.V_SCROLL | SWT.H_SCROLL;
        return new QuickAnnotationInformationControl(parent, shellStyle, treeStyle, editor);
      }
    });

    IInformationProvider provider = new JavaElementProvider(editor, false);
    presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);

    presenter.install(editor.getViewer());
    presenter.install(editor.getViewer().getTextWidget());
    presenter.showInformation();
  }

}
