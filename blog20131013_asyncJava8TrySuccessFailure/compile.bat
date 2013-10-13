@echo off

set JAVA_HOME="c:\Program Files\Java\jdk1.8.0\bin"

dir /B  /S src\*.java > filelist.txt

echo Compiling, using Java from here: %JAVA_HOME%

%JAVA_HOME%\javac -sourcepath src -d bin -Xlint:unchecked @filelist.txt  

