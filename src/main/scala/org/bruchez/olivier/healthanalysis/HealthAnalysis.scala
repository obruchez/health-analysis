package org.bruchez.olivier.healthanalysis

import com.google.api.services.sheets.v4.Sheets

import java.nio.file.Path
import scala.jdk.CollectionConverters._

/*
@todo
- import from health diary
- import from crosstrainer/push-ups/planks
- import from meditation
- import from something else?
- merge
- normalization of variables ([0-1]?)
- for supplements: variable "delay" to detect correlation between days? use a more complex model (e.g. machine learning)?
 */

object HealthAnalysis {
  def main(args: Array[String]): Unit = {
    val config = Config()

    val sheetsServiceBuilder: SheetsServiceBuilder = new SheetsServiceBuilder {
      override protected val credentialsFile: Path = config.sheetsConfig.credentialsFile
      override protected val tokensDirectory: Path = config.sheetsConfig.tokensDirectory
    }

    implicit val sheetsService: Sheets = sheetsServiceBuilder.sheetsService()

    //testWithGoogleSample()

    //println(s"config.healthJournalConfig = ${config.healthJournalConfig}")

    val healthJournal = HealthJournal(config.healthJournalConfig)

    val healthJournalVariables = healthJournal.healthVariables()

    println(s"healthJournalVariables = $healthJournalVariables")
  }

  def testWithGoogleSample()(implicit sheets: Sheets): Unit = {
    // Adapted from Google Java sample

    val spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
    val range = "Class Data!A2:E"

    val response = sheets.spreadsheets.values.get(spreadsheetId, range).execute

    Option(response.getValues) match {
      case None =>
        println("No data found.")

      case Some(values) =>
        println("Name, Major")

        for (row <- values.asScala) {
          // Print columns A and E, which correspond to indices 0 and 4.
          // scalastyle:off magic.number
          System.out.printf("%s, %s\n", row.get(0), row.get(4))
          // scalastyle:on magic.number
        }
    }
  }
}
