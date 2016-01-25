package fhj.swengb.courseware

import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}
import scala.collection.mutable.ListBuffer


object Lecturer extends DB.DBEntity[Lecturer] {

  val dropTableSql = "drop table if exists Lecturers"
  val createTableSql = "CREATE TABLE \"Lecturers\" (\n\t`ID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n\t`firstname`\tTEXT NOT NULL,\n\t`lastname`\tTEXT NOT NULL,\n\t`title`\tTEXT\n)"
  val insertSql = "insert into \"Lecturers\" VALUES (?, ?, ?, ?)"


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
}

object LecturerData {
  def asMap(query:String = lecturerquery.selectall): Map[_, Lecturer] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Lecturer.fromDB(Lecturer.query(c)(query)
      ).map(l => (l.ID,l)).toMap
    } else { Map.empty }
    data
  }

  def createReport(query:String = lecturerquery.selectall): Unit = {
    import java.io._
    import java.awt.Desktop

    val path = "fhj.swengb.courseware/src/main/resources/fhj/swengb/courseware/reports/"
    val timestamp: String = (System.currentTimeMillis / 1000).toString
    val filename:String = path + "lecturerreport_" + timestamp + ".html"
    val file = new File(filename)
    val report = new PrintWriter(file)

    val lecturers:Map[_, Lecturer] = this.asMap(query)

    val htmltop:String = ("" +
      "<html>" +
      "<head>" +
      "<title>Lecturerreport " + timestamp + "</title>" +
      "<link rel=\"stylesheet\" type=\"text/css\" href=\"reportres/stylesheet.css\" />" +
      "<head>" +
      "<body>" +
      "<h1>Lecturerreport</h1>" +
      "<table>" +
      "<tr>" +
      "<th>ID</th><th>firstname</th><th>lastname</th><th>title</th>" +
      "</tr>")

    val htmlbottom:String = ("</table></body></html>")

    report.write(htmltop)

    for (lecturer <- lecturers.values){
      report.append("<tr>")
      report.append("<td>" + lecturer.ID + "</td>")
      report.append("<td>" + lecturer.firstname + "</td>")
      report.append("<td>" + lecturer.lastname + "</td>")
      report.append("<td>" + lecturer.title + "</td>")
      report.append("</tr>")
    }

    report.append(htmlbottom)
    report.close

    Desktop.getDesktop.open(file)
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

