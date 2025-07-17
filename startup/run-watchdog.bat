@echo off
set IP_ADDR=172.16.1.87
set PORT=7000
set DNS_CALLBACK_PORT=9970
set DNS_SOCKET=172.16.1.87:9000
set TIMESTAMP_PORT=9971
set SERVICE=watchdog



echo Running %JAR_PATH%...
java -jar "watchdog-service.jar"

pause