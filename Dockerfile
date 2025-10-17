FROM openjdk:17
WORKDIR /app
COPY src/ ./src/
RUN javac src/Main.java
CMD ["java", "-cp", "src", "Main"]