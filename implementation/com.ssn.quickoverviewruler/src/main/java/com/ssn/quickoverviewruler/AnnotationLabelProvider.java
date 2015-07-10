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
