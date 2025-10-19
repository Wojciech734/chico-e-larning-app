# Chico (E-learning Spring Boot App)

## Features

- **Authentication & Authorization**
  - Login/Register with **JWT** or **OAuth2** (Google/Facebook)
  - Email verification + password/email reset
  - Role system:
    - User chooses **Teacher** or **Student** role during registration
    - Roles can be changed later in profile settings
    - Teachers automatically also have the **Student** role
  - Private/Public teacher profiles:
    - If a teacher makes their profile private, all their courses are also hidden

- **User Profile**
  - Avatar image and personal description
  - Public teacher profiles (if enabled) show their courses and reviews

- **Courses & Lessons**
  - Teachers can create, edit and remove courses
  - Lessons can include:
    - Videos
    - PDFs
    - Quizzes
  - Students can:
    - Enroll in courses
    - Watch lessons
    - add reviews (rating + comment)
      
- **Reviews**
  - Course rating system (1-5 stars + comment)
  - Each student can review a course only once

- **Dashboard**
  - Students see progress in enrolled courses (e.g. "Lessons completed: 4/10")
  - Teachers see:
    - Number of enrolled students
    - Average course review

- **Achievements**
  - Gamification system (e.g. *"Complete your first 10 courses"*)
  - Progress tracking for achievements (e.g. *"Completed 6/10"*)

- **Notifications**
  -Email and in-app notifications about:
    - New courses
    - Updates from teachers

- **Certificates**
  - After completing a course, the system generates a **PDF certificate** with:
    - User's full name
    - Course name
    - Completion date

- **Recommendation System**
  - Personalized course recommendations based on:
    - Categories
    - Completed courses
    - Collaborative filtering (*"Users who completed this course also viewed…"*)

- **Planned Features**
  - Payment system (Stripe/PayPal integration for paid courses)

## Tech Stack

- **Backend**
  - Java 17+
  - Spring Boot 3 (Web, Security, Data JPA, Validation, Mail)
  - Spring Security (JWT Authentication + OAuth2)
  - Hibernate (JPA)
  - PostgreSQL
  - Lombok
  - JUnit 5, Mockito, Spring Test
 
- **Frontend**
  - React (Vite)
  - React Router
  - Redux Toolkit + RTK Query
  - Material UI
  - Formik + Yup
  - Axios
  - JWT-decode
  - Framer-motion
  - React-player
  - Chart.js
