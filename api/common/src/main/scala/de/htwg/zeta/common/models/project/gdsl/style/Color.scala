package de.htwg.zeta.common.models.project.gdsl.style

case class Color(
    r: Int,
    g: Int,
    b: Int
)

object Color {
  val defaultColor: Color = Color(0, 0, 0)

  def apply(color: javafx.scene.paint.Color): Color = new Color(
    (color.getRed * 255.0).round.toInt,
    (color.getGreen * 255.0).round.toInt,
    (color.getBlue * 255.0).round.toInt
  )
}
