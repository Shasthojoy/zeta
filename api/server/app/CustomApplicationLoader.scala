
import java.io.File


import de.htwg.zeta.common.cluster.ClusterManager
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import com.typesafe.config.ConfigParseOptions
import play.api.ApplicationLoader
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.guice.GuiceApplicationLoader
import scala.collection.convert.WrapAsScala

/**
 * Entrypoint of application
 */
class CustomApplicationLoader extends GuiceApplicationLoader() {


  private def parseConf(baseName: String, classLoader: ClassLoader): Config = {
    val conf = if (baseName.endsWith(".conf")) baseName else baseName + ".conf"

    val configParserOpts = ConfigParseOptions.defaults().setClassLoader(classLoader)
    val file: File = new java.io.File(classLoader.getResource(conf).toURI)
    ConfigFactory.parseFile(file, configParserOpts)
  }

  /**
   * Initiate configuration for builder
   *
   * @param context Application Context instance
   * @return Instance of a builder for an guice application
   */
  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val classLoader: ClassLoader = context.environment.classLoader

    val parsed = parseConf("production", classLoader)

    val parsedWithInit = parsed.withFallback(context.initialConfiguration.underlying)
    val clusterConfig = loadConfig(parsedWithInit.resolve())
    val mergedConfig = clusterConfig.withFallback(parsedWithInit).resolve()

    initialBuilder
      .in(context.environment)
      .loadConfig(Configuration(mergedConfig))
      .overrides(overrides(context): _*)
  }

  /**
   * This method given a class loader will return the configuration object for an ActorSystem
   * in a clustered environment
   *
   * @param initialConfig the initialConfig
   * @return Config
   */
  private def loadConfig(initialConfig: Config): Config = {

    val seeds: List[String] = Option(initialConfig.getStringList("zeta.actor.cluster")) match {
      case None => Nil
      case Some(javaList) => WrapAsScala.iterableAsScalaIterable(javaList).toList
    }
    val roles = List("api")
    val clusterConfig = ClusterManager.getClusterJoinConfig(roles, seeds, 0)

    clusterConfig
  }
}
