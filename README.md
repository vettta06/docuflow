# Платформа организации документооборота для малого бизнеса

Система автоматизирует процесс загрузки, распознавания (OCR) и проверки первичных бухгалтерских документов.

Автор: Кузнецова Елизавета Артемовна

## Запуск проекта через Docker

Система упакована в контейнеры и запускается одной командой.

**Требования:** установленный Docker Desktop.

1. Создайте файл `.env` в корне проекта на основе `.env.example`:

   ```bash
   cp .env.example .env
   ```

   При необходимости измените пароли и другие параметры в `.env`.

2. Соберите образы и запустите все контейнеры:

   ```bash
   docker compose up --build -d
   ```

3. Дождитесь запуска всех сервисов.
4. Откройте приложение в браузере: [http://localhost](http://localhost).

### Тестовые учетные записи

При первом запуске автоматически создается только аккаунт Менеджера (через ManagerBootstrap). Бухгалтеров и клиентов Менеджер создает через панель управления.

- **Менеджер:** `manager@platform.local` / `admin12345`

> Используйте тестовые учетные записи только в локальной среде разработки.

## Локальный запуск

Для запуска сервисов без Docker потребуются:

- Java 21 (JDK).
- Node.js 20+.
- Python 3.12+.
- PostgreSQL (порт `5433`).
- MinIO (порты `9000` и `9001`).

### 1. База данных и MinIO

Запустите контейнеры базы данных и файлового хранилища, если они еще не работают:

```bash
docker compose up -d postgres minio
```

### 2. Python-сервис (OCR)

Откройте отдельный терминал и выполните команды из корня проекта.

**Windows (PowerShell):**

```powershell
cd python-service
python -m venv venv
.\venv\Scripts\Activate.ps1
pip install -r requirements.txt
python grpc_server.py
```

**Linux/macOS:**

```bash
cd python-service
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python grpc_server.py
```

### 3. Java Backend

В другом терминале:

**Windows:**

```powershell
cd java-service
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
cd java-service
./mvnw spring-boot:run
```

### 4. Frontend

В еще одном терминале:

```bash
cd frontend
npm install
npm run dev
```

Откройте приложение: [http://localhost:5173](http://localhost:5173).

## Роли пользователей

1. **CLIENT (Владелец бизнеса):**
   - Создает операции (хозяйственные события).
   - Загружает документы (счета, акты, договоры).
   - Просматривает задачи от бухгалтера и загружает запрошенные файлы.
   - Проверяет и подтверждает данные, распознанные OCR.

2. **ACCOUNTANT (Бухгалтер):**
   - Видит рабочую очередь своих клиентов.
   - Проверяет документы: подтверждает или отправляет на доработку.
   - Создает задачи (запросы) для клиентов, если не хватает документов.
   - Оставляет комментарии к операциям.

3. **MANAGER (Руководитель):**
   - Управляет организациями и бухгалтерами.
   - Назначает клиентов на бухгалтеров.
   - Просматривает аналитику (нагрузку на команду).
   - Просматривает Audit Log (историю действий).

## Основные эндпоинты API (REST)

Документация Swagger доступна по адресу:

- При локальном запуске Java Backend: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

### Auth

- `POST /auth/register` — регистрация (только CLIENT).
- `POST /auth/login` — авторизация и получение JWT.

### Documents

- `POST /documents` — загрузка файла (`multipart/form-data`).
- `GET /documents` — список документов с учетом прав доступа.
- `GET /documents/{id}/details` — получение распознанных OCR-данных.
- `PUT /documents/{id}/confirm` — подтверждение данных клиентом.
- `PATCH /documents/{id}/status` — изменение статуса документа бухгалтером.

### Operations

- `POST /operations` — создание операции.
- `GET /operations` — получение списка операций.

### Tasks

- `POST /tasks` — создание задачи для клиента.
- `GET /tasks` — получение списка задач.

### Manager

- `GET /manager/dashboard` — аналитика нагрузки.
- `POST /manager/accountants` — создание бухгалтера.
- `GET /manager/audit` — история действий (Audit Log).

## Тестирование

В проекте предусмотрены unit-тесты и интеграционные тесты на базе MockMvc для проверки безопасности и бизнес-логики.

Запуск тестов из корня проекта:

**Windows:**

```powershell
cd java-service
.\mvnw.cmd test
```

**Linux/macOS:**

```bash
cd java-service
./mvnw test
```

Также настроен `pre-commit` с Ruff для Python и Spotless для Java. Он проверяет и/или форматирует код перед коммитом в соответствии с конфигурацией проекта.

## Структура проекта

```text
accounting-platform/
├── java-service/ # Основной бэкенд (Spring Boot)
│ ├── src/ # Исходный код (контроллеры, сервисы, сущности)
│ └── Dockerfile
├── python-service/ # Микросервис OCR (gRPC сервер, Tesseract)
│ ├── grpc_server.py
│ └── Dockerfile
├── frontend/ # Клиентское приложение (React)
│ ├── src/ # Компоненты и страницы
│ ├── nginx.conf # Конфиг Nginx для фронта
│ └── Dockerfile
├── proto/ # gRPC контракты (document.proto)
├── artifacts/ # Диаграммы и материалы для отчета (ER, BPMN)
├── docker-compose.yml # Оркестрация всех сервисов
├── .env.example # Шаблон переменных окружения
└── README.md # Документация
```

## Технологический стек

- **Backend:** Java 21, Spring Boot 4.0, Spring Security (JWT), Spring Data JPA, Hibernate.
- **База данных:** PostgreSQL 16, Flyway.
- **Файловое хранилище:** MinIO.
- **Микросервис OCR:** Python 3.12, gRPC, Tesseract, pdf2image.
- **Frontend:** React (Vite), Axios, React Router.
- **Инфраструктура:** Docker, Docker Compose, Nginx.
