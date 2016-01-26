package fhj.swengb.courseware

import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}
import scala.collection.mutable.ListBuffer


object Project extends DB.DBEntity[Project] {

  val dropTableSql = "drop table if exists Projects"
  val createTableSql = "CREATE TABLE \"Projects\" (\n\t`ID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n\t`name`\tTEXT NOT NULL,\n\t`begindate`\tTEXT NOT NULL,\n\t`deadline`\tTEXT\n)"
  val insertSql = "insert into \"Projects\" VALUES (?, ?, ?, ?)"
  val editSql = "update \"Projects\" set name=?, begindate=?, deadline=? where ID=?"
  val deleteSql = "delete from \"Projects\" where ID=?"


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

  def editDB(c: Connection)(p: Project): Int = {
    val pstmt = c.prepareStatement(editSql)
    pstmt.setString(1, p.name)
    pstmt.setString(2, p.begindate)
    pstmt.setString(3, p.deadline)
    pstmt.setInt(4, p.ID)
    pstmt.executeUpdate()
  }

  def deletefromDB(c: Connection)(ID: Int): Int = {
    val pstmt = c.prepareStatement(deleteSql)
    pstmt.setInt(1, ID)
    pstmt.executeUpdate()
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
}

object ProjectData {
  def asMap(query:String = projectquery.selectall): Map[_, Project] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Project.fromDB(Project.query(c)(query)
      ).map(p => (p.ID,p)).toMap
    } else { Map.empty }
    data
  }

  def createReport(query:String = projectquery.selectall): Unit = {
    import java.io._
    import java.awt.Desktop

    val path = "fhj.swengb.courseware/src/main/resources/fhj/swengb/courseware/reports/"
    val timestamp: String = (System.currentTimeMillis / 1000).toString
    val filename:String = path + "projectreport_" + timestamp + ".html"
    val file = new File(filename)
    val report = new PrintWriter(file)

    val projects:Map[_, Project] = this.asMap(query)

    val htmltop:String = ("" +
      "<html>" +
      "<head>" +
      "<title>Projectreport " + timestamp + "</title>" +
      "<link rel=\"stylesheet\" type=\"text/css\" href=\"reportres/stylesheet.css\" />" +
      "<head>" +
      "<body>" +
      "<h1>Projectreport</h1>" +
      "<table>" +
      "<tr>" +
      "<th>ID</th><th>name</th><th>begindate</th><th>deadline</th>" +
      "</tr>")

    val htmlbottom:String = ("</table></body></html>")

    report.write(htmltop)

    for (project <- projects.values){
      report.append("<tr>")
      report.append("<td>" + project.ID + "</td>")
      report.append("<td>" + project.name + "</td>")
      report.append("<td>" + project.begindate + "</td>")
      report.append("<td>" + project.deadline + "</td>")
      report.append("</tr>")
    }

    report.append(htmlbottom)
    report.close

    Desktop.getDesktop.open(file)
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

