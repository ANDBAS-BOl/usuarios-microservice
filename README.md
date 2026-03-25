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

## Cómo ejecutar localmente
Repositorio de infraestructura: [plazoleta-deployment](https://github.com/ANDBAS-BOl/plazoleta-deployment)

1. Levantar bases de datos:
   Desde la carpeta `plazoleta-deployment`, ejecute:
   ```bash
   docker compose -f docker/compose-db.yml up -d
   ```
2. Iniciar el microservicio:
   Desde la carpeta `usuarios-microservice`, ejecute:
   ```bash
   ./gradlew bootRun
   ```

El servicio se iniciará por defecto en el puerto `8081`.
