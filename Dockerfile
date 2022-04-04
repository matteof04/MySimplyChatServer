FROM openjdk:11
COPY ./build/install/mysimplychatserver/bin/mysimplychatserver /usr/bin/mysimplychatserver
COPY ./build/install/mysimplychatserver/lib /usr/lib
COPY ./mysimplychatserver.db /mysimplychatserver.db
CMD mysimplychatserver
EXPOSE 8080/tcp