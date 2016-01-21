package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Exam extends DB.DBEntity[Exam] {
  def fromDB(stringToSet: (String) => ResultSet) = ???


  val dropTableSql = "drop table if exists Exams"
  val createTableSql = "create table Exams (ID int, name string, date String)"
  val insertSql = "insert into Exams (ID, name, date) VALUES (?, ?, ?)"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Exam.dropTableSql)
    stmt.executeUpdate(Exam.createTableSql)
  }

  def toDB(c: Connection)(s: Exam): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.name)
    pstmt.setString(3, s.date)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Exam] = {
    val lb: ListBuffer[Exam] = new ListBuffer[Exam]()
    while (rs.next()) lb.append(
      Exam(
        rs.getInt("ID"),
        rs.getString("name"),
        rs.getString("date")
        )
    )
    lb.toList
  }
}

sealed trait Exams {

  def ID: Int

  def name: String

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
                    name: String,
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
  val p_name: SimpleStringProperty = new SimpleStringProperty()
  val p_date: SimpleStringProperty = new SimpleStringProperty()


  def setID(ID: Int) = p_ID.set(ID)
  def setName(name: String) = p_name.set(name)
  def setDate(date: String) = p_date.set(date)


}

object MutableExam {

  def apply(l: Exam): MutableExam = {
    val ml = new MutableExam
    ml.setID(l.ID)
    ml.setName(l.name)
    ml.setDate(l.date)
    ml
  }

}

