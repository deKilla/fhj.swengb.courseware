package fhj.swengb.courseware

import java.net.URL
import java.sql.{Connection, ResultSet, Statement}
import java.util.Calendar
import javafx.beans.property.{SimpleStringProperty, SimpleIntegerProperty}

import fhj.swengb.GitHub

import scala.collection.mutable.ListBuffer


object Student extends DB.DBEntity[Student] {

  val dropTableSql = "drop table if exists Students"
  val createTableSql = "CREATE TABLE \"Students\" (\n\t`ID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n\t`firstname`\tTEXT NOT NULL,\n\t`lastname`\tTEXT NOT NULL,\n\t`email`\tTEXT UNIQUE,\n\t`birthday`\tTEXT,\n\t`telnr`\tTEXT,\n\t`githubUsername`\tTEXT UNIQUE,\n\t`group`\tINTEGER\n)"
  val insertSql = "insert into \"Students\" VALUES (?, ?, ?, ?, ?, ?, ?, ?)"


  def reTable(stmt: Statement): Int = {
    stmt.executeUpdate(Student.dropTableSql)
    stmt.executeUpdate(Student.createTableSql)
  }

  def toDB(c: Connection)(s: Student): Int = {
    val pstmt = c.prepareStatement(insertSql)
    pstmt.setInt(1, s.ID)
    pstmt.setString(2, s.firstname)
    pstmt.setString(3, s.lastname)
    pstmt.setString(4, s.email)
    pstmt.setString(5, s.birthday)
    pstmt.setString(6, s.telnr)
    pstmt.setString(7, s.githubUsername)
    pstmt.setInt(8, s.group)
    pstmt.executeUpdate()
  }

  def fromDB(rs: ResultSet): List[Student] = {
    val lb: ListBuffer[Student] = new ListBuffer[Student]()
    while (rs.next()) lb.append(
      Student(
        rs.getInt("ID"),
        rs.getString("firstname"),
        rs.getString("lastname"),
        rs.getString("email"),
        rs.getString("birthday"),
        rs.getString("telnr"),
        rs.getString("githubUsername"),
        rs.getInt("group")
      )
    )
    lb.toList
  }

}

sealed trait Students {

  def ID: Int

  def firstname: String

  def lastname: String

  def email: String

  def birthday: String

  def telnr: String

  def githubUsername: String

  def group: Int

  def longName = s"$firstname $lastname"

  def normalize(in: String): String = {
    val mapping =
      Map("ä" -> "ae",
        "ö" -> "oe",
        "ü" -> "ue",
        "ß" -> "ss")
    mapping.foldLeft(in) { case ((s, (a, b))) => s.replace(a, b) }
  }

  def userId: String = {
    val fst = firstname(0).toLower.toString
    normalize(fst + lastname.toLowerCase)
  }

  val gitHubHome: String = s"https://github.com/$githubUsername/"

  def gitHubUser: GitHub.User = {
    import GitHub.GithubUserProtocol._
    import GitHub._
    import spray.json._

    val webserviceString: String = scala.io.Source.fromURL(new URL(s"https://api.github.com/users/$githubUsername")).mkString
    webserviceString.parseJson.convertTo[User]
  }

}

case class Student(ID: Int,
                   firstname: String,
                   lastname: String,
                   email: String,
                   birthday: String,
                   telnr: String,
                   githubUsername: String,
                   group: Int) extends Students

object studentquery {
  val selectall = "select * from Students"
  def query():String = {
    selectall
  }
}

object StudentData {
  def asMap(): Map[_, Student] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      Student.fromDB(Student.query(c)(studentquery.query())
      ).map(s => (s.ID,s)).toMap
    } else { Map.empty }
    data
  }

  def createReport(students:Set[Student]): Unit = {
    import java.io._
    val path = "fhj.swengb.courseware/src/main/resources/fhj/swengb/courseware/reports/"
    val timestamp: String = (System.currentTimeMillis / 1000).toString
    val pw = new PrintWriter(new File(path + "studentreport_" + timestamp + ".html"))

    val htmltop:String = ("<html><head><title>Studentreport " + timestamp + "</title><head>" +
      "<body>" +
      "<h1>Studentreport</h1>" +
      "<table>" +
      "<tr>" +
      "<th>ID</th><th>firstname</th><th>lastname</th><th>email</th><th>telnr</th><th>githubUsername</th><th>group</th>" +
      "</tr>")

    val htmlbottom:String = ("</table></body></html>")

    pw.write(htmltop)

    for (student <- students){
      pw.append("<tr>")
      pw.append("<td>" + student.ID + "</td>")
      pw.append("<td>" + student.firstname + "</td>")
      pw.append("<td>" + student.lastname + "</td>")
      pw.append("<td>" + student.email + "</td>")
      pw.append("<td>" + student.birthday + "</td>")
      pw.append("<td>" + student.telnr + "</td>")
      pw.append("<td>" + student.githubUsername + "</td>")
      pw.append("<td>" + student.group + "</td>")
      pw.append("</tr>")
    }

    pw.append(htmlbottom)
    pw.close
  }

}

class MutableStudent {

  val p_ID: SimpleIntegerProperty = new SimpleIntegerProperty()
  val p_firstname: SimpleStringProperty = new SimpleStringProperty()
  val p_lastname: SimpleStringProperty = new SimpleStringProperty()
  val p_email: SimpleStringProperty = new SimpleStringProperty()
  val p_birthday: SimpleStringProperty = new SimpleStringProperty()
  val p_telnr: SimpleStringProperty = new SimpleStringProperty()
  val p_githubUsername: SimpleStringProperty = new SimpleStringProperty()
  val p_group: SimpleIntegerProperty = new SimpleIntegerProperty()

  def setID(ID: Int) = p_ID.set(ID)
  def setFirstname(firstname: String) = p_firstname.set(firstname)
  def setLastname(lastname: String) = p_lastname.set(lastname)
  def setEmail(email:String) = p_email.set(email)
  def setBirthday(birthday:String) = p_birthday.set(birthday)
  def setTelnr(telnr: String) = p_telnr.set(telnr)
  def setGithubUsername(githubUsername: String) = p_githubUsername.set(githubUsername)
  def setGroup(group: Int) = p_group.set(group)

}

object MutableStudent {

  def apply(s: Student): MutableStudent = {
    val ms = new MutableStudent
    ms.setID(s.ID)
    ms.setFirstname(s.firstname)
    ms.setLastname(s.lastname)
    ms.setEmail(s.email)
    ms.setBirthday(s.birthday)
    ms.setTelnr(s.telnr)
    ms.setGithubUsername(s.githubUsername)
    ms.setGroup(s.group)
    ms
  }

}

