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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

@SuppressWarnings("restriction")
public class AnnotationContentProvider implements ITreeContentProvider {

  private final QuickAnnotationInformationControl infoControl;

  private List<Object> annotationTypes;
  private int currentIndex = -1;
  private List<AnnotationWithPosition> annotations;
  private DefaultMarkerAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();

  public AnnotationContentProvider(QuickAnnotationInformationControl infoControl) {
    super();
    this.infoControl = infoControl;
  }

  private List<AnnotationWithPosition> findAnnotations() {
    List<AnnotationWithPosition> annos = new ArrayList<AnnotationWithPosition>();
    try {
      IAnnotationModel model = infoControl.getEditor().getViewer().getAnnotationModel();
      for (Iterator iterator = model.getAnnotationIterator(); iterator.hasNext();) {
        Annotation anno = (Annotation) iterator.next();
        if (containsAnnotationType(anno)) {
          Position position = model.getPosition(anno);
          AnnotationWithPosition newAnno = new AnnotationWithPosition(anno, position);
          if (!containsAnnotation(annos, newAnno)) {
            annos.add(newAnno);
          }
        }
      }
    } catch (Exception e) {
      QuickAnnotationInformationControl.logError(e);
    }
    return annos;
  }

  private boolean containsAnnotation(List<AnnotationWithPosition> annos, AnnotationWithPosition newAnno) {
    for (Iterator iterator = annos.iterator(); iterator.hasNext();) {
      AnnotationWithPosition old = (AnnotationWithPosition) iterator.next();
      if (old.getAnnotation().getType().equals(newAnno.getAnnotation().getType()) && old.getPosition().equals(newAnno.getPosition())) {
        return true;
      }
    }
    return false;
  }

  private boolean containsAnnotationType(Annotation anno) {
    for (Iterator iterator = annotationTypes.iterator(); iterator.hasNext();) {
      Object annoType = iterator.next();
      if (hasAnnotationType(anno, annoType)) {
        return true;
      }
    }
    return annotationTypes.contains(anno.getType());
  }

  private boolean hasAnnotationType(Annotation anno, Object annoType) {
    return annotationAccess.isSubtype(anno.getType(), annoType);
  }

  public Object[] getChildren(Object parentElement) {
    if (parentElement instanceof AnnotationWithPosition) {
      return new Object[0];
    } else {
      return getAnnotationsOfType(parentElement);
    }
  }

  private Object[] getAnnotationsOfType(Object type) {
    ArrayList<AnnotationWithPosition> annosOfType = new ArrayList<AnnotationWithPosition>();
    for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {
      AnnotationWithPosition anno = (AnnotationWithPosition) iterator.next();
      if (hasAnnotationType(anno.getAnnotation(), type)) {
        annosOfType.add(anno);
      }
    }
    Collections.sort(annosOfType);
    return annosOfType.toArray();
  }

  public Object getParent(Object element) {
    if (element instanceof AnnotationWithPosition) {
      AnnotationWithPosition anno = (AnnotationWithPosition) element;
      return anno.getAnnotation().getType();
    }
    return null;
  }

  public boolean hasChildren(Object element) {
    return !(element instanceof AnnotationWithPosition);
  }

  public Object[] getElements(Object inputElement) {
    if (currentIndex == -1) {
      return getUsedCategories();
    } else {
      return new Object[] { annotationTypes.get(currentIndex) };
    }
  }

  private Object[] getUsedCategories() {
    ArrayList<Object> types = new ArrayList<Object>();
    for (Iterator iterator = annotationTypes.iterator(); iterator.hasNext();) {
      Object t = iterator.next();
      if (hasAnnotationOfType(t)) {
        types.add(t);
      }
    }
    return types.toArray();
  }

  private boolean hasAnnotationOfType(Object type) {
    for (Iterator iterator = getAnnotations().iterator(); iterator.hasNext();) {
      AnnotationWithPosition anno = (AnnotationWithPosition) iterator.next();
      if (hasAnnotationType(anno.getAnnotation(), type)) {
        return true;
      }
    }
    return false;
  }

  private List<AnnotationWithPosition> getAnnotations() {
    if (annotations == null) {
      annotations = findAnnotations();
    }
    return annotations;
  }

  public void dispose() {
    // empty
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // empty
  }

  public List<Object> getAnnotationTypes() {
    return annotationTypes;
  }

  public void setAnnotationTypes(List<Object> annotationTypes) {
    this.annotationTypes = annotationTypes;
  }

  public void showNextAnnotationType() {
    currentIndex++;
    if (currentIndex >= annotationTypes.size()) {
      currentIndex = -1;
    }
  }

}
