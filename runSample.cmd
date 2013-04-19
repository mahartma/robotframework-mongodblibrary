REM ======= LOCAL =======
java -jar robot-bin/robotframework-2.7.5.jar -P build/libs/robotframework-mongodblibrary-0.1-with-dependencies.jar -d target sample/mongodblibrarySample.txt
REM ======= REMOTE =======
java -jar robot-bin/robotframework-2.7.5.jar -d target sample/remoteMongodblibrarySample.txt