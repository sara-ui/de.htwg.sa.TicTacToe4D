FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8
EXPOSE 9090
WORKDIR /CheckWin
ADD . /CheckWin
RUN apt-get update -y
RUN apt-get install -y --no-install-recommends openjfx
CMD sbt run