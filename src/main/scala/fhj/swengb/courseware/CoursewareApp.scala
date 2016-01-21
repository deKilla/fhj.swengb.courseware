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

  def showCourses(): Unit = {
    val courseLoader = new FXMLLoader(getClass.getResource("Course.fxml"))
    val courseStage = new Stage()

    courseStage.setTitle("Courseware | Courses")
    courseLoader.load[Parent]()
    courseStage.setScene(new Scene(courseLoader.getRoot[Parent]))

    courseStage.show()
  }

  def showGroups(): Unit = {
    val groupLoader = new FXMLLoader(getClass.getResource("Group.fxml"))
    val groupStage = new Stage()

    groupStage.setTitle("Courseware | Courses")
    groupLoader.load[Parent]()
    groupStage.setScene(new Scene(groupLoader.getRoot[Parent]))

    groupStage.show()
  }

  def showExams(): Unit = {
    val examLoader = new FXMLLoader(getClass.getResource("Exam.fxml"))
    val examStage = new Stage()

    examStage.setTitle("Courseware | Exams")
    examLoader.load[Parent]()
    examStage.setScene(new Scene(examLoader.getRoot[Parent]))

    examStage.show()
  }

  def showProjects(): Unit = {
    val projectLoader = new FXMLLoader(getClass.getResource("Project.fxml"))
    val projectStage = new Stage()

    projectStage.setTitle("Courseware | Projects")
    projectLoader.load[Parent]()
    projectStage.setScene(new Scene(projectLoader.getRoot[Parent]))

    projectStage.show()
  }

  def showAssignments(): Unit = {
    val assignmentLoader = new FXMLLoader(getClass.getResource("GroupAssignment.fxml"))
    val assignmentStage = new Stage()

    assignmentStage.setTitle("Courseware | Assignments")
    assignmentLoader.load[Parent]()
    assignmentStage.setScene(new Scene(assignmentLoader.getRoot[Parent]))

    assignmentStage.show()
  }

  def showHomeworks(): Unit = {
    val homeworkLoader = new FXMLLoader(getClass.getResource("Homework.fxml"))
    val homeworkStage = new Stage()

    homeworkStage.setTitle("Courseware | Homework")
    homeworkLoader.load[Parent]()
    homeworkStage.setScene(new Scene(homeworkLoader.getRoot[Parent]))

    homeworkStage.show()
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

  val students: Set[Student] = Set(
    Student(1,"Michael","Fuchs","michael.fuchs@edu.fh-joanneum.at","25.10.1987","06642282330","deKilla",1)
   ,Student(2,"Carina","Herzog","carina.herzog@edu.fh-joanneum.at","14.10.1993","asdf","carinaher",1)
  )

  def recreate(): Unit = {for (c <- DB.maybeConnection){Student.reTable(c.createStatement())}}
  def add(): Unit = {for (c <- DB.maybeConnection){students.map(Student.toDB(c)(_))}}
  def menu(): Unit = {}

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

class CWCourseController extends Initializable {

  import JfxUtils._

  type CourseTC[T] = TableColumn[MutableCourse, T]

  @FXML var tableView: TableView[MutableCourse] = _
  @FXML var C1: CourseTC[Int] = _
  @FXML var C2: CourseTC[String] = _
  @FXML var C3: CourseTC[String] = _
  @FXML var C4: CourseTC[Int] = _

  def initTableViewColumn[T]: (TableColumn[MutableCourse, T], (MutableCourse) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableCourse, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    val mutableCourses = mkObservableList(for (course <- CourseData.asMap) yield MutableCourse(course._2))
    tableView.setItems(mutableCourses)

    initTableViewColumn[Int](C1, _.p_ID)
    initTableViewColumn[String](C2, _.p_name)
    initTableViewColumn[String](C3, _.p_branch)
    initTableViewColumn[Int](C4, _.p_year)
  }

}

class CWGroupController extends Initializable {

  import JfxUtils._

  type GroupTC[T] = TableColumn[MutableGroup, T]

  @FXML var tableView: TableView[MutableGroup] = _
  @FXML var C1: GroupTC[Int] = _
  @FXML var C2: GroupTC[String] = _

  def initTableViewColumn[T]: (TableColumn[MutableGroup, T], (MutableGroup) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableGroup, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    val mutableGroups = mkObservableList(for (group <- GroupData.asMap) yield MutableGroup(group._2))
    tableView.setItems(mutableGroups)

    initTableViewColumn[Int](C1, _.p_ID)
    initTableViewColumn[String](C2, _.p_name)
  }

}


class CWExamController extends Initializable {

  import JfxUtils._

  type ExamTC[T] = TableColumn[MutableExam, T]

  @FXML var tableView: TableView[MutableExam] = _
  @FXML var C1: ExamTC[Int] = _
  @FXML var C2: ExamTC[String] = _
  @FXML var C3: ExamTC[Int] = _
  @FXML var C4: ExamTC[String] = _

  def initTableViewColumn[T]: (TableColumn[MutableExam, T], (MutableExam) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableExam, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    val mutableExams = mkObservableList(for (exam <- ExamData.asMap) yield MutableExam(exam._2))
    tableView.setItems(mutableExams)

    initTableViewColumn[Int](C1, _.p_ID)
    initTableViewColumn[String](C2, _.p_course)
    initTableViewColumn[Int](C3, _.p_attempt)
    initTableViewColumn[String](C4, _.p_date)
  }

}

class CWProjectController extends Initializable {

  import JfxUtils._

  type ProjectTC[T] = TableColumn[MutableProject, T]

  @FXML var tableView: TableView[MutableProject] = _
  @FXML var C1: ProjectTC[Int] = _
  @FXML var C2: ProjectTC[String] = _
  @FXML var C3: ProjectTC[String] = _
  @FXML var C4: ProjectTC[String] = _

  def initTableViewColumn[T]: (TableColumn[MutableProject, T], (MutableProject) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableProject, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    val mutableProjects = mkObservableList(for (project <- ProjectData.asMap) yield MutableProject(project._2))
    tableView.setItems(mutableProjects)

    initTableViewColumn[Int](C1, _.p_ID)
    initTableViewColumn[String](C2, _.p_name)
    initTableViewColumn[String](C3, _.p_begindate)
    initTableViewColumn[String](C4, _.p_deadline)
  }

}

class CWAssignmentController extends Initializable {

  import JfxUtils._

  type AssignmentTC[T] = TableColumn[MutableAssignment, T]

  @FXML var tableView: TableView[MutableAssignment] = _
  @FXML var C1: AssignmentTC[Int] = _
  @FXML var C2: AssignmentTC[String] = _
  @FXML var C3: AssignmentTC[String] = _

  def initTableViewColumn[T]: (TableColumn[MutableAssignment, T], (MutableAssignment) => Any) => Unit =
    initTableViewColumnCellValueFactory[MutableAssignment, T]

  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    val mutableAssignments = mkObservableList(for (assignment <- AssignmentData.asMap) yield MutableAssignment(assignment._2))
    tableView.setItems(mutableAssignments)

    initTableViewColumn[Int](C1, _.p_ID)
    initTableViewColumn[String](C2, _.p_name)
    initTableViewColumn[String](C3, _.p_description)

  }

}


