package org.bruchez.olivier.healthanalysis

import java.time.LocalDate

case class HealthVariable(date: LocalDate, name: String, value: Double)

case class HealthVariables(variables: Seq[HealthVariable]) {
  lazy val byDate: Map[LocalDate, Seq[HealthVariable]] = variables.groupBy(_.date)
}
