# Scala Web App
## App and DB setup (Compulsory for the app to function)
#### 1. build.sbt is modified in line 23 to add a dot for ./$sjsName
(if not pulled before)
#### 2.  docker pull centos/postgresql-12-centos8 
(no other containers of myapp running)
#### 3.  docker run -d --name myapp -e POSTGRESQL_USER=scalauser -e POSTGRESQL_PASSWORD=pwd123 -e POSTGRESQL_DATABASE=myapp -p 5432:5432 centos/postgresql-12-centos8
#### 4. docker exec -it myapp /bin/bash
(no password required for postgres user)
#### 5. psql -d myapp -U postgres
#### 6. CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
(note there is a change in user, failure to do so will lead to DB access permission error)
#### 7. \q
#### 8. psql -d myapp -U scalauser
#### 9. 
    CREATE TABLE todo(
      id UUID DEFAULT uuid_generate_v4 (),
      serial SERIAL NOT NULL,
      item VARCHAR(1000) NOT NULL,
      completed BOOLEAN DEFAULT false,
      priority VARCHAR(8) DEFAULT 1,
      deadline DATE NULL,
      PRIMARY KEY (id)
    );
#### 10. \d
#### 11. select * from todo;
(using intellij or sbt shell)
#### 12. reload all sbt projects 
#### 13. sbt shell run sjs/fastOptJS 
(if there are DB connection errors, do sbt clean for root directory and sjs directory)
#### 14. run MyApp.scala 
(do not right-click index.html and open with browser in intellij)
#### 15. open URL manually in browser http://localhost:8000/static/index.html

## App functionality
(screenshot of app, app_screenshot.png is available in project root directory)
#### 1. Items can be marked completed by checking a checkbox, which strikes out the item content.
#### 2. Item priority can be selected for (i) low; (ii) mid; (iii) high by selecting the dropdown box options. The item background will become (i) light green color for low priority; (ii) light yellow color for mid priority; and (iii) pink color for high priority.
#### 3. Item deadlines can be tracked for (i) past deadline; (ii) on the day of deadline; and (iii) within 7 days of deadline by messages that show upon selection of the deadline in the date input box.
#### 4. Items can be deleted by clicking the delete button. This delete function has been improved as it now deletes by primary key. This fixes a bug where items are deleted by the message content, and items with same content could be deleted mistakenly.
#### 5. Submit button (green) and Delete buttons (red) now have different color. Previously, they were all green.
#### 6. The ordering of items will be preserved according to the sequence in which they were submitted. This is a deliberate and controlled feature, because without it item sequence will change randomly as changes are made by the user to the items. 
#### 7. Users are not forced to provide too much information when adding an item initially, only the item content is required. Users can update other fields including completed, priority and deadline when they are ready.
#### 8. All user input and updates are persisted in the database and will persist with page refresh.
#### 9. All user input and updates will be reflected in the UI.
#### 10. Appropriate types are used in database and not just stored as string (i) UUID type is used for primary key id; (ii) serial type is used for serial value; (iii) Boolean type is used for checkbox value; (iv) Date type is used for deadline value.

## Enhance the current app

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
