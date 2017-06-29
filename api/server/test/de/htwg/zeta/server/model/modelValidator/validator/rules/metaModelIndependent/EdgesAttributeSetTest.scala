package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesAttributeSetTest extends FlatSpec with Matchers {

  val rule = new EdgesAttributeSet
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq.empty)


  "isValid" should "return true on valid edges attribute sets" in {

    val attributes: Map[String, Seq[AttributeValue]] = Map(
      "attributeName1" -> Seq(),
      "attributeName2" -> Seq()
    )
    val edge = Edge("", mReference, Seq(), Seq(), attributes)
    rule.isValid(edge).get should be(true)
  }


  it should "return false on invalid edges attribute sets" in {
    val attributes: Map[String, Seq[AttributeValue]] = Map(
      "duplicateAttributeName" -> Seq(),
      "duplicateAttributeName" -> Seq()
    )
    val edge = Edge("", mReference, Seq(), Seq(), attributes)
    rule.isValid(edge).get should be(false)
  }

}
