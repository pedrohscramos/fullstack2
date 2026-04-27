# Fullstack2 Repository - Agent Guide

## Frontend (jtech-tasklist-frontend)
- **Dev server**: `npm run dev`
- **Build**: `npm run build`
- **Unit tests**: `npm run test:unit`
- **Type check**: `npm run type-check`
- **Lint & fix**: `npm run lint`
- **Format**: `npm run format`

## Backend (jtech-tasklist-backend)
- **Run application**: `./gradlew bootRun`
- **Run tests**: `./gradlew test`
- **Build**: `./gradlew build`
- **Generate Javadoc**: `./gradlew javadoc`

## Architecture Notes
- Frontend: Vue 3 + Composition API + Pinia + Vue Router
- Backend: Spring Boot 3 + Hexagonal Architecture (Ports/Adapters)
- Backend uses Java 21 with Lombok for boilerplate reduction
- Testing: H2 database for unit tests, PostgreSQL for production

## Important Conventions
- Backend follows strict layer separation: Controller → Service → Repository → Domain
- Frontend state managed by Pinia stores in src/stores/
- Vue components in src/components/ and views in src/views/
- Backend adapters handle protocol translation (input/output)
- Configuration in backend/src/main/java/br/com/jtech/tasklist/config/

## Environment Setup
- Frontend requires Node.js 20.19.0+ or 22.12.0+
- Backend requires JDK 21+
- Backend uses Gradle wrapper (gradlew) for consistent builds