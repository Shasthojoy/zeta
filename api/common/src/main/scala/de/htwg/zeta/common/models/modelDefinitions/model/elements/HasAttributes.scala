package de.htwg.zeta.common.models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue

/** A mixin that offers the attributes field */
trait HasAttributes {
  val attributes: Map[String, Seq[AttributeValue]]
}
