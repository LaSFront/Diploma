# Дипломный проект по профессии «Тестировщик»

Тестируемое приложение — веб-сервис, который предлагает купить тур по определённой цене двумя способами:
1. Обычная оплата по дебетовой карте.
2. Уникальная технология: выдача кредита по данным банковской карты.

Приложение не обрабатывает данные по картам, а пересылает их банковским сервисам:
1. сервису платежей, далее Payment Gate; 
2. кредитному сервису, далее Credit Gate.

Заявлена поддержка двух СУБД:
1. MySQL;
2. PostgreSQL.

## Документация

1. [План автоматизации](https://github.com/LaSFront/Diploma/blob/main/documentation/Plan.md)
2. [Отчет о тестировании]()
3. [Отчет об автоматизации]()
## Окружение
- Google Chrome.
- IntelliJ IDEA.
- JAVA openjdk (версия - не выше 11).
- Docker Desktop.
- DBeaver.

## Подготовка
1. Запустить на ПК Docker Desktop.
2. Открыть вкладку Terminal IntelliJ IDEA. Клонировать проект: [репозиторий](https://github.com/LaSFront/Diploma). Команда: `git clone https://github.com/LaSFront/Diploma`.
3. Открыть проект в IntelliJ IDEA.
4. Во вкладке Terminal IntelliJ IDEA запустить Docker. Команда: `docker-compose up -d`.
5. Во вкладке Terminal IntelliJ IDEA запустить SUT. Команда: `java -jar ./artifacts/aqa-shop.jar`.
   
## Запуск автотестов

1. В новой вкладке Terminal IntelliJ IDEA запустить в headless-режиме автотесты. Команда: `./gradlew test --info "-Dselenideheadless=true"`.

## Генерация отчетов
1. Во вкладке Terminal IntelliJ IDEA. Команда: `./gradlew allureServe`.

