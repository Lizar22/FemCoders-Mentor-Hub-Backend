# 💻 Femcoders Mentor Hub

![Project Status](https://img.shields.io/badge/status-active-success)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

**Femcoders Mentor Hub** is a platform designed to connect **mentors** with **mentees** in the tech field. Its goal is to create an inclusive, safe, and accessible space where women starting their programming careers can find guidance, support, and mentorship.

It provides a safe and empowering space where mentors can share knowledge and mentees can grow professionally.  

This project is being built as a **Minimum Viable Product (MVP)** with scalability in mind, ready to expand with new features in the future.

<br>

## 📑 Table of Contents
- [MVP Features](#-mvp-features)
- [Technologies Used](#-technologies-used)
- [Main Endpoints](#-main-endpoints)
- [Example API Requests](#-example-api-requests)
- [Installation & Setup](#-installation--setup)
- [Running Tests](#-running-tests)
- [Docker Setup](#-docker-setup-optional)
- [Project Goal](#-project-goal)
- [Contributing](#-contributing)
- [License](#-license)

<br>

## 🚀 MVP Features
- **User registration and authentication** with JWT (roles: `MENTOR` and `MENTEE`).
- **Mentor profiles**:  
  - Public routes to explore all mentors or view a single mentor profile.  
  - Private routes for each mentor to create, update, or delete their own profile.  
- **Data validation** for DTOs (full name, bio, technologies, level, etc.).  
- **Standardized error handling** using `@ControllerAdvice` and an `ErrorCode` system.  
- **Feature-based modular architecture** (`auth`, `mentors`, `mentees`, `security`).

<br>

## 🛠️ Technologies Used
- **Backend**: Java 21, Spring Boot 3  
- **Security**: Spring Security + JWT  
- **Persistence**: JPA/Hibernate, H2 (tests), MySQL (production)  
- **Testing**: JUnit 5, Spring Boot Test, MockMvc, H2  
- **Others**: Lombok

<br>

## 📌 API Endpoints

| Method | Endpoint            | Description                         | Auth | Role |
|--------|---------------------|-------------------------------------|------|------|
| **POST** | `/api/auth/register` | Register a new user (mentor or mentee) | ![No](https://img.shields.io/badge/Auth-No-red) | N/A |
| **POST** | `/api/auth/login`    | Login with username/email + password | ![No](https://img.shields.io/badge/Auth-No-red) | N/A |
| **GET**  | `/api/mentors`       | List all mentors                    | ![No](https://img.shields.io/badge/Auth-No-red) | N/A |
| **GET**  | `/api/mentors/{id}`  | Get mentor profile by ID            | ![No](https://img.shields.io/badge/Auth-No-red) | N/A |
| **POST** | `/api/mentors`       | Create mentor profile (self)        | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentor](https://img.shields.io/badge/Role-MENTOR-blue) |
| **PUT**  | `/api/mentors/me`    | Update authenticated mentor profile | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentor](https://img.shields.io/badge/Role-MENTOR-blue) |
| **DELETE** | `/api/mentors/me`  | Delete authenticated mentor profile | ![Yes](https://img.shields.io/badge/Auth-Yes-green) | ![Mentor](https://img.shields.io/badge/Role-MENTOR-blue) |

<br>

## 📥 Installation & Setup

1. **Clone the repository**
```bash
git clone https://github.com/your-username/femcoders-mentor-hub.git
cd femcoders-mentor-hub
````

2. **Set up environment variables** (example `.env` file)

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/femcoders
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

You can either build the image locally **or** pull it directly from Docker Hub.

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

## 🎯 Project Goal

The purpose of this project is to serve as a **functional MVP** for an inclusive mentorship platform.
The architecture allows scalability for future features, such as:

* Mentee profiles
* Mentor-mentee matching system
* Secure internal messaging

<br>

## 🤝 Contributing

Contributions are welcome! You can help by:

* Improving architecture
* Adding new features
* Writing tests
* Fixing bugs

Please open an **issue** or **pull request** to collaborate ☺

<br>

## 📄 License

This project is licensed under the **MIT License**.


