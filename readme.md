# Mikhipu - Backend

Este es un proyecto de demostración basado en **Spring Boot**, que forma parte de un sistema de gestión académica. El proyecto implementa funcionalidades como autenticación, gestión de usuarios, cursos, estudiantes, representantes, profesores y más.

## 📦 Información del Proyecto

- **Nombre**: mikhipu
- **Artifact ID**: mikhipu
- **Descripción**: Demo project for Spring Boot

## 🗂️ Estructura del Proyecto

El backend está estructurado en diferentes paquetes según su funcionalidad. A continuación, se presenta una descripción general de las principales áreas del sistema:

### Autenticación (`auth`)
- Controlador de autenticación (`AuthController`)
- Servicios y DTOs para login y generación de JWT

### Configuración (`config`)
- Configuración de seguridad (`SecurityConfig`)
- Configuración de OpenAPI (`OpenApiConfig`)
- Inicializador de administrador (`AdminInitializer`)

### Email (`email`)
- Envío de correos HTML (`EmailController`, `EmailService`)

### Excepciones (`exception`)
- Manejador global de excepciones
- Clases de error y excepciones personalizadas

### Reseteo de Contraseñas (`password`)
- Controladores y servicios para recuperación de contraseñas
- Gestión de tokens de recuperación

### Gestión Académica (`person`)
- CRUD para:
  - Personas (`PersonController`)
  - Estudiantes (`StudentController`)
  - Representantes (`RepresentativeController`)
  - Profesores y sus horarios (`TeacherController`, `TeacherScheduleController`)
  - Cursos y matrículas (`CourseController`, `EnrollmentController`)
- Mappers y DTOs para transformación de datos

### Roles y Permisos (`role`)
- Gestión de roles y permisos con sus respectivos controladores, servicios y repositorios

### Seguridad (`security`)
- Servicios y filtros de autenticación JWT personalizados

### Usuarios (`user`)
- Gestión de usuarios con DTOs, mappers y repositorio

## ✅ Pruebas

El proyecto cuenta con múltiples pruebas unitarias en los siguientes paquetes:
- `auth.service`
- `config`
- `email.service`
- `password.controller`
- `password.service`
- `person.service`
- `role.service`
- `security`

## 🛠️ Tecnologías

- Java
- Spring Boot
- Spring Security (JWT)
- JPA / Hibernate
- OpenAPI (Swagger)
- Maven

## 🚀 Cómo empezar

1. Clonar el repositorio
2. Configurar la base de datos en `application.properties`
3. Ejecutar la clase principal: `MikhipuApplication.java`
4. Acceder a la documentación en `http://localhost:8080/swagger-ui.html`
