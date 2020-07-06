#!/bin/sh
export JAVA_HOME=/usr/lib/jvm/old
export JAVAFX_MODS=/usr/lib/jvm/javafx-jmods-14.0.1
echo "Removing previous builds..."
sudo rm -fr jre
echo "Linking..."
$JAVA_HOME/bin/jlink --module-path $JAVAFX_MODS:bin/virtualdm --add-modules=virtualdm --output jre
echo "Running..."
jre/bin/java -m virtualdm/virtualdm.Game
