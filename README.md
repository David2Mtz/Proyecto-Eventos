# Proyecto Eventos (Zentry) - Guía de Ejecución y Desarrollo

Este proyecto es una plataforma integral para la gestión de eventos, invitados, invitaciones y control de accesos mediante códigos QR. Los anfitriones pueden organizar sus eventos y generar invitaciones digitales que se envían por correo electrónico a los invitados con un código QR único. El personal de staff utiliza la cámara de la aplicación para escanear y validar estos códigos al ingresar al recinto, obteniendo respuestas visuales y sonoras en tiempo real.

---

## 1. Arquitectura y Tecnologías

### Backend (`eventos_backend`)
*   **Framework:** Spring Boot (Java 21)
*   **Seguridad:** Spring Security con autenticación basada en tokens JWT.
*   **Base de Datos:** PostgreSQL (Desarrollo local) y YugabyteDB (Producción).
*   **Mapeo ORM:** Spring Data JPA con Hibernate (autocreación/actualización de tablas con `ddl-auto=update`).
*   **Servicio de Correo:** Spring Mail para el envío de invitaciones con QR.
*   **Documentación:** Springdoc-openapi (Swagger UI).

### Frontend (`eventos_frontend`)
*   **Framework:** Angular (v20)
*   **Escaner QR:** Integración con `@zxing/ngx-scanner` y `@zxing/library` para la lectura desde la cámara del dispositivo.
*   **Alertas:** Visualizaciones interactivas mediante SweetAlert2.
*   **Reportes:** Generación de PDF en el cliente con la biblioteca `jsPDF`.

---

## 2. Requisitos Previos

Asegúrate de tener instalados los siguientes componentes antes de iniciar:

*   **Docker** y **Docker Compose**
*   **Node.js** (v18 o superior) y **npm** (para ejecutar el frontend localmente fuera de Docker si se requiere)

Puedes verificar tu entorno con:
```bash
docker --version
docker compose version
node -v
npm -v
```

---

## 3. Configuración de Variables de Entorno (.env)

Crea un archivo llamado `.env` en la raíz de la carpeta `eventos_backend/` con las siguientes variables para configurar la base de datos local y el servicio de correos:

```env
# Configuración de Base de Datos (Local PostgreSQL en Docker)
DB_NAME=zentry_eventos
DB_USER=postgres
DB_PASSWORD=postgres

# Configuración del Servidor de Correo (Gmail de ejemplo)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=TU_CORREO@gmail.com
SPRING_MAIL_PASSWORD=TU_PASSWORD_DE_APLICACION

# Configuración de Seguridad JWT
JWT_SECRET=WmVudHJ5U2VjcmV0S2V5MjAyNlByb3llY3RvRXZlbnRvc1NlZ3VyYQ==
JWT_EXPIRATION_MS=86400000
```

> **Nota:** Si utilizas Gmail para enviar correos, asegúrate de generar y colocar una **Contraseña de aplicación** de Google, no la contraseña general de tu cuenta de correo.

---

## 4. Ejecución del Backend con Docker

Para levantar la base de datos PostgreSQL local y compilar/ejecutar el backend en un contenedor:

1.  Navega a la carpeta del backend:
    ```bash
    cd eventos_backend
    ```
2.  Levanta los servicios con Docker Compose:
    ```bash
    docker compose up --build
    ```

Esto iniciará dos contenedores:
*   `zentry-postgres`: Base de datos PostgreSQL en el puerto `5432`.
*   `eventos-backend`: Servidor Spring Boot en el puerto `8080`.

Puedes verificar que estén corriendo usando:
```bash
docker ps
```

### Documentación del API (Swagger UI)
Una vez levantado el backend, puedes acceder a la interfaz de Swagger para ver y probar todos los endpoints disponibles:
```text
http://localhost:8080/documentacion/swagger-ui/index.html
```

---

## 5. Cuentas y Roles de Prueba Iniciales

El sistema se encarga de poblar automáticamente la base de datos con roles y usuarios de prueba al arrancar por primera vez (`DataInitializer`). Las cuentas disponibles son:

