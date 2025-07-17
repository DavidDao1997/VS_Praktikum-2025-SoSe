@echo off
set IP_ADDR=172.16.1.87
set LOG_LEVEL=WARNING
set DNS_SOCKET=172.16.1.87:9000
set PORT=6000
set DNS_CALLBACK_PORT=6001



echo Running %JAR_PATH%...
java -jar "view.jar"

pause