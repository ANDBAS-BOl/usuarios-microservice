# Usuarios Microservice

Este microservicio es responsable de la gestión centralizada de usuarios, roles y autenticación para el Sistema Plaza de Comidas.

## Rol en el Sistema
* **Autenticación:** Es el único microservicio autorizado para emitir tokens JWT tras validar las credenciales (correo y clave) de los usuarios.
* **Roles Administrados:** Administrador, Propietario, Empleado, Cliente.
* **Base de Datos:** MySQL.

## Requisitos Previos
* JDK 17 o superior (Recomendado JDK 21 compilando a Target 17).
* Gradle 8.5.
* Docker y Docker Compose para levantar la base de datos (puerto 3306).

## Configuración y Ejecución
1. Levantar la base de datos:
   Desde la raíz del proyecto principal, ejecute:
   ```bash
   docker compose -f docker/compose-db.yml up -d
   ```
2. Iniciar el microservicio:
   Desde la carpeta `usuarios-microservice`, ejecute:
   ```bash
   ./gradlew bootRun
   ```

El servicio se iniciará por defecto en el puerto `8081`.
