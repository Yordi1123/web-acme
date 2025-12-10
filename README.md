# Sistema de Gestión ACME

Sistema Web empresarial desarrollado con tecnologías modernas de **Java Jakarta EE**, diseñado para la gestión integral de almacenes, compras y maestros de una organización.

![Java](https://img.shields.io/badge/Java-17-orange)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-blue)
![PrimeFaces](https://img.shields.io/badge/PrimeFaces-13.0-red)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

## 📋 Características Principales

El sistema está dividido en módulos funcionales accesibles a través de un Dashboard interactivo:

### 🏭 Módulo de Almacén
Gestión de inventario y catálogos base.
- **Grupos y Unidades**: Clasificación de items y unidades de medida.
- **Materiales**: Catálogo maestro de materiales disponibles.

### 👥 Módulo de Maestros
Administración de entidades principales del negocio.
- **Proyectos**: Gestión de obras o centros de costo operativos.
- **Proveedores**: Cartera de proveedores para las órdenes de compra.
- **Usuarios**: Gestión de acceso y roles de sistema.

### 🛒 Módulo de Compras (Flujo Completo)
Flujo de negocio transaccional para el abastecimiento.
1. **Mis Pedidos**: Creación de requerimientos de materiales con interfaz Maestro-Detalle.
2. **Aprobación**: Workflow para autorizar o rechazar solicitudes.
3. **Generar Orden**: Generación automática de Órdenes de Compra basadas en requerimientos aprobados.
4. **Reportes**: Consulta y seguimiento histórico de órdenes generadas.

## 🛠️ Tecnologías

Este proyecto utiliza el stack estándar de Jakarta EE 10:

*   **Backend**: Java 17, Jakarta Persistence (JPA), EJB/CDI (Managed Beans).
*   **Frontend**: JSF 3.0 (Facelets), PrimeFaces 13 (Componentes UI), OmniFaces 4.3 (Utilidades).
*   **Base de Datos**: MySQL 8.0.
*   **Servidor de Aplicaciones**: Payara Server 6 (o GlassFish 7+).
*   **Construcción**: Apache Maven.

## 🚀 Instalación y Despliegue

### Prerrequisitos
*   JDK 17 instalado.
*   Apache Maven configurado en el PATH.
*   Servidor Payara 6 o compatible.
*   MySQL Server corriendo.

### Configuración de Base de Datos
1.  Crear la base de datos `dbprueba`.
2.  Importar el script SQL ubicado en la raíz: `dbprueba.sql`.
3.  Verificar las credenciales de conexión en: `src/main/resources/META-INF/persistence.xml`.

### Compilación
Ejecutar el siguiente comando en la raíz del proyecto para descargar dependencias y generar el WAR:

```bash
mvn clean package
```

### Despliegue
1.  Iniciar el dominio de Payara.
2.  Desplegar el archivo generado `target/web-acme-1.0-SNAPSHOT.war` desde la consola de administración o copiándolo a la carpeta `autodeploy`.
3.  Acceder a `http://localhost:8080/web-acme/`

## 📂 Estructura del Proyecto

```
web-acme/
├── src/main/
│   ├── java/com/uns/
│   │   ├── config/      # Configuración JPA (EntityManagerFactory)
│   │   ├── controller/  # Managed Beans (Lógica de vista)
│   │   ├── dao/         # Data Access Objects (Acceso a BD)
│   │   ├── entities/    # Entidades JPA (Mapeo ORM)
│   │   └── enums/       # Enumeradores de estado y roles
│   ├── resources/
│   │   └── META-INF/persistence.xml  # Configuración JPA
│   └── webapp/
│       ├── WEB-INF/     # template.xhtml, web.xml, beans.xml
│       ├── admin/       # Dashboard: Usuarios, Proyectos, Proveedores
│       ├── almacen/     # Gestión: Grupos, Materiales, Unidades
│       ├── compras/     # Transaccional: Órdenes, Pool, Reportes, Seguimiento
│       ├── encargado/   # Mis Pedidos y Seguimiento
│       ├── jefe/        # Aprobación y Seguimiento de Pedidos
│       ├── resources/   # Imágenes y assets estáticos
│       ├── index.xhtml  # Página de inicio
│       └── login.xhtml  # Autenticación
├── target/              # Compilados y WAR generado
├── pom.xml              # Dependencias Maven
├── dbprueba.sql         # Script inicial de base de datos
└── README.md            # Este archivo
```

## 🔄 Estado Actual del Proyecto

### ✅ Completado
- Arquitectura base con Jakarta EE 10
- Módulos de almacén, maestros y compras implementados
- Interfaz con PrimeFaces y Facelets
- Modelos de datos completos (Entidades JPA)
- DAOs y acceso a base de datos funcional
- Managed Beans para controladores

### 🔧 En Desarrollo / Pendiente
- Sistema de autenticación y autorización avanzado
- Workflow de aprobación con notificaciones
- Reportes avanzados con exportación
- Validaciones de negocio completas
- Pruebas unitarias e integración
- Documentación de APIs

## 📊 Módulos Implementados

| Módulo | Estado | Funcionalidades |
|--------|--------|-----------------|
| **Admin** | ✅ Básico | Gestión de Usuarios, Proyectos, Proveedores |
| **Almacén** | ✅ Básico | Grupos, Materiales, Unidades de Medida |
| **Compras** | 🔄 En Progreso | Órdenes, Pool de Compras, Reportes, Seguimiento |
| **Encargado** | ✅ Básico | Mis Pedidos, Seguimiento |
| **Jefe** | ✅ Básico | Aprobación, Seguimiento |

## 📄 Licencia

Este proyecto es de uso exclusivo para demostración y evaluación académica/profesional.
