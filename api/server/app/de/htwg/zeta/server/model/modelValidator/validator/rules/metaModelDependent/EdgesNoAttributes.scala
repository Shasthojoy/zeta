package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgesNoAttributes(val edgeType: String) extends SingleEdgeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must not have attributes."
  override val possibleFix: String = s"Remote all attributes from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.reference.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.attributes.values.flatten.isEmpty

  override val dslStatement: String = s"""Edges ofType "$edgeType" haveNoAttributes ()"""
}

object EdgesNoAttributes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.referenceMap.values
    .filter(_.attributes.isEmpty)
    .map(ref => new EdgesNoAttributes(ref.name)).toSeq
}