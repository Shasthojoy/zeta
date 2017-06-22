package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeInputsUpperBound(val nodeType: String, val inputType: String, val upperBound: Int) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must have a maximum of $upperBound input edges of type $inputType."
  override val possibleFix: String = s"Remove input edges of type $inputType from nodes of type $nodeType until there are a maximum of $upperBound input edges."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = if (upperBound == -1) true else node.inputs.find(_.reference.name == inputType) match {
    case Some(input) => input.edgeNames.size <= upperBound
    case None => true
  }

  override val dslStatement: String = s"""Inputs ofNodes "$nodeType" toEdges "$inputType" haveUpperBound $upperBound"""
}

object NodeInputsUpperBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritInputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.inputs.map(input => new NodeInputsUpperBound(currentClass.name, input.name, input.upperBound))
    }
}