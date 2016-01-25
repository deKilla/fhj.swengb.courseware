package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Assignment extends DB.DBEntity[Assignment] {

  val dropTableSql = "drop table if exists GroupAssignments"
  val createTableSql = "CREATE TABLE \"GroupAssignments\" (\n\t`ID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n\t`name`\tTEXT NOT NULL,\n\t`description`\tTEXT\n)"
  val insertSql = "insert into \"GroupAssignments\" VALUES (?, ?, ?)"


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
  def asMap(query:String = assignmentquery.selectall): Map[_, Assignment] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Assignment.fromDB(Assignment.query(c)(assignmentquery.query())
      ).map(l => (l.ID,l)).toMap
    } else { Map.empty }
    data
  }

  def createReport(query:String = assignmentquery.selectall): Unit = {
    import java.io._
    import java.awt.Desktop

    val path = "fhj.swengb.courseware/src/main/resources/fhj/swengb/courseware/reports/"
    val timestamp: String = (System.currentTimeMillis / 1000).toString
    val filename:String = path + "assignmentreport_" + timestamp + ".html"
    val file = new File(filename)
    val report = new PrintWriter(file)

    val assignments:Map[_, Assignment] = this.asMap(query)

    val htmltop:String = ("" +
      "<html>" +
      "<head>" +
      "<meta charset=\"utf-8\">" +
      "<title>Assignmentreport " + timestamp + "</title>" +
      "<link rel=\"stylesheet\" type=\"text/css\" href=\"reportres/stylesheet.css\" />" +
      "<head>" +
      "<body>" +
      "<h1>Assignmentreport</h1>" +
      "<table>" +
      "<tr>" +
      "<th>ID</th><th>name</th><th>description</th>" +
      "</tr>")

    val htmlbottom:String = ("</table></body></html>")

    report.write(htmltop)

    for (assignment <- assignments.values){
      report.append("<tr>")
      report.append("<td>" + assignment.ID + "</td>")
      report.append("<td>" + assignment.name + "</td>")
      report.append("<td>" + assignment.description + "</td>")
      report.append("</tr>")
    }

    report.append(htmlbottom)
    report.close

    Desktop.getDesktop.open(file)
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

