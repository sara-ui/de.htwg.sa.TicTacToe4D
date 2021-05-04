FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8
EXPOSE 9090
WORKDIR /TicTacToe-3D-4x4
ADD . /TicTacToe-3D-4x4
RUN apt-get update -y
RUN apt-get install -y --no-install-recommends openjfx
RUN apt-get install -y --no-install-recommends software-properties-common
RUN add-apt-repository -y ppa:openjdk-r/ppa
RUN apt-get install -y openjdk-8-jdk
RUN apt-get install -y openjdk-8-jre
RUN update-alternatives --config java
RUN update-alternatives --config javac
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
CMD sbt run
