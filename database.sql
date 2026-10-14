-- ==========================================================
-- ProblemHub Database Schema & Initial Demo Data
-- Real-World Problems -> Student Projects
-- Compatible with MySQL 8.0+
-- ==========================================================

CREATE DATABASE IF NOT EXISTS problemhub_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE problemhub_db;

-- Drop tables in reverse order of foreign key dependencies
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS bookmarks;
DROP TABLE IF EXISTS verification;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS solutions;
DROP TABLE IF EXISTS project_members;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS problem_technologies;
DROP TABLE IF EXISTS problems;
DROP TABLE IF EXISTS technologies;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS organizations;
DROP TABLE IF EXISTS users;

-- ==========================================================
-- 1. USERS TABLE
-- ==========================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'FACULTY', 'ORGANIZATION', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    college VARCHAR(200),
    department VARCHAR(150),
    year_of_study VARCHAR(50),
    skills TEXT,
    interests TEXT,
    bio TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- ==========================================================
-- 2. ORGANIZATIONS TABLE
-- ==========================================================
CREATE TABLE organizations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    org_name VARCHAR(200) NOT NULL,
    org_type VARCHAR(100) NOT NULL, -- Industry, Startup, NGO, Government, Educational Institution, Hackathon, Innovation Cell
    website VARCHAR(255),
    description TEXT,
    contact_email VARCHAR(150),
    contact_phone VARCHAR(50),
    is_verified BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_org_name (org_name)
) ENGINE=InnoDB;

-- ==========================================================
-- 3. CATEGORIES / DOMAINS TABLE
-- ==========================================================
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ==========================================================
-- 4. TECHNOLOGIES TABLE
-- ==========================================================
CREATE TABLE technologies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ==========================================================
-- 5. PROBLEMS TABLE
-- ==========================================================
CREATE TABLE problems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    real_world_context TEXT,
    current_challenges TEXT,
    expected_outcome TEXT,
    why_it_matters TEXT,
    deliverables TEXT,
    possible_extensions TEXT,
    domain VARCHAR(100) NOT NULL,
    difficulty VARCHAR(50) NOT NULL, -- Beginner, Intermediate, Advanced
    duration VARCHAR(50) NOT NULL,   -- 1–2 Weeks, 1 Month, 2–3 Months, 3+ Months
    project_type VARCHAR(50) NOT NULL, -- Mini Project, Major Project, Hackathon, Research
    source_type VARCHAR(100) NOT NULL, -- Industry, Startup, NGO, Government, Educational, Hackathon, Innovation Cell
    organization_name VARCHAR(200) NOT NULL,
    contact_info VARCHAR(255),
    required_skills TEXT,
    suggested_tech VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'APPROVED', -- PENDING, APPROVED, REJECTED
    is_verified BOOLEAN NOT NULL DEFAULT TRUE,
    views_count INT NOT NULL DEFAULT 0,
    created_by_user_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_problems_domain (domain),
    INDEX idx_problems_difficulty (difficulty),
    INDEX idx_problems_status (status)
) ENGINE=InnoDB;

