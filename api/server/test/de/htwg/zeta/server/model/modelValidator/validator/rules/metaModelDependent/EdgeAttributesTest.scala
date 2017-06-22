package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "reference",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty,
    Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgeAttributes("reference", Seq("stringAttribute", "boolAttribute"))

  "the rule" should "be true for valid edge" in {
    val attributes: Map[String, Set[AttributeValue]] = Map(
      "stringAttribute" -> Set(MString("test")),
      "boolAttribute" -> Set(MBool(true))
    )
    val edge = Edge("edgeId", mReference, Set(), Set(), attributes)

    rule.isValid(edge).get should be(true)
  }

  it should "be false for invalid edges" in {
    val attributes: Map[String, Set[AttributeValue]] = Map(
      "stringAttribute" -> Set(MString("test")),
      "boolAttribute" -> Set(MBool(true)),
      "invalidAttribute" -> Set(MDouble(1.0))
    )

    val edge = Edge("edgeId", mReference, Set(), Set(), attributes)

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {

    val nonMatchingReference = MReference(
      "nonMatchingReference",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute]()
    )

    val attributes: Map[String, Set[AttributeValue]] = Map(
      "stringAttribute" -> Set(MString("test")),
      "boolAttribute" -> Set(MBool(true))
    )

    val edge = Edge("edgeId", nonMatchingReference, Set(), Set(), attributes)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes inEdges "reference" areOfTypes Set("stringAttribute", "boolAttribute")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, StringType, MString(""), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, -1, 0)
    val attribute2 = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, MInt(0), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, -1, 0)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set[MAttribute]
      (attribute, attribute2))
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgeAttributes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributes =>
        rule.edgeType should be("reference")
        rule.attributeTypes should be(Set("attributeName", "attributeName2"))
      case _ => fail
    }
  }

}