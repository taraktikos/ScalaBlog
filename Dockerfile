FROM ubuntu

# basic stuff
RUN apt-get install -y git curl
RUN apt-get update

# install java
RUN apt-get install -y openjdk-8-jdk

# install scala
RUN curl -o /tmp/scala-2.11.7.tgz http://downloads.typesafe.com/scala/2.11.7/scala-2.11.7.tgz
RUN tar xzf /tmp/scala-2.11.7.tgz -C /usr/share/
RUN ln -s /usr/share/scala-2.11.7 /usr/share/scala

# symlink scala binary to /usr/bin
RUN for i in scala scalc fsc scaladoc scalap; do ln -s /usr/share/scala/bin/${i} /usr/bin/${i}; done

# install sbt
RUN curl -o /tmp/sbt-0.13.9.deb  https://dl.bintray.com/sbt/debian/sbt-0.13.9.deb
RUN dpkg -i /tmp/sbt-0.13.9.deb

RUN cd /opt/ ; git clone https://github.com/taraktikos/ScalaBlog.git
RUN cd /opt/ScalaBlog ; sbt compile

ENTRYPOINT cd /opt/scala-play-hello ; sbt "run 9901"
