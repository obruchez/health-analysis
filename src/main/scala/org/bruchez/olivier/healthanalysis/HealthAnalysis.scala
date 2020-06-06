package org.bruchez.olivier.healthanalysis

import java.nio.file.Path
import scala.jdk.CollectionConverters._

object HealthAnalysis {
  def main(args: Array[String]): Unit = {
    val config = Config()

    val sheetsServiceBuilder: SheetsServiceBuilder = new SheetsServiceBuilder {
      override protected val credentialsFile: Path = config.sheetsConfig.credentialsFile
      override protected val tokensDirectory: Path = config.sheetsConfig.tokensDirectory
    }

    val sheetsService = sheetsServiceBuilder.sheetsService()

    val spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
    val range = "Class Data!A2:E"
    val response = sheetsService.spreadsheets.values.get(spreadsheetId, range).execute

    Option(response.getValues) match {
      case None =>
        println("No data found.")

      case Some(values) =>
        println("Name, Major")

        for (row <- values.asScala) { // Print columns A and E, which correspond to indices 0 and 4.
          System.out.printf("%s, %s\n", row.get(0), row.get(4))
        }
    }
  }
}
