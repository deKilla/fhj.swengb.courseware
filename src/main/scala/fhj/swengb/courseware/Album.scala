package fhj.swengb.courseware

import java.sql.{ResultSet,Connection}

import scala.collection.mutable.ListBuffer

object Album extends DB.DBEntity[Album] {

  def fromDB(rs: ResultSet): List[Album] = {
    val lb: ListBuffer[Album] = new ListBuffer[Album]()
    while (rs.next()) lb.append(Album(rs.getInt("AlbumId"), rs.getString("Title"), rs.getInt("ArtistId")))
    lb.toList
  }

  def showeverything(c: Connection): ResultSet = query(c)("select * from Album")

}

case class Album(AlbumId: Int, Title:String, ArtistId: Int) extends DB.DBEntity[Album] {
  def fromDB(rs: ResultSet): List[Album] = List()
  def getAlbumId() = this.AlbumId
  def getTitle() = this.Title
  def getArtistId() = this.ArtistId
}

object AlbumData {
  def main(args: Array[String]) {
    //this.asString()
    println(this.asMap())
  }
  def asString() {
    val connection = DB.maybeConnection
    if (connection.isSuccess) {
      //println("connection established")
      println("printing results:")
      println("-------------------------------------------------")
      for {c <- connection
           album <- Album.fromDB(Album.showeverything(c))
      } {
        println(album)
      }
      println("-------------------------------------------------")
      println("results printed")
    }
  }
  def asMap(): Map[_ <: Int, Album] = {
    val connection = DB.maybeConnection
    val data = if (connection.isSuccess) {
      val c = connection.get
      val albums = for (album <- Album.fromDB(Album.showeverything(c))) yield album
      albums.map(a => (a.getAlbumId(),a)).toMap
    } else { Map.empty }
    data
  }
}

