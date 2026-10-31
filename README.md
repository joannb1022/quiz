# Quiz AI

## Features

- Create quizzes from uploaded PDF files with Gemini AI
- Review answers 
- Keep track of questions that were answered wrongly 
- Organize questions from one topic into folders

## Tech stack

A full-stack quiz application with:
- Spring Boot backend
- Kotlin
- React + Vite frontend
- PostgreSQL database
- Google OAuth
- Gemini API integration for quiz generation


- Backend: Kotlin, Spring Boot 3, Spring Security, Spring Data JPA
- Frontend: React, TypeScript, Vite
- Database: PostgreSQL
- Containerization: Docker Compose
- Build tools: Gradle, npm

## Project structure

```text
quiz/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/
│   │   │   └── resources/
│   │   └── test/
│   └── build.gradle.kts
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── docker-compose.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md