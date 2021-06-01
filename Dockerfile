FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8
EXPOSE 9090
WORKDIR /TicTacToe-3D-4x4
ADD . /TicTacToe-3D-4x4
CMD sbt run
