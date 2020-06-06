package org.bruchez.olivier.healthanalysis

import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}
import java.nio.file.{Path, Paths}

case class SheetsConfig(credentialsFile: Path, tokensDirectory: Path)

object SheetsConfig {
  def apply(config: TypesafeConfig): SheetsConfig =
    SheetsConfig(
      credentialsFile = Paths.get(config.getString("credentialsFile")),
      tokensDirectory = Paths.get(config.getString("tokensDirectory"))
    )
}
case class Config(sheetsConfig: SheetsConfig)
object Config {
  def apply(): Config = {
    val config = ConfigFactory.load()
    Config(sheetsConfig = SheetsConfig(config.getConfig("sheets")))
  }
}
