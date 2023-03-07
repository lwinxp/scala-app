# IT5100A 2023 - Project

## Problem Statement

The objective of this project is to evolve the web app presented in Lecture 7
with features that make it even more likely to be useful to its users.

## Prepare your environment

The first task, however, is to adapt the files given in the lecture for
the current project setup. As you may have noticed, there are no
Scala files in this project. You will have to paste in the files discussed
in the lecture and adapt / tweak them until you have an app that replicates
the behavior of the one presented in the lecture.

The provided files are as follows:
  * `project/plugins.sbt` - you are not allowed to modify this file
  * `build.sbt` - the only modifications allowed are adding/deleting dependency libraries
  * `static/index.html` - the only modifications allowed are in the style section, or possibly adding fonts
  * `src/main/resources/secret.conf` - you are not allowed to modify this file

You are expected to take the files `MyApp.scala`, `DbDetails.scala`, and `Main.scala`,
place them in their proper folders, and tweak them until you obtain a working app,
identical to the one presented in class.

Once you have reached this milestone, you can proceed to the next step:

## Enhance the current app

There are many ways to enhance this app by adding new features that make it
more usable. We leave it up to you to choose these features, and the
number of them you would like to implement. We endeavor to make the
following suggestions:

  * Add multi-user capability
       * Add login with plain text password, or
       * Add login with encrypted password,
       * Implement OAuth standard where, after authentication has been 
         done, a token key is used to continue authentication during the session.
  * Add a checkbox that can be checked or unchecked 
    (useful for keeping track of correlated tasks)
       * Once a checkbox has been checked, cross the item with a horizontal line.
  * Implement reordering.
  * Implement priorities (1 to 3) and color the items differently by priority.
  * Implement UNDO/REDO.
  * Implement "Search" by keyword.
  * Implement filtering by keyword or priority.
  * Add deadline for each item.
       * Highlight items whose deadline is exceeded.
       * Highlight items close to the deadline in a "warning" color.

You do not have to implement all these features. Choose a reasonable set.
The expected effort here is 12-16h of work overall.

As usual, mark your submission by tagging the commit you want marked with "v1.0"

## Additional notes added by student
* build.sbt was modified in line 23 to add a dot for 
  * ./$sjsName
* MyApp.scala was modified for
  * def staticFileRoutes1() file path to suit this project structure
  * def staticFileRoutes2() file path to suit this project structure
* To ensure proper behaviour, it is best to
  * docker to create and use new DB
    * (if not pulled before) docker pull centos/postgresql-12-centos8 
    * docker run -d --name myapp -e POSTGRESQL_USER=scalauser -e POSTGRESQL_PASSWORD=pwd123 -e POSTGRESQL_DATABASE=myapp -p 5432:5432 centos/postgresql-12-centos8
    * docker exec -it myapp /bin/bash
    * psql -d myapp -U scalauser
    * CREATE TABLE todo(item varchar(1000));
    * \d todo
  * reload all sbt projects 
  * sbt shell run sjs/fastOptJS 
  * run MyApp.scala 
  * sbt shell run sjs/fastOptJS 
  * open URL manually in browser http://localhost:8000/static/index.html
    * (do not right-click index.html and open with browser in intellij)