### 1. Administrador (ADMIN)
*   **Nombre de usuario:** `admin`
*   **Correo:** `admin@zentry.com`
*   **Contraseña:** `admin2026`
*   **Funciones:** Gestión total de usuarios, roles, eventos y visualización de reportes globales.

### 2. Anfitrión (ANFITRION)
*   **Nombre de usuario:** `organizador`
*   **Correo:** `anfitrion@zentry.com`
*   **Contraseña:** `eventos2026`
*   **Funciones:** Creación de eventos, registro de invitados e invitaciones, envío automático de correos de invitación y descarga de reportes de asistencia en PDF.

### 3. Personal (STAFF)
*   **Nombre de usuario:** `staff`
*   **Correo:** `staff@zentry.com`
*   **Contraseña:** `staff2026`
*   **Funciones:** Control de accesos y escaneo de códigos QR en tiempo real desde la cámara del frontend.

---

## 6. Ejecución del Frontend (Angular)

Para ejecutar el frontend de manera local en modo desarrollo:

1.  Navega a la carpeta del frontend:
    ```bash
    cd eventos_frontend
    ```
2.  Instala las dependencias:
    ```bash
    npm install
    ```
3.  Verifica que el archivo de configuración de desarrollo `src/environments/environment.ts` apunte a tu API local:
    ```typescript
    export const environment = {
      production: false,
      apiUrl: 'http://localhost:8080/api'
    };
    ```
4.  Inicia la aplicación:
    ```bash
    npm run start
    ```

El servidor de desarrollo de Angular estará disponible en:
```text
http://localhost:4200
```

---

## 7. Flujo Principal de Trabajo del Proyecto

Para demostrar todas las características integradas de la plataforma:

1.  **Creación de Evento:** Inicia sesión con la cuenta del Anfitrión (`organizador` / `eventos2026`). Crea un nuevo evento ingresando nombre, fecha, lugar y una imagen promocional.
2.  **Registro de Invitado e Invitación:** En el mismo panel de Anfitrión, agrega un nuevo invitado proporcionando su nombre y correo. Asócialo al evento creado. Esto generará una invitación y enviará de inmediato un correo electrónico al invitado con el código QR único (enlace gráfico autogenerado).
3.  **Control de Acceso (Escaneo QR):**
    *   Inicia sesión con la cuenta del Personal de Staff (`staff` / `staff2026`).
    *   Ingresa a la sección del escáner y otorga permisos de cámara al navegador.
    *   Presenta el código QR de la invitación recibida en el correo.
    *   **Comportamiento en pantalla:** Al detectar el código, el escáner se bloqueará temporalmente mostrando un indicador de carga (spinner). Una vez validado con el backend, mostrará de forma visual si la validación es correcta (palomita verde) o incorrecta (cruz roja con la descripción del error, ej. *invitación ya escaneada*, *evento en fecha incorrecta* o *invitado bloqueado*). Luego, se desbloqueará para continuar con el siguiente código.
4.  **Generación de Reportes:** El Anfitrión puede ingresar a la vista del evento y descargar un reporte de asistencia en formato PDF generado localmente mediante `jsPDF` con los datos obtenidos en `/api/reportes/asistencia/evento/{idEvento}`.

---

## 8. Comandos Útiles

*   **Detener contenedores:**
    ```bash
    docker compose down
    ```
*   **Detener contenedores borrando la base de datos (limpieza completa):**
    ```bash
    docker compose down -v
    ```
*   **Ver logs de la aplicación Spring Boot:**
    ```bash
    docker logs -f eventos-backend
    ```
*   **Ver logs del contenedor PostgreSQL:**
    ```bash
    docker logs -f zentry-postgres
    ```

---

## 9. Despliegue en la Nube

Para realizar el despliegue del clúster de base de datos en **Yugabyte Cloud**, el backend en **Render** (compilación automática con el Dockerfile) y el frontend en **Netlify** (con redirección de rutas y CORS enlazados), consulta la guía detallada ubicada en el archivo:

*   [GuiaDespliegue.md](file:///Users/david/Documents/WebClient&BackendDevelopmentFramework/ProyectoEventos/GuiaDespliegue.md)
