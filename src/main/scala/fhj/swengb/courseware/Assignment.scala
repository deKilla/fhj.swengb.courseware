package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Assignment extends DB.DBEntity[Assignment] {
  def fromDB(stringToSet: (String) => ResultSet) = ???


  val dropTableSql = "drop table if exists GroupAssignments"
  val createTableSql = "create table GroupAssignments (ID int, name string, description String)"
  val insertSql = "insert into GroupAssignments (ID, name, description) VALUES (?, ?, ?)"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Assignment.dropTableSql)
    stmt.executeUpdate(Assignment.createTableSql)
  }

  def toDB(c: Connection)(s: Assignment): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.name)
    pstmt.setString(3, s.description)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Assignment] = {
    val lb: ListBuffer[Assignment] = new ListBuffer[Assignment]()
    while (rs.next()) lb.append(
      Assignment(
        rs.getInt("ID"),
        rs.getString("name"),
        rs.getString("description")
        )
    )
    lb.toList
  }
}

sealed trait Assignments {

  def ID: Int

  def name: String

  def description: String

  def normalize(in: String): String = {
    val mapping =
      Map("ä" -> "ae",
        "ö" -> "oe",
        "ü" -> "ue",
        "ß" -> "ss")
    mapping.foldLeft(in) { case ((s, (a, b))) => s.replace(a, b) }
  }

}

case class Assignment(ID: Int,
                    name: String,
                    description: String
                    ) extends Assignments

object assignmentquery {
  val selectall = "select * from GroupAssignments"
  def query():String = {
    selectall
  }
}

object AssignmentData {
  def asMap(): Map[_, Assignment] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Assignment.fromDB(Assignment.query(c)(assignmentquery.query())
      ).map(l => (l.ID,l)).toMap
    } else { Map.empty }
    data
  }
}


class MutableAssignment {

  val p_ID: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_name: SimpleStringProperty = new SimpleStringProperty()
  val p_description: SimpleStringProperty = new SimpleStringProperty()

  def setID(ID: Int) = p_ID.set(ID)
  def setName(name: String) = p_name.set(name)
  def setDescription(description: String) = p_description.set(description)

}

object MutableAssignment {

  def apply(l: Assignment): MutableAssignment = {
    val ml = new MutableAssignment
    ml.setID(l.ID)
    ml.setName(l.name)
    ml.setDescription(l.description)
    ml
  }

}

