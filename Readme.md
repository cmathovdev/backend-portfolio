# ⚙️ Portfolio Backend — Leaderboard API

> REST API built with Java 17 and Spring Boot 3.2 to power the global leaderboard of the [Desktop Portfolio](https://cmathovdev.github.io) project. Deployed on Render via Docker with a PostgreSQL database.

**🌐 Frontend:** [cmathovdev.github.io](https://cmathovdev.github.io)
&nbsp;&nbsp;|&nbsp;&nbsp;
**🖥️ Frontend repo:** [cmathovdev.github.io](https://github.com/cmathovdev/cmathovdev.github.io)

---

## 📌 Overview

This service stores and retrieves the top 20 high scores from the space shooter game embedded in the portfolio. It exposes a minimal REST API (`GET` + `POST /scores`) backed by PostgreSQL, with insertion logic handled entirely in SQL to keep the table lean without any scheduled jobs.

---

## 🚀 API Reference

### `GET /scores`

Returns the current top 20 leaderboard, ordered by score descending (ties broken by earliest submission).

**Response:**
```json
[
  {
    "id": 1,
    "player": "Cami",
    "score": 340,
    "createdAt": "2024-04-01T20:15:00"
  },
  ...
]
```

---

### `POST /scores`

Submits a new score. The entry is only persisted if it qualifies for the top 20.

**Request body:**
```json
{
  "player": "Cami",
  "score": 340
}
```

**Validation:**
- `player` — required, non-blank
- `score` — required, minimum value `0`

**Response:** Updated top 20 leaderboard (same format as `GET /scores`).

---

## 🧠 Leaderboard Logic

The top-20 constraint is enforced at the database level using two native queries, keeping the service stateless and simple:

**Insert condition** (`insertIfTop`):
```sql
INSERT INTO scores (player, score, created_at)
SELECT :player, :score, NOW()
WHERE (
    (SELECT COUNT(*) FROM scores) < 20
    OR :score > (SELECT MIN(score) FROM scores)
)
```

A score is only inserted if:
- The table has fewer than 20 entries, **or**
- The new score beats the current minimum.

**Cleanup** (`cleanup`):
```sql
DELETE FROM scores
WHERE id NOT IN (
    SELECT id FROM scores
    ORDER BY score DESC, created_at ASC
    LIMIT 20
)
```

After every insert, any record outside the top 20 is immediately deleted. The table never grows beyond 20 rows.

> This approach avoids application-level logic for table management and leverages the database engine directly.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation (`@NotBlank`, `@Min`) |
| Containerization | Docker (multi-stage build) |
| Deployment | Render (free tier) |

---

## 🐳 Docker

The image uses a **multi-stage build** to keep the final artifact small:

1. **Build stage** — Maven compiles and packages the JAR (`mvn clean package -DskipTests`)
2. **Runtime stage** — only the JRE and the packaged JAR are copied over

Database credentials and server port are injected at runtime via environment variables — no secrets in the image.

```dockerfile
CMD ["sh", "-c", "java -jar app.jar \
  --spring.datasource.url=$DB_URL \
  --spring.datasource.username=$DB_USER \
  --spring.datasource.password=$DB_PASS \
  --server.port=${PORT:-10000}"]
```

---

## 📁 Project Structure

```
src/main/java/com/portfolio/leaderboard/
├── Application.java                  # Spring Boot entry point
├── controller/
│   └── ScoreController.java          # REST endpoints (GET + POST /scores)
├── model/
│   └── Score.java                    # JPA entity + Bean Validation
├── repository/
│   └── ScoreRepository.java          # JPA repo + native SQL queries
└── service/
    └── ScoreService.java             # Business logic + @Transactional
```

---

## ⚙️ Configuration

The application expects three environment variables:

| Variable | Description |
|---|---|
| `DB_URL` | JDBC connection string (e.g. `jdbc:postgresql://host:5432/db`) |
| `DB_USER` | Database username |
| `DB_PASS` | Database password |
| `PORT` | Server port (defaults to `10000`) |

`spring.jpa.hibernate.ddl-auto=update` is set so the `scores` table is created automatically on first boot.

---

## 🔗 Related

- **Frontend (Vanilla JS):** [cmathovdev.github.io repo](https://github.com/cmathovdev/cmathovdev.github.io)
- **Live portfolio:** [cmathovdev.github.io](https://cmathovdev.github.io)

---

## 👩‍💻 About

Built by **Camila Mathov** — fullstack developer candidate, Systems Engineering student.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Camila_Mathov-0077B5?style=flat&logo=linkedin)](https://www.linkedin.com/in/camila-mathov/)