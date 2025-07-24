# El Buen Sabor - API REST

Una API REST completa para la gestión de un restaurante/negocio gastronómico. Desarrollada con Spring Boot, esta API permite la gestión de productos, pedidos, clientes, integración de pagos con MercadoPago, y un robusto control de stock.

## 🚀 Características Principales

* **Gestión de Productos**: Artículos manufacturados e insumos con control de stock.
* **Sistema de Pedidos**: Gestión completa del flujo de pedidos, cubriendo estados desde `PENDIENTE` hasta `ENTREGADO`.
* **Gestión de Clientes**: Funcionalidades para registro, autenticación y manejo de domicilios de los clientes.
* **Integración con MercadoPago**: Procesamiento de pagos online de forma segura.
* **Control de Stock**: Seguimiento automático de ingredientes y productos para una gestión eficiente del inventario.
* **Sistema de Categorías**: Organización jerárquica de productos para facilitar la navegación y gestión.
* **Autenticación JWT**: Seguridad robusta basada en tokens para proteger los endpoints de la API.
* **Arquitectura Modular**: Implementación con Data Transfer Objects (DTOs), Mappers (MapStruct) y Capas de Servicio para una clara separación de responsabilidades.

## 🛠️ Tecnologías Utilizadas

* **Backend**: Spring Boot 3.x
* **Base de Datos**: MySQL 8
* **Seguridad**: Spring Security + JWT
* **Mapeo**: MapStruct
* **Validación**: Bean Validation
* **Documentación**: Spring Boot DevTools
* **Pagos**: MercadoPago SDK
* **ORM**: Hibernate/JPA
  
## 💻 Frontend del Proyecto

Puedes acceder a la interfaz de usuario de este proyecto (frontend) en el siguiente enlace:

