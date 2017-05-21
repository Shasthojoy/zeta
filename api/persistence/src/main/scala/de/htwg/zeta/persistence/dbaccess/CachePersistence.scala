package de.htwg.zeta.persistence.dbaccess

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

import models.document.Document

/** Cache implementation of [[Persistence]].
 *
 * @tparam T type of the document
 */
class CachePersistence[T <: Document] extends Persistence[T] { // scalastyle:ignore

  private val cache: TrieMap[String, T] = TrieMap.empty[String, T]

  /** Create a new document.
   *
   * @param doc the document to save
   * @return Future, which can fail
   */
  override def create(doc: T): Future[Unit] = {
    if (cache.putIfAbsent(doc.id(), doc).isEmpty) {
      Future.successful(Unit)
    } else {
      Future.failed(new IllegalArgumentException("cant't create the document, a document with same id already exists"))
    }
  }

  /** Get a single document.
   *
   * @param id The id of the entity
   * @return Future which resolve with the document and can fail
   */
  override def read(id: String): Future[T] = {
    val doc = cache.get(id)
    if (doc.isDefined) {
      Future.successful(doc.get)
    } else {
      Future.failed(new IllegalArgumentException("can't read the document, a document with the id doesn't exist"))
    }
  }

  /** Update a document.
   *
   * @param doc The document to update
   * @return Future, which can fail
   */
  override def update(doc: T): Future[Unit] = {
    if (cache.replace(doc.id(), doc).isDefined) {
      Future.successful(Unit)
    } else {
      Future.failed(new IllegalArgumentException("can't update the document, a document with the id doesn't exist"))
    }
  }

  /** Delete a document.
   *
   * @param id The id of the document to delete
   * @return Future, which can fail
   */
  override def delete(id: String): Future[Unit] = {
    if (cache.remove(id).isDefined) {
      Future.successful(Unit)
    } else {
      Future.failed(new IllegalArgumentException("can't delete the document, a document with the id doesn't exist"))
    }
  }

  /** Get the id's of all documents.
   *
   * @return Future containing all id's of the document type, can fail
   */
  override def readAllIds: Future[Seq[String]] = {
    Future.successful(cache.keys.toSeq)
  }
}
