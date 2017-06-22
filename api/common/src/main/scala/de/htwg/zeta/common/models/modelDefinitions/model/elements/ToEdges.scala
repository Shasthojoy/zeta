package de.htwg.zeta.common.models.modelDefinitions.model.elements

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.Format
import play.api.libs.json.Json

/** Represents outgoing edges of a node
 *
 * @param reference the MReference instance that represents the type of the references
 * @param edgeNames the names of the linked edges
 */
case class ToEdges(reference: MReference, edgeNames: Set[String]) extends Link

object ToEdges {

  implicit val playJsonToEdgesFormat: Format[ToEdges] = Json.format[ToEdges]

}