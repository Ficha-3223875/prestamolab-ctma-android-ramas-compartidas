# PréstamoLab CTMA

## 1. Descubrimiento y Product Goal
* **Problema:** Falta de visibilidad y control sobre la disponibilidad, préstamos activos y devoluciones de equipos y herramientas compartidas en los laboratorios del CTMA.
* **Usuarios:** Solicitante demo (aprendices e instructores) y Gestor simulado.
* **Necesidades:** Consultar catálogo en tiempo real, solicitar préstamos de forma rápida, validar reglas de uso y rastrear el estado de las solicitudes.
* **Restricciones:** Entorno de ejecución local/emulado, datos puramente sintéticos, prototipo sin persistencia remota en el MVP (InMemoryRepository).
* **Valor Esperado:** Optimizar la trazabilidad y reducir el tiempo de gestión de préstamos en el centro de formación.
* **Product Goal:** *Mejorar la trazabilidad y la consulta de préstamos de recursos de formación mediante una experiencia móvil intuitiva y confiable.*

---

## 2. Arquitectura actual

```
Compose UI  ── eventos ──►  ViewModel  ──►  Repository (único acceso a datos)
      ▲                        │                  ├── Local : Room + DataStore  (fuente canónica)
      └──── UiState/StateFlow ◄─┘                  └── Remoto: Retrofit + OkHttp (DTO ↔ dominio)
```

* **UI (Compose):** representa estado y emite eventos. No conoce Room ni Retrofit.
* **ViewModel + UiState/StateFlow:** coordina estado y acciones; expone estados `Cargando / Contenido / Vacío / Error`.
* **Repository:** decide el origen de datos (local-first: Room responde, la red sincroniza).
* **Persistencia:** Room (entidades + DAO) y DataStore (sesión/preferencias).
* **Red:** Retrofit + OkHttp con DTO, timeouts y mapeo de errores HTTP (401/404/5xx).

Detalle completo en [`docs/`](docs/).

## 3. Calidad, pruebas y CI

| Elemento | Ubicación |
|---|---|
| Matriz de riesgos | [`docs/RIESGOS.md`](docs/RIESGOS.md) |
| Plan de pruebas y casos (TC-01…TC-15) | [`docs/PLAN_PRUEBAS.md`](docs/PLAN_PRUEBAS.md) |
| Bitácora de ejecución (PASS/FAIL/BLOCKED) | [`docs/BITACORA_PRUEBAS.md`](docs/BITACORA_PRUEBAS.md) |
| Trazabilidad HU → CA → Riesgo → TC → PR → Bug | [`docs/MATRIZ_TRAZABILIDAD.md`](docs/MATRIZ_TRAZABILIDAD.md) |
| Definition of Done | [`docs/DEFINITION_OF_DONE.md`](docs/DEFINITION_OF_DONE.md) |
| Control de versiones por incremento | [`docs/VERSIONES.md`](docs/VERSIONES.md) |
| Quality gates (build + unit tests + lint) | [`.github/workflows/ci.yml`](.github/workflows/ci.yml) |

Ejecutar localmente los mismos quality gates que CI:

```bash
./gradlew testDebugUnitTest   # pruebas unitarias
./gradlew assembleDebug       # compilación
./gradlew lintDebug           # análisis estático
```

---

# Historias de Usuario

## HU-01 - Autenticación de Usuario

**Como** usuario del laboratorio,
**quiero** iniciar sesión con mis credenciales,
**para** acceder a los servicios de préstamo de equipos.

### Criterios de aceptación

* **CA-01.1:** El sistema debe validar las credenciales ingresadas contra el repositorio de usuarios registrados.
* **CA-01.2:** Si las credenciales son incorrectas o están vacías, se debe mostrar un mensaje de error indicando la falla.
* **CA-01.3:** Al autenticarse correctamente, el sistema debe redirigir al usuario a la vista principal del catálogo según su rol.

### Riesgos relacionados

* R-01

### Casos de prueba relacionados

* TC-01
* TC-02

---

## HU-02 - Visualización y Filtrado del Catálogo de Equipos

**Como** usuario,
**quiero** consultar el catálogo de equipos disponibles y filtrarlos por categoría o búsqueda de texto,
**para** encontrar lo que necesito rápidamente.

### Criterios de aceptación

* **CA-02.1:** El catálogo debe mostrar la lista de equipos registrados con su foto, nombre, categoría y disponibilidad.
* **CA-02.2:** El usuario debe poder filtrar la lista seleccionando categorías específicas (ej. Laptops, Cámaras, Herramientas).
* **CA-02.3:** La búsqueda por texto debe actualizar dinámicamente el catálogo según las coincidencias en el nombre del equipo.

### Riesgos relacionados

* R-02
* R-03

### Casos de prueba relacionados

* TC-03
* TC-04

---

## HU-03 - Consulta de Detalle del Equipo

