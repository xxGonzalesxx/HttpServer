FROM openjdk:17
WORKDIR /app
COPY . .
RUN javac src/Main.java
CMD ["java", "-cp", "src", "Main"]
