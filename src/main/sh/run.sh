#!/bin/bash
java -Xms64m -Xmx1024m -cp corsen-0.37a.jar:commons-cli-1.0.jar:commons-math-1.1.jar:jsr223-api-1.0.jar:phobos-rhino-0.5.6-SNAPSHOT.jar fr.ens.transcriptome.corsen.Corsen -batchFile $1
