package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new NodesAttributesNamesNotEmpty
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)

  "isValid" should "return true on non-empty attribute names" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeName1" -> Seq())
    val node = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), attribute)
    rule.isValid(node).get should be(true)
  }

  it should "return false on empty attribute names" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("" -> Seq())
    val node = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), attribute)
    rule.isValid(node).get should be(false)
  }

}
