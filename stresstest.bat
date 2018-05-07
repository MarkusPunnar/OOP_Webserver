@echo off
setlocal enableextensions enabledelayedexpansion
:while1
	curl "localhost:1337"
	curl -v "localhost:1337/Readme.md"
	goto :while1
endlocal