**Como** usuario,
**quiero** ver la información detallada de un equipo,
**para** conocer sus especificaciones antes de solicitarlo.

### Criterios de aceptación

* **CA-03.1:** Al seleccionar un equipo del catálogo, se debe abrir la pantalla con la descripción completa, número de serie y estado actual.
* **CA-03.2:** Si el equipo está disponible, la pantalla debe incluir un acceso directo para iniciar la solicitud de préstamo.

### Riesgos relacionados

* R-04

### Casos de prueba relacionados

* TC-05

---

## HU-04 - Validación de Disponibilidad para Préstamo

**Como** usuario,
**quiero** que el sistema me impida solicitar equipos no disponibles,
**para** evitar solicitudes inválidas.

### Criterios de aceptación

* **CA-04.1:** Si el equipo tiene estado **"Disponible"**, se debe habilitar la opción de préstamo.
* **CA-04.2:** Si el equipo tiene estado **"En préstamo"** o **"En mantenimiento"**, el botón de solicitud debe permanecer deshabilitado.

### Riesgos relacionados

* R-05

### Casos de prueba relacionados

* TC-06
* TC-07

---

## HU-05 - Formulario y Creación de Solicitud de Préstamo

**Como** usuario,
**quiero** diligenciar un formulario con el motivo y la fecha de devolución,
**para** formalizar mi pedido de equipo.

### Criterios de aceptación

* **CA-05.1:** El formulario debe requerir el motivo del préstamo y la fecha estimativa de devolución.
* **CA-05.2:** El botón de confirmación debe permanecer desactivado hasta que todos los campos requeridos contengan datos válidos.
* **CA-05.3:** Al enviar la solicitud, esta se debe registrar con estado **"Pendiente"** y mostrar un mensaje de confirmación al usuario.

### Riesgos relacionados

* R-06
* R-07

### Casos de prueba relacionados

* TC-08
* TC-09

---

## HU-06 - Seguimiento de Mis Solicitudes

**Como** usuario,
**quiero** ver el listado de mis solicitudes realizadas,
**para** conocer su estado actual (Pendiente, Aprobada, Rechazada).

### Criterios de aceptación

* **CA-06.1:** La vista **"Mis Solicitudes"** debe listar únicamente las solicitudes asociadas al usuario autenticado.
* **CA-06.2:** Cada elemento de la lista debe mostrar el equipo, la fecha de solicitud, el motivo y una etiqueta indicando el estado.

### Riesgos relacionados

* R-08

### Casos de prueba relacionados

* TC-10

---

## HU-07 - Panel de Solicitudes Pendientes para Administrador

**Como** administrador del laboratorio,
**quiero** revisar el listado de solicitudes pendientes de todos los usuarios,
**para** gestionar sus aprobaciones.

### Criterios de aceptación

* **CA-07.1:** La vista debe ser accesible únicamente para usuarios con rol de **Administrador**.
* **CA-07.2:** Debe listar todas las solicitudes entrantes que estén en estado **"Pendiente"**, ordenadas secuencialmente.

### Riesgos relacionados

* R-09

### Casos de prueba relacionados

* TC-11

---

## HU-08 - Aprobación y Rechazo de Solicitudes

**Como** administrador,
**quiero** aprobar o rechazar las solicitudes de préstamo,
**para** controlar el flujo de entrega del laboratorio.

### Criterios de aceptación

* **CA-08.1:** Al presionar **"Aprobar"**, la solicitud pasa a estado **"Aprobada"** y el equipo cambia automáticamente a **"En préstamo"**.
* **CA-08.2:** Al presionar **"Rechazar"**, la solicitud pasa a estado **"Rechazada"** y el equipo se mantiene **"Disponible"**.

### Riesgos relacionados

* R-05
* R-09

### Casos de prueba relacionados

* TC-12
* TC-13

---

## HU-09 - Gestión del Inventario de Equipos

**Como** administrador,
**quiero** agregar, editar o cambiar el estado de los equipos en el inventario,
**para** mantener la lista actualizada.

### Criterios de aceptación

* **CA-09.1:** Permite registrar un nuevo equipo definiendo su nombre, categoría, descripción y estado inicial.
* **CA-09.2:** Permite modificar la información existente o actualizar el estado del equipo manualmente (Disponible/Mantenimiento).

### Riesgos relacionados

* R-10

### Casos de prueba relacionados

* TC-14

---

## HU-10 - Cierre de Sesión

**Como** usuario,
**quiero** cerrar mi sesión de forma segura,
**para** proteger mis datos en dispositivos compartidos.

### Criterios de aceptación

* **CA-10.1:** Debe existir un botón accesible de cierre de sesión en la barra de navegación superior.
* **CA-10.2:** Al confirmar el cierre, se debe limpiar el estado del usuario activo y redirigir a la pantalla de **Login**.

### Riesgos relacionados

* R-01

### Casos de prueba relacionados

* TC-15

---

# Alineación con la Guía de Aprendizaje Integradora (Semanas 5–9)

