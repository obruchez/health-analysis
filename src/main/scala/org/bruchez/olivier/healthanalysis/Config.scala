package org.bruchez.olivier.healthanalysis

import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}
import java.nio.file.{Path, Paths}
import scala.jdk.CollectionConverters._

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

case class HealthJournalConfig(spreadsheetId: String,
                               sheet: String,
                               dateColumnIndex: Int,
                               variables: Seq[HealthJournalVariable])

object HealthJournalConfig {
  def apply(config: TypesafeConfig): HealthJournalConfig =
    HealthJournalConfig(
      spreadsheetId = config.getString("spreadsheetId"),
      sheet = config.getString("sheet"),
      dateColumnIndex = config.getInt("dateColumnIndex"),
      variables = HealthJournalVariable.variables(config.getConfig("variables"))
    )
}

case class Config(sheetsConfig: SheetsConfig, healthJournalConfig: HealthJournalConfig)

object Config {
  def apply(): Config = {
    val config = ConfigFactory.load()
    Config(sheetsConfig = SheetsConfig(config.getConfig("sheets")),
           healthJournalConfig = HealthJournalConfig(config.getConfig("spreadsheets.healthJournal")))
  }
}
