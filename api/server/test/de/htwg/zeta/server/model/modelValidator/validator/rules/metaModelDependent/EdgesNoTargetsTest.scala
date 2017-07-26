package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesNoTargetsTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )
  val rule = new EdgesNoTargets("edgeType")

  "check" should "return true on edges of type edgeType with no targets" in {
    val edge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), Map.empty)
    rule.isValid(edge).get should be(true)
  }

  it should "return false on edges of type edgeType with targets" in {
    val target = MClass(
      name = "",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )
    val toNode = ToNodes(className = target.name, nodeIds = Seq(UUID.randomUUID()))
    val edge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(toNode), Map.empty)

    rule.isValid(edge).get should be(false)
  }

  it should "return true on edges of type edgeType with empty target list" in {
    val target = MClass(
      name = "",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )
    val toNode = ToNodes(className = target.name, nodeIds = Seq())
    val edge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(toNode), Map.empty)

    rule.isValid(edge).get should be(true)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differenteEdgeType",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = Edge(UUID.randomUUID(), differentReference.name, Seq(), Seq(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Edges ofType "edgeType" haveNoTargets ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty,
      Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgesNoTargets.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgesNoTargets =>
        rule.edgeType should be ("reference")
      case _ => fail
    }
  }

}
