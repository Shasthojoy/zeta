package de.htwg.zeta.server.controller.codeEditor

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorRef
import akka.actor.Props
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedEntityPersistence
import de.htwg.zeta.server.model.codeEditor.CodeDocManagerContainer
import de.htwg.zeta.server.model.codeEditor.CodeDocWsActor
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result


class CodeEditorController @Inject()(
    codeDocManager: CodeDocManagerContainer,
    metaModelEntityPersistence: AccessRestrictedEntityPersistence[MetaModelEntity]
) extends Controller {

  def codeEditor(metaModelId: UUID, dslType: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.metamodel.MetaModelCodeEditor(Some(request.identity), metaModelId, dslType))
  }

  def codeSocket(metaModelId: UUID, dslType: String)(securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): Props = {
    CodeDocWsActor.props(out, codeDocManager.manager, metaModelId, dslType)
  }

  def methodClassCodeEditor(metaModelId: UUID, methodName: String, className: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityPersistence.restrictedTo(request.identity.id).read(metaModelId).map { metaModelEntity =>
      val code = metaModelEntity.metaModel.classMap(className).methodMap(methodName).code
      Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity, metaModelId, methodName, "class", code, className))
    }
  }

  def methodReferenceCodeEditor(metaModelId: UUID, methodName: String, referenceName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityPersistence.restrictedTo(request.identity.id).read(metaModelId).map { metaModelEntity =>
      val code = metaModelEntity.metaModel.referenceMap(referenceName).methodMap(methodName).code
      Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity, metaModelId, methodName, "reference", code, referenceName))
    }
  }

  def methodMainCodeEditor(metaModelId: UUID, methodName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityPersistence.restrictedTo(request.identity.id).read(metaModelId).map { metaModelEntity =>
      val code = metaModelEntity.metaModel.methodMap(methodName).code
      Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity, metaModelId, methodName, "common", code, ""))
    }
  }

}
