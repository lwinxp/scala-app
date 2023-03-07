import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.*
import org.scalajs.dom.html.*

import scala.scalajs.js
import scala.scalajs.js._
import scala.scalajs.js.Thenable.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global

object MainJS:
  @JSExportTopLevel("main")
  def main() =
    val workspace: Div = document.getElementById("workspace").asInstanceOf[Div] // retrieve the workspace
    workspace.appendChild(createInputArea())                                    // add the input area to the workspace
    val panel = document.createElement("div").asInstanceOf[Div]                 // create the panel containing the todo items
    panel.id = "panel"                                                          // name it so it can be accessed later
    panel.style.background = "lightyellow"                                      // give it a distinguishing color so that it is easily identifiable visually
    workspace.appendChild(panel)                                                // add the panel to the workspace, below the input area
    updatePanel()                                                               // populate the panel with the current todo items

/** Create buttons with a suggestive animation when clicked on */
def createButton(text: String, action: UIEvent => Unit): Button =
  val button = document.createElement("button").asInstanceOf[Button] // fix as type button
  button.innerText = text // mutable, scalaJS faithful to JS mutability
  button.addEventListener("click", action)
  button

/** Create read-only text fields */
def createROText(text: String): Div =
  val div = document.createElement("div").asInstanceOf[Div]
  div.className = "divreadonly"
  div.style.display = "inline-block"  // stack them left-to-right instead of top-down
  div.innerText = text
  div

/** Create read-write fields (i.e. input fields) */
def createRWText(initialText: String): Div =
  val div = document.createElement("div").asInstanceOf[Div]
  div.className = "divreadwrite"
  div.style.display = "inline-block"     // stack them left-to-right instead of top-down
  div.contentEditable = "true"           // the attribute making fields editable.
  div.innerText = initialText
  div

/** Create the input area, containing the editable field allowing the user to add todo items.
 * Adding items is done by pressing a 'Submit' button */
def createInputArea(): Div =
  val enclosure = document.createElement("div").asInstanceOf[Div]  // Enclosure to contain all the elements for this area
  val label = createROText("Input TODO item:")                     // Label inviting the user to add TODO items
  val item = createRWText("")                                      // Input field where the todo item is to be added
  item.id = "todoitem"                                             // Name it so it can be access later from a global scope
  val submit = createButton("Submit", submitAction)                // Create the submit button, with a submit action to be executed when clicked
  Seq(label, item, submit).foreach(enclosure.appendChild)          // Add all the components to the enclosure
  enclosure.style.background = "lightblue"                         // Use a distinct color to make this area stand out visually
  enclosure                                                        // Return the enclosure, which is to be added to the workspace

/** Action to be executed when the submit button is clicked. */
def submitAction = (_: UIEvent) =>
  val submitText = document.getElementById("todoitem").innerText  // the input field must be non-empty for a successful submission
  if submitText != null && submitText.nonEmpty
  then
    fetch("/submit", new RequestInit {                   // Async interaction with the back-end to submit a TODO item
      method = HttpMethod.POST  // POST goes to backend in MyApp.scala line 40, @cask.post("/submit")
      body = submitText
    })
    document.getElementById("todoitem").innerText = ""   // Upon successful submission, empty the submission field to avoid repeat submissions of the same thing
    window.setTimeout(updatePanel,100)                   // After a timeout that allows the current DB transaction to complete, refresh the panel so that the newly submitted item becomes visible.
  else
    ()
  ()

/** We update the panel on every change to the DB */
def updatePanel = () => fetchContent(contentAction) // object to be invoked later, working with promises and future which are monads, send action inside future

/** Asynchronously fetch all the todos, so that refreshing the panel becomes possible */
def fetchContent(contentAction: String => Unit) = // goes to backend MyApp.scala line 47, @cask.get("/readtodos")
  val contentFuture = for
    response <- fetch("/readtodos", new RequestInit { // fetch returns a promise, which is a Monad, so we deal with it in a 'for'
      method = HttpMethod.GET
    })
    text <- response.text()  // response is a promise in turn, and 'text' is a future. // this text is response from backend
  yield text
  for // change the 'for', to handle the future
    content <- contentFuture // will not get rid of the future, must send the action inside the future, inside the yield, send content inside contentAction, send inside the monad
  yield {
    contentAction(content)  // we can't take the content out of the Mondad, so we put the processing function 'contentAction' inside the Monad.
  } // this returns no result, contentAction refreshes the panel

/**
 * Update the panel by creating a visual element for every item fetched from the database. Add a 'Delete' btn to each items container.
 */
def contentAction(content: String): Unit = // string inside json array
  val payload = JSON.parse(content).selectDynamic("items").asInstanceOf[Array[String]] // extract the content string, recall in MyApp.scala backend line 58, the object is wrapped. selectDynamic extracts the items field, to get the array of strings, can see the object
  /** Put the visual elements containing the items into a list that will replace the current panel elements */
  val elements = payload.map { text => // every line is going to be the container, repeatedly map
    val itemText = createROText(text) // inside the container only has readonly text and button
    itemText.style.display = "inline-block" // items side by side
    itemText.style.minWidth = "675px" // buttons aligned
    val deleteBtn = createButton("Delete", deleteAction(text))  // create delete button that is specific to the item // the button is specialized to delete only the current item
    itemText.style.display = "inline-block"  // stack from left to right, instead of top-bottom                                                                                                                  sssssssssssssss
    val container = document.createElement("div").asInstanceOf[Div] // each item + delete bottom goes into a separate container, whose componets stack horizontally
    List(itemText, deleteBtn).foreach(container.appendChild) // add both the item and button to the container, hence append child to container
    container // return container from processing of individual item text
  }.to(Seq) // return a list item containers
  val panel = document.getElementById("panel").asInstanceOf[Div] // get access to panel by searching
  panel.replaceChildren(elements*)  // replace old items by new items, pass to children, create variadic arguments, panel method to replace existing children with new children, hence refresh

/** Delete action to go into the Delete button for every item */
def deleteAction(text: String): UIEvent => Unit = _ => // does not return result, returns a function, UIEvent which returns Unit nothing
  fetch("/delete", new RequestInit { // Async request to the backend to delete the item from the DB
    method = HttpMethod.DELETE // delete method as the path or route to backend. goes to MyApp.scala line 63, @cask.delete("/delete")
    body = text // the text that represents the item to be deleted from DB, if no response means successful
  })
  window.setTimeout(updatePanel,100) // After a small timeout of 100ms to allow the DB transaction to complete, refresh the panel to allow the changes to be reflected.
  ()