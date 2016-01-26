package fhj.swengb.courseware

import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}
import scala.collection.mutable.ListBuffer


object Homework extends DB.DBEntity[Homework] {

  val dropTableSql = "drop table if exists Homeworks"
  val createTableSql = "CREATE TABLE \"Homeworks\" (\n\t`ID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n\t`name`\tTEXT NOT NULL,\n\t`description`\tTEXT\n)"
  val insertSql = "insert into \"Homeworks\" VALUES (?, ?, ?)"
  val editSql = "update \"Homeworks\" set name=?, description=? where ID=?"
  val deleteSql = "delete from \"Homeworks\" where ID=?"


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

  def editDB(c: Connection)(h: Homework): Int = {
    val pstmt = c.prepareStatement(editSql)
    pstmt.setString(1, h.name)
    pstmt.setString(2, h.description)
    pstmt.setInt(3, h.ID)
    pstmt.executeUpdate()
  }

  def deletefromDB(c: Connection)(ID: Int): Int = {
    val pstmt = c.prepareStatement(deleteSql)
    pstmt.setInt(1, ID)
    pstmt.executeUpdate()
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
  val selectwhatever = "select * from Homeworks where name=\"uuu\""
}

object HomeworkData {
  def asMap(query:String = homeworkquery.selectall): Map[_, Homework] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Homework.fromDB(Homework.query(c)(query)
      ).map(h => (h.ID,h)).toMap
    } else { Map.empty }
    data
  }

  def createReport(query:String = homeworkquery.selectall): Unit = {
    import java.io._
    import java.awt.Desktop

    val path = "fhj.swengb.courseware/src/main/resources/fhj/swengb/courseware/reports/"
    val timestamp: String = (System.currentTimeMillis / 1000).toString
    val filename:String = path + "homeworkreport_" + timestamp + ".html"
    val file = new File(filename)
    val report = new PrintWriter(file)

    val homeworks:Map[_, Homework] = this.asMap(query)

    val htmltop:String = ("" +
      "<html>" +
      "<head>" +
      "<title>Homeworkreport " + timestamp + "</title>" +
      "<link rel=\"stylesheet\" type=\"text/css\" href=\"reportres/stylesheet.css\" />" +
      "<head>" +
      "<body>" +
      "<h1>Homeworkreport</h1>" +
      "<table>" +
      "<tr>" +
      "<th>ID</th><th>name</th><th>description</th>" +
      "</tr>")

    val htmlbottom:String = ("</table></body></html>")

    report.write(htmltop)

    for (homework <- homeworks.values){
      report.append("<tr>")
      report.append("<td>" + homework.ID + "</td>")
      report.append("<td>" + homework.name + "</td>")
      report.append("<td>" + homework.description + "</td>")
      report.append("</tr>")
    }

    report.append(htmlbottom)
    report.close

    Desktop.getDesktop.open(file)
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

