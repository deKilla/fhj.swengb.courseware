package fhj.swengb.courseware


import java.awt.Button
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml._
import javafx.scene.control.{TableColumn, TableView}
import javafx.scene.layout.AnchorPane
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

  val loader = new FXMLLoader(getClass.getResource("/fhj/swengb/courseware/CW_main.fxml"))

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("CoursewareApp - Window 1")
      loader.load[Parent]()
      stage.setScene(new Scene(loader.getRoot[Parent]))
      stage.show()
    } catch {
      case NonFatal(e) => e.printStackTrace()
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

class CoursewareAppController extends Initializable {

  @FXML var btnTest: Button = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

  }

  def test(): Unit = {
    val loader_test = new FXMLLoader(getClass.getResource("Courseware.fxml"))
    val stage_test = new Stage()

    stage_test.setTitle("CoursewareApp - Windows 2")
    loader_test.load[Parent]()
    stage_test.setScene(new Scene(loader_test.getRoot[Parent]))

    stage_test.show()
  }
}


/*class CoursewareAppController extends Initializable{

  import JfxUtils._

  type StudentTC[T] = TableColumn[MutableStudent, T]

  @FXML var tableView: TableView[MutableStudent] = _
  @FXML var C1: StudentTC[Int] = _
  @FXML var C2: StudentTC[String] = _
  @FXML var C3: StudentTC[String] = _

  def initTableViewColumn[T]: (StudentTC[T], (MutableStudent) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableStudent, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    val mutableStudents = mkObservableList(for(student <- StudentData.asMap) yield MutableStudent(student._2))
    tableView.setItems(mutableStudents)

    initTableViewColumn[Int](C1, _.pID)
    initTableViewColumn[String](C2, _.pFirstname)
    initTableViewColumn[String](C3, _.pLastname)

  }


}*/