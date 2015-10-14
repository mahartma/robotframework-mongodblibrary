Robotframework-Mongodblibrary
=============================

The **Robot Framework MongoDB Library** is a library for testing MongoDB applications with the Robotframework. 
Please see the [Keyword-Documentation](http://mahartma.github.com/robotframework-mongodblibrary/de.codecentric.robot.mongodblibrary.keywords.MongodbLibrary.html) for more information about the provided keywords.

The library is written in Java using the MongoDB Java Driver.

Sample test:

```
*** Settings ***
Library   de.codecentric.robot.mongodblibrary.keywords.MongodbLibrary
Suite Setup  Startup Embedded  2.4.4
Suite TearDown  Shutdown Embedded
Test Setup  Setup MongoDB

*** Test Cases ***
should insert given document
	Insert Document  myCollection  {say : 'Hello MongoDb!'}
	Collection Should Exist  myCollection
	Document Should Exist  myCollection  {say : 'Hello MongoDb!'}
should insert data from file
	Import Documents  myCollection  sample/data.json
	Collection Should Exist  myCollection
	Document Should Exist  myCollection  {name : 'Mike'}
should insert data from file (row-seperated)
	Import Documents Row Seperated  myCollection  sample/dataMultipleRows.json
	Collection Should Exist  myCollection
	Document Should Exist  myCollection  {name : 'Mike'}
	Document Should Exist  myCollection  {name : 'Tom'}
	Document Should Exist  myCollection  {name : 'Eric'}

*** Keywords ***
Setup MongoDB
	Connect To Server  localhost  27017  robotdb1
	Drop Database  robotdb1
```

Dependencies
------------
- [Robotframework >= 2.7.5 (with Jython)](http://code.google.com/p/robotframework/downloads/list)
- [MongoDB Java Driver >= 2.10.0](http://central.maven.org/maven2/org/mongodb/mongo-java-driver)
- [apache-commons-io 1.3.2](http://search.maven.org/remotecontent?filepath=org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.jar)
- [junit 4.10](http://search.maven.org/remotecontent?filepath=junit/junit/4.10/junit-4.10.jar)
- [MongoDB Server >= 2.2.1](http://www.mongodb.org/downloads)
- [Embedded MongoDB >= 1.31](https://github.com/flapdoodle-oss/embedmongo.flapdoodle.de)

Install
-------
- download [robotframework-mongodblibrary-0.2.1-with-dependencies.jar](http://mahartma.github.com/robotframework-mongodblibrary/robotframework-mongodblibrary-0.2.1-with-dependencies.jar)
- start the mongoDB daemon or use the embedded keywords ( _Startup Embedded_, _Shutdown Embedded_ )
- add **robotframework-mongodblibrary-0.2.1-with-dependencies.jar** to the CLASSPATH (see runSample.cmd)
- start the Robot-Tests
- this can also be done by a gradle task:
```groovy
  configurations { 
      robot
  }

  dependencies {
      robot files("libs/robotframework-mongodblibrary-0.2.1-with-dependencies.jar")
  }

  task(type : JavaExec, 'run tests') {
      classpath = configurations.robot
      main = 'org.robotframework.RobotFramework'
      args = [
          '-d',
          'build',
          'sample/mongodblibrarySample.txt'
      ]
  }
```

Remote-Library
--------------
- the library also contains the Remote-Server from the Robotframework for executing the keywords on a dedicated JVM (see [Robot-Remote-Library](http://code.google.com/p/robotframework/wiki/RemoteLibrary))
- it's very useful when you want to use python in the main suite instead of jython
- the server can be started with **java -jar build/libs/robotframework-mongodblibrary-0.2.1-with-dependencies.jar**
- see the example below:

```
*** Settings ***
Library   Remote    http://localhost:8270
Suite Setup  Startup Embedded  2.4.4
Suite TearDown  Shutdown Embedded
Test Setup  Setup MongoDB

*** Test Cases ***
should insert given document
	Insert Document  myCollection  {say : 'Hello MongoDb!'}
	Collection Should Exist  myCollection
	Document Should Exist  myCollection  {say : 'Hello MongoDb!'}
should insert data from file
	Import Documents  myCollection  sample/data.json
	Collection Should Exist  myCollection
	Document Should Exist  myCollection  {name : 'Mike'}
should insert data from file (row-seperated)
	Import Documents Row Seperated  myCollection  sample/dataMultipleRows.json
	Collection Should Exist  myCollection
	Document Should Exist  myCollection  {name : 'Mike'}
	Document Should Exist  myCollection  {name : 'Tom'}
	Document Should Exist  myCollection  {name : 'Eric'}

*** Keywords ***
Setup MongoDB
	Connect To Server  localhost  27017  robotdb1
	Drop Database  robotdb1
```
