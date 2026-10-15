# 💡 ProblemHub

> **"Real Problems. Meaningful Projects. Lasting Impact."**  
> A centralized platform connecting students with authentic challenges from industries, startups, NGOs, government bodies, and hackathons — while preserving completed solutions for future batches.

---

## 📌 Project Overview

ProblemHub is designed specifically as an undergraduate engineering college mini/major project. It directly addresses the common academic dilemma where students repeatedly build repetitive, low-impact applications (like basic book stores or generic CRUDs) while local organizations, healthcare clinics, and environmental initiatives face genuine operational bottlenecks.

With ProblemHub:
1. **Organizations & Innovation Cells** crowdsource authentic problem statements.
2. **Students** discover problems matched to their specific skills, interests, and academic year.
3. **Teams** track ongoing project progress with status milestones and GitHub links.
4. **Graduating Batches** archive verified solutions with architectures, test results, and documented limitations.
5. **Future Students** can **"Build on This Project"** to develop Version 2.0 without reinventing the wheel.

---

## 🛠️ Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Language** | Java 17+ / Java 26 | Beginner-friendly, clean object-oriented design |
| **Framework** | Spring Boot 3.3.x | Lightweight Web MVC (Controllers & Services) |
| **Database Access** | **Direct JDBC** | PreparedStatement & ResultSet (Strictly **No JPA/Hibernate**, **No MongoDB**) |
| **Database** | MySQL 8.0+ | 13 normalized tables with foreign keys and indexes |
| **Connection Pool**| HikariCP | High-performance connection pooling |
| **Password Security**| BCrypt | Salted password hashing via `spring-security-crypto` |
| **Frontend** | HTML5, CSS3, JavaScript | Modern responsive UI, Bootstrap 5.3, Bootstrap Icons |
| **Templating** | Thymeleaf | Server-side template rendering with reusable fragments |
| **Data Viz** | Chart.js 4.4 | Interactive domain and project analytics charts |
| **Build Tool** | Apache Maven 3.9+ | Built-in Maven wrapper (`mvnw.cmd` / `mvnw`) |

---

## 🏛️ System Architecture

The project strictly follows a transparent, 3-tier layered architecture pattern:

```
[Browser / User]
       ↓ (HTTP Requests & HTML Templates)
Controller Layer (Spring MVC)
       ↓ (Transfers DTOs / Models)
Service Layer (Business Logic & Recommendation Engine)
       ↓ (Direct DAO Invocations)
DAO Layer (Data Access Objects)
       ↓ (try-with-resources)
JDBC Connection & PreparedStatement (SQL Injection Prevention)
       ↓ (Structured Queries)
MySQL Database (problemhub_db)
```

### 📂 Directory & Package Structure

```
ProblemHub 2/
├── pom.xml                               # Maven project dependencies
├── database.sql                          # MySQL schema and realistic demo dataset
├── README.md                             # Complete project documentation
├── mvnw & mvnw.cmd                       # Self-contained Maven wrapper
├── .mvn/                                 # Maven wrapper configuration
└── src/
    └── main/
        ├── java/com/problemhub/
        │   ├── ProblemHubApplication.java # Spring Boot entrypoint
        │   ├── config/
        │   │   ├── DatabaseConfig.java    # Database configuration
        │   │   ├── DatabaseConnection.java# Reusable direct JDBC connection provider
        │   │   ├── AuthInterceptor.java   # Session & role-based route guard
        │   │   └── WebMvcConfig.java      # Interceptor registry
        │   ├── controller/
        │   │   ├── HomeController.java    # Landing page & About page
        │   │   ├── AuthController.java    # Login, register, logout, demo switcher
        │   │   ├── ProblemController.java # Discovery, filters, details, post problem
        │   │   ├── ProjectController.java # Student project tracking, progress updates
        │   │   ├── SolutionController.java# Solutions library, archiving, star ratings
        │   │   ├── BookmarkController.java# Saved problems management
        │   │   ├── UserController.java    # Student/Org dashboards & Profile edit
        │   │   ├── AdminController.java   # Moderation of problems & solutions
        │   │   └── AnalyticsController.java# Visual platform data analytics
        │   ├── service/
        │   │   ├── AuthService.java       # User authentication & registration
        │   │   ├── ProblemService.java    # Problem discovery & status workflow
        │   │   ├── ProjectService.java    # Project management
        │   │   ├── SolutionService.java   # Knowledge base archive & ratings
        │   │   ├── BookmarkService.java   # Bookmark toggling
        │   │   ├── UserService.java       # User profile operations
        │   │   ├── RecommendationService.java # Content-based skill matching algorithm
        │   │   └── AnalyticsService.java  # Metrics aggregation
        │   ├── dao/
        │   │   ├── UserDAO.java           # JDBC DAO for users
        │   │   ├── ProblemDAO.java        # JDBC DAO for problems
        │   │   ├── ProjectDAO.java        # JDBC DAO for projects
        │   │   ├── SolutionDAO.java       # JDBC DAO for solutions
        │   │   ├── RatingDAO.java         # JDBC DAO for solution ratings & reviews
        │   │   ├── BookmarkDAO.java       # JDBC DAO for bookmarks
        │   │   ├── OrganizationDAO.java   # JDBC DAO for organizations
        │   │   └── AnalyticsDAO.java      # JDBC DAO for charts & metrics
        │   ├── model/
        │   │   ├── User.java              # User entity
        │   │   ├── Organization.java      # Organization entity
        │   │   ├── Problem.java           # Problem entity
        │   │   ├── Project.java           # Project entity
        │   │   ├── Solution.java          # Solution entity
        │   │   ├── Rating.java            # Rating entity
        │   │   ├── Bookmark.java          # Bookmark entity
        │   │   └── RecommendationDTO.java # Recommendation match DTO
        │   └── util/
        │       └── PasswordUtil.java      # BCrypt password encoder
        └── resources/
            ├── application.properties     # Database & server configurations
            ├── static/
            │   ├── css/styles.css         # Modern educational-tech design system
            │   └── js/main.js             # Client-side helpers & confirmation modals
            └── templates/                 # Thymeleaf HTML5 views
                ├── fragments/             # Reusable header, navbar, footer, alerts
                ├── index.html             # Professional landing page
                ├── about.html             # Platform mission & architecture guide
                ├── auth/                  # Login & registration views
                ├── problems/              # Explore catalogue, details & submission
                ├── projects/              # My projects tracking & start project
                ├── solutions/             # Solutions library & submission form
                ├── bookmarks/             # Saved problems list
                ├── user/                  # Student, Org dashboards & Profile edit
                ├── admin/                 # Admin console & verification tables
                └── analytics/             # Chart.js platform analytics
```

