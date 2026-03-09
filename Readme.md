# 🌥  Cloud-Vault – Enterprise-Grade Personal Cloud Storage

[![Java 17](https://img.shields.io/badge/Java-21-blue?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.11-green?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-14-black?logo=next.js&logoColor=white)](https://nextjs.org/)
[![Vercel](https://img.shields.io/badge/Vercel-Frontend-brightgreen?logo=vercel&logoColor=white)](https://vercel.com/)
[![Oracle Cloud](https://img.shields.io/badge/Oracle%20Cloud-Backend-orange?logo=oracle&logoColor=white)](https://www.oracle.com/cloud/)


**Cloud-Vault** is a **full-stack, cloud-native personal storage platform** that allows users to securely **upload, download, and manage files** anywhere. Designed with scalability, performance, and security in mind, Cloud-Vault is an **ideal portfolio project** for demonstrating modern web development, cloud integration, and backend architecture.

Repository: [https://github.com/jeet7122/personal_cloud_storage](https://github.com/jeet7122/personal_cloud_storage)

---

## 🚀 Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Usage](#usage)
- [Deployment](#deployment)
- [Screenshots](#screenshots)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

---

## 🌟 Project Overview
- Link in TOC: [Project Overview](#project-overview)


Cloud-Vault is a **modern, secure, and highly responsive cloud storage solution**, designed for personal use or as a **portfolio-ready full-stack application**.

**Why Cloud-Vault?**

- **Secure:** Files are stored using **Cloudflare R2** for fast, encrypted cloud storage.
- **Scalable:** Backend powered by **Spring Boot** and deployable on **Oracle Cloud VM**.
- **Responsive:** Frontend built with **Next.js + TailwindCSS**, optimized for **mobile and desktop**.
- **Portfolio-Ready:** Demonstrates full-stack skills, cloud deployment, API integration, and modern UI/UX.

---

## ✅ Key Features

- **Robust File Management:** Upload, download, and organize files in folders.
- **User Authentication:** Secure login, signup, and session management.
- **Cloud Storage Integration:** Optimized storage via **Cloudflare R2**.
- **Responsive Design:** Modern interface optimized for **desktop and mobile**.
- **Shareable Links (Optional):** Securely share files with custom links.
- **Full-Stack Architecture:** Backend in **Spring Boot**, frontend in **Next.js**.

---

## 🛠 Tech Stack

| Layer       | Technology                                   |
|------------|----------------------------------------------|
| Frontend   | Next.js, React, TailwindCSS                  |
| Backend    | Spring Boot, Java 21                         |
| Database   | H2 / MySQL / PostgreSQL                      |
| Storage    | Cloudflare R2                                |
| Deployment | Oracle Cloud VM (Backend), Vercel (Frontend) |

---

## ⚙️ Installation

Clone the repository:

```bash
    git clone https://github.com/jeet7122/personal_cloud_storage.git
    cd personal_cloud_storage
```

## Backend (Spring Boot)
```bash
    cd photo-store-backend
    ./mnvw clean install
    ./mvnw spring-boot:run
```

The backend will run at http://localhost:8080.

## Frontend (Next.js)
```bash
    cd photo-store-frontend
    npm install
```

#### Create a .env.local file:
```dotenv
NEXT_PUBLIC_API_URL=http://<your-oracle-vm-ip>:8080/api
CLOUDFLARE_ACCOUNT_ID=<your_account_id>
CLOUDFLARE_API_TOKEN=<your_api_token>
```

#### Run Frontend
```bash
    npm run dev
```

Open http://localhost:3000 to view the app.

---

## 💻 Usage
1. Sign up or log in securely.
2. Upload files via drag-and-drop or file selector.
3. Download files or folders effortlessly.
4. Organize your storage with folders and labels.

---

## 🌐 Deployment
#### Backend (Oracle Cloud VM):
* Deploy Spring Boot on a free Oracle Cloud VM. 
* Ensure Java 21 is installed and VM allows traffic on port 8080. 
* Run the backend using Maven commands.

### Frontend (Vercel):
* Connect the GitHub repo to Vercel. 
* Add environment variables in the Vercel dashboard. 
* Deploy and the frontend communicates with your Oracle Cloud backend.

## 🔮 Future Enhancements
* Advanced search and filtering for large datasets. 
* Role-based access and sharing permissions.

---
## 🤝 Contributing
1. Fork the repository 
2. Create a branch (git checkout -b feature/YourFeature)
3. Commit your changes (git commit -m 'Add feature')
4. Push the branch (git push origin feature/YourFeature)
5. Open a Pull Request

---
