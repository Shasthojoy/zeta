package de.htwg.zeta.persistence.microService

import de.htwg.zeta.persistence.service.CachePersistenceService

/** Start the Persistence-Server at localhost:8080 with a CachePersistence. */
object StartServer extends App {

  private val port = 8080

  PersistenceServer.start("localhost", port, new CachePersistenceService)

}
