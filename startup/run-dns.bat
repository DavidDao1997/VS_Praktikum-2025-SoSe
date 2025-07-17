@echo off
set LOG_LEVEL=DEBUG
set IP_ADDR=172.16.1.87
set PORT=9000

echo Running %JAR_PATH%...
java -jar "dns-service.jar"

pause