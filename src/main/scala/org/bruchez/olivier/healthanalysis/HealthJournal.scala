package org.bruchez.olivier.healthanalysis

import com.google.api.services.sheets.v4.Sheets
import org.bruchez.olivier.healthanalysis.SheetsHelper._

import scala.util.Try

case class HealthJournal(config: HealthJournalConfig) {
  def healthVariables()(implicit sheets: Sheets): HealthVariables = {
    val allValues = sheets.allValues(config.spreadsheetId, config.sheet)

    val variables = (for { rowIndex <- 1 until allValues.rowCount } yield {
      val row = allValues.row(rowIndex)
      val date = row.localDate(config.dateColumnIndex)

      val simpleColumnVariables = config.variables flatMap { variable =>
        Try(HealthVariable(date, name = variable.variableName, value = row.double(variable.columnIndex))).toOption
      }

      val splitColumnVariables = config.columnsToSplit.flatMap { columnToSplit =>
        row.string(columnToSplit.index).split(columnToSplit.separator).toSeq.map { name =>
          HealthVariable(date, name = name, value = 1.0)
        }
      }

      simpleColumnVariables ++ splitColumnVariables
    }).flatten

    HealthVariables(variables)
  }
}
