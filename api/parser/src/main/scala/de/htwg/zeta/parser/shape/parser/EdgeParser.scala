package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.Collector
import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.UnorderedParser
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Offset
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Placing
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree

object EdgeParser extends CommonParserMethods with UniteParsers with UnorderedParser {

  def edge: Parser[EdgeParseTree] = {
    val attributes = unordered(once(target), min(1, placing))
    ("edge" ~> ident) ~ ("for" ~> conceptConnection) ~ (leftBrace ~> attributes <~ rightBrace) ^^ { parseResult =>
      val edge ~ conceptConnection ~ attributes = parseResult
      val attrs = Collector(attributes)
      EdgeParseTree(edge,
        conceptConnection,
        attrs.![Target],
        attrs.*[Placing])
    }
  }

  private def target: Parser[Target] = {
    "target" ~! colon ~ ident ^^ {
      case _ ~ _ ~ name => Target(name)
    }
  }.named("target")

  private def placing: Parser[Placing] = {
    val attributes = unordered(optional(style), once(geoModel), once(offset))
    "placing" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      val attrs = Collector(parseResult)
      Placing(
        attrs.?[Style],
        attrs.![Offset],
        attrs.![GeoModelParseTree]
      )
    }
  }.named("placing")

  private def offset: Parser[Offset] = {
    literal("offset") ~! colon ~ argumentDouble ^^ {
      case _ ~ _ ~ offset => Offset(offset)
    }
  }.named("offset")

  private def style = include(GeoModelAttributeParser.style).named("style")

  private def geoModel = include(GeoModelParser.geoModel).named("geoModel")

  // Concept connections can be addressed with java / scala object syntax
  // e.g.: Knoten.hatKind
  private def conceptConnection = regex("\\w+.\\w+".r)

}