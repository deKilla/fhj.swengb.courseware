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

object CoursewareApp {
  def main(args: Array[String]) {
    Application.launch(classOf[CoursewareApp], args: _*)
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

class CoursewareApp extends javafx.application.Application {

  val loader = new FXMLLoader(getClass.getResource("Menu.fxml"))

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("Courseware | Übersicht")
      loader.load[Parent]()
      stage.setScene(new Scene(loader.getRoot[Parent]))
      stage.show()
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }

}

class CoursewareAppController extends Initializable {

  @FXML var btnShowStudents: Button = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {}

  def showStudents(): Unit = {
    val studentLoader = new FXMLLoader(getClass.getResource("Student.fxml"))
    val studentStage = new Stage()

    studentStage.setTitle("Courseware | Students")
    studentLoader.load[Parent]()
    studentStage.setScene(new Scene(studentLoader.getRoot[Parent]))

    studentStage.show()
  }

  def showLecturers(): Unit = {
    val lecturerLoader = new FXMLLoader(getClass.getResource("Lecturer.fxml"))
    val lecturerStage = new Stage()

    lecturerStage.setTitle("Courseware | Lecturers")
    lecturerLoader.load[Parent]()
    lecturerStage.setScene(new Scene(lecturerLoader.getRoot[Parent]))

    lecturerStage.show()
  }

}


class CWStudentController extends Initializable {

  import JfxUtils._

  type StudentTC[T] = TableColumn[MutableStudent, T]

  @FXML var tableView: TableView[MutableStudent] = _
  @FXML var C1: StudentTC[Int] = _
  @FXML var C2: StudentTC[String] = _
  @FXML var C3: StudentTC[String] = _
  @FXML var C4: StudentTC[String] = _
  @FXML var C5: StudentTC[String] = _
  @FXML var C6: StudentTC[String] = _
  @FXML var C7: StudentTC[String] = _
  @FXML var C8: StudentTC[Int] = _


  def initTableViewColumn[T]: (TableColumn[MutableStudent, T], (MutableStudent) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableStudent, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    val mutableStudents = mkObservableList(for (student <- StudentData.asMap) yield MutableStudent(student._2))
    tableView.setItems(mutableStudents)

    initTableViewColumn[Int](C1, _.p_ID)
    initTableViewColumn[String](C2, _.p_firstname)
    initTableViewColumn[String](C3, _.p_lastname)
    initTableViewColumn[String](C4, _.p_email)
    initTableViewColumn[String](C5, _.p_birthday)
    initTableViewColumn[String](C6, _.p_telnr)
    initTableViewColumn[String](C7, _.p_githubUsername)
    initTableViewColumn[Int](C8, _.p_group)
  }

}

class CWLecturerController extends Initializable {

  import JfxUtils._

  type LecturerTC[T] = TableColumn[MutableLecturer, T]

  @FXML var tableView: TableView[MutableLecturer] = _
  @FXML var C1: LecturerTC[Int] = _
  @FXML var C2: LecturerTC[String] = _
  @FXML var C3: LecturerTC[String] = _
  @FXML var C4: LecturerTC[String] = _

  def initTableViewColumn[T]: (TableColumn[MutableLecturer, T], (MutableLecturer) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableLecturer, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    val mutableLecturers = mkObservableList(for (lecturer <- LecturerData.asMap) yield MutableLecturer(lecturer._2))
    tableView.setItems(mutableLecturers)

    initTableViewColumn[Int](C1, _.p_ID)
    initTableViewColumn[String](C2, _.p_firstname)
    initTableViewColumn[String](C3, _.p_lastname)
    initTableViewColumn[String](C4, _.p_title)
  }

}