# URL Shortener - Spring Boot Microservices

A production-ready URL Shortener backend built with Spring Boot microservices architecture.

## Architecture

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │     (8080)      │
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│   URL Service   │ │Redirect Service │ │Analytics Service│
│     (8081)      │ │     (8082)      │ │     (8083)      │
└────────┬────────┘ └────────┬────────┘ └────────┬────────┘
         │                   │                   │
         ▼                   ▼                   ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│     MySQL       │ │     Redis       │ │     Kafka       │
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Single entry point, routes requests |
| URL Service | 8081 | Create/manage short URLs |
| Redirect Service | 8082 | Handle redirects with Redis caching |
| Analytics Service | 8083 | Consume and store click analytics |

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.x**
- **Spring Cloud Gateway**
- **Spring Data JPA**
- **MySQL**
- **Redis**
- **Apache Kafka**
- **Docker Compose**

## Quick Start

### Prerequisites
- Java 21
- Maven 3.8+
- Docker & Docker Compose

### Start Infrastructure
```bash
docker-compose up -d
```

### Build & Run Services
```bash
# Build all modules
mvn clean install

# Run individual services
cd url-service && mvn spring-boot:run
cd redirect-service && mvn spring-boot:run
cd analytics-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

## API Endpoints

### Create Short URL
```bash
POST /api/urls
Content-Type: application/json

{
  "longUrl": "https://example.com/very/long/url",
  "expiryDays": 30
}
```

### Redirect
```bash
GET /{shortCode}
# Returns HTTP 302 redirect
```

### Get Analytics
```bash
GET /api/analytics/{shortCode}/count
GET /api/analytics/{shortCode}
```

## Features

- ✅ Base62 short code generation
- ✅ Configurable URL expiry
- ✅ Redis caching for low-latency redirects
- ✅ Async analytics via Kafka
- ✅ Click tracking with IP and User-Agent
- ✅ Graceful fallback (Redis → DB)
- ✅ Horizontal scalability
