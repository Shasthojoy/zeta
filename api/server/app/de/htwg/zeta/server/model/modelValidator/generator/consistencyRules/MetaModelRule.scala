package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel

trait MetaModelRule {

  val name: String
  val description: String

  def check(metaModel: MetaModel): Boolean

  override def toString: String = s"$name: $description"

}