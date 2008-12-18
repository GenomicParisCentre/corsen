@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
set JARS=
for %%i in (*.jar) do set JARS=!JARS!%%i;
java -Xms64m -Xmx1024m -cp %JARS% fr.ens.transcriptome.corsen.Corsen -batchFile %1
