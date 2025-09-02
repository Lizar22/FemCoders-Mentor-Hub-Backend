# 💻 Femcoders Mentor Hub

![Project Status](https://img.shields.io/badge/status-active-success)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

**Femcoders Mentor Hub** is a platform designed to connect **mentors** with **mentees** in the tech field. Its goal is to create an inclusive, safe, and accessible space where women starting their programming careers can find guidance, support, and mentorship.

It provides a safe and empowering space where mentors can share knowledge and mentees can grow professionally.  

This project is being built as a **prototype (MVP)** with scalability in mind, ready to expand with new features in the future.

<br>

## 📑 Table of Contents
- [🚀 MVP Features](#-mvp-features)
- [🛠️ Technologies & Tools](#-technologies--tools)
- [📌 API Endpoints](#-api-endpoints)
- [📥 Installation & Setup](#-installation--setup)
- [🧪 Running Tests](#-running-tests)
- [🐳 Docker Setup](#-docker-setup-)
- [📧 MailHog Setup](#-mailhog-setup-for-development)
- [🎯 Project Goal](#-project-goal)
- [🤝 Contributing](#-contributing)
- [📝 License](#-license)
- [💜 Created with 💜 by Lizar22](#-created-with--by-lizar22-)


<br>

## 🚀 MVP Features
- **User registration and authentication** with JWT (roles: `MENTOR` and `MENTEE`).
- **Mentor profiles**:  
  - Public routes to explore all mentors or view a single mentor profile.  
  - Private routes for each mentor to create, update, or delete their own profile.  
- **Data validation** for DTOs (full name, technologies, level, bio).  
- **Standardized error handling** using `@ControllerAdvice` and an `ErrorCode` system.  
- **Feature-based modular architecture** (`auth`, `mentors`, `requests`, `security`).

<br>

## 🛠️ Technologies & Tools

### Backend
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?logo=springboot&logoColor=white)

### Security
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)

### Persistence
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?logo=hibernate&logoColor=white)  
![MySQL](https://img.shields.io/badge/MySQL-Production-4479A1?logo=mysql&logoColor=white)  
![H2](https://img.shields.io/badge/H2-Testing-00599C?logo=databricks&logoColor=white)

### Testing
![JUnit5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)  
![Spring Boot Test](https://img.shields.io/badge/Spring%20Boot%20Test-Testing-blue)  
![MockMvc](https://img.shields.io/badge/MockMvc-Testing-blue)

### Email
![Spring Mail](https://img.shields.io/badge/Spring%20Mail-Email-6DB33F?logo=spring&logoColor=white)  
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Templates-005F0F?logo=thymeleaf&logoColor=white)  
![MailHog](https://img.shields.io/badge/MailHog-Dev%20Mail-FF69B4)

### Documentation
![OpenAPI](https://img.shields.io/badge/OpenAPI-3-6BA539?logo=openapiinitiative&logoColor=white)  
![Swagger](https://img.shields.io/badge/Swagger-Docs-85EA2D?logo=swagger&logoColor=black)

### Validation
![Spring Validation](https://img.shields.io/badge/Spring%20Validation-BuiltIn-6DB33F?logo=spring&logoColor=white)

### Infrastructure & DevOps
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?logo=docker&logoColor=white)  
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Orchestration-2496ED?logo=docker&logoColor=white)  
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI-2088FF?logo=githubactions&logoColor=white)

### Utilities
![Lombok](https://img.shields.io/badge/Lombok-Java%20Helper-BC2C1A)

### Design & Workflow
![Canva](https://img.shields.io/badge/Canva-Design-00C4CC?logo=canva&logoColor=white)  
![Jira](https://img.shields.io/badge/Jira-Project%20Management-0052CC?logo=jira&logoColor=white)  
![Confluence](https://img.shields.io/badge/Confluence-Docs-85EA2D?logo=swagger&logoColor=black)  
![Figma](https://img.shields.io/badge/Figma-Design-00C4CC?logo=canva&logoColor=white)  
![dbdiagram.io](https://img.shields.io/badge/dbdiagram.io-ERD-FF9900)


<br>

## 📌 API Endpoints

### 🔑 Authentication
| Method   | Endpoint              | Description                     | Auth | Role |
|----------|----------------------|---------------------------------|------|------|
| **POST** | `/api/auth/register`  | Register a new user (mentor or mentee) | ![No](https://img.shields.io/badge/Auth-No-red) | N/A |
| **POST** | `/api/auth/login`     | Login with username/email + password | ![No](https://img.shields.io/badge/Auth-No-red) | N/A |

### 👩‍🏫 Mentor Profiles
| Method     | Endpoint           | Description                         | Auth | Role |
|------------|------------------|-------------------------------------|------|------|
| **GET**    | `/api/mentors`     | List all mentors                    | ![No](https://img.shields.io/badge/Auth-No-red) | N/A |
| **GET**    | `/api/mentors/{id}`| Get mentor profile by ID            | ![No](https://img.shields.io/badge/Auth-No-red) | N/A |
| **POST**   | `/api/mentors`     | Create mentor profile (self)        | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentor](https://img.shields.io/badge/Role-MENTOR-blue) |
| **PUT**    | `/api/mentors/me`  | Update authenticated mentor profile | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentor](https://img.shields.io/badge/Role-MENTOR-blue) |
| **DELETE** | `/api/mentors/me`  | Delete authenticated mentor profile | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentor](https://img.shields.io/badge/Role-MENTOR-blue) |

### 📩 Mentoring Requests
| Method     | Endpoint                                   | Description                                   | Auth | Role |
|------------|-------------------------------------------|-----------------------------------------------|------|------|
| **GET**    | `/api/mentoring-requests`                  | List mentoring requests for the user          | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentor](https://img.shields.io/badge/Role-MENTOR-blue) / ![Mentee](https://img.shields.io/badge/Role-MENTEE-purple) |
| **POST**   | `/api/mentoring-requests`                  | Create a new mentoring request                | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentee](https://img.shields.io/badge/Role-MENTEE-purple) |
| **PUT**    | `/api/mentoring-requests/{id}/respond`    | Respond to a mentoring request (accept/decline) | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentor](https://img.shields.io/badge/Role-MENTOR-blue) |

<br>

## 📥 Installation & Setup

1. **Clone the repository**
```bash
git clone https://github.com/your-username/femcoders-mentor-hub.git
cd femcoders-mentor-hub
````

2. **Set up environment variables** (example `.env` file)

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/femcoders-mentor-hub
SPRING_DATASOURCE_USERNAME=yourusername
SPRING_DATASOURCE_PASSWORD=yourpassword
JWT_SECRET=yourjwtsecret
```

3. **Build the project**

```bash
./mvnw clean install
```

4. **Run the application**

```bash
./mvnw spring-boot:run
```

<br>

## 🧪 Running Tests

```bash
./mvnw test
```

> Tests use an **H2 in-memory database** to avoid affecting production data.

<br>

## 🐳 Docker Setup 

You can either **build the image locally** or **pull it directly from Docker Hub**.

**Option 1 – Build locally**
```bash
docker build -t femcoders-mentor-hub .
docker run -p 8080:8080 femcoders-mentor-hub
```

**Option 2 – Use pre-built image from Docker Hub**

```bash
docker pull your-dockerhub-username/femcoders-mentor-hub:latest
docker run -p 8080:8080 your-dockerhub-username/femcoders-mentor-hub:latest
```

The API will be available at:
👉 `http://localhost:8080/api/`

<br>

## 📧 MailHog Setup (for development)

To safely test emails without sending them to real addresses, use [MailHog](https://github.com/mailhog/MailHog).

1. **Run MailHog with Docker**

```bash
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
````

* SMTP server available at `localhost:1025`
* Web UI available at `http://localhost:8025`

2. **Configure Spring Boot for development**

Add this to your `application.properties` (or `.env`):

```properties
SPRING_MAIL_HOST=localhost
SPRING_MAIL_PORT=1025
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=
```

3. **View emails**

Open [http://localhost:8025](http://localhost:8025) to see captured emails.
No real emails will be sent outside your local environment.

<br>

## 🎯 Project Goal

The purpose of this project is to serve as a **functional MVP** for an inclusive mentorship platform.
The architecture allows scalability for future features, such as:

* Mentee profiles
* Post-Session Feedback/Reviews
* View mentoring sessions in calendar
* Mentor-mentee matching system
* Secure internal messaging


<br>

## 🤝 Contributing

Contributions are welcome! You can help by:

* Improving architecture
* Adding new features
* Writing tests
* Fixing bugs

Please open an **issue** or **pull request** to collaborate 😊

<br>

## 📝 License

This project is open source and is licensed under the [Apache License 2.0].

Copyright 2025 FemCodersMentorHub.

You are free to use, modify, and distribute this code, as long as you keep this notice and give proper credit to the original author.

The license also covers any patents related to this project; all patent rights remain with the author.


<br>

## 💜 Created with 💜 by Lizar22 

**FemCoders Mentor Hub** is a respectful, inclusive, and scalable mentorship platform designed to amplify and support **women’s voices in tech**.

Through this project, I have been able to apply best practices in backend development, while building software that puts **equality** and **collaboration** at the center.
