/*
 * Copyright (c) 2006 SSI Schaefer Noell GmbH
 *
 * $Header: /home/cvs/data1/InternalProjects/SSNEclipsePlugin/Implementation/plugins/com.ssn.ecp.quickoverviewruler/src/java/impl/com/ssn/ecp/quickoverviewruler/OpenHandler.java,v 1.2 2008/06/27 08:33:29 cvogele Exp $
 *
 * Change History
 *   $Log: OpenHandler.java,v $
 *   Revision 1.2  2008/06/27 08:33:29  cvogele
 *   fixed annotation
 *
 *   Revision 1.1  2007/10/01 10:07:10  cvogele
 *   don't use action
 *
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
