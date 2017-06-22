package de.htwg.zeta.common.models.modelDefinitions.model.elements

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import play.api.libs.json.Format
import play.api.libs.json.Json

/** Represents an MClass type instance.
 *
 * @param name       the name of the node
 * @param clazz      the MClass instance that represents the node's type
 * @param outputs    the outgoing edges
 * @param inputs     the incoming edges
 * @param attributes a map with attribute names and the assigned values
 */
case class Node(
    name: String,
    clazz: MClass,
    outputs: Set[ToEdges],
    inputs: Set[ToEdges],
    attributes: Map[String, Set[AttributeValue]]
) extends ModelElement with HasAttributes

object Node {

  implicit val playJsonNodeFormat: Format[Node] = Json.format[Node]

}