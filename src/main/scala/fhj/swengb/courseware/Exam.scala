package fhj.swengb.courseware

import java.sql.{Connection, ResultSet, Statement}
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}
import scala.collection.mutable.ListBuffer

object Exam extends DB.DBEntity[Exam] {

  val dropTableSql = "drop table if exists Exams"
  val createTableSql = "CREATE TABLE \"Exams\" (\n\t`ID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n\t`course`\tTEXT NOT NULL,\n\t`attempt`\tINTEGER NOT NULL,\n\t`date`\tTEXT\n)"
  val insertSql = "insert into \"Exams\" VALUES (?, ?, ?, ?)"
  val editSql = "update \"Exams\" set course=?, attempt=?, date=? where ID=?"
  val deleteSql = "delete from \"Exams\" where ID=?"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Exam.dropTableSql)
    stmt.executeUpdate(Exam.createTableSql)
  }

  def toDB(c: Connection)(s: Exam): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.course)
    pstmt.setInt(3, s.attempt)
    pstmt.setString(3, s.date)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Exam] = {
    val lb: ListBuffer[Exam] = new ListBuffer[Exam]()
    while (rs.next()) lb.append(
      Exam(
        rs.getInt("ID"),
        rs.getString("course"),
        rs.getInt("attempt"),
        rs.getString("date")
        )
    )
    lb.toList
  }

  def editDB(c: Connection)(e: Exam): Int = {
    val pstmt = c.prepareStatement(editSql)
    pstmt.setString(1, e.course)
    pstmt.setInt(2, e.attempt)
    pstmt.setString(3, e.date)
    pstmt.setInt(4, e.ID)
    pstmt.executeUpdate()
  }

  def deletefromDB(c: Connection)(ID: Int): Int = {
    val pstmt = c.prepareStatement(deleteSql)
    pstmt.setInt(1, ID)
    pstmt.executeUpdate()
  }
}

sealed trait Exams {

  def ID: Int

  def course: String

  def attempt: Int

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
                course: String,
                attempt: Int,
                date: String
                    ) extends Exams

object examquery {
  val selectall = "select * from Exams"
}

object ExamData {
  def asMap(query:String = examquery.selectall): Map[_, Exam] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Exam.fromDB(Exam.query(c)(query)
      ).map(e => (e.ID,e)).toMap
    } else { Map.empty }
    data
  }

  def createReport(query:String = examquery.selectall): Unit = {
    import java.io._
    import java.awt.Desktop

    val path = "fhj.swengb.courseware/src/main/resources/fhj/swengb/courseware/reports/"
    val timestamp: String = (System.currentTimeMillis / 1000).toString
    val filename:String = path + "examreport_" + timestamp + ".html"
    val file = new File(filename)
    val report = new PrintWriter(file)

    val exams:Map[_, Exam] = this.asMap(query)

    val htmltop:String = ("" +
      "<html>" +
      "<head>" +
      "<title>Examreport " + timestamp + "</title>" +
      "<link rel=\"stylesheet\" type=\"text/css\" href=\"reportres/stylesheet.css\" />" +
      "<head>" +
      "<body>" +
      "<h1>Examreport</h1>" +
      "<table>" +
      "<tr>" +
      "<th>ID</th><th>course</th><th>attemp</th><th>date</th>" +
      "</tr>")

    val htmlbottom:String = ("</table></body></html>")

    report.write(htmltop)

    for (exam <- exams.values){
      report.append("<tr>")
      report.append("<td>" + exam.ID + "</td>")
      report.append("<td>" + exam.course + "</td>")
      report.append("<td>" + exam.attempt + "</td>")
      report.append("<td>" + exam.date + "</td>")
      report.append("</tr>")
    }

    report.append(htmlbottom)
    report.close

    Desktop.getDesktop.open(file)
  }

}


class MutableExam {

  val p_ID: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_course: SimpleStringProperty = new SimpleStringProperty()
  val p_attempt: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_date: SimpleStringProperty = new SimpleStringProperty()


  def setID(ID: Int) = p_ID.set(ID)
  def setCourse(course: String) = p_course.set(course)
  def setAttempt(attempt: Int) = p_attempt.set(attempt)
  def setDate(date: String) = p_date.set(date)


}

object MutableExam {

  def apply(l: Exam): MutableExam = {
    val ml = new MutableExam
    ml.setID(l.ID)
    ml.setCourse(l.course)
    ml.setAttempt(l.attempt)
    ml.setDate(l.date)
    ml
  }

}

