package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Project extends DB.DBEntity[Project] {
  def fromDB(stringToSet: (String) => ResultSet) = ???


  val dropTableSql = "drop table if exists Projects"
  val createTableSql = "create table Projects (ID int, name string, begindate String, deadline String)"
  val insertSql = "insert into Projects (ID, name, begindate, deadline) VALUES (?, ?, ?, ?)"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Project.dropTableSql)
    stmt.executeUpdate(Project.createTableSql)
  }

  def toDB(c: Connection)(s: Project): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.name)
    pstmt.setString(3, s.begindate)
    pstmt.setString(4, s.deadline)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Project] = {
    val lb: ListBuffer[Project] = new ListBuffer[Project]()
    while (rs.next()) lb.append(
      Project(
        rs.getInt("ID"),
        rs.getString("name"),
        rs.getString("begindate"),
        rs.getString("deadline")
        )
    )
    lb.toList
  }
}

sealed trait Projects {

  def ID: Int

  def name: String

  def begindate: String

  def deadline: String


  def normalize(in: String): String = {
    val mapping =
      Map("ä" -> "ae",
        "ö" -> "oe",
        "ü" -> "ue",
        "ß" -> "ss")
    mapping.foldLeft(in) { case ((s, (a, b))) => s.replace(a, b) }
  }

}

case class Project(ID: Int,
                    name: String,
                    begindate: String,
                    deadline: String
                    ) extends Projects

object projectquery {
  val selectall = "select * from Projects"
  def query():String = {
    selectall
  }
}

object ProjectData {
  def asMap(): Map[_, Project] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Project.fromDB(Project.query(c)(projectquery.query())
      ).map(l => (l.ID,l)).toMap
    } else { Map.empty }
    data
  }
}


class MutableProject {

  val p_ID: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_name: SimpleStringProperty = new SimpleStringProperty()
  val p_begindate: SimpleStringProperty = new SimpleStringProperty()
  val p_deadline: SimpleStringProperty = new SimpleStringProperty()


  def setID(ID: Int) = p_ID.set(ID)
  def setName(name: String) = p_name.set(name)
  def setBegindate(begindate: String) = p_begindate.set(begindate)
  def setDeadline(deadline: String) = p_deadline.set(deadline)


}

object MutableProject {

  def apply(l: Project): MutableProject = {
    val ml = new MutableProject
    ml.setID(l.ID)
    ml.setName(l.name)
    ml.setBegindate(l.begindate)
    ml.setDeadline(l.deadline)
    ml
  }

}

