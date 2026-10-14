# ProblemHub Deployment & Viva Presentation Guide

---

## 1. Why Not Vercel? (Technical Explanation)

| Feature | Vercel | ProblemHub Requirement |
| :--- | :--- | :--- |
| **Runtime** | Serverless Node.js, Python, Go, Edge Functions | Long-running Java 17 Virtual Machine (JVM) |
| **Process Model** | Ephemeral, stateless execution (max ~15-60s timeout) | Persistent Spring Boot embedded Apache Tomcat daemon |
| **Database** | Read-only / external HTTP APIs (e.g. Prisma serverless) | Stateful TCP socket connection pool (HikariCP + MySQL) |
| **Build Target** | Static assets, HTML/JS/CSS bundles, lambda handlers | Maven `.jar` compiled executable bytecode |

> **Conclusion**: Vercel is designed for Jamstack frontends (Next.js, React, Astro, static HTML). Full-stack monolithic enterprise Java applications with direct MySQL connections cannot run on Vercel's serverless infrastructure.

---

## 2. Recommended Presentation Setup: Localhost (`localhost:8080`)

> [!IMPORTANT]
> **Why Indian University Viva Evaluators Prefer Localhost:**
> 1. **Zero Wi-Fi Dependency**: College Wi-Fi networks often have captive portals, blocked ports, or high latency during lab sessions. Running locally ensures your demo never crashes due to internet dropouts.
> 2. **Verification of Authenticity**: External examiners specifically ask students to:
>    - Show the running terminal / console logs to verify that you compiled and launched the server.
>    - Open MySQL CLI or MySQL Workbench to prove live database queries execute and state persists.
> 3. **Instant Response Time**: Zero cold starts or latency delays during your viva.

### How to Run Locally for Presentation:
1. **Ensure MySQL is running:**
   ```bash
   # Check MySQL status
   brew services list   # On macOS
   # or
   mysql -u root -p
   ```
2. **Start the Application:**
   ```bash
   cd /Users/Farooq/newProblemHUb
   ./mvnw spring-boot:run
   ```
3. **Open in Browser:**
   - Homepage: `http://localhost:8080/`
   - Interactive REST API Docs: `http://localhost:8080/api-docs`
   - Platform Analytics: `http://localhost:8080/analytics`

---

## 3. Cloud Deployment Options (If a Public Link is Needed)

If your college professor insists on a live public URL, deploy to platforms that natively support Docker containers and relational databases:

### Option A: Railway.app (Easiest & Fastest)
1. Push your code to GitHub.
2. Sign in to [Railway.app](https://railway.app/) using GitHub.
3. Click **New Project** → **Provision MySQL**.
4. In the MySQL database settings, open the Query tab and run the script in [`database.sql`](./database.sql).
5. Click **New** → **GitHub Repo** → select your ProblemHub repository.
6. Railway will automatically detect the [`Dockerfile`](./Dockerfile) and build it.
7. Set the Environment Variables in Railway dashboard:
   - `DB_URL`: `jdbc:mysql://${{MYSQL_HOST}}:${{MYSQL_PORT}}/${{MYSQL_DATABASE}}?useSSL=false`
   - `DB_USERNAME`: `${{MYSQL_USER}}`
   - `DB_PASSWORD`: `${{MYSQL_PASSWORD}}`
   - `PORT`: `8080`
8. Generate a public domain (e.g., `problemhub.up.railway.app`).

### Option B: Render.com
1. Sign up on [Render.com](https://render.com/).
2. Create a **Web Service** linked to your GitHub repository.
3. Choose **Docker** as the runtime (Render will automatically detect the [`Dockerfile`](./Dockerfile)).
4. Attach an external free MySQL database (such as Aiven MySQL or PlanetScale).
5. Set environment variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.

---

## 4. 1-Command Local Containerized Stack (Docker Compose)

If testing on any machine without installing MySQL or Java:
```bash
docker-compose up --build
```
This automatically spins up:
- MySQL 8.0 on port 3307 with `database.sql` pre-loaded.
- ProblemHub Java 17 app on port 8080.

---

## 5. Viva Presentation Demo Credentials

Use these 1-click credentials to switch roles seamlessly in front of the examiner:

| Role | Email | Password | Viva Demonstration Purpose |
| :--- | :--- | :--- | :--- |
| **Student** | `rahul.student@problemhub.com` | `password123` | Demonstrates Content-Based Recommendations, project submission, and tracking. |
| **Faculty Guide** | `prof.sharma@problemhub.com` | `password123` | Demonstrates faculty review, milestone grading, and approval workflow. |
| **Industry NGO** | `contact@cleanearthngo.org` | `password123` | Demonstrates posting real-world challenges and reviewing past solutions. |
| **Admin** | `admin@problemhub.com` | `password123` | Demonstrates administrative control, challenge approvals, and analytics. |

*(Quick switch URL: `http://localhost:8080/demo-login?role=STUDENT` / `FACULTY` / `ORGANIZATION` / `ADMIN`)*
