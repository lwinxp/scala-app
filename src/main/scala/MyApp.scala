/**
 * We are developing a simple web app, completely in Scala. Importantly, the app is a Single Page App (SPA),
 * and interacts completely asynchronously with the back-end.
 * Its objective is to show simple UI element manipulation, as well as
 * interaction with a database.
 * 
 * The app implements a simple TODO list.
 * 
 * This app will be the basis for your project as well.
 */

import com.typesafe.config.{Config, ConfigFactory}

import java.io.InputStreamReader
import java.sql.{Connection, DriverManager, Date}
import java.util.UUID

/** Cask is a web framework similar to Python's Flask. As a framework, it has its own 'main' and does a lot of magic
 *  behind the scenes. */
object MyApp extends cask.MainRoutes:
  /** Allows access to the web server from a different machine */
  override def host: String = "0.0.0.0"
  /** Good to set the port explicitly, and not use port 80 for experiments -- leave encryption to a wrapper such as nginx */
  override def port: Int = sys.env.get("PORT").map(_.toInt).getOrElse(8000) // avoid clash 8080
//  override def port: Int = 8000 // avoid clash 8080

  /** Turn this on during development */
  override def debugMode: Boolean = true

  /** Homepage entry point */
  @cask.get("/") // path for the server, index tranlated to static folder, index.html file
  def index() = cask.Redirect("/static/index.html")

  /** Static file repository, in case we need to serve static files, e.g. index.html */
  @cask.staticFiles("/static/", headers = Seq("Content-Type" -> "text/html")) // set Content-Type to render instead of download
  def staticFileRoutes1() = "/static/" // we're not in week07 as working directory, working directory is higher, need to prepend week07

  /** Static file repository for Javascript files (the target compilation folder for our ScalaJS project) */
  @cask.staticFiles("/js/", headers = Seq("Content-Type" -> "text/javascript")) // whenever get /js/ will pickup the main.js file and generate
  def staticFileRoutes2() = "/sjs/target/scala-3.2.2/sjs-fastopt/" // this is where main.js is generated. compiled scala code to main.js

  /** End-point for TODO item submission */
  @cask.post("/submit")
  def submit(request: cask.Request): Unit = // there is request but no response, just writing to DB next line
    val todoInsertStmt = DbDetails.conn.prepareStatement("""insert into todo(item) values(?)""") // connection in file DbDetails.scala
    todoInsertStmt.setString(1, request.text()) // counting from left to right, ? is first position, starting from 1 not 0 index
    todoInsertStmt.execute() // say what to fill in for placeholder, prevent sql injection attacks

  /** End-point for reading all TODOs, for updating the item panel after every modification */
  @cask.get("/readtodos") // comes from frontend MainJS.scala line 76 fetchContent
  def readtodos(): String =
    val readStmt = DbDetails.conn.prepareStatement("""select id, item, completed, priority, deadline from todo order by serial asc""") // item is the only thing in the table, item is a string, otherwise none
    val qResults = readStmt.executeQuery() // query results is iterator, but not standard iterator
    val todos = Iterator.continually { // use iterator continually
      if (qResults.next()) {
        Option(
          qResults.getString("id"),
          qResults.getString("item"),
          qResults.getBoolean("completed"),
          qResults.getString("priority"),
          qResults.getString("deadline")
        )
      } else {
        None
      }
    }.takeWhile(_.nonEmpty).flatten // iterator of options can be flattened into iterator of strings, convert into JSON array, but cannot send array, send object

    val result =
      if todos.isEmpty
      then "[]"
      else todos.mkString("[\"","\",\"","\"]") // do not want array with empty string, indicates the left bound, separator, right bound
    val wrapped = s"""{ "items" : $result }""" // artificially create JSON object called items with curly braces
    println(wrapped)
    wrapped // when we have a get method, there is a response, and here we say it is a string, this string response goes back to MainJS.scala line 81 fetchContent text <- response.text()
    // the entire refresh of the item panel as a JSON object
  /** End-point for deleting one TODO item */
  @cask.delete("/delete")
  def delete(request: cask.Request): Unit =
    val idVal = UUID.fromString(request.text().trim())
    val delStmt = DbDetails.conn.prepareStatement("""delete from todo where id = ?""")
    delStmt.setObject(1, idVal)
    delStmt.execute() // no result to send back, just 200 response ok, backend send to browser, or error code and message

  @cask.put("/update")
  def update(request: cask.Request): Unit =
    val requestProcessed = request.text().stripPrefix("{").stripSuffix("}").split(",")
    val idVal = UUID.fromString(requestProcessed(0).trim())
    val itemVal = requestProcessed(1).trim()
    val completedVal = requestProcessed(2).trim().toBoolean
    val priorityVal = requestProcessed(3).trim()
    val dateVal = if (requestProcessed(4).trim().isEmpty) null else Date.valueOf(requestProcessed(4).trim())
    val updateStmt = DbDetails.conn.prepareStatement("""update todo set item=?, completed=?, priority=?, deadline=? where id = ?""")
    updateStmt.setString(1, itemVal)
    updateStmt.setBoolean(2, completedVal)
    updateStmt.setString(3, priorityVal)
    updateStmt.setObject(4, dateVal)
    updateStmt.setObject(5, idVal)

    updateStmt.execute() // no result to send back, just 200 response ok, backend send to browser, or error code and message

  /** This starts the Cask framework */
  initialize()