/*
 * Copyright (c) 2006 SSI Schaefer Noell GmbH
 *
 * $Header: /home/cvs/data1/InternalProjects/SSNEclipsePlugin/Implementation/plugins/com.ssn.ecp.quickoverviewruler/src/java/impl/com/ssn/ecp/quickoverviewruler/AnnotationWithPosition.java,v 1.3 2007/10/01 15:05:32 cvogele Exp $
 *
 * Change History
 *   $Log: AnnotationWithPosition.java,v $
 *   Revision 1.3  2007/10/01 15:05:32  cvogele
 *   show warnings
 *
 *   Revision 1.2  2007/10/01 10:11:27  cvogele
 *   sort
 *
 *   Revision 1.1  2007/10/01 09:53:32  cvogele
 *   initial checkin
 *
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
