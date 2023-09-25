# Diploma [![Build status](https://ci.appveyor.com/api/projects/status/vmnsr0mrguya6tp0?svg=true)](https://ci.appveyor.com/project/LaSFront/diploma)

# Дипломный проект по профессии «Тестировщик»

Тестируемое приложение — веб-сервис, который предлагает купить тур по определённой цене двумя способами:
1. Обычная оплата по дебетовой карте.
2. Уникальная технология: выдача кредита по данным банковской карты.

Приложение не обрабатывает данные по картам, а пересылает их банковским сервисам платежей (Payment Gate) и кредитному сервису (Credit Gate).

Заявлена поддержка двух СУБД: MySQL и PostgreSQL.

## Документация

1. [План автоматизации](https://github.com/LaSFront/Diploma/blob/main/documentation/Plan.md)
2. [Отчет о тестировании](https://github.com/LaSFront/Diploma/blob/main/documentation/Report.md)
3. [Отчет об автоматизации](https://github.com/LaSFront/Diploma/blob/main/documentation/Summury.md)
## Окружение
- Google Chrome.
- IntelliJ IDEA.
- JAVA openjdk (версия - не выше 11).
- Docker Desktop.
- DBeaver.

## Подготовка
1. Запустить на ПК Docker Desktop.
2. Открыть вкладку Terminal IntelliJ IDEA. Клонировать проект: ["Diploma"](https://github.com/LaSFront/Diploma). __Команда__: `git clone https://github.com/LaSFront/Diploma`.
3. Открыть проект в IntelliJ IDEA.
4. Запустить Docker. Ввести во вкладке Terminal IntelliJ IDEA __команду__: `docker-compose up -d`.
5. Запустить SUT. Ввести во вкладке Terminal IntelliJ IDEA:
   
   - __команду__: `java -jar artifacts/aqa-shop.jar`,
   
   - для СУБД  MySQL __команду__: `java -jar artifacts/aqa-shop.jar --spring.datasource.url=jdbc:mysql://localhost:3306/app`,
   
   - для СУБД PostgreSQL __команду__: `java -jar artifacts/aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/app`.
   
## Запуск автотестов

1. Запустить в headless-режиме автотесты. Ввести в новой вкладке Terminal IntelliJ IDEA:

   - __команду__: `./gradlew clean test "-Dselenideheadless=true" allureReport`,

   - для СУБД  MySQL __команду__: `./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app" allureReport`,
     
   - для СУБД PostgreSQL __команду__: `./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app" allureReport`.

## Генерация отчетов

Ввести во вкладке Terminal IntelliJ IDEA __команду__: `./gradlew allureServe`.

## Завершение работы:

Удалить контейнеры вводом во вкладке Terminal IntelliJ IDEA __команды__: `docker-compose down`.



