package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Exam extends DB.DBEntity[Exam] {
  def fromDB(stringToSet: (String) => ResultSet) = ???


  val dropTableSql = "drop table if exists Exams"
  val createTableSql = "create table Exams (ID int, course string, attempt int, date String)"
  val insertSql = "insert into Exams (ID, course, attempt, date) VALUES (?, ?, ?, ?)"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Exam.dropTableSql)
    stmt.executeUpdate(Exam.createTableSql)
  }

  def toDB(c: Connection)(s: Exam): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.course)
    pstmt.setInt(3, s.attempt)
    pstmt.setString(3, s.date)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Exam] = {
    val lb: ListBuffer[Exam] = new ListBuffer[Exam]()
    while (rs.next()) lb.append(
      Exam(
        rs.getInt("ID"),
        rs.getString("course"),
        rs.getInt("attempt"),
        rs.getString("date")
        )
    )
    lb.toList
  }
}

sealed trait Exams {

  def ID: Int

  def course: String

  def attempt: Int

  def date: String


  def normalize(in: String): String = {
    val mapping =
      Map("ä" -> "ae",
        "ö" -> "oe",
        "ü" -> "ue",
        "ß" -> "ss")
    mapping.foldLeft(in) { case ((s, (a, b))) => s.replace(a, b) }
  }

}

case class Exam(ID: Int,
                course: String,
                attempt: Int,
                date: String
                    ) extends Exams

object examquery {
  val selectall = "select * from Exams"
  def query():String = {
    selectall
  }
}

object ExamData {
  def asMap(): Map[_, Exam] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Exam.fromDB(Exam.query(c)(examquery.query())
      ).map(l => (l.ID,l)).toMap
    } else { Map.empty }
    data
  }
}


class MutableExam {

  val p_ID: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_course: SimpleStringProperty = new SimpleStringProperty()
  val p_attempt: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_date: SimpleStringProperty = new SimpleStringProperty()


  def setID(ID: Int) = p_ID.set(ID)
  def setCourse(course: String) = p_course.set(course)
  def setAttempt(attempt: Int) = p_attempt.set(attempt)
  def setDate(date: String) = p_date.set(date)


}

object MutableExam {

  def apply(l: Exam): MutableExam = {
    val ml = new MutableExam
    ml.setID(l.ID)
    ml.setCourse(l.course)
    ml.setAttempt(l.attempt)
    ml.setDate(l.date)
    ml
  }

}

