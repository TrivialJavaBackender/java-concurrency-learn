# Ex04: Docker Networking — Изоляция сетей

**Модуль:** 1 — Docker
**Сложность:** ★★★☆☆
**Тема:** Docker networks, DNS, сетевая изоляция, bridge network

## Контекст

В компании два приложения: `frontend` и `backend`. Backend работает с базой данных.
По требованиям безопасности: frontend не должен иметь доступ к БД напрямую.
Текущая конфигурация все сервисы в одной сети — это нарушение изоляции.

## Задача

Настроить `docker-compose.yml` с двумя изолированными сетями:
- `frontend-net`: frontend ↔ backend (frontend видит backend)
- `backend-net`: backend ↔ database (frontend НЕ видит database)

Требования:
- Frontend может обращаться к backend по имени сервиса
- Backend может обращаться к postgres по имени сервиса
- Frontend НЕ может напрямую обратиться к postgres (изолированная сеть)
- Только порт frontend'а открыт наружу (backend и postgres не публикуют порты на хост)
- Каждая сеть явно объявлена с типом `bridge`

## Файлы для изменения

- `docker-compose.yml` — написать с нуля

## Проверка

Упражнение считается выполненным, когда:
- [ ] Объявлены минимум 2 отдельные сети
- [ ] `frontend` подключён только к `frontend-net`
- [ ] `postgres` подключён только к `backend-net`
- [ ] `backend` подключён к обеим сетям (bridge между сетями)
- [ ] `ports` только у frontend сервиса
- [ ] `docker-compose config` выполняется без ошибок

## Полезные ссылки

- [theory/DOCKER.md — раздел 5: Docker Networking](../../theory/DOCKER.md)
