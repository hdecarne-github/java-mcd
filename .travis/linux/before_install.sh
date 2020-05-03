#!/bin/bash -x

curl -sL https://github.com/shyiko/jabba/raw/master/install.sh | bash -s -- --skip-rc && . ~/.jabba/jabba.sh
jabba ls-remote
jabba install $BUILD_JDK
export JAVA_HOME="$JABBA_HOME/jdk/$BUILD_JDK"
export PATH="$JAVA_HOME/bin:$PATH"
java -Xmx32m -version 
./gradlew -v
