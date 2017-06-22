package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ModelElement

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class ElementsIdsUnique extends ElementsRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Element Identifiers must be unique."
  override val possibleFix: String = "Make duplicate identifiers unique."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = elements.groupBy(_.name).values
    .foldLeft(Seq[ModelValidationResult]()) { (acc, elements) =>
      acc ++ elements.map(el => ModelValidationResult(rule = this, valid = elements.size == 1, modelElement = Some(el)))
    }
}