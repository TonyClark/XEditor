@echo off

set XMFHOME=%1%
set VERSION=%2%
set LIB=%XMFHOME%;%XMFHOME%/../../com.ceteva.xmf.machine/bin
set PORT=100
set HEAPSIZE=5000
set MAXJAVAHEAP=-Xmx200m
set LOCALE=-Duser.country=UK

java %LOCALE% %MAXJAVAHEAP% -cp %LIB% xos.OperatingSystem -port %PORT% -initFile %XMFHOME%/Boot/Boot.o -heapSize %HEAPSIZE% -arg home:%XMFHOME% -arg version:%VERSION%
