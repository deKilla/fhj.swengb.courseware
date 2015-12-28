package fhj.swengb.courseware

import java.sql.{Connection,DriverManager,ResultSet}
import scala.util.Try

object DB {

  trait DBEntity[T] {
    def fromDB(rs: ResultSet): List[T]

    def query(c: Connection)(q: String): ResultSet = {
      c.createStatement().executeQuery(q)
    }
  }

  lazy val maybeConnection: Try[Connection] =
    Try(DriverManager.getConnection("jdbc:sqlite:exampledb.sqlite"))

}


object DBAccess {

  def main(args: Array[String]) {
    for {con <- DB.maybeConnection
         content <- Album.fromDB(Album.showeverything(con))
    } {
      println("printing results:")
      println("-------------------------------------------------")
      println(content)
      println("-------------------------------------------------")
      println("results printed")
    }
  }
}


