del %~dp0%doc\libdoc\*.html
del %~dp0%doc\libdoc\*.xml
java -cp robot-bin/robotframework-2.7.5.jar;%JAVA_HOME%\lib\tools.jar org.python.util.jython -m robot.libdoc src/main/java/de/codecentric/robot/mongodblibrary/keywords/MongodbLibrary.java doc/libdoc/de.codecentric.robot.mongodblibrary.keywords.MongodbLibrary.html
java -cp robot-bin/robotframework-2.7.5.jar;%JAVA_HOME%\lib\tools.jar org.python.util.jython -m robot.libdoc --format XML src/main/java/de/codecentric/robot/mongodblibrary/keywords/MongodbLibrary.java doc/libdoc/de.codecentric.robot.mongodblibrary.keywords.MongodbLibrary.xml