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
class NodeAttributes(val nodeType: String, val attributeTypes: Seq[String]) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType are only allowed to have attributes of types ${attributeTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove all attributes that are not of types ${attributeTypes.mkString("{", ", ", "}")} from nodes of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributes.keys.foldLeft(true) { (acc, attributeName) =>
    if (attributeTypes.contains(attributeName)) acc else false
  }

  override val dslStatement: String = s"""Attributes inNodes "$nodeType" areOfTypes ${Util.stringSeqToSeqString(attributeTypes)}"""
}

object NodeAttributes extends GeneratorRule {

  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      if (currentClass.attributes.isEmpty) {
        acc
      } else {
        acc :+ new NodeAttributes(currentClass.name, currentClass.attributes.map(_.name))
      }
    }

}