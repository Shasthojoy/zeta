package de.htwg.zeta.common.models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.format.metaModel.AttributeFormat
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.HasAttributeValues
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.MethodMap
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes


/** Represents an MReference type instance.
 *
 * @param name            the name of this edge
 * @param referenceName   the name of the MReference instance that represents the edge's type
 * @param sourceNodeName          the name of the node that are the origin of relationships
 * @param targetNodeName          the name of the node that can be reached
 * @param attributeValues a map with attribute names and the assigned values
 */
case class Edge(
    name: String,
    referenceName: String,
    sourceNodeName: String,
    targetNodeName: String,
    attributes: Seq[MAttribute],
    attributeValues: Map[String, AttributeValue],
    methods: Seq[Method]
) extends ModelElement with HasAttributeValues with AttributeMap with MethodMap

object Edge {

  def empty(name: String, referenceName: String, source: String, target: String): Edge =
    Edge(name, referenceName, source, target, Seq.empty, Map.empty, Seq.empty)

  trait EdgeMap {

    val edges: Seq[Edge]

    /** Edges mapped to their own names. */
    final val edgeMap: Map[String, Edge] = Option(edges).fold(
      Map.empty[String, Edge]
    ) { edges =>
      edges.filter(Option(_).isDefined).map(edge => (edge.name, edge)).toMap
    }

  }

}
