package org.bruchez.olivier.healthanalysis

import java.time.{LocalDate, ZoneId}
import com.google.api.services.sheets.v4.Sheets
import org.apache.poi.ss.usermodel.DateUtil
import scala.jdk.CollectionConverters._

object SheetsHelper {
  implicit class SheetsOps(sheets: Sheets) {
    def allValues(spreadsheetId: String, sheet: String): SheetValues = {
      // There doesn't seem to be any enum for this
      val UnformattedValue = "UNFORMATTED_VALUE"

      val values = sheets.spreadsheets.values
        .get(spreadsheetId, sheet)
        .setValueRenderOption(UnformattedValue)
        .execute
        .getValues
        .asScala
        .map(values => SheetRow(values.asScala.toIndexedSeq))
        .toIndexedSeq

      SheetValues(values)
    }
  }
}

case class SheetRow(values: IndexedSeq[AnyRef]) {
  def localDate(columnIndex: Int): LocalDate = values(columnIndex) match {
    case localDate: LocalDate =>
      localDate
    case number: java.lang.Number =>
      DateUtil.getJavaDate(number.doubleValue).toInstant.atZone(ZoneId.systemDefault).toLocalDate
    case string: String =>
      LocalDate.parse(string)
  }

  def double(columnIndex: Int): Double = values(columnIndex) match {
    case number: java.lang.Number => number.doubleValue
    case string: String           => string.toDouble
  }

  def string(columnIndex: Int): String = values(columnIndex).toString
}

case class SheetValues(values: IndexedSeq[SheetRow]) {
  lazy val rowCount: Int = values.size

  def row(rowIndex: Int): SheetRow = values(rowIndex)
}