---

## ⚙️ Prerequisites & Setup

### 1. Requirements
- **Java**: JDK 17, 21, or 26 installed and accessible on PATH or `JAVA_HOME`.
- **MySQL**: MySQL Server 8.0+ running on port `3306`.
- **Operating System**: Windows / Linux / macOS.

### 2. MySQL Database Setup
Open your terminal or MySQL command client and execute the provided `database.sql` script:

```bash
# Using MySQL CLI
mysql -u root -p < database.sql
```
*(When prompted for password, enter your MySQL password, e.g. `dkte`)*

This script automatically creates the `problemhub_db` schema, builds all 13 tables, and inserts comprehensive, realistic demo data (11 authentic problems, 6 users, projects, solutions, ratings, and bookmarks).

### 3. Application Configuration
Verify or modify database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/problemhub_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=dkte
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
server.port=8080
```

---

## 🚀 How to Run the Application

The repository contains the bundled Maven wrapper, so you **do not need Maven installed globally**.

### Windows:
```powershell
# Set JAVA_HOME (if not already set)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26"

# Run Spring Boot application
.\mvnw.cmd spring-boot:run
```

### Linux / macOS:
```bash
./mvnw spring-boot:run
```

Once started, open your web browser and navigate to:
```
http://localhost:8080
```

---

## 🔑 Demo Login Accounts

All pre-seeded demo accounts use the standard password: **`password123`**

| Role | Email | Password | Details |
|---|---|---|---|
| **Student (Lead)** | `rahul.student@problemhub.com` | `password123` | 3rd Year AI & DS student with active & completed projects |
| **Student** | `sneha.student@problemhub.com` | `password123` | 4th Year CSE student exploring IoT and Cloud |
| **Faculty Guide** | `prof.sharma@problemhub.com` | `password123` | Project mentor reviewing undergraduate capstones |
| **Organization** | `contact@cleanearthngo.org` | `password123` | NGO representative posting environmental challenges |
| **Platform Admin**| `admin@problemhub.com` | `password123` | Administrator moderating problems & solutions |

> 💡 **Demo Role Switcher**: The login page (`/login`) includes **Quick Role Switcher Buttons** that allow you to instantly switch between Student, Faculty, Organization, and Admin perspectives with a single click.

---

## 🎯 Key Features & Technical Highlights

### 1. Direct JDBC Architecture (No JPA/Hibernate)
- All database operations are explicitly handled in the DAO layer using `java.sql.PreparedStatement` and `java.sql.ResultSet`.
- Connection pooling is provided by HikariCP through `DatabaseConnection.getConnection()`.
- Every database resource is wrapped in modern **`try-with-resources`** blocks to prevent connection leaks.
- Zero concatenated SQL strings ensures 100% security against SQL injection attacks.

### 2. Explainable Content-Based Recommendation Algorithm
The platform features an understandable, transparent score-based recommendation engine (`RecommendationService.java`):
- **Technology Match**: `+30 points` (Matches student skills with required skills/suggested tech).
- **Domain Match**: `+25 points` (Aligns problem domain with student interests).
- **Difficulty Match**: `+20 points` (Matches year of study: 3rd year &rarr; Intermediate; 4th year &rarr; Advanced).
- **Duration Match**: `+15 points` (Standard semester timeline).
- **Project Type Match**: `+10 points` (Mini Project vs Major Project alignment).
- **Explainability**: Displays clear reasons on the student dashboard: *"Recommended because it matches your Java + MySQL skills and aligns with your interest in Healthcare (+55 pts)."*

### 3. Continuous Project Lifecycle (Preserve & Extend)
- A problem statement can have **multiple solutions** from different student batches across academic years.
- Every solution view contains a dedicated **"How Can You Improve This?"** section outlining known limitations and future scope.
- Clicking **[Start New Project From This Solution]** immediately registers a new team project linked to the verified problem, preventing repetitive academic duplication.

### 4. Role-Based Moderation
- Challenges posted by partner organizations or faculty undergo review (`PENDING` &rarr; `APPROVED`).
- Admin console (`/admin`) allows 1-click approvals and rejections.
- Star ratings (1–5 stars) and peer reviews allow faculty and seniors to evaluate project quality.

---

## 📄 License & Academic Integrity
Developed as an academic software engineering project. Free for educational use and student collaboration.
