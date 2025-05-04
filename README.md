# Explore with Me

"Explore with Me" — микросервисное приложение для создания, поиска и участия в мероприятиях.

## Структура проекта

Приложение состоит из двух микросервисов:

- **main-service** — бизнес-логика (мероприятия, пользователи, комментарии, заявки).
- **stat-service** — сбор статистики посещений (отдельный сервис и клиент для взаимодействия).

Каждый микросервис имеет собственную базу данных и запускается в отдельном Docker-контейнере (всего 4 контейнера: 2 сервиса и 2 базы данных).

---

## Основной функционал

### Неавторизованные пользователи:
- Просмотр всех событий с фильтрацией по категориям.
- Просмотр деталей событий.
- Просмотр закреплённых подборок мероприятий.
- Чтение комментариев к событиям.

### Авторизованные пользователи:
- Создание и редактирование собственных мероприятий.
- Подача заявок на участие.
- Подтверждение/отклонение заявок к своим событиям.
- Оставление комментариев после участия в мероприятии.

### Администраторы:
- Управление категориями событий.
- Управление подборками событий на главной странице.
- Модерация событий (публикация/отклонение).

---

## Работа с комментариями

Комментарии доступны только для зарегистрированных пользователей и имеют следующие возможности:

### Эндпоинты:
- **POST** `/users/{userId}/events/{eventId}/comments` — добавить комментарий к событию.
- **GET** `/events/{eventId}/comments` — получить список комментариев к событию (с постраничной навигацией).
- **GET** `/users/{userId}/events/{eventId}/comments/{commentId}` — получить комментарий пользователя.
- **PATCH** `/users/{userId}/events/{eventId}/comments/{commentId}` — редактировать свой комментарий.
- **DELETE** `/users/{userId}/events/{eventId}/comments/{commentId}` — удалить свой комментарий.
- **GET** `/events/comments/{commentId}` — получить комментарий по его ID (публично).

### Ограничения:
- Комментарии можно оставлять только после участия в мероприятии.
- Комментарий содержит:
    - `id` — идентификатор комментария.
    - `text` — текст комментария.
    - `user` — автор комментария (id, email, name).
    - `event` — краткая информация о событии (id, title и т.д.).
    - `created` — дата и время создания.

---

## API Эндпоинты

<details>
<summary><strong>main-service:</strong></summary>

### Пользователи:
- `POST /users/{userId}/events`
- `GET /users/{userId}/events/{eventId}`
- `PATCH /users/{userId}/events/{eventId}`
- `GET /users/{userId}/events`
- `GET /users/{userId}/events/{eventId}/requests`
- `PATCH /users/{userId}/events/{eventId}/requests`

### Публичный доступ:
- `GET /categories`
- `GET /categories/{catId}`
- `GET /compilations`
- `GET /compilations/{compId}`
- `GET /events`
- `GET /events/{id}`

### Комментарии (см. выше)
</details>

<details>
<summary><strong>admin:</strong></summary>

- `GET /admin/events`
- `PATCH /admin/events/{eventId}`
- `POST /admin/users`
- `GET /admin/users`
- `DELETE /admin/users/{userId}`
- `POST /admin/compilations`
- `PATCH /admin/compilations/{compId}`
- `DELETE /admin/compilations/{compId}`
- `POST /admin/categories`
- `GET /admin/categories/{catId}`
- `DELETE /admin/categories/{catId}`
</details>

<details>
<summary><strong>stat-service:</strong></summary>

- `GET /stats` — получить статистику посещений.
- `POST /hit` — зафиксировать посещение URI.
</details>