## Checklist de la guía (sección 12) — estado en esta rama

| Requisito | Estado | Evidencia |
|---|---|---|
| Product Goal visible; Backlog basado en Issues reales | ⚠️ | Product Goal en §1; los HU viven en este README (migrar/verificar en GitHub Issues) |
| HU con criterios de aceptación verificables | ✅ | §2 Historias de Usuario (CA-01.1 … CA-10.2) |
| Cada HU con riesgos y casos de prueba relacionados | ✅ | `docs/RIESGOS.md`, `docs/PLAN_PRUEBAS.md` |
| Ramas y PR rastreables por HU | ⚠️ | Ramas `feature/*` + PR existentes; falta naming `feature/hu-XX-...` en ramas nuevas |
| La UI Compose no accede directamente a Room/Retrofit | ✅ | UI recibe `UiState` y eventos; datos en `data/local` y `data/remote` |
| ViewModel / UiState / StateFlow coherentes | ✅ | `PrestamoUiState` con estados `Cargando/Contenido/Vacío/Error` (Semana 7) |
| Room/DataStore con responsabilidad clara y fuente única de verdad | ✅ | `data/local/*`, `RoomPrestamoRepository` |
| Corrutinas/Flow respetan ciclo de vida y manejo de errores | ✅ | `collectAsStateWithLifecycle`, `CancellationException` propagada, `RedError` tipado |
| La integración REST maneja DTO, mapeo, timeouts y errores | ✅ | `data/remote/*` + `PrestamoApiServiceTest` (MockWebServer) |
| Pruebas unitarias y/o de integración automatizadas | ✅ | 46 tests unitarios, 0 fallos |
| Evidencia fotográfica con URI persistida (sin Bitmap/Base64) | ✅ | HU-13: Photo Picker + `EvidenciaEntity` (Room v2, migración explícita) + `EvidenciaTest` |
| GitHub Actions ejecuta build, tests y lint | ✅ | `.github/workflows/ci.yml` |
| Defectos documentados con evidencia reproducible | ✅ | `docs/BITACORA_PRUEBAS.md` §3 |
| Confirmación y regresión cuando aplica | ⬜ | Pendiente de ejecución manual y de abrir/cerrar Issues |
| Permisos con mínimo privilegio (cámara/notificaciones) | ✅ | Photo Picker del sistema: sin permisos de cámara/almacenamiento |
| Capacidad física adicional a la cámara, con justificación | ✅ | HU-14: sensor de luz ambiente (`data/capabilities/`), sin permisos y con privacidad documentada |
| Sin secretos ni datos sensibles expuestos | ✅ | URL de ambiente vía `BuildConfig.BASE_URL` inyectada por Gradle |
| Incremento demostrable desde `main` / tag de entrega | ⬜ | Falta etiquetar `v0.2.0`…`v0.6.0` (ver `docs/VERSIONES.md`) |
| Cada integrante explica una HU y modifica una parte en vivo | ⬜ | Sustentación individual |

Leyenda: ✅ cumplido · ⚠️ cumplido parcialmente · ⬜ pendiente.

## Uso responsable de IA (sección 14)

* Esta rama incluyó asistencia de IA para **generar y revisar código y documentación** (capa de red,
  estados de carga, artefactos de gestión y pruebas).
* **Verificación realizada por el equipo:** cada cambio se compiló (`assembleDebug`), se ejecutó la
  suite completa (`testDebugUnitTest`: 41 tests, 0 fallos) y el análisis estático (`lintDebug`) antes de subirse.
* **No se reportan PASS/FAIL inventados:** la bitácora solo registra ejecuciones reales
  (`docs/BITACORA_PRUEBAS.md`).
* **No hay secretos, tokens ni datos personales** generados ni versionados.
* Cada integrante debe poder **explicar y modificar** el código de su HU antes de sustentarla:
  leer `docs/MATRIZ_TRAZABILIDAD.md` para seguir la trazabilidad HU → CA → riesgo → TC → código → prueba.

## Estructura del repositorio

```
.github/workflows/ci.yml      Quality gates: build + unit tests + lint
docs/                         Riesgos, plan de pruebas, bitácora, trazabilidad, DoD, versiones
app/src/main/java/.../model/        Modelos de dominio (Equipo, SolicitudPrestamo, …)
app/src/main/java/.../domain/       Reglas y validaciones puras (fáciles de unit-testear)
app/src/main/java/.../data/local/   Room (Entity/DAO/Database) + DataStore
app/src/main/java/.../data/remote/  Retrofit/OkHttp, DTO, mapeo de errores y sincronización
app/src/main/java/.../data/repository/  Repository como único acceso a datos
app/src/main/java/.../ui/           ViewModel, UiState, navegación y pantallas Compose
app/src/test/                       Pruebas unitarias (JUnit + coroutines-test + MockWebServer)
app/src/androidTest/                Pruebas UI/instrumentadas (Compose Test)
```

