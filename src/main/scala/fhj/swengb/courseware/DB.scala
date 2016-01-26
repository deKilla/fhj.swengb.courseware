package fhj.swengb.courseware

import java.sql.{Statement, Connection, DriverManager, ResultSet}

import scala.util.Try


object DB {
  trait DBEntity[T] {
    def reTable(stmt: Statement): Int
    def toDB(c: Connection)(t: T): Int
    def fromDB(rs: ResultSet): List[T]
    def query(con: Connection)(query: String): ResultSet = {con.createStatement().executeQuery(query)}
    def dropTableSql: String
    def createTableSql: String
    def insertSql:String
  }

  val repopath:String = new java.io.File(".").getCanonicalPath
  val db: String = repopath  + "/fhj.swengb.courseware/src/main/resources/fhj/swengb/courseware/swengb.sqlite"

  lazy val maybeConnection: Try[Connection] = Try(DriverManager.getConnection("jdbc:sqlite:" + db))

}