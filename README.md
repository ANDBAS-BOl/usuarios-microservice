# Usuarios Microservice

Este microservicio es responsable de la gestión centralizada de usuarios, roles y autenticación para el Sistema Plaza de Comidas.

## El sistema completo

Este repositorio es **un componente del Sistema Plaza de Comidas**, compuesto por 4 microservicios independientes más su infraestructura. Cada servicio tiene su propia base de datos y valida el JWT de forma autónoma.

> **Para levantar el sistema, empieza por [`plazoleta-deployment`](https://github.com/ANDBAS-BOl/plazoleta-deployment)**, que arranca MySQL y MongoDB.

| Repositorio | Responsabilidad | Datos |
|---|---|---|
| **`usuarios-microservice`** ← estás aquí | Usuarios, roles y **emisión de JWT** (único emisor del sistema) | MySQL |
| [`plazoleta-microservice`](https://github.com/ANDBAS-BOl/plazoleta-microservice) | Catálogo de restaurantes/platos, flujo de pedidos y PIN de entrega | MySQL |
| [`trazabilidad-microservice`](https://github.com/ANDBAS-BOl/trazabilidad-microservice) | Historial de estados de pedidos y métricas de eficiencia | MongoDB |
| [`mensajeria-microservice`](https://github.com/ANDBAS-BOl/mensajeria-microservice) | Envío del SMS con el PIN, vía Twilio | — |
| [`plazoleta-deployment`](https://github.com/ANDBAS-BOl/plazoleta-deployment) | Infraestructura Docker: MySQL y MongoDB del sistema | — |

---
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