[El Buen Sabor - Frontend](https://github.com/FiammaB/El-Buen-Sabor-Front.git)
## 📋 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalados los siguientes componentes:

* **Java**: Versión 17 o superior.
* **MySQL**: Versión 8.0 o superior.
* **Maven**: Versión 3.6 o superior.
* **Cuenta de MercadoPago**: Necesaria para la funcionalidad de pagos.

## ⚙️ Configuración

Sigue estos pasos para configurar y ejecutar el proyecto localmente:


### 1. Base de Datos

Puedes  Crear  la base de datos en tu instancia de MySQL  o Dejar que cuando  corra el programa se cree sola. 
Puedes usar el siguiente comando SQL:

```sql
CREATE DATABASE elBuenSabor1;
```
2. Variables de Entorno
Crea o edita el archivo application.properties dentro de src/main/resources/ y configura las siguientes propiedades:

Properties

# Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/elbuensabor1?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=create
logging.level.org.springframework=INFO
logging.level.org.hibernate=INFO
logging.level.org.springframework.security=INFO
logging.level.org.springframework.web.servlet.resource=INFO
# MercadoPago
mercadopago.access.token=Tu access.token



mercadopago.notification.url=tu webhook

spring.mail.host=smtp.gmail.com
spring.mail.port=el puerto
spring.mail.username=el mail q vas a usar para envio de facturas
spring.mail.password=password q te brinda gmail
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
mail.from.address=el mail q vas a usar para envio de facturas

Para mercadopago.access.token y mercadopago.public.key, utiliza los tokens de prueba proporcionados por MercadoPago para desarrollo.

3. Instalación y Ejecución
Bash

# Clonar el repositorio
git clone [https://github.com/FiammaB/El-Buen-Sabor.git](https://github.com/FiammaB/El-Buen-Sabor.git)
cd ProyectoFinal # Asegúrate de estar en el directorio raíz del proyecto Spring Boot

# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run
La aplicación estará disponible en http://localhost:8080.

📚 Estructura del Proyecto
El proyecto sigue una estructura modular para una mejor organización y mantenibilidad:

src/main/java/ElBuenSabor/ProyectoFinal/
├── Auth/                   # Clases relacionadas con autenticación (Login, Register, Responses)
├── Config/                 # Configuraciones de seguridad (Spring Security, CORS)
├── Configuration/          # Configuraciones específicas (Cloudinary)
├── Controllers/            # Endpoints REST para la interacción con el frontend
├── DTO/                    # Data Transfer Objects para requests y responses
├── Entities/               # Clases de entidades JPA que representan la base de datos
├── Exceptions/             # Clases personalizadas para el manejo de excepciones
├── Mappers/                # Interfaces y clases de mapeo usando MapStruct
├── Repositories/           # Interfaces de repositorio para la persistencia de datos
├── Service/                # Lógica de negocio principal y métodos de servicio
└── Validations/            # Anotaciones y lógicas de validación personalizadas


💳 Integración con MercadoPago
Configuración
Asegúrate de que las siguientes propiedades estén configuradas en application.properties:

Properties

mercadopago.access.token=TEST-tu-token-de-mercadopago
mercadopago.public.key=TEST-tu-public-key-de-mercadopago
mercadopago.base.url=[https://api.mercadopago.com](https://api.mercadopago.com)
mercadopago.success.url=http://localhost:5173/pago-exitoso # URL de éxito en tu frontend
mercadopago.failure.url=http://localhost:5173/pago-fallido # URL de fallo en tu frontend
Flujo de Pago
El cliente realiza un pedido a través de la API.

Se genera una Factura para el pedido.

La API interactúa con el SDK de MercadoPago para crear una preferencia de pago.

Se redirige al cliente a la página de pago de MercadoPago.

Una vez completado el pago, MercadoPago notifica a la API a través de un webhook, actualizando el estado del pago en la base de datos.

🔒 Seguridad
La seguridad de la API se implementa con las siguientes características:

Autenticación: Se utilizan tokens JWT (JSON Web Tokens) para verificar la identidad de los usuarios en cada solicitud.

Autorización: Se gestionan diferentes roles de usuario (CLIENTE, ADMIN, COCINERO, DELIVERY, CAJERO) para controlar el acceso a los recursos.

CORS: Configurado para permitir solicitudes desde el frontend, especialmente útil en entornos de desarrollo local.

Validación: Se utiliza Bean Validation para asegurar la integridad de los datos en los DTOs de entrada.

🧪 Testing
Para ejecutar los tests del proyecto:

Bash

# Ejecutar todos los tests unitarios y de integración
mvn test
📦 Deployment
Desarrollo
Para ejecutar la aplicación en modo desarrollo (con auto-recarga y herramientas de desarrollo):

Bash

mvn spring-boot:run
Producción
Para generar un archivo JAR ejecutable para producción:

Bash

mvn clean package
Luego, puedes ejecutar la aplicación con:

Bash

java -jar target/ProyectoFinal-0.0.1-SNAPSHOT.jar # El nombre del JAR puede variar
🔧 Configuración para Producción
Para el despliegue en un entorno de producción, es crucial ajustar las siguientes configuraciones en application.properties:

Base de Datos: Configurar la URL, usuario y contraseña para la base de datos de producción.

MercadoPago: Cambiar los tokens de acceso y clave pública a los de producción. Asegurarse de que mercadopago.sandbox.mode sea false.

JWT: Utilizar un jwt.secret mucho más robusto y generado de forma segura.

CORS: Configurar los dominios permitidos para tu frontend en producción.

SSL: Habilitar HTTPS para todas las comunicaciones de la API en producción.

Ejemplo de configuración de producción:

Properties

# MercadoPago para Producción
mercadopago.access.token=APP_USR-tu-token-produccion
mercadopago.public.key=APP_PUB-tu-public-key-produccion
mercadopago.sandbox.mode=false

# JWT para Producción
jwt.secret=clave-super-segura-de-produccion-generada-aleatoriamente-larga-y-compleja
📝 Funcionalidades Destacadas
Control de Stock Inteligente: Implementación que permite el seguimiento automático de los ingredientes utilizados en los artículos manufacturados y la generación de alertas por stock crítico o bajo. Además, valida la disponibilidad de stock antes de confirmar pedidos.

Gestión de Recetas: Los artículos manufacturados se definen con sus respectivas listas de ingredientes, lo que permite un cálculo automático de costos y la gestión de márgenes de ganancia.

Sistema de Pagos Flexible: Soporte para múltiples pagos por factura y estados de pago detallados, con una integración completa y segura con MercadoPago.

Arquitectura Limpia: Adopción de patrones de diseño como DTOs para la transferencia de datos, MapStruct para el mapeo automático y un manejo centralizado de excepciones, asegurando una base de código robusta y mantenible.

🐛 Troubleshooting
Aquí hay algunas soluciones para problemas comunes que podrías encontrar:

Error de conexión a la Base de Datos:

Verifica que las credenciales en application.properties sean correctas.

Asegúrate de que tu servidor MySQL esté ejecutándose y sea accesible.

Comprueba si el nombre de la base de datos (el_buen_sabor) es correcto y si la propiedad createDatabaseIfNotExist=true está configurada si deseas que Spring la cree automáticamente.

Error de MercadoPago:

Confirma que tus tokens de acceso y claves públicas de MercadoPago son válidos y corresponden al entorno (prueba o producción).

Verifica la configuración de mercadopago.sandbox.mode según el entorno en el que estés trabajando.

Error de JWT (JSON Web Token):

Asegúrate de que la propiedad jwt.secret esté configurada en application.properties y que sea una cadena no vacía.

Verifica la validez y la expiración del token JWT si estás obteniendo errores de autenticación.




👥 Equipo de Desarrollo
Este proyecto está siendo desarrollado por:

Fiamma Brizuela - Gaston Sisterna -Faustino Viñiolo - Laura Pelayes


Para cualquier consulta o comentario sobre el proyecto, no dudes en contactar a cualquier miembro del equipo de desarrollo a través de sus perfiles de GitHub.
