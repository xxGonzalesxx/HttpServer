# Переходим в папку проекта
cd /c/Work/Http_Server_First/

# Собираем Docker образ
docker build -t portfolio-server .

# Запускаем контейнер
docker run -p 8080:8080 portfolio-server