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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.AbstractInformationControl;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;

@SuppressWarnings("restriction")
public class QuickAnnotationInformationControl extends AbstractInformationControl {

  private final JavaEditor editor;
  private AnnotationContentProvider contentProvider;
  private AnnotationLabelProvider labelProvider;

  public QuickAnnotationInformationControl(Shell parent, int shellStyle, int treeStyle, JavaEditor editor) {
    super(parent, shellStyle, treeStyle, null, true);
    this.editor = editor;
    setSize(400, 400);
  }

  @Override
  protected TreeViewer createTreeViewer(Composite parent, int style) {
    Tree tree = new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.heightHint = tree.getItemHeight() * 12;
    tree.setLayoutData(gd);

    tree.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        // do nothing
      }

      public void widgetDefaultSelected(SelectionEvent e) {
        e.doit = false;
        goToSelectedElement();
      }
    });
    contentProvider = new AnnotationContentProvider(this);
    labelProvider = new AnnotationLabelProvider(this);
    Map<Object, String> annotationTypes = loadAnnotationTypes();
    contentProvider.setAnnotationTypes(new ArrayList<Object>(annotationTypes.keySet()));
    labelProvider.setAnnotationTypes(annotationTypes);
    TreeViewer treeViewer = new TreeViewer(tree);
    treeViewer.setContentProvider(contentProvider);
    treeViewer.setLabelProvider(labelProvider);
    treeViewer.setAutoExpandLevel(2);
    return treeViewer;
  }

  @Override
  protected Text createFilterText(Composite parent) {
    Text fFilterText = new Text(parent, SWT.NONE);
    Dialog.applyDialogFont(fFilterText);

    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.CENTER;
    fFilterText.setLayoutData(data);

    fFilterText.addKeyListener(new KeyListener() {
      @SuppressWarnings("synthetic-access")
      public void keyPressed(KeyEvent e) {
        if (e.keyCode == 0x0D) // return
          goToSelectedElement();
        if (e.keyCode == SWT.ARROW_DOWN)
          getTreeViewer().getTree().setFocus();
        if (e.keyCode == SWT.ARROW_UP)
          getTreeViewer().getTree().setFocus();
        if (e.character == 0x1B) // ESC
          dispose();
      }

      public void keyReleased(KeyEvent e) {
        // do nothing
      }
    });

    try {
      Field field = AbstractInformationControl.class.getDeclaredField("fFilterText");
      field.setAccessible(true);
      field.set(this, fFilterText);
    } catch (Exception e) {
      logError(e);
    }

    return fFilterText;
  }

  void goToSelectedElement() {
    Object selectedElement = getSelectedElement();
    if (selectedElement instanceof AnnotationWithPosition) {
      AnnotationWithPosition anno = (AnnotationWithPosition) selectedElement;
      dispose();
      getEditor().getViewer().setSelectedRange(anno.getPosition().getOffset(), anno.getPosition().getLength());
      getEditor().getViewer().revealRange(anno.getPosition().getOffset(), anno.getPosition().getLength());
    }

  }

  private Map<Object, String> loadAnnotationTypes() {
    MarkerAnnotationPreferences markerPreferences = EditorsPlugin.getDefault().getMarkerAnnotationPreferences();
    Map<Object, String> types = new LinkedHashMap<Object, String>();
    for (Iterator iterator = markerPreferences.getAnnotationPreferences().iterator(); iterator.hasNext();) {
      AnnotationPreference elem = (AnnotationPreference) iterator.next();
      if (elem.getOverviewRulerPreferenceValue()) {
        types.put(elem.getAnnotationType(), elem.getPreferenceLabel());
      }
    }
    for (Iterator iterator = markerPreferences.getAnnotationPreferenceFragments().iterator(); iterator.hasNext();) {
      AnnotationPreference elem = (AnnotationPreference) iterator.next();
      if (elem.getOverviewRulerPreferenceValue()) {
        types.put(elem.getAnnotationType(), elem.getPreferenceLabel());
      }
    }
    return types;
  }

  @Override
  protected String getId() {
    return "com.ssn.ecp.quickoverviewruler.view.quickAnnotations";
  }

  @Override
  public void setInput(Object information) {
    getTreeViewer().setInput(information);
  }

  public JavaEditor getEditor() {
    return editor;
  }

  @Override
  protected void selectFirstMatch() {
    Tree tree = getTreeViewer().getTree();
    Object element = null;
    if (fPatternMatcher != null) {
      element = findElement(tree.getItems());
    }
    if (element != null)
      getTreeViewer().setSelection(new StructuredSelection(element), true);
    else
      getTreeViewer().setSelection(StructuredSelection.EMPTY);
  }

  private Object findElement(TreeItem[] items) {
    for (int i = 0; i < items.length; i++) {
      Object data = items[i].getData();
      if (data instanceof AnnotationWithPosition) {
        String text = labelProvider.getAnnotationText((AnnotationWithPosition) data).trim();
        if (fPatternMatcher.matches(text)) {
          return data;
        }
      }
      Object childElement = findElement(items[i].getItems());
      if (childElement != null) {
        return childElement;
      }
      if (fPatternMatcher.matches(items[i].getText())) {
        return data;
      }
    }
    return null;
  }

  public static void logError(final Exception e) {
    final Display display = Workbench.getInstance().getDisplay();
    display.syncExec(new Runnable() {
      public void run() {
        String message = "Error at opening overview ruler: " + e;
        Status status = new Status(IStatus.ERROR, "com.ssn.ecp.quickoverviewruler", message);
        ErrorDialog.openError(display.getActiveShell(), "Error", message, status);
      }
    });
  }

}
