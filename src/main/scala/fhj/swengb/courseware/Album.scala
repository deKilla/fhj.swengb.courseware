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
}