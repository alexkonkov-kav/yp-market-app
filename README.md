# Проектная работа яндекс практикум, спринт 5.

Приложение «Витрина интернет-магазина» с использованием SpringBoot, Maven, Liquibase и Docker.

---

## Что внутри

- **REST-контроллеры** `CartController`, `ItemController`, `OrderController` и `OrderBuyController`

- **DAO-слой**: `CartItem`, `Item`, `Order`, `OrderItem`" репозитории `JpaRepository`

- **База данных PostgreSQL** с использованием **Liquibase**

- **Тесты**:
    - пакет `service` — unit тесты сервиса
    - пакет `controller` и `repository` — интеграционные и WebMvc тесты контроллера и репозитория

---

## Запуск приложения

- **Создать в корне проекта файл .env:** с указанием `DB_USER, DB_PASSWORD, DB_NAME, DB_PORT`

- **Запуск:** `docker-compose up --build`

- **Приложение будет доступно по адресу:** `http://localhost:8080`