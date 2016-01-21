package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Lecturer extends DB.DBEntity[Lecturer] {
  def fromDB(stringToSet: (String) => ResultSet) = ???


  val dropTableSql = "drop table if exists Lecturers"
  val createTableSql = "create table Lecturers (ID int, firstname string, lastname String, title String)"
  val insertSql = "insert into Lecturers (ID, firstname, lastname, title) VALUES (?, ?, ?, ?)"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Lecturer.dropTableSql)
    stmt.executeUpdate(Lecturer.createTableSql)
  }

  def toDB(c: Connection)(s: Lecturer): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.firstname)
    pstmt.setString(3, s.lastname)
    pstmt.setString(4, s.title)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Lecturer] = {
    val lb: ListBuffer[Lecturer] = new ListBuffer[Lecturer]()
    while (rs.next()) lb.append(
      Lecturer(
        rs.getInt("ID"),
        rs.getString("firstname"),
        rs.getString("lastname"),
        rs.getString("title")
        )
    )
    lb.toList
  }
}

sealed trait Lecturers {

  def ID: Int

  def firstname: String

  def lastname: String

  def title: String

  def normalize(in: String): String = {
    val mapping =
      Map("ä" -> "ae",
        "ö" -> "oe",
        "ü" -> "ue",
        "ß" -> "ss")
    mapping.foldLeft(in) { case ((s, (a, b))) => s.replace(a, b) }
  }

}

case class Lecturer(ID: Int,
                    firstname: String,
                    lastname: String,
                    title: String
                    ) extends Lecturers

object lecturerquery {
  val selectall = "select * from Lecturers"
  def query():String = {
    selectall
  }
}

object LecturerData {
  def asMap(): Map[_, Lecturer] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Lecturer.fromDB(Lecturer.query(c)(lecturerquery.query())
      ).map(l => (l.ID,l)).toMap
    } else { Map.empty }
    data
  }
}


class MutableLecturer {

  val p_ID: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_firstname: SimpleStringProperty = new SimpleStringProperty()
  val p_lastname: SimpleStringProperty = new SimpleStringProperty()
  val p_title: SimpleStringProperty = new SimpleStringProperty()

  def setID(ID: Int) = p_ID.set(ID)
  def setFirstname(firstname: String) = p_firstname.set(firstname)
  def setLastname(lastname: String) = p_lastname.set(lastname)
  def setTitle(title:String) = p_title.set(title)

}

object MutableLecturer {

  def apply(l: Lecturer): MutableLecturer = {
    val ml = new MutableLecturer
    ml.setID(l.ID)
    ml.setFirstname(l.firstname)
    ml.setLastname(l.lastname)
    ml.setTitle(l.title)
    ml
  }

}

