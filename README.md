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

## App functionality
#### 1) Items can be marked completed by checking a checkbox, which strikes out the item content
#### 2) Item priority can be selected for (i) low; (ii) mid; (iii) high by selecting the dropdown box options. The item background will become (i) light green color for low priority; (ii) light yellow color for mid priority; and (iii) pink color for high priority.
#### 3) Item deadlines can be tracked for (i) past deadline; (ii) on the day of deadline; and (iii) within 7 days of deadline by messages that show upon selection of the deadline in the date input box
#### 4) Items can be deleted by clicking the delete button. This delete function has been improved as it now deletes by primary key. This fixes a bug of the lecture implementation where items are deleted by the message content, and items with same content could be deleted mistakenly.
#### 5) Submit button (green) and Delete buttons (red) now have different color. Previously, they were all green.
#### 6) The ordering of items will be preserved according to the sequence in which they were submitted. They will not change randomly as changes are made by the user.
#### 7) Users are not forced to provide too much information when adding an item initially, only the item content is required. Users can update other fields including completed, priority and deadline can be later when they are ready
#### 8) All user input and updates are persisted in the database and will persist with page refresh
#### 9) All user input and updates will be reflected in the UI

## App setup (Compulsory for the app to function)
* build.sbt is modified in line 23 to add a dot for 
  * ./$sjsName
* docker to create and use new PostgreSQL DB
  * (if not pulled before) docker pull centos/postgresql-12-centos8 
  * docker run -d --name myapp -e POSTGRESQL_USER=scalauser -e POSTGRESQL_PASSWORD=pwd123 -e POSTGRESQL_DATABASE=myapp -p 5432:5432 centos/postgresql-12-centos8
  * docker exec -it myapp /bin/bash
* psql commands below must be run in the PostgreSQL DB before running the app
  * psql -d myapp -U postgres
  * (no password required for postgres user)
  * CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
  * \q
  * (note there is a change in user, failure to do so will lead to DB access permission error)
  * psql -d myapp -U scalauser
  * CREATE TABLE todo(
      id UUID DEFAULT uuid_generate_v4 (),
      serial SERIAL NOT NULL,
      item VARCHAR(1000) NOT NULL,
      completed BOOLEAN DEFAULT false,
      priority VARCHAR(8) DEFAULT 1,
      deadline DATE NULL,
      PRIMARY KEY (id)
    );
  * \d
  * select * from todo;
* reload all sbt projects 
* sbt shell run sjs/fastOptJS 
* run MyApp.scala 
* (if there are DB connection errors, do sbt clean for root directory and sjs directory)
* open URL manually in browser http://localhost:8000/static/index.html
  * (do not right-click index.html and open with browser in intellij)
