FROM openjdk:17
WORKDIR /app

# Копируем исходный код
COPY src/ ./src/

# Компилируем Java
RUN javac src/Main.java

# Запускаем сервер
CMD ["java", "-cp", "src", "Main"]