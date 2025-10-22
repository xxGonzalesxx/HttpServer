FROM openjdk:17

WORKDIR /app

# Копируем все файлы
COPY . .

# Компилируем
RUN javac src/Main.java

# Запускаем
CMD ["java", "-cp", "src", "Main"]
