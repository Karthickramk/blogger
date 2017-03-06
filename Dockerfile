#docker build -f Dockerfile -t blog:v1
FROM debian:jessie
MAINTAINER YOUR NAME @ Cisco CMAD program

# Install dependencies
RUN \
apt-get update && \
apt-get install -y curl wget software-properties-common

# Install JDK 8 - TODO change the name of the gz
ADD jdk-8u112-linux-x64.tar.gz .
ADD blogger-0.0.1-SNAPSHOT-fat.jar .

RUN \
mkdir /opt/jdk1.8.0_112 && \
mv ./jdk1.8.0_112 /opt && \
update-alternatives --install "/usr/bin/java" "java" "/opt/jdk1.8.0_112/bin/java" 1 && \
update-alternatives --install "/usr/bin/javac" "javac" "/opt/jdk1.8.0_112/bin/javac" 1

# Define commonly used JAVA_HOME variable - TODO update the proper JDK name
ENV JAVA_HOME /opt/jdk1.8.0_112

#Adding the JAR file
ADD blogger-0.0.1-SNAPSHOT-fat.jar /opt

# Launch your app using the sample below - TODO
CMD ["java -jar /opt/blogger-0.0.1-SNAPSHOT-fat.jar", "run"]