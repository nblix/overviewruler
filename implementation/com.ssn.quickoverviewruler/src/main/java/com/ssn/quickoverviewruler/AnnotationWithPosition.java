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

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

public class AnnotationWithPosition implements Comparable<AnnotationWithPosition> {

  private Annotation annotation;
  private Position position;

  public AnnotationWithPosition(Annotation annotation, Position position) {
    super();
    this.annotation = annotation;
    this.position = position;
  }

  public Annotation getAnnotation() {
    return annotation;
  }

  public void setAnnotation(Annotation annotation) {
    this.annotation = annotation;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public int compareTo(AnnotationWithPosition o) {
    return position.getOffset() - o.getPosition().getOffset();
  }

  // @see java.lang.Object#equals(java.lang.Object)
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    } else {
      AnnotationWithPosition that = (AnnotationWithPosition) obj;
      return annotation.equals(that.annotation) && position.equals(that.position);
    }
  }

  @Override
  public int hashCode() {
    return annotation.hashCode() + 17 * position.hashCode();
  }
}
