# Codffee - Guía rápida de ejecución y pruebas

Este documento explica cómo ejecutar el proyecto **Codffee** usando Docker y cómo probar sus funcionalidades principales desde **Swagger UI**.

---

## 1. Requisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Docker
- Docker Compose
- Node.js y npm, solo si también se probará el frontend React

Puedes comprobar Docker con:

```bash
docker --version
docker compose version
```

Y Node.js con:

```bash
node -v
npm -v
```

---

## 2. Configurar variables de entorno del backend

En la raíz del proyecto backend, donde están `Dockerfile` y `docker-compose.yml`, crea un archivo llamado:

```text
.env
```

Agrega el siguiente contenido:

```env
MYSQL_DATABASE=codffee_db
MYSQL_ROOT_PASSWORD=root

SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=TU_CORREO@gmail.com
SPRING_MAIL_PASSWORD=TU_PASSWORD_DE_APLICACION

JWT_SECRET=Q29kZmZlZVNlY3JldEtleTIwMjZQcm95ZWN0b0NhZmV0ZXJpYVNlZ3VyYQ==
JWT_EXPIRATION_MS=86400000
```

Cambia `TU_CORREO@gmail.com` y `TU_PASSWORD_DE_APLICACION` por los datos reales del correo que enviará notificaciones.

> Si usas Gmail, necesitas una contraseña de aplicación, no la contraseña normal de tu cuenta.

---

## 3. Ejecutar el backend con Docker

Desde la raíz del backend ejecuta:

```bash
docker compose up --build
```

Cuando termine, deben estar activos estos contenedores:

```text
codffee-backend
codffee-mysql
```

Puedes verificarlo con:

```bash
docker ps
```

---

## 4. Abrir Swagger UI

Con el backend ejecutándose, abre:

```text
http://localhost:8080/swagger-ui/index.html
```

Desde Swagger se pueden probar los endpoints del backend.

---

## 5. Cuentas iniciales

El sistema crea automáticamente usuarios iniciales al arrancar.

### Administrador

```text
Correo: admin@codffee.com
Contraseña: 123456
Rol: ADMIN
```

### Personal de cafetería

```text
Correo: personal@codffee.com
Contraseña: 123456
Rol: PERSONAL
```

### Cliente

```text
Correo: cliente@codffee.com
Contraseña: 123456
Rol: CLIENTE
```

---

## 6. Iniciar sesión en Swagger

En Swagger busca:

```http
POST /api/auth/login
```

Ejemplo para administrador:

```json
{
  "correo": "admin@codffee.com",
  "contrasena": "123456"
}
```

Ejemplo para cliente:

```json
{
  "correo": "cliente@codffee.com",
  "contrasena": "123456"
}
```

La respuesta incluirá un token JWT:

```json
{
  "mensaje": "Inicio de sesión exitoso",
  "token": "TOKEN_JWT",
  "tipoToken": "Bearer",
  "id": 1,
  "nombre": "Administrador Codffee",
  "correo": "admin@codffee.com",
  "rol": "ADMIN"
}
```

Copia el valor de `token`.

---

## 7. Autorizar Swagger con JWT

En Swagger presiona el botón:

```text
Authorize
```

Pega el token con este formato:

```text
Bearer TOKEN_JWT
```

Luego presiona **Authorize** y después **Close**.

---

## 8. Pruebas principales en Swagger

### Consultar productos disponibles

Endpoint:

```http
GET /api/productos/disponibles
```

Sirve para ver los productos que puede pedir un cliente.

---

### Crear un pedido

Inicia sesión como cliente y autoriza Swagger con su token.

Endpoint:

```http
POST /api/pedidos
```

Ejemplo:

```json
{
  "usuarioId": 3,
  "metodoPago": "EFECTIVO",
  "observaciones": "Pasaré por el pedido en el receso",
  "productos": [
    {
      "productoId": 1,
      "cantidad": 1
    },
    {
      "productoId": 2,
      "cantidad": 1
    }
  ]
}
```

