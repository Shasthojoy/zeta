package generator.generators.vr.shape


import generator.model.diagram.node.Node
import generator.parser.Cache
import models.file.File


/**
 * The ShapeGenerator Object
 */
object VrShapeGenerator {

  def doGenerateFile(cache: Cache, nodes: List[Node]): List[File] = {
    val shapes = cache.shapeHierarchy.nodeView.values.map(s => s.data).toList

    val shapeDefinition = VrGeneratorShapeDefinition.doGenerateFile(shapes)
    val connectionDefinition = VrGeneratorConnectionDefinition.doGenerateFile(cache.connections.values)

    shapeDefinition ::: connectionDefinition
  }


}
