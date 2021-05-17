package org.bruchez.olivier.healthanalysis

import com.typesafe.config.{ConfigFactory, ConfigList, Config => TypesafeConfig}

import java.nio.file.{Path, Paths}
import scala.jdk.CollectionConverters._
import scala.util.Try

case class SheetsConfig(credentialsFile: Path, tokensDirectory: Path)

object SheetsConfig {
  def apply(config: TypesafeConfig): SheetsConfig =
    SheetsConfig(
      credentialsFile = Paths.get(config.getString("credentialsFile")),
      tokensDirectory = Paths.get(config.getString("tokensDirectory"))
    )
}

case class HealthJournalVariable(columnIndex: Int, variableName: String)

object HealthJournalVariable {
  def variables(config: TypesafeConfig): Seq[HealthJournalVariable] = {
    config.entrySet().asScala.toSeq map { entry =>
      HealthJournalVariable(entry.getKey.toInt, entry.getValue.unwrapped().asInstanceOf[String])
    }
  }
}

case class ColumnToSplit(index: Int,
                         toLowerCase: Boolean,
                         separator: String,
                         tokensToIgnore: Seq[String],
                         tokensToTranslate: Map[String, String])

object ColumnToSplit {
  def columnsToSplit(config: TypesafeConfig): Seq[ColumnToSplit] =
    for {
      columnString <- Config.keys(config)
      columnConfig = config.getConfig(columnString)
    } yield
      ColumnToSplit(
        index = columnString.toInt,
        toLowerCase = columnConfig.getBoolean("toLowerCase"),
        separator = columnConfig.getString("separator"),
        tokensToIgnore =
          Try(columnConfig.getAnyRefList("tokensToIgnore").asScala.toSeq.map(_.toString)).getOrElse(Seq()),
        tokensToTranslate =
          Try(columnConfig.getList("tokensToTranslate")).toOption.map(tokensToTranslate).getOrElse(Map())
      )

  private def tokensToTranslate(configList: ConfigList): Map[String, String] = {
    assert(configList.unwrapped().size() % 2 == 0,
           "Number of strings in 'tokensToIgnore' list must be even (key-value pairs)")
    configList.unwrapped().asScala.map(_.toString).grouped(2).map(group => group.head -> group(1)).toMap
  }
}

case class HealthJournalConfig(spreadsheetId: String,
                               sheet: String,
                               dateColumnIndex: Int,
                               variables: Seq[HealthJournalVariable],
                               columnsToSplit: Seq[ColumnToSplit])

object HealthJournalConfig {
  def apply(config: TypesafeConfig): HealthJournalConfig =
    HealthJournalConfig(
      spreadsheetId = config.getString("spreadsheetId"),
      sheet = config.getString("sheet"),
      dateColumnIndex = config.getInt("dateColumnIndex"),
      variables = HealthJournalVariable.variables(config.getConfig("variables")),
      columnsToSplit = ColumnToSplit.columnsToSplit(config.getConfig("columnsToSplit"))
    )
}

case class Config(sheetsConfig: SheetsConfig, healthJournalConfig: HealthJournalConfig)

object Config {
  def apply(): Config = {
    val config = ConfigFactory.load()
    Config(sheetsConfig = SheetsConfig(config.getConfig("sheets")),
           healthJournalConfig = HealthJournalConfig(config.getConfig("spreadsheets.healthJournal")))
  }

  def keys(config: TypesafeConfig): Seq[String] =
    config
      .entrySet()
      .asScala
      .toSeq
      .map { entry =>
        val path = entry.getKey
        val dotIndex = path.indexOf(".")
        if (dotIndex >= 0) path.substring(0, dotIndex) else path
      }
      .distinct
}
