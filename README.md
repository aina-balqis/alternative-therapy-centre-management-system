# Alternative Therapy Centre Management System (ATCMS)

ATCMS is a web-based management system developed as my Final Year Project for the Bachelor of Computer Science (Software Engineering). The system is designed to support the daily operations of an alternative therapy centre by managing appointments, therapy packages, payments, and reporting in a structured and maintainable way.

---

## Project Overview
The objective of ATCMS is to streamline therapy centre workflows through a centralized platform for administrators, therapists, and clients. The system focuses on appointment lifecycle management, payment verification, performance tracking, and operational reporting.

The project emphasizes backend logic, system workflows, and clean architectural structure rather than UI-heavy design.

---

## System Architecture
The application follows the **MVC (Model–View–Controller)** architecture with a **DAO (Data Access Object)** pattern to ensure separation of concerns, maintainability, and scalability.

- **Model Layer** – Represents core business entities and domain logic  
- **Controller Layer** – Handles request routing, validation, and workflow control  
- **View Layer** – JSP-based interfaces for different user roles  
- **DAO Layer** – Manages database access and persistence logic  

---

## System Modules Overview
The system is organized into modular components to support clear responsibilities and easier maintenance.

- **Authentication & User Management**  
  Handles login, role-based access control, password recovery, and session management for Admin, Therapist, and Client users.

- **Appointment Management**  
  Manages appointment booking, rescheduling, automatic cancellation, reminder notifications, and time slot validation to prevent conflicts.

- **Therapist & Schedule Management**  
  Controls therapist profiles, availability, assigned schedules, and therapist performance tracking.

- **Therapy Package Management**  
  Manages therapy packages offered by the centre and supports selection during appointment booking.

- **Payment Processing**  
  Integrates online payment workflows, payment verification callbacks, and fallback confirmation handling.

- **Feedback Management**  
  Allows clients to submit feedback after completed therapy sessions.

- **Reporting & Analytics**  
  Generates operational and appointment-based reports with support for PDF export.

---

## Model Layer Design
The model layer represents real-world entities within an alternative therapy centre:

- **Admin** – Administrative users with system management privileges  
- **Client** – Client profiles and appointment records  
- **Therapist** – Therapist profiles and assignments  
- **TherapyPackage** – Therapy services offered by the centre  
- **Appointment** – Appointment lifecycle and scheduling  
- **TimeSlot** – Appointment time slot availability and validation  
- **Payment** – Payment records and verification status  
- **Feedback** – Client feedback after sessions  
- **Report** – Reporting data structures  
- **PackagePerformance** – Performance tracking for therapy packages  
- **TherapistPerformance** – Therapist activity and performance metrics  
- **TherapistSchedule** – Therapist availability and assigned schedules  

---


## Technology Stack
- **Backend**: Java (Servlets, JSP)  
- **Frontend**: HTML, CSS, JavaScript  
- **Database**: MySQL  
- **Architecture**: MVC, DAO Pattern  

---
## How to Run the Project (ATCMS)

- **Download / Clone the project**
  Download the repository as a ZIP file and extract it.
  
- **Import the project into IDE**
   Open the project using NetBeans / IntelliJ / Eclipse as a Java Web Application.
  Configure Apache Tomcat as the server.
  
- **Database setup**
  Create a MySQL database (atcms).
  Import the provided SQL file into the database.
  Update database configuration
  Update the database connection settings (URL, username, password) in the project to match your local MySQL setup.
  
- **Run the application**
  Start the Tomcat server.
  Access the system via browser:
  http://localhost:8080/ATCMS3/
  When running the application locally, users should start from the landing page (`LoginChoice.jsp`), which serves as the entry point before authentication.

---

## Notes
This project was developed as part of an academic program and demonstrates a complete web-based system implementation. Running the system locally requires Java, MySQL, and a servlet container such as Apache Tomcat.


