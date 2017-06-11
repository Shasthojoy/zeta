package models.entity

import java.util.UUID


case class Filter(
    id: UUID = UUID.randomUUID,
    name: String,
    description: String,
    instanceIds: List[UUID]
) extends Entity
