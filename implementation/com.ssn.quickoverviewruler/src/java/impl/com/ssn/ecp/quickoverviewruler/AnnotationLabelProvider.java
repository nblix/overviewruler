/*
 * Copyright (c) 2006 SSI Schaefer Noell GmbH
 *
 * $Header: /home/cvs/data1/InternalProjects/SSNEclipsePlugin/Implementation/plugins/com.ssn.ecp.quickoverviewruler/src/java/impl/com/ssn/ecp/quickoverviewruler/AnnotationLabelProvider.java,v 1.2 2007/10/01 15:05:32 cvogele Exp $
 *
 * Change History
 *   $Log: AnnotationLabelProvider.java,v $
 *   Revision 1.2  2007/10/01 15:05:32  cvogele
 *   show warnings
 *
 *   Revision 1.1  2007/10/01 09:53:32  cvogele
 *   initial checkin
 *
 */

package com.ssn.ecp.quickoverviewruler;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.LabelProvider;

public class AnnotationLabelProvider extends LabelProvider {

  private final QuickAnnotationInformationControl informationControl;
  private Map<Object, String> annotationTypes;

  public Map<Object, String> getAnnotationTypes() {
    return annotationTypes;
  }

  public void setAnnotationTypes(Map<Object, String> annotationTypes) {
    this.annotationTypes = annotationTypes;
  }

  public AnnotationLabelProvider(QuickAnnotationInformationControl informationControl) {
    this.informationControl = informationControl;
  }

  @Override
  public String getText(Object element) {
    if (element instanceof AnnotationWithPosition) {
      AnnotationWithPosition annotation = (AnnotationWithPosition) element;
      String lineText = getAnnotationText(annotation);
      IDocument document = informationControl.getEditor().getViewer().getDocument();
      try {
        int lineNr = document.getLineOfOffset(annotation.getPosition().getOffset());
        lineNr++; // +1, because the returned line is 0-based
        return lineNr + ": " + lineText;
      } catch (BadLocationException e) {
        e.printStackTrace();
        return lineText;
      }
    } else {
      return annotationTypes.get(element);
    }
  }

  public String getAnnotationText(AnnotationWithPosition annotation) {
    try {
      IDocument document = informationControl.getEditor().getViewer().getDocument();
      IRegion lineRegion = document.getLineInformationOfOffset(annotation.getPosition().getOffset());
      String lineText = document.get(lineRegion.getOffset(), lineRegion.getLength()).replace("\t", "  ");
      String type = annotation.getAnnotation().getType();
      if (type.contains("warning") || type.contains("info") || type.contains("warning")) {
        return annotation.getAnnotation().getText() + ": " + lineText;
      } else {
        return lineText;
      }
    } catch (BadLocationException e) {
      String annoText = annotation.getAnnotation().getText() + " (" + annotation.getAnnotation().getType() + ")";
      return annoText;
    }
  }
}
