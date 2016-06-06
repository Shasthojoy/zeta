package generator.generators.shape

import generator.generators.style.StyleGenerator
import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics.Alignment.{CENTER, HAlign, LEFT, RIGHT}
import generator.model.shapecontainer.shape.geometrics._
import generator.model.shapecontainer.shape.geometrics.layouts.{CommonLayout, TextLayout}
import generator.model.style.HasStyle

import scala.collection.mutable
import scala.collection.mutable.{HashMap, ListBuffer}

/**
  * Created by julian on 19.01.16.
  * shape and inlinestyle generator kindof useless since inlinestyles are now automatically merged with the actual styles....
  *
  */


object GeneratorShapeAndInlineStyle {

  def shapeStyleHead =
    """
        function getShapeStyle(elementName) {

        var style = {};

        switch(elementName) {
    """

  def generateShapeStyle(shape: Shape, packageName: String, LastElement: Boolean, attrs: HashMap[String, HashMap[GeometricModel, String]]): String = {

    if (shape != null && attrs.keys.exists(_ == shape.name)) {
      val att = attrs(shape.name)
      s"""
          case "${shape.name}":
          ${(for (s <- shape.shapes.get) yield generateStyles(s, att).mkString).mkString}
          break;

          """
    } else {
      ""
    }

  }

  def generateStyles(shape: GeometricModel, attrs: HashMap[GeometricModel, String]): mutable.ListBuffer[String] = {
    var ret = mutable.ListBuffer[String]()
    ret += getRightShape(shape, attrs(shape))
    shape match {
      case e: Ellipse =>
        for (child <- e.children) ret ++= generateStyles(child, attrs)
      case r: Rectangle =>
        for (child <- r.children) ret ++= generateStyles(child, attrs)
      case rr: RoundedRectangle =>
        for (child <- rr.children) ret ++= generateStyles(child, attrs)
      case p: Polygon =>
        for (child <- p.children) ret ++= generateStyles(child, attrs)
      case _ =>
    }
    ret
  }

  private def getRightShape(g: GeometricModel, shapeClass: String) = {
    g match {
      case l: Line => getShape(l, shapeClass)
      case rr: RoundedRectangle => getShape(rr, shapeClass)
      case e: Ellipse => getShape(e, shapeClass)
      case r: Rectangle => getShape(r, shapeClass)
      case t: Text => getShape(t, shapeClass)
      case pl: PolyLine => getShape(pl, shapeClass)
      case p: Polygon => getShape(p, shapeClass)
    }
  }


  protected def getShape(shape: Line, shapeClass: String) = {
    var ret =
      """
      """
    if (shape.style.isDefined) {
      ret += """style['line.""" + shapeClass +"""'] = getStyle('""" + shape.style.get.name +"""').line;"""
    }

    ret
  }

  protected def getShape(shape: Ellipse, shapeClass: String) = {
    var ret =
      """
      """
    if (shape.style.isDefined) {
      ret += """style['ellipse.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').ellipse;"""
    }
    ret
  }

  protected def getShape(shape: Rectangle, shapeClass: String) = {

    var ret =
      """
      """
    if (shape.style.isDefined) {
      ret += """style['rect.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').rect;"""
    }
    ret
  }

  protected def getShape(shape: RoundedRectangle, shapeClass: String) = {
    var ret =
      """
      """
    if (shape.style.isDefined) {
      ret += """style['rect.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').rect;"""
    }
    ret
  }


  protected def getShape(shape: Text, shapeClass: String) = {
    var ret =
      """
      """
    if (shape.style.isDefined) {
      ret += """style['text.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').text;"""
    }
    ret
  }

  protected def getShape(shape: PolyLine, shapeClass: String) = {
    var ret =
      """
      """
    if (shape.style.isDefined) {
      ret += """style['polyline.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').polyline;"""
    }
    ret
  }

  protected def getShape(shape: Polygon, shapeClass: String) = {
    var ret =
      """
      """
    if (shape.style.isDefined) {
      ret += """style['polygon.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').polygon;"""
    }
    ret
  }

  def shapeStyleFooter =
    """
        default:
          style = {};
        break;

      }
      return style;
    }

    """


  /**
    * getInlineStyle is deprecated, since the style attributes are now implicitly mixed in the surrounding style instance*/
  @deprecated
  protected def getInlineStyle(shape: GeometricModel) = {
    shape match {
      case hs: HasStyle if hs.style isDefined => s"""
        ${StyleGenerator.commonAttributes(hs.style.get)}
        ${if (shape.isInstanceOf[TextLayout]) StyleGenerator.fontAttributes(hs.style.get)}
        """
      case _ => ""
    }
  }

  protected def getRefXValue(alignment: HAlign) = {
    alignment match {
      case LEFT => 0.0
      case RIGHT => 1.0
      case CENTER => 0.5
      case _ => 0.0
    }
  }
}
