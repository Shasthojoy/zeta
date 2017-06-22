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
class NodeOutputsUpperBound(val nodeType: String, val outputType: String, val upperBound: Int) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Nodes of type $nodeType must have a maximum of $upperBound output edges of type $outputType."
  override val possibleFix: String =
    s"Remove output edges of type $outputType from nodes of type $nodeType until there are a maximum of $upperBound output edges."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = if (upperBound == -1) true else node.outputs.find(_.reference.name == outputType) match {
    case Some(output) => output.edgeNames.size <= upperBound
    case None => true
  }

  override val dslStatement: String = s"""Outputs ofNodes "$nodeType" toEdges "$outputType" haveUpperBound $upperBound"""
}

object NodeOutputsUpperBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritOutputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.outputs.map(output => new NodeOutputsUpperBound(currentClass.name, output.name, output.upperBound))
    }
}