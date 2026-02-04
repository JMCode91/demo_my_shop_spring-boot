# 🛒 My Shop - Curso Spring Boot

Proyecto desarrollado durante el curso de **Desarrollo de Aplicaciones Web con Spring Boot**.
Esta aplicación es un e-commerce simulado que implementa una arquitectura **MVC** completa, gestión de usuarios, catálogo de productos y soporte multiidioma.

## 🚀 Tecnologías utilizadas

* **Java** (Backend logic)
* **Spring Boot** (Framework principal)
* **Thymeleaf** (Motor de plantillas HTML)
* **Bootstrap** (Estilos y diseño responsive)
* **Spring Data JPA** (Persistencia de datos)
* **H2 / MySQL** (Base de datos)
* **Maven** (Gestión de dependencias)

## 📚 Progreso del Curso

El curso se divide en dos grandes bloques. A continuación se detalla el progreso actual del desarrollo:

### Parte 1: Creando una App Web

- [x] **Arquitectura Base:** Configuración inicial, estructura de carpetas y `pom.xml`.
- [x] **Capa de Modelo (Entities):** Creación de entidades y Repositorios.
- [x] **Capa de Negocio (Service):** Lógica de negocio e inyección de dependencias.
- [x] **Capa Web (Controller):** Controladores y mapeo de rutas.
- [x] **Vistas (Thymeleaf):** Integración de plantillas HTML dinámicas y fragmentos (Header, Footer).
- [x] **Formularios:** Procesamiento de datos y validación (Registro de usuarios).
- [x] **Gestión de Errores (UD6):**
    - [x] Control de excepciones específicas (`UserRegistrationException`).
    - [x] Control global de errores (`@ExceptionHandler`).
    - [x] Página de error personalizada (`error.html`).
- [x] **Internacionalización - i18n (UD7):**
    - [x] Soporte para Español (ES), Inglés (EN) y Francés (FR).
    - [x] `LocaleResolver` y `LocaleChangeInterceptor` mediante cookies y parámetros URL.
    - [x] Archivos `.properties` en UTF-8.
- [ ] **Depuración y Logging (UD8):** (Pendiente)
- [ ] **Seguridad con Spring Security (UD9):** (Pendiente)

### Parte 2: Enriqueciendo la App Web
- [ ] (Próximamente...)

## ✨ Funcionalidades Destacadas

1.  **Catálogo de Productos:** Visualización dinámica de productos iterando sobre listas desde el controlador.
2.  **Registro de Usuarios:** Formulario funcional con validaciones.
3.  **Multiidioma:** La web detecta el idioma o permite cambiarlo manualmente (`?lang=en`, `?lang=es`, `?lang=fr`).
4.  **Sistema Robusto:** Si ocurre un fallo, el usuario ve una página de error amigable en lugar de un "stack trace".

## 📸 Capturas de Pantalla

*(Puedes subir tus imágenes a una carpeta llamada /screenshots en tu proyecto y enlazarlas aquí)*

| Catálogo (Español) | Catálogo (Inglés) |
|:---:|:---:|
| ![Catalogo ES](./screenshots/catalogo_es.png) | ![Catalogo EN](./screenshots/catalogo_en.png) |

|        Error Personalizado        | Página de Registro |
|:---------------------------------:|:---:|
| ![Error](./screenshots/error.png) | ![Registro](./screenshots/registro.png) |

## 🔧 Cómo ejecutar el proyecto

1.  Clonar el repositorio:
    ```bash
    git clone [https://github.com/tu-usuario/nombre-repo.git](https://github.com/tu-usuario/nombre-repo.git)
    ```
2.  Abrir con IntelliJ IDEA.
3.  Ejecutar la clase `DemoApplication.java`.
4.  Abrir el navegador en `http://localhost:8080`.

---
*Commit realizado siguiendo la convención Conventional Commits.*