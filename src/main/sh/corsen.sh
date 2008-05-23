#!/bin/bash
me=$(dirname $0)

if ! java -version 2>&1 | grep -q "1\.[5-9]"
then
    echo "Qt Jambi requires Java version 1.5.0 or higher to be preinstalled"
    echo "to work. If Java is installed then make sure that the 'java' executable"
    echo "is available in the PATH environment."
    exit 1
fi

OS=`uname`
CLASSPATH="$me/lib/commons-cli-1.0.jar:$me/lib/$OS/jogl.jar:$me/lib/$OS/qtjambi.jar:$me/classes"
LIBRARY_PATH="$me/lib/$OS:$me/lib/$OS/lib"

case "$OS" in
  Linux) LD_LIBRARY_PATH=$LIBRARY_PATH:$LD_LIBRARY_PATH  java -cp $CLASSPATH fr.ens.transcriptome.corsen.Corsen $* ;;
  Darwin) DYLD_LIBRARY_PATH=$LIBRARY_PATH:$DYLD_LIBRARY_PATH java -XstartOnFirstThread -cp $CLASSPATH fr.ens.transcriptome.corsen.Corsen $* ;;
  *)     echo "Corsen does not support your Operating system";;
esac

#PATH=$me/bin:$PATH DYLD_LIBRARY_PATH=$me/lib:$DYLD_LIBRARY_PATH java -XstartOnFirstThread -cp $me/qtjambi.jar:$me
#LD_LIBRARY_PATH=$LIBRARY_PATH:$LD_LIBRARY_PATH  java -cp $CLASSPATH fr.ens.transcriptome.corsen.Corsen
