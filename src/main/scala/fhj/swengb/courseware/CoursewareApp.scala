package fhj.swengb.courseware


import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml._
import javafx.scene.control.{TableColumn, TableView}
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage
import javafx.util.Callback

import scala.collection.JavaConversions
import scala.util.Random
import scala.util.control.NonFatal

import java.sql.{Connection, DriverManager, ResultSet, Statement}


/**
 * Shows a way to use a JavaFX TableView with Scala
 */
object CoursewareApp {
  def main(args: Array[String]) {
    Application.launch(classOf[CoursewareApp], args: _*)
  }
}

/**
 * Setup for the javafx app
 */
class CoursewareApp extends javafx.application.Application {

  val loader = new FXMLLoader(getClass.getResource("/fhj/swengb/courseware/Courseware.fxml"))

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("CoursewareApp")
      loader.load[Parent]()
      stage.setScene(new Scene(loader.getRoot[Parent]))
      stage.show()
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }

}

/**
 * domain object
 */
case class Student(id: Int, name: String)

/**
 * domain object, but usable with javafx
 */
class MutableStudent {

  val idProperty: SimpleIntegerProperty = new SimpleIntegerProperty()
  val nameProperty: SimpleStringProperty = new SimpleStringProperty()

  def setId(id: Int) = idProperty.set(id)

  def setName(name: String) = nameProperty.set(name)
}

/**
 * companion object to get a better initialisation story
 */
object MutableStudent {

  def apply(s: Student): MutableStudent = {
    val ms = new MutableStudent
    ms.setId(s.id)
    ms.setName(s.name)
    ms
  }
}

object DataSource {

  val data =
    (1 to 29) map {
      case i => Student(i, "Name")
    }

}

object JfxUtils {

  type TCDF[S, T] = TableColumn.CellDataFeatures[S, T]

  import JavaConversions._

  def mkObservableList[T](collection: Iterable[T]): ObservableList[T] = {
    FXCollections.observableList(new java.util.ArrayList[T](collection))
  }

  private def mkCellValueFactory[S, T](fn: TCDF[S, T] => ObservableValue[T]): Callback[TCDF[S, T], ObservableValue[T]] = {
    new Callback[TCDF[S, T], ObservableValue[T]] {
      def call(cdf: TCDF[S, T]): ObservableValue[T] = fn(cdf)
    }
  }

  def initTableViewColumnCellValueFactory[S, T](tc: TableColumn[S, T], f: S => Any): Unit = {
    tc.setCellValueFactory(mkCellValueFactory(cdf => f(cdf.getValue).asInstanceOf[ObservableValue[T]]))
  }

}

class CoursewareAppController extends Initializable{

  import JfxUtils._

  type StudentTC[T] = TableColumn[MutableStudent, T]

  @FXML var tableView: TableView[MutableStudent] = _
  @FXML var C1: StudentTC[Int] = _
  @FXML var C2: StudentTC[String] = _

  val mutableStudents = mkObservableList(DataSource.data.map(MutableStudent(_)))

  /**
   * provide a table column and a generator function for the value to put into
   * the column.
   *
   * @tparam T the type which is contained in the property
   * @return
   */
  def initTableViewColumn[T]: (StudentTC[T], (MutableStudent) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableStudent, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    tableView.setItems(mutableStudents)

    initTableViewColumn[Int](C1, _.idProperty)
    initTableViewColumn[String](C2, _.nameProperty)

  }


}
