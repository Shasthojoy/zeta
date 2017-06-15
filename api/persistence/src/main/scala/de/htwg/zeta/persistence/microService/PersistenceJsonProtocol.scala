package de.htwg.zeta.persistence.microService

import java.time.Instant
import java.util.UUID

import de.htwg.zeta.common.models.document.DockerSettings
import de.htwg.zeta.common.models.document.JobSettings
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MObject
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import spray.json.DefaultJsonProtocol
import spray.json.JsArray
import spray.json.JsBoolean
import spray.json.JsNumber
import spray.json.JsObject
import spray.json.JsString
import spray.json.JsValue
import spray.json.RootJsonFormat
import spray.json.pimpAny

/**
 * Provides implicit conversion for using the Spray-Json library.
 */
object PersistenceJsonProtocol extends DefaultJsonProtocol with App {

  private val sType = "type"
  private val sValue = "value"
  private val sString = "string"
  private val sBool = "bool"
  private val sInt = "int"
  private val sDouble = "double"

  private val sMType = "mType"
  private val sMClass = "mClass"
  private val sMReference = "mReference"
  private val sUpperBound = "upperBound"
  private val sLowerBound = "lowerBound"
  private val sDeleteIfLower = "deleteIfLower"

  private val sName = "name"
  private val sAbstractness = "abstractness"
  private val sSuperTypeNames = "superTypeNames"
  private val sInputs = "inputs"
  private val sOutputs = "outputs"
  private val sAttributes = "attributes"

  private val sSourceDeletionDeletesTarget = "sourceDeletionDeletesTarget"
  private val sTargetDeletionDeletesSource = "targetDeletionDeletesSource"
  private val sSource = "source"
  private val sTarget = "target"

  private val sValues = "values"

  private val sMObject = "mObject"
  private val sMAttribute = "mAttribute"
  private val sMEnum = "mEnum"

