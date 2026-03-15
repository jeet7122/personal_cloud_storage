# 🌥 CloudVault – Event-Driven Personal Cloud Storage

[![Java](https://img.shields.io/badge/Java-21-blue?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-16-black?logo=next.js&logoColor=white)](https://nextjs.org/)
[![Kafka](https://img.shields.io/badge/Apache-Kafka-black?logo=apachekafka)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Containers-blue?logo=docker)](https://docker.com/)
[![Cloudflare R2](https://img.shields.io/badge/Cloudflare-R2-orange?logo=cloudflare)](https://www.cloudflare.com/)
[![Oracle Cloud](https://img.shields.io/badge/Oracle-Cloud-red?logo=oracle)](https://www.oracle.com/cloud/)
[![Vercel](https://img.shields.io/badge/Vercel-Frontend-black?logo=vercel)](https://vercel.com/)

**CloudVault** is a **cloud-native personal storage platform** built with a **modern event-driven architecture**.  
It allows users to securely **upload, store, preview, and manage images**, with scalable backend processing powered by **Kafka workers** and **Cloudflare R2 object storage**.

This project demonstrates **real-world distributed system architecture**, containerized deployment, and cloud integrations.

Repository:  
https://github.com/jeet7122/personal_cloud_storage

---

## 🚀 Table of Contents

- Project Overview
- Architecture
- Key Features
- Tech Stack
- Installation
- Local Development
- Deployment
- Future Improvements
- Contributing

---

## 🌟 Project Overview

CloudVault is designed to mimic the **core architecture of modern cloud storage platforms**.

Instead of uploading files synchronously, the system uses an **asynchronous event-driven pipeline** to process uploads reliably and scalably.

### Why this architecture?

Uploading files directly to storage can cause:

- slow APIs
- blocked threads
- poor scalability

CloudVault solves this by introducing **Kafka-based background workers**.

### Upload Flow
```text

Frontend (Next.js)
        │
        ▼
Spring Boot API
        │
        ▼
Kafka Event (photo-upload)
        │
        ▼
Worker Service
        │
        ▼
Cloudflare R2 Storage
        │
        ▼
PostgreSQL Metadata
```


This architecture allows the system to scale independently for:

- API traffic
- file processing
- storage operations

---

## 🏗 System Architecture

CloudVault uses a **microservice-style architecture** with two backend services:

### 1️⃣ API Service
Responsible for:

- authentication validation
- upload request validation
- event publishing to Kafka
- metadata queries

### 2️⃣ Worker Service
Responsible for:

- consuming Kafka events
- uploading files to Cloudflare R2
- storing metadata in PostgreSQL
- deleting temporary files

### Infrastructure
```text
Next.js Frontend (Vercel)
        │
        ▼
Spring Boot API (Oracle Cloud)
        │
        ▼
Kafka (Docker)
        │
        ▼
Worker Service (Docker)
        │
        ▼
Cloudflare R2 + PostgreSQL

```

---

## ✅ Key Features

### 📤 Asynchronous File Uploads
Uploads are processed via **Kafka events**, enabling high scalability.

### ☁ Cloud Object Storage
Files are stored using **Cloudflare R2 (S3-compatible object storage)**.

### 🔐 Secure Authentication
Frontend authentication powered by **Clerk JWT tokens**.

### ⚡ Event-Driven Processing
Background workers process uploads asynchronously.

### 📱 Responsive UI
Modern **Next.js + TailwindCSS** interface with drag-and-drop uploads.

### 🔄 Infinite Scrolling Gallery
Photos are loaded with paginated APIs and intersection observers.

### 🐳 Containerized Backend
Kafka, API, and worker services run in **Docker containers**.

---

## 🛠 Tech Stack

| Layer | Technology                     |
|------|--------------------------------|
| Frontend | Next.js 16, React, TailwindCSS |
| Backend API | Spring Boot 3, Java 21         |
| Worker Service | Spring Boot Kafka Consumer     |
| Messaging | Apache Kafka                   |
| Database | PostgreSQL                     |
| Storage | Cloudflare R2                  |
| Authentication | Clerk JWT                      |
| Containers | Docker + Docker Compose        |
| Deployment | Oracle Cloud VM                |
| Frontend Hosting | Vercel                         |

---

## ⚙ Installation

Clone repository

```bash

git clone https://github.com/jeet7122/personal_cloud_storage.git
cd personal_cloud_storage
```

---

## 🐳 Run with Docker (Recommended)

Start the entire system:
```bash
  docker compose up --build
```

### Services started:
* Kafka
* Zookeeper
* Spring Boot API
* Worker Service

---
### Backend API
```bash
    cd photo-store-backend
    ./mvnw spring-boot:run
```

#### Runs at
```bash
    http://localhost:8080
```
---

### Frontend
```bash
    cd photo-store-frontend
    npm install
    npm run dev
```

#### Open:

```bash
    http://localhost:3000
```
---

## 🔐 Environment Variables

```dotenv
DB_URL=
DB_USER=
DB_PASSWORD=

R2_ACCESS_KEY=
R2_SECRET_KEY=
R2_ENDPOINT=
R2_BUCKET=

JWKS_URI=
FRONTEND_URL=
APP_APPROVED_USERID=
```

---
## 🌐 Deployment

### Backend (Oracle Cloud)

* Deploy Docker containers on an Oracle Cloud VM 
* Expose port 8080 
* Configure environment variables

---

### Frontend (Vercel)

* Connect GitHub repo 
* Add environment variables 
* Deploy automatically

--- 

# 🔮 Future Improvements

### Planned Features
* image thumbnails 
* drag-to-folder organization 
* search with metadata indexing 
* background virus scanning 
* multi-user support 
* signed URL downloads 
* Redis caching 
* rate limiting

## 🤝 Contributing

Contributions are welcome! If you’d like to improve CloudVault, please follow these steps.

### 1. Fork the Repository

Click the **Fork** button on the top right of the repository page.

### 2. Clone Your Fork

```bash
    git clone https://github.com/<your-username>/personal_cloud_storage.git
    cd personal_cloud_storage
```

### 3. Create new Branch

Create a feature branch for your changes.

```bash
    git checkout -b feature/your-feature-name
```

### 4. Make your changes
Implement your feature or bug fix and ensure the code follows the project structure and conventions.

### 5. Commit your changes
Write clear and descriptive commit messages.

```bash
    git commit -m "Add: description of the feature or fix"
```

### 6. Push to Your Fork

```bash
    git push origin feature/your-feature-name
```

### 7. Open a Pull Request

* Go to the original repository 
* Click New Pull Request 
* Describe your changes clearly

### Contribution Guidelines
#### Please ensure:
* Code is clean and readable 
* No unnecessary dependencies are added 
* Existing functionality is not broken 
* Commit messages are meaningful 
* Documentation is updated when necessary

### Reporting Issues

If you find a bug or want to request a feature:
1. Go to the Issues tab 
2. Create a new issue 
3. Provide a clear description and steps to reproduce

---
### Thank you for helping improve CloudVault! 🚀
