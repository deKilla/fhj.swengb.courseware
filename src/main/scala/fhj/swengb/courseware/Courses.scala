package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Course extends DB.DBEntity[Course] {
  def fromDB(stringToSet: (String) => ResultSet) = ???


  val dropTableSql = "drop table if exists Courses"
  val createTableSql = "create table Courses (ID int, name string, branch String, year int)"
  val insertSql = "insert into Courses (ID, name, branch, year) VALUES (?, ?, ?, ?)"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Course.dropTableSql)
    stmt.executeUpdate(Course.createTableSql)
  }

  def toDB(c: Connection)(s: Course): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.name)
    pstmt.setString(3, s.branch)
    pstmt.setInt(4, s.year)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Course] = {
    val lb: ListBuffer[Course] = new ListBuffer[Course]()
    while (rs.next()) lb.append(
      Course(
        rs.getInt("ID"),
        rs.getString("name"),
        rs.getString("branch"),
        rs.getInt("year")
        )
    )
    lb.toList
  }
}

sealed trait Courses {

  def ID: Int

  def name: String

  def branch: String

  def year: Int

  def normalize(in: String): String = {
    val mapping =
      Map("ä" -> "ae",
        "ö" -> "oe",
        "ü" -> "ue",
        "ß" -> "ss")
    mapping.foldLeft(in) { case ((s, (a, b))) => s.replace(a, b) }
  }

}

case class Course(ID: Int,
                    name: String,
                    branch: String,
                    year: Int
                    ) extends Courses

object coursequery {
  val selectall = "select * from Courses"
  def query():String = {
    selectall
  }
}

object CourseData {
  def asMap(): Map[_, Course] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Course.fromDB(Course.query(c)(coursequery.query())
      ).map(l => (l.ID,l)).toMap
    } else { Map.empty }
    data
  }
}


class MutableCourse {

  val p_ID: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_name: SimpleStringProperty = new SimpleStringProperty()
  val p_branch: SimpleStringProperty = new SimpleStringProperty()
  val p_year: SimpleIntegerProperty = new SimpleIntegerProperty()

  def setID(ID: Int) = p_ID.set(ID)
  def setName(name: String) = p_name.set(name)
  def setBranch(branch: String) = p_branch.set(branch)
  def setYear(year:Int) = p_year.set(year)

}

object MutableCourse {

  def apply(l: Course): MutableCourse = {
    val ml = new MutableCourse
    ml.setID(l.ID)
    ml.setName(l.name)
    ml.setBranch(l.branch)
    ml.setYear(l.year)
    ml
  }

}

