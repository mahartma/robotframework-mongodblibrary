Robotframework-Mongodblibrary
=============================

The **Robot Framework MongoDB Library** is a library for testing MongoDB applications with the Robotframework. 
Please see the [Keyword-Documentation](http://mahartma.github.com/robotframework-mongodblibrary/de.codecentric.robot.mongodblibrary.keywords.MongodbLibrary.html) for more information about the provided keywords.

The library is written in Java using the MongoDB Java Driver.

Take a look at the sample test: [**sample/mongodblibrarySample.txt**](https://github.com/mahartma/robotframework-mongodblibrary/blob/master/sample/mongodblibrarySample.txt)

Dependencies
------------
- [Robotframework >= 2.7.5 (with Jython)](http://code.google.com/p/robotframework/downloads/list)
- [MongoDB Java Driver >= 2.10.0](http://central.maven.org/maven2/org/mongodb/mongo-java-driver)
- [apache-commons-io 1.3.2](http://search.maven.org/remotecontent?filepath=org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.jar)
- [junit 4.10](http://search.maven.org/remotecontent?filepath=junit/junit/4.10/junit-4.10.jar)
- [MongoDB Server >= 2.2.1](http://www.mongodb.org/downloads)

Install
-------
- download [robotframework-mongodblibrary.jar](http://mahartma.github.com/robotframework-mongodblibrary/robotframework-mongodblibrary-0.1.jar)
- start the mongoDB daemon
- add **robotframework-mongodblibrary.jar** and the **3rd party libs (see dependencies)** to the CLASSPATH
- start the Robot-Tests, this can also be done by a gradle task:
```groovy
  dependencies {
      compile group: 'junit', name: 'junit', version: "4.10"
      compile group: 'org.apache.commons', name: 'commons-io', version: "1.3.2"
      compile group: 'org.mongodb', name: 'mongo-java-driver', version: "2.10.0"
      robot group: 'org.robotframework', name: 'robotframework', version: "2.7.5"
  }

  task(type : JavaExec, 'run tests') {
      classpath = configurations.robot + configurations.compile + sourceSets.main.runtimeClasspath
      main = 'org.robotframework.RobotFramework'
      args = [
          '-d',
          'build',
          'sample/mongodblibrarySample.txt'
      ]
  }
```