Al crear el pedido, el sistema guarda la orden, calcula el total, descuenta stock y envía correo de confirmación.

---

### Consultar pedidos

Endpoint:

```http
GET /api/pedidos
```

También puedes consultar detalles de un pedido:

```http
GET /api/pedidos/{pedidoId}/detalles
```

---

### Cambiar estado de un pedido

Inicia sesión como `ADMIN` o `PERSONAL`.

Endpoint:

```http
PUT /api/pedidos/{pedidoId}/estado/{estado}
```

Estados válidos:

```text
PENDIENTE
EN_PREPARACION
LISTO
ENTREGADO
CANCELADO
```

Ejemplo:

```text
pedidoId: 1
estado: LISTO
```

Al cambiar el estado, el sistema envía un correo al usuario.

---

### Cancelar un pedido

Endpoint:

```http
PUT /api/pedidos/{pedidoId}/cancelar
```

El sistema cambia el estado a `CANCELADO`, regresa el stock de los productos y envía correo de cancelación.

---

### Generar reporte PDF general

Inicia sesión como `ADMIN`.

Endpoint:

```http
GET /api/reportes/pedidos/pdf
```

El sistema descargará un archivo PDF con el reporte general de pedidos.

---

### Generar reporte PDF filtrado

Endpoint:

```http
GET /api/reportes/pedidos/pdf/filtrado
```

Parámetros:

```text
fechaInicio
fechaFin
estado
```

Ejemplo:

```text
fechaInicio=2026-01-01
fechaFin=2026-12-31
estado=ENTREGADO
```

El parámetro `estado` es opcional.

---

## 9. Ejecutar el frontend React

Entra a la carpeta del frontend:

```bash
cd codffee-frontend
```

Instala dependencias:

```bash
npm install
```

Crea un archivo `.env` en la raíz del frontend con:

```env
VITE_API_URL=http://localhost:8080/api
```

Ejecuta el frontend:

```bash
npm run dev
```

Abre la URL que indique Vite, normalmente:

```text
http://localhost:5173
```

Puedes iniciar sesión con las mismas cuentas de prueba.

---

## 10. Detener el proyecto

Para detener los contenedores:

```bash
docker compose down
```

Para detenerlos y borrar la base de datos del contenedor:

```bash
docker compose down -v
```

---

## 11. Reiniciar desde cero

Si quieres borrar todo y volver a crear la base con datos iniciales:

```bash
docker compose down -v
docker compose up --build
```

---

## 12. Comandos útiles

Ver contenedores activos:

```bash
docker ps
```

Ver logs del backend:

```bash
docker logs -f codffee-backend
```

Ver logs de MySQL:

```bash
docker logs -f codffee-mysql
```

---

## 13. Problemas comunes

### Error 403 Forbidden

El login fue correcto, pero el usuario no tiene permiso para ese endpoint.

Ejemplo: un usuario `CLIENTE` no puede consultar:

```http
GET /api/usuarios
```

Ese endpoint requiere rol `ADMIN`.

### No llegan correos

Revisa en `.env`:

```env
SPRING_MAIL_USERNAME=TU_CORREO@gmail.com
SPRING_MAIL_PASSWORD=TU_PASSWORD_DE_APLICACION
```

Si usas Gmail, asegúrate de usar contraseña de aplicación.

### Swagger no abre

Verifica que el backend esté activo:

```bash
docker ps
docker logs -f codffee-backend
```

---

## 14. Flujo recomendado para demostrar el proyecto

1. Levantar backend con Docker.
2. Abrir Swagger UI.
3. Iniciar sesión como cliente.
4. Consultar productos disponibles.
5. Crear un pedido.
6. Verificar correo de confirmación.
7. Iniciar sesión como personal.
8. Cambiar estado del pedido a `LISTO`.
9. Verificar correo de actualización.
10. Iniciar sesión como administrador.
11. Generar reporte PDF general o filtrado.
12. Probar login desde el frontend React.
# Proyecto-Eventos
