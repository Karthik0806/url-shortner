<div align="center">

<br/>

# knurls.me 🔗
### Scalable URL Shortener

<br/>

**REAL-Time URL shortener with custom aliases, click analytics, Redis caching, and JWT + Google OAuth2 authentication.**

<br/>

[![Live Demo](https://img.shields.io/badge/%F0%9F%9A%80%20Live%20Demo-knurls.me-C8933A?style=for-the-badge&labelColor=0d0c0a)](https://knurls.me)
[![API Docs](https://img.shields.io/badge/%F0%9F%93%96%20API%20Docs-Developer%20Portal-2ea44f?style=for-the-badge&labelColor=0d0c0a)](https://doc.knurls.me)
[![Portfolio](https://img.shields.io/badge/%F0%9F%8C%90%20Portfolio-karthiknarravula.dev-9b59b6?style=for-the-badge&labelColor=0d0c0a)](https://karthiknarravula.dev)

<br/>

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_(RDS)-316192?style=flat-square&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS_EC2-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)

</div>

<br/>

---

## 🔗 Live Links

| Resource | URL |
|:---|:---|
| 🌐 App | [knurls.me](https://knurls.me) |
| ⚙️ Backend API | [api.knurls.me](https://api.knurls.me) |
| 📖 Custom API Documentation | [doc.knurls.me](https://doc.knurls.me) |
| 📖 Swagger UI | [/swagger-ui/index.html](https://api.knurls.me/swagger-ui/index.html) |
| 📄 OpenAPI JSON | [/v3/api-docs](https://api.knurls.me/v3/api-docs) |

---

## ✨ Features

<table>
<tr>
<td width="50%">

### 🔐 Authentication & Security
- JWT access + refresh token flow
- Google OAuth2 ID-token login
- BCrypt password hashing
- Role-based access control (`USER` / `ADMIN`)
- Auto-seeded admin account on startup
- Centralized exception handling with typed errors

### 🔗 URL Management
- Short link generation via Base62 encoding
- Custom alias support with reserved-word protection
- Optional expiration (TTL) per link
- Duplicate-link detection per user
- Per-user paginated link history

</td>
<td width="50%">

### 📊 Analytics & Caching
- Click-through tracking with atomic counters
- Per-click metadata: IP, browser, device type, referer
- Redis-backed cache for hot redirects (24h TTL)
- Cache-aside pattern with DB fallback
- IP-based rate limiting (50 req/min) on redirects

### ☁️ DevOps & Cloud
- Dockerized backend on AWS EC2
- AWS RDS PostgreSQL database
- Nginx reverse proxy with HTTPS
- GitHub Actions CI/CD pipeline
- Swagger/OpenAPI 3 and custom documentation
- Actuator + Micrometer/Prometheus metrics

</td>
</tr>
</table>

---

## 🛠 Tech Stack

| Layer | Technologies |
|:---|:---|
| **Backend** | Java 21 · Spring Boot 3 · Spring Security · Spring Data JPA · Hibernate · JWT (jjwt) · Google OAuth2 Client |
| **Data** | PostgreSQL (AWS RDS) · Redis (caching + rate limiting) |
| **Observability** | Spring Actuator · Micrometer · Prometheus |
| **DevOps / Cloud** | Docker · Docker Hub · AWS EC2 · AWS RDS · Nginx · GitHub Actions |
| **Docs** | springdoc-openapi (Swagger UI) |

---

## 🏗 Architecture

```
┌─────────────────────────────────┐
│         Client / Browser        │
└────────────────┬────────────────┘
                 │  HTTPS
                 ▼
┌─────────────────────────────────┐
│       Nginx Reverse Proxy       │
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│  Spring Boot Backend            │
│  (Docker Container on EC2)      │
│                                 │
│  ┌──────────┐  ┌─────────────┐  │
│  │ REST API │  │  Redirector │  │
│  │ /api/**  │  │    /r/**    │  │
│  └────┬─────┘  └──────┬──────┘  │
│       │                │        │
│       ▼                ▼        │
│  ┌──────────┐    ┌───────────┐  │
│  │  Redis   │    │ Analytics │  │
│  │  Cache   │    │  Capture  │  │
│  └──────────┘    └───────────┘  │
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│    PostgreSQL Database          │
│         (AWS RDS)               │
└─────────────────────────────────┘
```

## 🔐 Authentication Flow

```
User Login
 __________________________________
|    │                             |
|    ├── Email + Password          |
|    │        │                    |
|    │        ▼                    |
|    │   Spring Security           |
|    │        │                    |
|    │        ▼                    |
|    │   JWT (access + refresh)    |
|    │                             |
|    └── Google OAuth2             |
|             │                    |
|             ▼                    |
|       ID Token Verification      |
|             │                    |
|             ▼                    |
|     Find-or-create User          |
|             │                    |
|             ▼                    |
|       JWT Generation             |
|__________________________________|
```

## ⚡ Redirect Flow

```
GET /r/{shortCode}
        │
        ▼
  Rate Limit Check (Redis, 50/min per IP)
        │
        ▼
  Cache Lookup (Redis)
   │              │
  Hit            Miss
   │              │
   ▼              ▼
Return URL   Query PostgreSQL
   │              │
   │              ▼
   │        Populate Cache
   │              │
   └──────┬───────┘
          ▼
  Increment Click Count
          ▼
  Save Analytics (IP, browser, device, referer)
          ▼
   302 Redirect to Original URL
```

---

## 🚀 Running Locally

### Prerequisites
- Java 21+
- PostgreSQL
- Redis

### 1 · Clone the Repository

```bash
git clone https://github.com/Karthik0806/url-shortner.git
cd url-shortner
```

### 2 · Configure Environment Variables

Create a `.env` file (or set as environment variables):

```env
SPRING_PROFILES_ACTIVE=prod

DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

REDIS_HOST=your_redis_host
REDIS_PORT=6379

JWT_SECRET=your_jwt_secret
JWT_ACCESS_TOKEN_EXPIRATION=900000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

GOOGLE_CLIENT_ID=your_google_client_id

ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=change_me

APP_BASE_SHORT_URL=https://knursl.me/r/
```

### 3 · Run the Application

```bash
./mvnw spring-boot:run
```

> Swagger UI will be available at `http://localhost:8080/swagger-ui/index.html`

---

## 🐳 Docker Deployment

### Build & Push Image

```bash
docker buildx build \
  --platform linux/amd64 \
  -t karthi2005/knursl-backend:latest \
  --push .
```

### Deploy to EC2

```bash
./deploy.sh
```

---

## ⚙️ CI/CD Pipeline

The GitHub Actions workflow runs automatically on every push:

```
Push to main
     │
     ▼
Build Docker Image
     │
     ▼
Push to Docker Hub
     │
     ▼
SSH into EC2
     │
     ▼
Pull & Restart Container
```

---

## 🗺 Roadmap

| Status | Feature |
|:---:|:---|
| ✅ | Custom alias + reserved alias protection |
| ✅ | Redis-backed caching with TTL |
| ✅ | IP-based rate limiting |
| ✅ | Click analytics (browser, device, referer, IP) |
| ✅ | JWT auth + refresh tokens |
| ✅ | Google OAuth2 login |
| ✅ | Role-based admin access |
| ✅ | Docker + AWS EC2 deployment |
| ✅ | GitHub Actions CI/CD |
| 🔲 | QR code generation for short links |
| 🔲 | Bulk link creation & CSV export |
| 🔲 | Geo-location analytics |
| 🔲 | Admin dashboard UI |
| 🔲 | Kubernetes deployment |
| 🔲 | Grafana dashboards for Prometheus metrics |

---

## 📚 What I Learned

Building knursl.me gave me hands-on experience across backend and cloud engineering:

- **Caching strategy** — Implementing a cache-aside pattern with Redis to keep hot redirects fast while staying consistent with PostgreSQL
- **Rate limiting** — Designing an IP-based sliding-window limiter backed by Redis counters
- **Security** — Building a JWT access + refresh token flow alongside Google OAuth2 login
- **Data modeling** — Structuring entities for analytics, auditing (created/updated timestamps), and click tracking at scale
- **Cloud deployment** — Provisioning and operating PostgreSQL on AWS RDS alongside a Dockerized app on EC2
- **Reverse proxying** — Configuring Nginx for SSL termination and routing between API and redirect paths
- **CI/CD** — Automating build, push, and deploy with GitHub Actions
- **Observability** — Exposing health and metrics via Actuator and Micrometer/Prometheus

---

## 👤 Author

<div align="center">

**Karthik Narravula**

[![GitHub](https://img.shields.io/badge/GitHub-Karthik0806-181717?style=for-the-badge&logo=github)](https://github.com/Karthik0806)
[![Portfolio](https://img.shields.io/badge/Portfolio-karthiknarravula.dev-C8933A?style=for-the-badge&logo=google-chrome&logoColor=white)](https://karthiknarravula.dev)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Karthik%20Narravula-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/karthik-narravula)

</div>

---

## 📄 License

This project is built for educational and portfolio purposes. Feel free to explore the code and architecture — and don't forget to star ⭐ the repo if you found it useful!

---

<div align="center">

*Made with ☕ and a lot of debugging by [Karthik Narravula](https://karthiknarravula.dev)*

</div>
