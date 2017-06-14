package de.htwg.zeta.persistence.behavior

import scala.concurrent.Future

import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.fixtures.AccessAuthorisationFixtures
import de.htwg.zeta.persistence.fixtures.BondedTaskFixtures
import de.htwg.zeta.persistence.fixtures.EventDrivenTaskFixtures
import de.htwg.zeta.persistence.fixtures.FilterImageTestFixtures
import de.htwg.zeta.persistence.fixtures.FilterTestFixtures
import de.htwg.zeta.persistence.fixtures.GeneratorFixtures
import de.htwg.zeta.persistence.fixtures.GeneratorImageFixtures
import de.htwg.zeta.persistence.fixtures.LogFixtures
import de.htwg.zeta.persistence.fixtures.SettingsFixtures
import de.htwg.zeta.persistence.fixtures.TimedTaskFixtures
import de.htwg.zeta.persistence.fixtures.UserFixtures
import de.htwg.zeta.persistence.general.Repository
import models.entity.AccessAuthorisation
import models.entity.BondedTask
import models.entity.EventDrivenTask
import models.entity.Filter
import models.entity.FilterImage
import models.entity.Generator
import models.entity.GeneratorImage
import models.entity.Log
import models.entity.Settings
import models.entity.TimedTask
import models.entity.User


/** PersistenceBehavior. */
trait RepositoryBehavior extends EntityPersistenceBehavior with FilePersistenceBehavior
  with LoginInfoPersistenceBehavior with PasswordInfoPersistenceBehavior {

  def repositoryBehavior(repository: Repository, restricted: Boolean): Unit = { // scalastyle:ignore

    if (restricted) {
      "AccessAuthorisation(restricted)" should "throw an UnsupportedOperationException" in {
        recoverToSucceededIf[UnsupportedOperationException] {
          Future(repository.accessAuthorisation)
        }
      }
    } else {
      "AccessAuthorisation" should behave like entityPersistenceBehavior[AccessAuthorisation](
        repository.accessAuthorisation,
        AccessAuthorisationFixtures.entity1,
        AccessAuthorisationFixtures.entity2,
        AccessAuthorisationFixtures.entity2Updated,
        AccessAuthorisationFixtures.entity3
      )
    }


    "BondedTask" should behave like entityPersistenceBehavior[BondedTask](
      repository.bondedTask,
      BondedTaskFixtures.entity1,
      BondedTaskFixtures.entity2,
      BondedTaskFixtures.entity2Updated,
      BondedTaskFixtures.entity3
    )

    "EventDrivenTask" should behave like entityPersistenceBehavior[EventDrivenTask](
      repository.eventDrivenTask,
      EventDrivenTaskFixtures.entity1,
      EventDrivenTaskFixtures.entity2,
      EventDrivenTaskFixtures.entity2Updated,
      EventDrivenTaskFixtures.entity3
    )

    "Filter" should behave like entityPersistenceBehavior[Filter](
      repository.filter,
      FilterTestFixtures.entity1,
      FilterTestFixtures.entity2,
      FilterTestFixtures.entity2Updated,
      FilterTestFixtures.entity3
    )

    "FilterImage" should behave like entityPersistenceBehavior[FilterImage](
      repository.filterImage,
      FilterImageTestFixtures.entity1,
      FilterImageTestFixtures.entity2,
      FilterImageTestFixtures.entity2Updated,
      FilterImageTestFixtures.entity3
    )

    "Generator" should behave like entityPersistenceBehavior[Generator](
      repository.generator,
      GeneratorFixtures.entity1,
      GeneratorFixtures.entity2,
      GeneratorFixtures.entity2Updated,
      GeneratorFixtures.entity3
    )

    "GeneratorImage" should behave like entityPersistenceBehavior[GeneratorImage](
      repository.generatorImage,
      GeneratorImageFixtures.entity1,
      GeneratorImageFixtures.entity2,
      GeneratorImageFixtures.entity2Updated,
      GeneratorImageFixtures.entity3
    )

    "Log" should behave like entityPersistenceBehavior[Log](
      repository.log,
      LogFixtures.entity1,
      LogFixtures.entity2,
      LogFixtures.entity2Updated,
      LogFixtures.entity3
    )

    /* "MetaModelEntity" should behave like entityPersistenceBehavior[MetaModelEntity](
      repository.metaModelEntities,
      null,
      null, // TODO
      null,
      null
    ) */

    /* "MetaModelRelease" should behave like entityPersistenceBehavior[MetaModelRelease](
      repository.metaModelReleases,
      null,
      null, // TODO
      null,
      null
    ) */

    /* "ModelEntity" should behave like entityPersistenceBehavior[ModelEntity](
      repository.modelEntities,
      null,
      null, // TODO
      null,
      null
    ) */

    "Settings" should behave like entityPersistenceBehavior[Settings](
      repository.settings,
      SettingsFixtures.entity1,
      SettingsFixtures.entity2,
      SettingsFixtures.entity2Updated,
      SettingsFixtures.entity3
    )

    "TimedTask" should behave like entityPersistenceBehavior[TimedTask](
      repository.timedTask,
      TimedTaskFixtures.entity1,
      TimedTaskFixtures.entity2,
      TimedTaskFixtures.entity2Updated,
      TimedTaskFixtures.entity3
    )

    if (restricted) {
      "User(restricted)" should "throw an UnsupportedOperationException" in {
        recoverToSucceededIf[UnsupportedOperationException] {
          Future(repository.user)
        }
      }
    } else {
      "User" should behave like entityPersistenceBehavior[User](
        repository.user,
        UserFixtures.entity1,
        UserFixtures.entity2,
        UserFixtures.entity2Updated,
        UserFixtures.entity3
      )
    }

    "File" should behave like filePersistenceBehavior(repository.file)

    if (restricted) {
      "LoginInfo(restricted)" should "throw an UnsupportedOperationException" in {
        recoverToSucceededIf[UnsupportedOperationException] {
          Future(repository.loginInfo)
        }
      }
    } else {
      "LoginInfo" should behave like loginInfoPersistenceBehavior(repository.loginInfo)
    }

    if (restricted) {
      "PasswordInfo(restricted)" should "throw an UnsupportedOperationException" in {
        recoverToSucceededIf[UnsupportedOperationException] {
          Future(repository.passwordInfo)
        }
      }
    } else {
      "PasswordInfo" should behave like passwordInfoPersistenceBehavior(repository.passwordInfo)
    }
  }

}
