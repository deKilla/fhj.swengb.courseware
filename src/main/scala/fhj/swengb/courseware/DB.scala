package fhj.swengb.courseware

import java.sql.{Connection,DriverManager,ResultSet}
import scala.util.Try

object DB {
  trait DBEntity[T] {
    def fromDB(rs: ResultSet): List[T]
    def query(c: Connection)(q: String): ResultSet = c.createStatement().executeQuery(q)
  }
  lazy val maybeConnection: Try[Connection] = Try(DriverManager.getConnection("jdbc:sqlite:E:/SWENGB/fhj.swengb.courseware/src/main/resources/fhj/swengb/courseware/exampledb.sqlite"))
}


object DBAccess {
  def main(args: Array[String]) {
    val connection = DB.maybeConnection
    if (connection.isSuccess) println("connection established")
    println("printing results:")
    println("-------------------------------------------------")
    for {c <- connection
         row <- Album.fromDB(Album.showeverything(c))
    } {
      println(row)
    }
    println("-------------------------------------------------")
    println("results printed")
  }
}


