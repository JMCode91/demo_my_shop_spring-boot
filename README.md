# 🛒 E-commerce Full-Stack: My Shop

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap_5-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)

Plataforma de comercio electrónico completa, robusta y escalable desarrollada desde cero. El proyecto abarca todo el ciclo de vida del software, desde el modelado de la base de datos relacional hasta el despliegue en un servidor de producción (Linux VPS), aplicando patrones de diseño (MVC) y buenas prácticas de seguridad.

---

## 🚀 Características Principales

* **Autenticación y Autorización:** Gestión de roles de usuario (`ADMIN` y `USER`) mediante Spring Security.
* **Gestión de Catálogo (CRUD):** Panel de administración privado para crear, editar y ocultar productos, con subida de imágenes optimizada.
* **Proceso de Compra Completo:** Carrito de la compra dinámico, cálculo de impuestos, gestión de stock y generación automática de **facturas en formato PDF**.
* **Área de Cliente:** Historial de pedidos, lista de deseos (Wishlist) y personalización del perfil con generación de avatares automáticos.
* **Dark Mode Nativo:** Tema oscuro integrado con persistencia en `localStorage` del navegador para mejorar la experiencia de usuario.
* **Promociones Dinámicas:** Banner geolocalizado en el frontend (Vanilla JS) de forma asíncrona para evitar cuellos de botella en el servidor.

---

## 🛠️ Stack Tecnológico

| Área | Tecnologías |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot, Spring Security, Spring Data JPA |
| **Frontend** | Thymeleaf (Fragmentos Modulares), HTML5, CSS3, Bootstrap 5, Vanilla JS |
| **Base de Datos** | MySQL |
| **Integraciones (APIs)** | Cloudinary (CDN de Imágenes), GeoJS (Localización), DiceBear (Avatares) |
| **Infraestructura** | Servidor Linux (Hetzner), Bash Scripting (Gestión de variables de entorno) |

---

## 🏗️ Arquitectura y Buenas Prácticas

1.  **Separación de Responsabilidades:** Arquitectura MVC estricta. Lógica de negocio encapsulada en la capa `@Service`.
2.  **Modularización del Frontend:** Uso intensivo de *Fragments* de Thymeleaf para reutilizar componentes (Headers, Footers, Modales) manteniendo un código DRY.
3.  **Seguridad de Credenciales:** Claves de APIs de terceros (Cloudinary) externalizadas a través del sistema operativo del servidor en producción, evitando su exposición en el código fuente.
4.  **Optimización de Almacenamiento:** Delegación del almacenamiento de imágenes de productos a una CDN (Cloudinary) para reducir la carga de la base de datos y optimizar los tiempos de carga.

