@echo off
cd /d "%~dp0"
mvn -Dtest=ApiCrudTest#put_shouldReturn200AndUpdatedBody test
pause
