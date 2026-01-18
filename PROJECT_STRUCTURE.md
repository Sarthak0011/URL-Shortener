# 🏗️ LinkSnap: Microservices Architecture & Project Structure

This document provides a comprehensive overview of the **LinkSnap** URL Shortener project structure, its microservices, and how they interact.

---

## 📂 Root Directory Structure

```text
URLShortener/
├── 📁 analytics-service/    # Tracks and stores click data
├── 📁 api-gateway/          # Central entry point & request routing
├── 📁 common-lib/          # Shared models and Kafka event schemas
├── 📁 discovery-service/    # Netflix Eureka Server (Service Registry)
├── 📁 redirect-service/     # High-performance redirection (Edge service)
├── 📁 url-service/          # Core logic for URL creation & management
├── 📁 docker-data/          # Persistent data for Docker containers
├── 📄 docker-compose.yml    # Infrastructure (MySQL, Kafka, Redis, Zookeeper)
├── 📄 pom.xml               # Parent Maven Project
├── 📄 start_services.sh     # Local orchestration script
└── 📄 URL_Shortener_API.postman_collection.json # API Documentation
```

---

## 🛠️ Microservices Breakdown

### 🛰️ 1. Discovery Service (`discovery-service`)
- **Technology**: Netflix Eureka Server
- **Port**: `8761`
- **Role**: Acts as the "phonebook" for the microservices. Every service registers here so they can find each other dynamically without hardcoded IPs.

### 🚪 2. API Gateway (`api-gateway`)
- **Technology**: Spring Cloud Gateway
- **Port**: `8080`
- **Role**: The single entry point for all client requests (Frontend/Postman).
- **Features**:
  - Routes requests to specific services using `lb://` (Load Balanced) URIs.
  - Handles CORS and global logging.

### 🔗 3. URL Service (`url-service`)
- **Technology**: Spring Boot, Spring Data JPA
- **Port**: `8081`
- **Database**: MySQL (`url_shortener` DB)
- **Role**: 
  - Generates unique short codes for long URLs.
  - Manages expiration dates and metadata.
  - Performs the primary CRUD operations for URL mappings.

### 🚀 4. Redirect Service (`redirect-service`)
- **Technology**: Spring Boot, Spring Data Redis
- **Port**: `8082`
- **Cache**: Redis
- **Role**: 
  - Handles the actual redirection (`GET /shortCode`).
  - Optimized for speed using Redis caching.
  - Publishes `UrlClickEvent` to **Kafka** asynchronously for every visit.

### 📈 5. Analytics Service (`analytics-service`)
- **Technology**: Spring Boot, Spring Kafka, JPA
- **Port**: `8083`
- **Database**: MySQL
- **Role**: 
  - Consumes click events from Kafka.
  - Persists visit data (IP, User Agent, Timestamp).
  - Provides APIs for viewing link performance.

### 📦 6. Common Library (`common-lib`)
- **Role**: A shared module used by all services.
- **Contents**:
  - Kafka event definitions (e.g., `UrlClickEvent`).
  - Shared constants and utility classes.

---

## 🔄 Data & Communication Flow

1.  **Creation**: User submits a URL to `api-gateway` → Routed to `url-service` → Short code saved in **MySQL**.
2.  **Redirection**: User visits short link → Routed to `redirect-service` → Checks **Redis** (Cache) → Redirects user → Sends background event to **Kafka**.
3.  **Analytics**: `analytics-service` picks up event from **Kafka** → Saves visit details to **MySQL**.
4.  **Discovery**: Services look up each other's location via `discovery-service`.

---

## 🐳 Infrastructure (Docker)

The project relies on several key pieces of infrastructure defined in `docker-compose.yml`:
- **MySQL**: Primary relational storage for URLs and Analytics.
- **Redis**: High-speed cache for the Redirect service.
- **Kafka & Zookeeper**: Event-driven backbone for asynchronous analytics processing.
- **Kafka-UI**: (Port `8090`) Useful for debugging messages flowing through the system.

---

## 🚦 Local Startup Order
To ensure the system boots correctly:
1. **Infrastructure**: `docker-compose up -d`
2. **Registry**: `discovery-service` (Wait ~15s)
3. **Core Services**: `url-service`, `redirect-service`, `analytics-service`
4. **Entrance**: `api-gateway`
