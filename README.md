# Дипломная работа “Облачное хранилище”

## Описание проекта
Приложение представляет собой REST-сервис и предназначено для быстрой и удобной работы пользователя 
с файлами: загрузки файлов на сервер, скачивание файлов с сервера, переименование и удаление файлов, 
а также получение списка файлов, хранящихся на сервере в настоящий момент. 

Данные пользователей (логин/пароль), а также данные о файлах, находящихся в облачном хранилище, 
заносятся в соответствующие базы данных (СУБД PostgreSQL).

Все запросы к сервису авторизованы.
Заранее подготовленное веб-приложение (FRONT) подключается к разработанному сервису без доработок, 
а также использует функционал FRONT для авторизации, загрузки и вывода списка файлов пользователя.

Изначально FRONT доступен на порту 8080, BACKEND - на порту 5050.

## Пользователи:
**USERNAME:** user_one **PASSWORD:** user_one
**USERNAME:** user_two **PASSWORD:** user_two

## Описание реализации:
- Приложение разработано с использованием Spring Boot;
- Использован сборщик пакетов Maven;
- Использована база данных PostgreSQL;
- Для запуска используется docker, docker-compose;
- Код размещен на github;
- Код покрыт unit тестами с использованием mockito;
- Добавлены интеграционные тесты с использованием testcontainers;
- Информация о пользователях сервиса и файлах хранится в базе данных.

## ВАЖНО!!!
ИНТЕГРАЦИОННЫЙ ТЕСТ (src/test/java/ru/netology/NetologyCloudStorageApplicationTests.java) и
JPA ТЕСТЫ (src/test/java/ru/netology/repository) (StorageFileRepositoryTest.java, UserRepositoryTest.java)
закомментированы, поскольку с ними не собирается docker-контейнер.
После сборки docker-контейнера необходимо раскомментировать и запустить тесты.

### Запуск FRONT:
1. Установить nodejs (версия не ниже 19.7.0) на компьютер, [следуя инструкции](https://nodejs.org/ru/download/current).
2. Скачать [FRONT](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend) 
(JavaScript);
3. Перейти в папку FRONT приложения и все команды для запуска выполнять из нее;
4. Следуя описанию README.md FRONT проекта запустить nodejs приложение (npm install, npm run serve);
5. В файле .env FRONT (находится в корне проекта) приложения нужно изменить url до backend,
например: `VUE_APP_BASE_URL=http://localhost:8080`.
6. Нужно указать корневой url вашего backend, к нему frontend будет добавлять все пути согласно
   спецификации.
   Для `VUE_APP_BASE_URL=http://localhost:8080` при выполнении логина frontend вызовет
   `http://localhost:8080/login`
7. Для запуска FRONT приложения с расширенным логированием использовать команду: `npm run serve`.
8. Изменённый url сохранится для следующих запусков.

### Запуск BACKEND:
1. Скачать данный проект, выполнить `maven -> clean -> package`;
2. Запустить `docker-compose.yml`.
Автоматически создадутся все необходимые в базе данных таблицы (с двумя стартовыми пользователями в таблице users).

BACKEND можно запустить и через main метод в классе NetologyCloudStorageApplication.java,
не используя docker-контейнер. Базу данных же все равно запускать через docker-контейнер.

Для этого на п.2 "Запуск BACKEND" вместо запуска всего `docker-compose.yml` следует запустить
только `docker-compose.yml -> database`, а само приложение через main метод.

## Работа приложения

### 1. Аутентификация
`http://localhost:5050/login`
`POST`
`Content-Type: application/json`

#### *пример:*
`{"login":"user_one",
"password":"user_one"
}`

#### *Результаты обработки:*
- предоставление доступа к приложению, успешный ответ с кодом "200" и "auth-token" - токен доступа, 
сформированный для пользователя, по которому будет происходить авторизация к дальнейшим запросам
- неуспешный ответ с кодом "400"(неверные параметры аутентификации)
- неуспешный ответ с кодом "500"(ошибка приложения)

### 2. Загрузка файла
`http://localhost:5050/file?filename=example.jpg`
`POST`
`Content-Type: multipart/form-data`

#### *Результаты обработки:*
- загрузка файлав хранилище, успешный ответ с кодом "200"
- неуспешный ответ с кодом "500"(ошибка приложения)

### 3. Удаление файла
`http://localhost:5050/file?filename=example.jpg`
`DELETE`

#### *Результаты обработки:*
- удаление файла из хранилища, успешный ответ с кодом "200"
- неуспешный ответ с кодом "400"(неверные параметры)
- неуспешный ответ с кодом "500"(ошибка приложения)

### 4. Изменение имени файла
`http://localhost:5050/file?filename=example.jpg`
`PUT`
`Content-Type: application/json`

#### *пример:*
`{"filename":"new_filename.jpg"}`

#### *Результаты обработки:*
- изменение имени указанного файла, успешный ответ с кодом "200"
- неуспешный ответ с кодом "400"(неверные параметры)
- неуспешный ответ с кодом "500"(ошибка приложения)

### 5. Скачать файл
`http://localhost:5050/file?filename=example.jpg`
`GET`

#### *Результаты обработки:*
multipart/form-data контент, успешный ответ с кодом "200"
неуспешный ответ с кодом "400"(неверные параметры)
неуспешный ответ с кодом "500"(ошибка приложения)


### 6. Получить список файлов
`http://localhost:5050/list?limit=5`
`GET`
#### *Результаты обработки:
- список файлов пользователя, успешный ответ с кодом "200"
- неуспешный ответ с кодом "400"(неверные параметры)
- неуспешный ответ с кодом "500"(ошибка приложения)
