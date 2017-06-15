package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeTargetsUpperBoundTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty,
    Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgeTargetsUpperBound("edgeType", "targetType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or less target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val twoTargetNodes = ToNodes(clazz = targetType, nodeNames = Set("1", "2"))

    val edgeTwoTargetNodes = Edge("", mReference, Set(), Set(twoTargetNodes), Map.empty)

    rule.isValid(edgeTwoTargetNodes).get should be(true)


    val oneTargetNode = ToNodes(clazz = targetType, nodeNames = Set("1"))

    val edgeOneTargetNode = Edge("", mReference, Set(), Set(oneTargetNode), Map.empty)

    rule.isValid(edgeOneTargetNode).get should be(true)


    val edgeNoTargetNodes = Edge("", mReference, Set(), Set(), Map.empty)

    rule.isValid(edgeNoTargetNodes).get should be(true)
  }

  it should "return false on edges of type edgeType having more than 2 target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val threeTargetNodes = ToNodes(clazz = targetType, nodeNames = Set("1", "2", "2"))

    val edgeThreeTargetNodes = Edge("", mReference, Set(), Set(threeTargetNodes), Map.empty)

    rule.isValid(edgeThreeTargetNodes).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute]()
    )
    val edge = Edge("", differentReference, Set(), Set(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Targets ofEdges "edgeType" toNodes "targetType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val targetLinkDef = MClassLinkDef(class1.name, 7, 0, deleteIfLower = false)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set(targetLinkDef),
      Set[MAttribute]())
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgeTargetsUpperBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeTargetsUpperBound =>
        rule.edgeType should be("reference")
        rule.targetType should be("class")
        rule.upperBound should be(7)
      case _ => fail
    }
  }
}