-- ==========================================================
-- 6. PROBLEM_TECHNOLOGIES (Many-to-Many junction)
-- ==========================================================
CREATE TABLE problem_technologies (
    problem_id INT NOT NULL,
    technology_id INT NOT NULL,
    PRIMARY KEY (problem_id, technology_id),
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    FOREIGN KEY (technology_id) REFERENCES technologies(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================================================
-- 7. PROJECTS TABLE
-- ==========================================================
CREATE TABLE projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    problem_id INT NOT NULL,
    student_user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    team_members TEXT,
    technologies_used VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS', -- PROPOSED, IN_PROGRESS, COMPLETED, ABANDONED
    progress_percent INT NOT NULL DEFAULT 10,
    start_date DATE,
    completion_date DATE,
    repo_url VARCHAR(255),
    documentation_url VARCHAR(255),
    faculty_feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    FOREIGN KEY (student_user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_projects_status (status),
    INDEX idx_projects_student (student_user_id)
) ENGINE=InnoDB;

-- ==========================================================
-- 8. PROJECT_MEMBERS TABLE
-- ==========================================================
CREATE TABLE project_members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    user_id INT NULL,
    member_name VARCHAR(150) NOT NULL,
    role_title VARCHAR(100) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ==========================================================
-- 9. SOLUTIONS TABLE
-- ==========================================================
CREATE TABLE solutions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NULL,
    problem_id INT NOT NULL,
    submitted_by_user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    team_names VARCHAR(255) NOT NULL,
    college VARCHAR(200) NOT NULL,
    completion_year INT NOT NULL,
    technologies VARCHAR(255) NOT NULL,
    solution_overview TEXT NOT NULL,
    architecture_details TEXT,
    implementation_details TEXT,
    key_features TEXT,
    results TEXT,
    challenges TEXT,
    limitations TEXT,
    future_scope TEXT,
    github_url VARCHAR(255),
    documentation_url VARCHAR(255),
    demo_url VARCHAR(255),
    screenshots_json TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'APPROVED', -- PENDING, APPROVED, REJECTED
    is_verified BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    FOREIGN KEY (submitted_by_user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_solutions_problem (problem_id),
    INDEX idx_solutions_status (status)
) ENGINE=InnoDB;

-- ==========================================================
-- 10. BOOKMARKS TABLE
-- ==========================================================
CREATE TABLE bookmarks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    problem_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_problem (user_id, problem_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================================================
-- 11. RATINGS TABLE
-- ==========================================================
CREATE TABLE ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    solution_id INT NOT NULL,
    user_id INT NOT NULL,
    rating_value INT NOT NULL CHECK (rating_value BETWEEN 1 AND 5),
    review_comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_solution_rating (solution_id, user_id),
    FOREIGN KEY (solution_id) REFERENCES solutions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================================================
-- 12. VERIFICATION AUDIT LOG
-- ==========================================================
CREATE TABLE verification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    entity_type ENUM('PROBLEM', 'SOLUTION', 'ORGANIZATION') NOT NULL,
    entity_id INT NOT NULL,
    verified_by_user_id INT NOT NULL,
    status ENUM('APPROVED', 'REJECTED') NOT NULL,
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (verified_by_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================================================
-- 13. NOTIFICATIONS TABLE
-- ==========================================================
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'INFO',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    link VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;


-- ==========================================================
-- SEED DATA INSERTION
-- Password for all demo accounts is: password123
-- BCrypt Hash: $2a$10$kNeBX2zpVGCiMBJebf.edOn5ITX6GUvCSRyYmP.yC3DZPJkQZqPjC
-- ==========================================================

-- Insert Users
INSERT INTO users (id, full_name, email, password_hash, role, college, department, year_of_study, skills, interests, bio) VALUES
(1, 'Admin ProblemHub', 'admin@problemhub.com', '$2a$10$kNeBX2zpVGCiMBJebf.edOn5ITX6GUvCSRyYmP.yC3DZPJkQZqPjC', 'ADMIN', 'DKTE Society Textile & Engineering Institute', 'Computer Science', 'Faculty Head', 'Java, Spring Boot, MySQL, System Architecture', 'Education, AI & Data Science, Smart City', 'Platform Administrator & Innovation Coordinator at ProblemHub.'),
(2, 'Rahul Sharma', 'rahul.student@problemhub.com', '$2a$10$kNeBX2zpVGCiMBJebf.edOn5ITX6GUvCSRyYmP.yC3DZPJkQZqPjC', 'STUDENT', 'DKTE Society Textile & Engineering Institute', 'AI & Data Science', '3rd Year', 'Java, MySQL, Spring Boot, Python, Machine Learning', 'Healthcare, Environment, Smart City', 'Passionate 3rd year engineering student eager to build real-world software solutions.'),
(3, 'Sneha Patil', 'sneha.student@problemhub.com', '$2a$10$kNeBX2zpVGCiMBJebf.edOn5ITX6GUvCSRyYmP.yC3DZPJkQZqPjC', 'STUDENT', 'Government College of Engineering', 'Computer Science & Engineering', '4th Year', 'Java, SQL, React, IoT, Cloud', 'Education, Finance, Agriculture', 'Final year undergraduate exploring distributed systems and scalable backends.'),
(4, 'Dr. Arvind Kulkarni', 'prof.sharma@problemhub.com', '$2a$10$kNeBX2zpVGCiMBJebf.edOn5ITX6GUvCSRyYmP.yC3DZPJkQZqPjC', 'FACULTY', 'DKTE Society Textile & Engineering Institute', 'Computer Science', 'Professor', 'Java, Algorithms, Database Systems', 'Education, Smart Campus, Research', 'Project guide and mentor reviewing undergraduate mini and capstone projects.'),
(5, 'Clean Earth Foundation', 'contact@cleanearthngo.org', '$2a$10$kNeBX2zpVGCiMBJebf.edOn5ITX6GUvCSRyYmP.yC3DZPJkQZqPjC', 'ORGANIZATION', 'Clean Earth NGO', 'Sustainability Division', 'NGO', 'Environmental Science, Waste Auditing', 'Environment, Smart City, Social Impact', 'Non-profit organization tackling municipal solid waste management and recycling automation.'),
(6, 'FinPulse Technologies', 'info@finpulse.io', '$2a$10$kNeBX2zpVGCiMBJebf.edOn5ITX6GUvCSRyYmP.yC3DZPJkQZqPjC', 'ORGANIZATION', 'FinPulse Fintech Labs', 'Fintech Research', 'Startup', 'Financial Modeling, Risk Analytics', 'Finance, AI & Data Science, Cybersecurity', 'Fintech startup pioneering micro-lending verification engines for unbanked communities.');

-- Insert Organizations
INSERT INTO organizations (id, user_id, org_name, org_type, website, description, contact_email, contact_phone, is_verified) VALUES
(1, 5, 'Clean Earth Foundation', 'NGO', 'https://cleanearthfoundation.org', 'NGO focusing on decentralized municipal waste reduction, community composting, and electronic trash recovery.', 'contact@cleanearthngo.org', '+91 9822011223', TRUE),
(2, 6, 'FinPulse Technologies', 'Startup', 'https://finpulse.io', 'Fintech laboratory building risk engines and micro-credit fraud detection algorithms for rural borrowers.', 'info@finpulse.io', '+91 9845012345', TRUE),
(3, NULL, 'Aarogya Seva Trust', 'NGO', 'https://aarogyaseva.org', 'Healthcare NGO deploying mobile medical clinics to rural talukas and remote primary healthcare centres.', 'reach@aarogyaseva.org', '+91 9123456780', TRUE),
(4, NULL, 'DKTE Innovation & Incubation Cell', 'Innovation Cell', 'https://dktes.com/incubation', 'Campus innovation council nurturing student startups, patent proposals, and industrial hardware-software projects.', 'incubation@dktes.com', '+91 2302432340', TRUE),
(5, NULL, 'Municipal Smart City Corporation', 'Government', 'https://smartcitycorp.gov.in', 'City administration agency modernizing municipal transit, signal timing, and public energy infrastructure.', 'traffic-cell@smartcitycorp.gov.in', '+91 2026112233', TRUE);

-- Insert Categories / Domains
INSERT INTO categories (id, name, description, icon) VALUES
(1, 'AI & Data Science', 'Machine learning, predictive models, predictive analytics, computer vision', 'bi-cpu'),
(2, 'Healthcare', 'Telemedicine, clinic management, triage, medical records, patient assistance', 'bi-heart-pulse'),
(3, 'Education', 'Learning portals, assessment automation, skill mapping, campus tools', 'bi-mortarboard'),
(4, 'Environment', 'Waste management, air quality, recycling optimization, water conservation', 'bi-tree'),
(5, 'Smart City', 'Urban transport, smart parking, municipal services, public utilities', 'bi-building'),
(6, 'Agriculture', 'Smart irrigation, soil health, crop disease detection, supply chain tracking', 'bi-flower1'),
(7, 'Finance', 'Fintech, micro-lending, expense auditing, fraud mitigation, digital invoicing', 'bi-cash-coin'),
(8, 'Social Impact', 'Volunteer coordination, transparent NGO donations, community grievance tracking', 'bi-people'),
(9, 'Cybersecurity', 'Vulnerability auditing, phishing detection, campus network access control', 'bi-shield-lock'),
(10, 'Transportation', 'Bus fleet routing, ETA prediction, congestion mitigation, commuter dashboards', 'bi-bus-front');

-- Insert Technologies
INSERT INTO technologies (id, name, category) VALUES
(1, 'Java', 'Backend'),
(2, 'Spring Boot', 'Backend'),
(3, 'MySQL', 'Database'),
(4, 'Python', 'AI/ML'),
(5, 'AI/ML', 'Data Science'),
(6, 'IoT', 'Hardware & Sensors'),
(7, 'Web Development', 'Frontend'),
(8, 'Mobile', 'Mobile Apps'),
(9, 'Cloud', 'Infrastructure'),
(10, 'Data Science', 'Analytics');

-- ==========================================================
-- Insert 10+ Detailed Realistic Problem Statements
-- ==========================================================

INSERT INTO problems (
    id, title, description, real_world_context, current_challenges, expected_outcome,
    why_it_matters, deliverables, possible_extensions, domain, difficulty, duration,
    project_type, source_type, organization_name, contact_info, required_skills, suggested_tech,
    status, is_verified, views_count, created_by_user_id
) VALUES
(
    1,
    'Smart Waste Collection Route Optimization for Municipal Trucks',
    'Municipal garbage trucks currently follow static scheduled routes irrespective of whether bins are completely overflowing or completely empty. This causes massive fuel wastage, missed pickups in dense zones, and environmental health hazards.',
    'In urban municipalities, collection vehicles travel over 120 km daily on predefined paths. Over 40% of bins visited are less than a quarter full, while residential commercial hotspots overflow hours before scheduled arrival.',
    'Lack of dynamic route calculation, unpredictable bin fill rates during weekends/festivals, fuel expense overruns, and absence of an alert dashboard for municipal sanitary inspectors.',
    'A working Java web platform that ingests bin volume telemetry (or simulated ward sensor logs) and calculates the shortest, fuel-optimal route for municipal dump trucks each morning.',
    'Reduces municipal diesel expenditure by an estimated 25%, prevents disease vectors from overflowing garbage, and streamlines city sanitary operations.',
    '1. Web dashboard for Ward Officer showing interactive bin statuses\n2. Dynamic daily route generation engine\n3. Driver turn-by-turn waypoint itinerary\n4. Analytics on fuel saved and tonnage collected',
    'Integration with GPS vehicle trackers, mobile app for truck drivers with offline map caching, and recycling bin weight classification.',
    'Environment',
    'Intermediate',
    '2–3 Months',
    'Major Project',
    'NGO',
    'Clean Earth Foundation',
    'contact@cleanearthngo.org',
    'Java, Object-Oriented Design, Graph Algorithms (Dijkstra/TSP Heuristics), SQL Queries',
    'Java, MySQL, Leaflet/OpenStreetMap, HTML/CSS/JS',
    'APPROVED',
    TRUE,
    142,
    5
),
(
    2,
    'Rural Primary Healthcare Tele-Triage & Clinic Appointment Scheduler',
    'Rural clinics and primary health sub-centres (PHCs) lack resident specialist doctors. Villagers travel up to 60 km only to find clinic queues closed or visiting doctors unavailable. A lightweight, low-bandwidth appointment and symptom triage tool is urgently needed.',
    'In sub-district regions, accredited social health activists (ASHA workers) assist patients with vital checks but must rely on paper registers that are frequently lost or uncoordinated with visiting doctors.',
    'Intermittent rural internet connectivity, high patient volume during harvest seasons, lack of structured clinical history records, and language barriers.',
    'A responsive portal where community healthcare workers can record patient vitals, queue preliminary symptom questionnaires, and schedule confirmed appointments with visiting district physicians.',
    'Prevents unnecessary long journeys for vulnerable rural families, shortens clinic wait times from 4 hours to under 30 minutes, and maintains digital continuity of medical records.',
    '1. Low-bandwidth accessible web application\n2. Priority triage scoring based on vital signs (fever, SpO2, blood pressure)\n3. Doctor roster and automated SMS/token generation\n4. Patient medical history export in PDF',
    'Offline Progressive Web App (PWA) sync, vernacular Marathi/Hindi audio voice prompts for illiterate patients, and telemedicine video consultation room.',
    'Healthcare',
    'Intermediate',
    '1 Month',
    'Mini Project',
    'NGO',
    'Aarogya Seva Trust',
    'reach@aarogyaseva.org',
    'Java, Relational Database Modeling, Session Authentication, Responsive UI',
    'Java, MySQL, Bootstrap 5, Thymeleaf, JDBC',
    'APPROVED',
    TRUE,
    215,
    1
),
(
    3,
    'College Campus Energy Consumption & Peak Load Forecasting',
    'Engineering colleges and university campuses pay substantial penalties when peak electricity demand exceeds contracted maximum power limits. Labs, hostels, and workshops draw erratic power loads without centralized real-time visibility.',
    'The campus infrastructure comprises 14 major blocks with independent distribution panels. Without automated load monitoring, air conditioners, CNC machines, and computing clusters run concurrently during noon hours, tripping substations.',
    'Siloed energy sub-meters, absence of historical usage trend analytics, failure to warn hostel wardens before peak threshold breaches, and lack of energy conservation auditing.',
    'An IoT-connected or data-driven telemetry dashboard that ingests meter readings, forecasts peak electrical load periods, and sends automated throttling warnings to department administrators.',
    'Saves educational institutions up to 15% on monthly commercial electricity tariffs and minimizes blackout risks during university examinations and practical sessions.',
    '1. Time-series power consumption charts by department and block\n2. Peak threshold alert system with email/notification triggers\n3. Daily and monthly billing audit summaries\n4. Department-wise energy conservation leaderboard',
    'Integration with ESP32 smart meter hardware, relay switching for non-essential hostel loads, and solar panel yield correlation.',
    'Smart City',
    'Advanced',
    '2–3 Months',
    'Major Project',
    'Educational',
    'DKTE Innovation & Incubation Cell',
    'incubation@dktes.com',
    'Java, Time-series SQL Aggregations, IoT Data Protocols, Charting',
    'Java, MySQL, IoT, Chart.js, Spring Boot JDBC',
    'APPROVED',
    TRUE,
    98,
    4
),
(
    4,
    'Student Skill-to-Project Matching & Peer Team Formation Engine',
    'Students often form project groups solely based on casual friendships, resulting in unbalanced teams with 4 frontend coders and no backend developer, or lack of machine learning expertise needed for ambitious projects.',
    'During 3rd-year mini-project allocations, faculty coordinators review 60+ team submissions manually. Over 30% of projects stagnate halfway due to irreconcilable skill voids in the group.',
    'Subjective skill self-assessments, difficulty finding compatible teammates with shared problem interests, and faculty overhead in auditing project feasibility.',
    'An intelligent recommendation engine that matches students with complementarily skilled peers and suitable real-world problem statements based on verified skill scores and domain interests.',
    'Ensures well-rounded student teams capable of successfully shipping full-stack products, improving campus project completion rates and hackathon victories.',
    '1. Student profile builder with skill proficiency matrix\n2. Team compatibility recommendation algorithm\n3. Problem-to-team suitability score\n4. Faculty approval and team roster export',
    'Peer code review rating integration, automated GitHub repository collaborator invitation, and hackathon team matching mode.',
    'Education',
    'Intermediate',
    '1 Month',
    'Mini Project',
    'Hackathon',
    'Smart India Hackathon Innovation Cell',
    'hackathons@aicte-india.org',
    'Java, Match Scoring Algorithms, Normalized SQL Schemas, Modern CSS',
    'Java, MySQL, HTML5, Bootstrap 5, JDBC',
    'APPROVED',
    TRUE,
    312,
    1
),
(
    5,
    'NGO Volunteer Coordination & Transparent Donation Impact Tracker',
    'Donors hesitate to contribute to grassroots NGOs because they rarely see concrete proof of how small contributions translate into actual field outcomes like school kits distributed or saplings planted.',
    'Grassroots charitable societies struggle to manage weekend volunteer rosters while maintaining public accountability for physical supplies purchased through micro-donations.',
    'Manual spreadsheet volunteer scheduling leads to no-shows, lack of verifiable ledger for donations under 500 rupees, and opaque feedback to prospective donors.',
    'A public-facing portal where NGOs publish volunteer task drives and itemized campaign budgets, letting donors trace exactly which cause their funds assisted.',
    'Builds donor trust, increases citizen volunteer participation by 40%, and provides grassroots non-profits with a professional digital footprint.',
    '1. Volunteer drive calendar with RSVP and attendance check-in\n2. Campaign milestone tracker showing target vs achieved impact\n3. Public transparency ledger for verified expenditures\n4. Digital volunteer certificate generator',
    'UPI payment gateway integration, geo-tagged photograph uploads for campaign proof, and automated donor tax exemption receipt emailing.',
    'Social Impact',
    'Beginner',
    '1–2 Weeks',
    'Mini Project',
    'NGO',
    'Clean Earth Foundation',
    'contact@cleanearthngo.org',
    'Java, Basic SQL CRUD, Session Management, Form Validation',
    'Java, MySQL, HTML5, CSS3, JDBC',
    'APPROVED',
    TRUE,
    184,
    5
),
(
    6,
    'Automated Urban Traffic Congestion Bottleneck Analyzer',
    'City traffic police lack real-time digital insights into which specific lane intersections experience chronic gridlock during rain, school hours, or emergency vehicle movements.',
    'Traffic signal timings in tier-2 cities remain fixed cycle timers designed years ago, failing to adapt to asymmetric holiday traffic surges and causing kilometer-long snarls.',
    'Absence of unified telemetry from traffic cameras or density sensors, delayed police deployment, and lack of historical congestion heatmaps.',
    'A central monitoring console that ingests intersection vehicle flow reports, highlights recurring congestion hotspots on an interactive map, and suggests adjusted green-signal timings.',
    'Reduces average commuter commute delays by 18%, lowers urban carbon emissions from idling engines, and allows traffic wardens to deploy proactively.',
    '1. Interactive GIS city map with color-coded congestion severity\n2. Algorithmic green-time adjustment calculator\n3. Historical bottleneck time-of-day analytics\n4. Emergency green corridor alert dispatch',
    'Computer vision camera feed integration via OpenCV, integration with Google Maps Distance Matrix API, and automated citizen congestion broadcast.',
    'Smart City',
    'Advanced',
    '3+ Months',
    'Major Project',
    'Government',
    'Municipal Smart City Corporation',
    'traffic-cell@smartcitycorp.gov.in',
    'Java, Graph Data Structures, Multi-threading, Spatial Analytics',
    'Java, Python, MySQL, OpenStreetMap, Chart.js',
    'APPROVED',
    TRUE,
    120,
    1
),
(
    7,
    'Micro-Financing Loan Default & Rule-Based Fraud Detection Platform',
    'Microfinance institutions (MFIs) providing 10,000 to 50,000 rupee credit to self-help groups suffer from ghost identities and overlapping multi-borrower defaults across competing agencies.',
    'Small field loan officers verify applicant credentials using paper passbooks, leaving room for fabricated income certificates and duplicate loans taken simultaneously from three different lenders.',
    'Lack of credit bureau integration for micro-amounts, identity spoofing, delayed delinquency reporting, and vulnerable rural women trapped by predatory informal loan sharks.',
    'A secure verification platform using weighted rule-based fraud scoring and cross-reference checks on household repayment capacity before loan sanction.',
    'Safeguards micro-finance capital, prevents over-indebtedness among low-income households, and accelerates genuine loan approvals from 7 days to 24 hours.',
    '1. Loan applicant onboarding with automated fraud checklist\n2. Risk score calculator (+/- rule factors based on income, debt ratio)\n3. Repayment schedule generator with delinquency warnings\n4. MFI branch manager audit log',
    'Machine learning random forest default probability scoring, SMS repayment reminders, and biometric fingerprint hash verification.',
    'Finance',
    'Advanced',
    '2–3 Months',
    'Major Project',
    'Startup',
    'FinPulse Technologies',
    'info@finpulse.io',
    'Java, Security Best Practices, Financial Math, Complex SQL Queries',
    'Java, MySQL, Spring Boot, JDBC, Bootstrap 5',
    'APPROVED',
    TRUE,
    167,
    6
),
(
    8,
    'Cold Storage Produce Telemetry & Perishable Spoilage Alert System',
    'Over 20% of harvested tomatoes, grapes, and dairy produce in agrarian districts spoils in transit or farm-level cold warehouses due to undetected refrigerant leaks and humidity anomalies.',
    'Farmers store produce in regional co-operative cold storages that maintain temperature logs by hand on clipboards once a day, missing overnight chiller failures.',
    'Delayed detection of temperature spikes, lack of perishable shelf-life estimation, disputes between farmers and warehouse operators over who is responsible for spoiled crates.',
    'A telemetry ingestion platform that monitors simulated temperature and humidity sensors in storage bays, predicting days-to-spoilage and raising urgent SMS/buzzer alerts upon breach.',
    'Protects agrarian farmers from catastrophic harvest loss, ensures food security, and provides cold room operators with auditable storage quality logs.',
    '1. Bay-wise environmental monitoring dashboard\n2. Real-time warning triggers for temperature/humidity excursions\n3. Crop-specific safe threshold lookup engine\n4. Quality certificate generation for buyers',
    'Hardware integration with DHT22 sensors and Raspberry Pi gateway, dynamic produce pricing calculator based on remaining shelf life.',
    'Agriculture',
    'Intermediate',
    '1 Month',
    'Mini Project',
    'Hackathon',
    'DKTE Innovation & Incubation Cell',
    'incubation@dktes.com',
    'Java, Sensor Data Handling, Event Listeners, SQL Schemas',
    'Java, MySQL, IoT, WebSockets, Bootstrap 5',
    'APPROVED',
    TRUE,
    133,
    4
),
(
    9,
    'Phishing URL & Educational Campus Network Threat Analyzer',
    'College campuses face frequent spear-phishing attacks impersonating exam portals, scholarship forms, and faculty payroll notifications, compromising student credentials and lab machines.',
    'Undergraduate students frequently click fraudulent links promising free semester question papers or stipend grants, unknowingly exposing university network credentials.',
    'Sophisticated lookalike domain names (typosquatting), evasion of standard antivirus filters, lack of central threat database for college cyber administrators.',
    'A web application where students and faculty can submit suspicious URLs and emails for instant heuristic security analysis (domain age, typosquatting score, SSL validity, blacklists).',
    'Guards sensitive student academic data, raises campus cybersecurity awareness, and enables IT staff to block malicious hostnames on the college firewall.',
    '1. Instant URL submission and heuristic reputation score engine\n2. Breakdown of detected threat indicators (suspicious TLD, unicode tricks)\n3. Campus-wide threat alert notification feed\n4. Cyber hygiene quiz module for students',
    'Integration with VirusTotal and Google Safe Browsing APIs, automated IP DNS lookup, and browser extension plugin.',
    'Cybersecurity',
    'Intermediate',
    '1 Month',
    'Mini Project',
    'Educational',
    'DKTE Innovation & Incubation Cell',
    'cyberlab@dktes.com',
    'Java, Regular Expressions, Network Protocols (DNS/HTTP), UI Design',
    'Java, MySQL, Thymeleaf, JDBC, CSS3',
    'APPROVED',
    TRUE,
    205,
    1
),
(
    10,
    'Public Transit Real-Time Bus Arrival & Fleet Dispatch Optimizer',
    'Commuters waiting at rural and suburban bus stops have no reliable information on when the next state transport bus will arrive, leading to long waits and overcrowded private vans.',
    'Municipal and state transport depots operate buses on static timetable sheets. When a vehicle breaks down or gets delayed in highway traffic, passengers remain unaware.',
    'Absence of lightweight bus fleet monitoring, unpredictable headway intervals, crowded bus stops, and inability of depot managers to reassign reserve buses dynamically.',
    'A public web transit dashboard offering expected bus arrival times (ETA), live route schedules, and a depot dispatcher console to manage delays and route reallocations.',
    'Restores commuter confidence in public transit, shortens bus stop wait times, and maximizes municipal bus seat occupancy.',
    '1. Passenger route lookup and expected stop arrival schedule\n2. Depot manager fleet dispatch and delay recording dashboard\n3. Driver schedule assignment portal\n4. Public delay advisory announcement banner',
    'GPS simulated bus movement on live map, QR code scan at bus stop poles for instant timetable display, and bilingual speech synthesis for bus announcements.',
    'Transportation',
    'Intermediate',
    '2–3 Months',
    'Major Project',
    'Government',
    'Municipal Smart City Corporation',
    'traffic-cell@smartcitycorp.gov.in',
    'Java, Schedule Calculations, Data Structures, Relational Database Design',
    'Java, MySQL, Bootstrap 5, Leaflet, JDBC',
    'APPROVED',
    TRUE,
    177,
    1
),
(
    11,
    'AI-Assisted Diagnostic Triage for Diabetic Retinopathy Screening',
    'Rural diabetic patients frequently develop diabetic retinopathy without early symptoms. By the time vision deteriorates, damage is irreversible. Specialist ophthalmologists are unavailable in rural tehsils.',
    'Over 70 million diabetic individuals in the country require annual fundus camera eye scans. Government rural health camps collect retinal images but take over 3 weeks to receive specialist doctor reviews.',
    'Massive eye doctor shortage in rural areas, backlog of unprocessed retinal photograph batches, lack of an automated severity grading tool.',
    'A triage tool where rural clinic operators upload fundus camera photographs and obtain preliminary severity grades (Normal, Mild, Moderate, Severe, Proliferative) to prioritize emergency referrals.',
    'Enables early clinical intervention for preventable blindness and prioritizes severe patient cases for urgent district hospital laser therapy.',
    '1. High-resolution medical image upload pipeline\n2. Automated classification inference result screen\n3. Referral ticket generation with eye specialist notes\n4. Camp-wise diabetic patient statistical report',
    'Mobile smartphone fundus lens attachment support, automated DICOM file metadata parsing, and doctor tele-consultation signoff.',
    'Healthcare',
    'Advanced',
    '3+ Months',
    'Research',
    'Startup',
    'Aarogya Seva Trust',
    'reach@aarogyaseva.org',
    'Java, Image Handling, REST API Integration with AI Model, Secure DB',
    'Java, Python, AI/ML, MySQL, Bootstrap 5',
    'APPROVED',
    TRUE,
    260,
    1
);

-- ==========================================================
-- Insert Problem Technologies Junction
-- ==========================================================
INSERT INTO problem_technologies (problem_id, technology_id) VALUES
(1, 1), (1, 3), (1, 5),     -- Problem 1: Java, MySQL, AI/ML
(2, 1), (2, 3), (2, 7),     -- Problem 2: Java, MySQL, Web Development
(3, 1), (3, 3), (3, 6),     -- Problem 3: Java, MySQL, IoT
(4, 1), (4, 3), (4, 5),     -- Problem 4: Java, MySQL, AI/ML
(5, 1), (5, 3), (5, 7),     -- Problem 5: Java, MySQL, Web Development
(6, 1), (6, 4), (6, 6),     -- Problem 6: Java, Python, IoT
(7, 1), (7, 3), (7, 5),     -- Problem 7: Java, MySQL, AI/ML
(8, 1), (8, 3), (8, 6),     -- Problem 8: Java, MySQL, IoT
(9, 1), (9, 4), (9, 7),     -- Problem 9: Java, Python, Web Development
(10, 1), (10, 3), (10, 8),  -- Problem 10: Java, MySQL, Mobile
(11, 1), (11, 4), (11, 5);  -- Problem 11: Java, Python, AI/ML

-- ==========================================================
-- Insert Projects
-- ==========================================================
INSERT INTO projects (
    id, problem_id, student_user_id, title, description, team_members,
    technologies_used, status, progress_percent, start_date, completion_date,
    repo_url, documentation_url
) VALUES
(
    1,
    1,
    2,
    'GreenRoute: Municipal Waste Truck Route Optimizer',
    'Implementing a graph heuristic algorithm to dynamically map the optimal morning collection trajectory based on ward bin capacity thresholds.',
    'Rahul Sharma, Rohan Kulkarni, Aniket Shinde',
    'Java, MySQL, Leaflet, Spring Boot',
    'IN_PROGRESS',
    65,
    '2026-01-15',
    NULL,
    'https://github.com/rahul-sharma/greenroute-waste-opt',
    'https://docs.google.com/document/d/greenroute-architecture'
),
(
    2,
    2,
    2,
    'CareConnect: Rural Clinic Outpatient Triage & Scheduling Portal',
    'Successfully developed and deployed a lightweight web triage portal used by 3 rural primary healthcare sub-centres in Kolhapur district.',
    'Rahul Sharma, Sneha Patil, Amit Deshmukh',
    'Java, MySQL, Bootstrap 5, Thymeleaf',
    'COMPLETED',
    100,
    '2025-08-01',
    '2025-11-20',
    'https://github.com/rahul-sharma/careconnect-telehealth',
    'https://careconnect.org/docs/final_report.pdf'
),
(
    3,
    3,
    3,
    'CampusGrid: Real-Time Substation Energy Profiler',
    'Setting up simulated smart sub-meter listeners and predictive threshold notification daemon for the college engineering campus.',
    'Sneha Patil, Pooja Joshi',
    'Java, MySQL, IoT, Chart.js',
    'PROPOSED',
    20,
    '2026-02-10',
    NULL,
    'https://github.com/sneha-patil/campus-grid-monitor',
    NULL
);

-- ==========================================================
-- Insert Project Members
-- ==========================================================
INSERT INTO project_members (project_id, user_id, member_name, role_title) VALUES
(1, 2, 'Rahul Sharma', 'Team Lead & Backend Developer'),
(1, NULL, 'Rohan Kulkarni', 'Algorithm & Route Planner'),
(1, NULL, 'Aniket Shinde', 'Frontend & GIS Integration'),
(2, 2, 'Rahul Sharma', 'Full-Stack Developer'),
(2, 3, 'Sneha Patil', 'Database & UI Designer'),
(2, NULL, 'Amit Deshmukh', 'Field Testing & Documentation'),
(3, 3, 'Sneha Patil', 'Lead Developer'),
(3, NULL, 'Pooja Joshi', 'Hardware & Telemetry Specialist');

-- ==========================================================
-- Insert Solutions (Archived Student Solutions)
-- ==========================================================
INSERT INTO solutions (
    id, project_id, problem_id, submitted_by_user_id, title, team_names,
    college, completion_year, technologies, solution_overview, architecture_details,
    implementation_details, key_features, results, challenges, limitations,
    future_scope, github_url, documentation_url, demo_url, screenshots_json,
    status, is_verified
) VALUES
(
    1,
    2,
    2,
    2,
    'CareConnect: Low-Bandwidth Rural Clinic Triage System',
    'Rahul Sharma, Sneha Patil, Amit Deshmukh',
    'DKTE Society Textile & Engineering Institute, Ichalkaranji',
    2025,
    'Java, Spring Boot, MySQL, JDBC, Bootstrap 5, HTML5',
    'CareConnect is a lightweight patient queue management and clinical triage web portal crafted specifically for rural healthcare sub-centres with intermittent 2G/3G connectivity. It allows ASHA health workers to record vital signs and queue patients for visiting district physicians.',
    'Layered MVC pattern using Spring Boot Controller -> Service -> JDBC DAO -> MySQL. Zero reliance on heavy client frameworks ensures fast page loads (< 1.2s on simulated 2G mobile data).',
    'Built with direct JDBC PreparedStatement queries to guarantee swift database transactions. Session-based role authentication for Health Workers and Visiting Doctors. Automated SMS gateway integration using REST webhooks.',
    '1. Rapid vital signs triage scoring\n2. Token generation and estimated wait time display\n3. Patient history card generation in printable format\n4. Low-data consumption responsive design',
    'Deployed as a pilot across 3 Primary Health Centres serving over 1,400 outpatients. Reduced average queue waiting times from 3.5 hours to 35 minutes.',
    'Handling intermittent electricity cuts in village clinics required persistent local caching and automated session recovery.',
    'Currently requires internet connectivity for doctor synchronization; offline browser IndexedDB synchronization is not yet implemented.',
    'Future students can enhance this project by implementing: 1. Offline Progressive Web App (PWA) with local background sync; 2. Vernacular audio questionnaires in Marathi/Kannada for illiterate elders; 3. Telemedicine WebRTC video integration.',
    'https://github.com/rahul-sharma/careconnect-telehealth',
    'https://careconnect.org/docs/final_report.pdf',
    'https://careconnect-demo.test',
    '["/images/solutions/careconnect-1.png", "/images/solutions/careconnect-2.png"]',
    'APPROVED',
    TRUE
),
(
    2,
    NULL,
    1,
    3,
    'EcoRoute: Municipal Garbage Collection Dijkstra Optimizer',
    'Sneha Patil, Aniket Jadhav',
    'Government College of Engineering, Karad',
    2024,
    'Java, MySQL, OpenStreetMap, GraphStream, JDBC',
    'An algorithmic route planning desktop-web hybrid that ingests ward garbage bin level thresholds and constructs an Eulerian path minimizing truck fuel usage.',
    'Client-server model: Java backend runs a modified Traveling Salesperson Problem (TSP) with 2-opt heuristic on a weighted street graph stored in MySQL.',
    'Implemented graph structures natively in Java to achieve sub-second recalculations for 150 municipal bin nodes.',
    '1. Daily turn-by-turn waypoint itinerary for truck drivers\n2. Priority collection for bins exceeding 85% fill level\n3. Diesel savings calculator compared to static baseline routes',
    'Tested on municipal Ward 4 simulation dataset: achieved a 22.4% reduction in total vehicle kilometers traveled per weekly cycle.',
    'Modeling one-way streets and narrow alleyways where heavy dump trucks cannot turn around.',
    'Lacks real-time ultrasonic sensor hardware integration; relied on simulated daily bin level updates.',
    'Future batches can: 1. Integrate physical LoRaWAN ultrasonic bin sensors; 2. Add a mobile driver navigation UI with voice alerts; 3. Integrate real-time traffic congestion avoidance.',
    'https://github.com/sneha-patil/ecoroute-municipal-tsp',
    'https://ecoroute.org/docs/ecoroute-paper.pdf',
    'https://ecoroute-demo.test',
    '["/images/solutions/ecoroute-1.png"]',
    'APPROVED',
    TRUE
);

-- ==========================================================
-- Insert Ratings
-- ==========================================================
INSERT INTO ratings (solution_id, user_id, rating_value, review_comment) VALUES
(1, 4, 5, 'Exceptional real-world implementation. The team worked directly with rural healthcare workers, resulting in practical, clean software design.'),
(1, 1, 5, 'Exemplary project architecture. Simple, clean JDBC code that runs smoothly with negligible server memory overhead.'),
(1, 3, 4, 'Very inspirating work! The triage calculation algorithm is clean and we are extending this for our final year project.'),
(2, 4, 5, 'Great algorithmic approach to the municipal waste challenge. The graph heuristic is thoroughly evaluated.');

-- ==========================================================
-- Insert Bookmarks
-- ==========================================================
INSERT INTO bookmarks (user_id, problem_id) VALUES
(2, 1),
(2, 3),
(2, 7),
(3, 2),
(3, 4);

-- ==========================================================
-- Insert Notifications
-- ==========================================================
INSERT INTO notifications (user_id, title, message, type, is_read, link) VALUES
(2, 'Solution Approved!', 'Your submission for CareConnect has been verified and added to the ProblemHub knowledge base.', 'SUCCESS', TRUE, '/solutions/1'),
(2, 'Recommendation Available', 'A new problem "College Energy Consumption" matches your skills in Java and MySQL.', 'INFO', FALSE, '/problems/3'),
(3, 'Welcome to ProblemHub', 'Discover real-world challenges from verified industries and NGOs to jumpstart your project.', 'INFO', TRUE, '/problems');
