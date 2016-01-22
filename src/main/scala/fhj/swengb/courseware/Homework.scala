package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Homework extends DB.DBEntity[Homework] {
  def fromDB(stringToSet: (String) => ResultSet) = ???


  val dropTableSql = "drop table if exists Homeworks"
  val createTableSql = "create table Homeworks (ID int, name string, description String)"
  val insertSql = "insert into Homeworks (ID, name, description) VALUES (?, ?, ?)"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Homework.dropTableSql)
    stmt.executeUpdate(Homework.createTableSql)
  }

  def toDB(c: Connection)(s: Homework): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.name)
    pstmt.setString(3, s.description)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Homework] = {
    val lb: ListBuffer[Homework] = new ListBuffer[Homework]()
    while (rs.next()) lb.append(
      Homework(
        rs.getInt("ID"),
        rs.getString("name"),
        rs.getString("description")
        )
    )
    lb.toList
  }
}

sealed trait Homeworks {

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

case class Homework(ID: Int,
                    name: String,
                    description: String
                    ) extends Homeworks

object homeworkquery {
  val selectall = "select * from Homeworks"
  def query():String = {
    selectall
  }
}

object HomeworkData {
  def asMap(): Map[_, Homework] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Homework.fromDB(Homework.query(c)(homeworkquery.query())
      ).map(l => (l.ID,l)).toMap
    } else { Map.empty }
    data
  }
}


class MutableHomework {

  val p_ID: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_name: SimpleStringProperty = new SimpleStringProperty()
  val p_description: SimpleStringProperty = new SimpleStringProperty()

  def setID(ID: Int) = p_ID.set(ID)
  def setName(name: String) = p_name.set(name)
  def setDescription(description: String) = p_description.set(description)

}

object MutableHomework {

  def apply(l: Homework): MutableHomework = {
    val ml = new MutableHomework
    ml.setID(l.ID)
    ml.setName(l.name)
    ml.setDescription(l.description)
    ml
  }

}