  /** Spray-Json conversion protocol for UUID */
  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {

    /** Write a UUID.
     *
     * @param x UUID
     * @return JsValue
     */
    def write(x: UUID): JsValue = {
      JsString(x.toString)
    }

    /** Read a UUID.
     *
     * @param value JsValue
     * @return UUID
     */
    def read(value: JsValue): UUID = {
      value match { // TODO pattern match not needed for single value
        case JsString(x) => UUID.fromString(x)
      }
    }

  }

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType]] */
  private implicit object AttributeTypeFormat extends RootJsonFormat[AttributeType] {

    /** Write a AttributeType.
     *
     * @param attrType AttributeType
     * @return JsValue
     */
    def write(attrType: AttributeType): JsString = {
      attrType match {
        case StringType => JsString(sString)
        case BoolType => JsString(sBool)
        case IntType => JsString(sInt)
        case DoubleType => JsString(sDouble)
      }
    }

    /** Read a AttributeType.
     *
     * @param value JsValue
     * @return AttributeType
     */
    def read(value: JsValue): AttributeType = {
      value match {
        case JsString(`sString`) => StringType
        case JsString(`sBool`) => BoolType
        case JsString(`sInt`) => IntType
        case JsString(`sDouble`) => DoubleType
      }
    }

  }

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue]] */
  private implicit object AttributeValueFormat extends RootJsonFormat[AttributeValue] {

    /** Write a AttributeValue.
     *
     * @param attrValue AttributeValue
     * @return JsValue
     */
    def write(attrValue: AttributeValue): JsObject = {
      attrValue match {
        case MString(v) => JsObject(sType -> JsString(sString), sValue -> JsString(v))
        case MBool(v) => JsObject(sType -> JsString(sBool), sValue -> JsBoolean(v))
        case MInt(v) => JsObject(sType -> JsString(sInt), sValue -> JsNumber(v))
        case MDouble(v) => JsObject(sType -> JsString(sDouble), sValue -> JsNumber(v))
      }
    }

    /** Read a AttributeValue.
     *
     * @param value JsValue
     * @return AttributeValue
     */
    def read(value: JsValue): AttributeValue = {
      value.asJsObject.getFields(sType, sValue) match {
        case Seq(JsString(`sString`), JsString(v)) => MString(v)
        case Seq(JsString(`sBool`), JsBoolean(v)) => MBool(v)
        case Seq(JsString(`sInt`), JsNumber(v)) => MInt(v.toInt)
        case Seq(JsString(`sDouble`), JsNumber(v)) => MDouble(v.toDouble)
      }
    }

  }

  private implicit val mAttributeFormat: RootJsonFormat[MAttribute] = jsonFormat12(MAttribute.apply)



  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass]] */
  private implicit object MClassFormat extends RootJsonFormat[MClass] {

    /** Write a MClass.
     *
     * @param mClass MClass
     * @return JsObject
     */
    def write(mClass: MClass): JsObject = {
      JsObject(
        sName -> JsString(mClass.name),
        sAbstractness -> JsBoolean(mClass.abstractness),
        sSuperTypeNames -> mClass.superTypeNames.toJson,
        sInputs -> null, // TODO mClass.inputs.toJson,
        sOutputs -> null, // mClass.outputs.toJson,
        sAttributes -> mClass.attributes.toJson
      )
    }

    /** Read a MClass.
     *
     * @param value JsValue
     * @return MClass
     */
    def read(value: JsValue): MClass = {
      value.asJsObject.getFields(sName, sAbstractness, sSuperTypeNames, sInputs, sOutputs, sAttributes) match {
        case Seq(JsString(name), JsBoolean(abstractness), superTypes, inputs, outputs, attributes) =>
          MClass(
            name,
            abstractness,
            superTypes.convertTo[List[String]],
            null, // TODO inputs.convertTo[List[MLinkDef]],
            null, // TODO outputs.convertTo[List[MLinkDef]],
            attributes.convertTo[List[MAttribute]]
          )
      }
    }

  }


  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference]] */
  private implicit object MReferenceFormat extends RootJsonFormat[MReference] {

    /** Write a MReference.
     *
     * @param mReference MReference
     * @return JsObject
     */
    def write(mReference: MReference): JsObject = {
      JsObject(
        sName -> JsString(mReference.name),
        sSourceDeletionDeletesTarget -> JsBoolean(mReference.sourceDeletionDeletesTarget),
        sTargetDeletionDeletesSource -> JsBoolean(mReference.targetDeletionDeletesSource),
        sSource -> null, // TODO mReference.source.toJson,
        sTarget -> null, // TODO mReference.target.toJson,
        sAttributes -> mReference.attributes.toJson
      )
    }

    /** Read a MReference.
     *
     * @param value JsValue
     * @return MReference
     */
    def read(value: JsValue): MReference = {
      value.asJsObject.getFields(sName, sSourceDeletionDeletesTarget, sTargetDeletionDeletesSource, sSource, sTarget, sAttributes) match {
        case Seq(JsString(name), JsBoolean(sourceDeletionDeletesTarget), JsBoolean(targetDeletionDeletesSource), source, target, attributes) =>
          MReference(
            name,
            sourceDeletionDeletesTarget,
            targetDeletionDeletesSource,
            null, // TODO source.convertTo[List[MLinkDef]],
            null, // TODO target.convertTo[List[MLinkDef]],
            attributes.convertTo[List[MAttribute]]
          )
      }
    }

  }

  private def forwardRefMClassToJson(mClass: MClass): JsValue = {
    mClass.toJson
  }

  private def forwardRefMReferenceToJson(mReference: MReference): JsValue = {
    mReference.toJson
  }

  private def forwardRefMClassConvertTo(mClass: JsValue): MClass = {
    mClass.convertTo[MClass]
  }

  private def forwardRefMReferenceConvertTo(mReference: JsValue): MClass = {
    mReference.convertTo[MClass]
  }


  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum]] */
  private implicit object MEnumFormat extends RootJsonFormat[MEnum] {

    /** Write a MEnum.
     *
     * @param mEnum MEnum
     * @return JsObject
     */
    def write(mEnum: MEnum): JsObject = {
      JsObject(
        sName -> JsString(mEnum.name),
        sValues -> JsArray(mEnum.symbols.map(value => JsString(value.name)).toVector)
      )
    }

    /** Read a MEnum.
     *
     * @param value JsValue
     * @return MEnum
     */
    def read(value: JsValue): MEnum = {
      value.asJsObject.getFields(sName, sValues) match {
        case Seq(JsString(name), JsArray(values)) =>
          val enum = MEnum(name, null) // scalastyle:ignore
          MEnum(
            name,
            values.map {
              case JsString(v) => null // TODO EnumSymbol(v, enum)
            }
          )
      }
    }

  }


  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum]] */
  private implicit object MObjectFormat extends RootJsonFormat[MObject] {

    /** Write a MObject.
     *
     * @param mObject MObject
     * @return JsObject
     */
    def write(mObject: MObject): JsObject = {

      JsObject(
        sType -> JsString(
          mObject match {
            case _: MClass => sMClass
            case _: MReference => sMReference
            case _: MAttribute => sMAttribute
            case _: MEnum => sMEnum
          }
        ),
        sMObject -> mObject.toJson)
    }


    /** Read a MObject.
     *
     * @param value JsValue
     * @return MObject
     */
    def read(value: JsValue): MObject = {
      value.asJsObject.getFields(sName, sMObject) match {
        case Seq(JsString(`sMClass`), obj) => obj.convertTo[MClass]
        case Seq(JsString(`sMReference`), obj) => obj.convertTo[MReference]
        case Seq(JsString(`sMAttribute`), obj) => obj.convertTo[MAttribute]
        case Seq(JsString(`sMEnum`), obj) => obj.convertTo[MEnum]

      }
    }

  }


  /** Spray-Json conversion protocol for [[java.time.Instant]] */
  private implicit object InstantFormat extends RootJsonFormat[Instant] {

    /** Write a Instant.
     *
     * @param instant Instant
     * @return JsObject
     */
    def write(instant: Instant): JsString = {
      JsString(instant.toString)
    }

    /** Read a Instant.
     *
     * @param value JsValue
     * @return Instant
     */
    def read(value: JsValue): Instant = {
      Instant.parse(value.toString)
    }

  }



  // private implicit val metaModelFormat: RootJsonFormat[MetaModel] = jsonFormat3(MetaModel.apply)

  // private implicit val modelFormat: RootJsonFormat[Model] = jsonFormat4(Model.apply)


  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  implicit val eventDrivenTaskFormat: RootJsonFormat[EventDrivenTask] = jsonFormat5(EventDrivenTask.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.BondedTask]] */
  implicit val bondedTaskFormat: RootJsonFormat[BondedTask] = jsonFormat6(BondedTask.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.Generator]] */
  implicit val generatorFormat: RootJsonFormat[Generator] = jsonFormat3(Generator.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.Filter]] */
  implicit val filterFormat: RootJsonFormat[Filter] = jsonFormat4(Filter.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  implicit val generatorImageFormat: RootJsonFormat[GeneratorImage] = jsonFormat3(GeneratorImage.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.FilterImage]] */
  implicit val filterImageFormat: RootJsonFormat[FilterImage] = jsonFormat3(FilterImage.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.Settings]] */
  implicit val settingsFormat: RootJsonFormat[Settings] = {
    implicit val dockerSettingsFormat: RootJsonFormat[DockerSettings] = jsonFormat2(DockerSettings.apply)
    implicit val jobSettingsInfoFormat: RootJsonFormat[JobSettings] = jsonFormat3(JobSettings.apply)
    jsonFormat3(Settings.apply)
  }

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  // TODO: implicit val metaModelEntityFormat: RootJsonFormat[MetaModelEntity] = jsonFormat6(MetaModelEntity.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  // TODO: implicit val metaModelReleaseFormat: RootJsonFormat[MetaModelRelease] = jsonFormat6(MetaModelRelease.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  // implicit val modelEntityFormat: RootJsonFormat[ModelEntity] = jsonFormat7(ModelEntity.apply)

  /** Spray-Json conversion protocol for [[de.htwg.zeta.common.models.entity.Log]] */
  implicit val logFormat: RootJsonFormat[Log] = jsonFormat5(Log.apply)


}

