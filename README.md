# WeatherReport
Weather Report

## Below are the instructions to deloy the weather.war file.

 * Download apache-tomcat-9.0.20 and un zip it.

* set the environment variable  CATALINA_HOME as  C:\apache-tomcat-9.0.20

* set the path variable Path=%CATALINA_HOME%\bin

* set "JRE_HOME=C:\jre1.8" in setenv.bat under  C:\apache-tomcat-9.0.20\bin folder

* Go to C:\apache-tomcat-9.0.20\webapps\myapp and then unzip the war or use the below commond to unzip the war file.

  jar -xvf weather.war (Please give the exact path where weather.war is downloaded)

* Go to \apache-tomcat-9.0.20\bin\startup.bat

* Open the  URL: http://localhost:8080/myapp/mainPage.jsp
