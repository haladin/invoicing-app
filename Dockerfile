FROM openjdk:11-jre-slim-buster
#
# Identify the maintainer of an image
LABEL maintainer="myname@somecompany.com"
#
# Update the image to the latest packages
RUN apt-get update && apt-get upgrade -y

COPY ./backend/build/libs/Invoicing-0.0.1-SNAPSHOT.jar /app/Invoicing-0.0.1-SNAPSHOT.jar
#

EXPOSE 8080
CMD ["java", "-jar", "/app/Invoicing-0.0.1-SNAPSHOT.jar", "com.robobender.invoicing.InvoicingApplication"]