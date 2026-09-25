# Resumen de Evidencias — Semana 5: Arquitectura, Primer Incremento y Calidad (v0.2.0)

> [!NOTE]
> Este documento consolida todas las evidencias, artefactos de gestión, planes de prueba, matrices, riesgos y resultados de calidad correspondientes al incremento funcional v0.2.0 de **PréstamoLab CTMA**.

---

## 1. Product Backlog Actualizado

| ID | Historia de Usuario (HU) / Funcionalidad | Prioridad | Estado |
|:---|:---|:---|:---|
| HU-01 | Autenticación de usuarios por roles (Aprendiz / Encargado) | Alta | Completado |
| HU-02 | Visualización de catálogo de equipos con filtros por categoría | Alta | Completado |
| HU-03 | Detalle de equipo (especificaciones, estado y disponibilidad) | Alta | Completado |
| HU-04 | Validación de disponibilidad del equipo antes de solicitar | Alta | Completado |
| HU-05 | Formulario de solicitud de préstamo (ambiente, propósito, duración) | Alta | Completado |
| HU-06 | Listado de "Mis Solicitudes" para seguimiento del aprendiz | Alta | Completado |
| HU-07 | Panel de Encargado: Solicitudes pendientes de aprobación/rechazo | Media | Completado |
| HU-08 | Aprobación o rechazo de solicitudes con justificación | Media | Completado |
| HU-09 | Gestión y administración de inventario de equipos | Media | Completado |
| HU-10 | Captura instantánea de foto con cámara (o galería) y visor de evidencia de devolución | Media | Completado |
| HU-11 | Notificaciones locales automáticas para nuevas solicitudes de préstamo | Media | Completado |

---

## 2. Sprint Goal (Objetivo del Sprint)
> *"Estabilizar la arquitectura base de la aplicación, implementar el flujo end-to-end de solicitudes (Catálogo ➔ Detalle ➔ Solicitar ➔ Mis Solicitudes) utilizando Jetpack Compose, Arquitectura MVVM con StateFlow, persistencia local con Room / Repositorio InMemory, validaciones robustas de negocio, captura fotográfica de devolución con cámara y notificaciones locales, asegurando una suite completa de pruebas unitarias y manuales."*

---

## 3. Sprint Backlog

- [x] Configuración de navegación centralizada (`PrestamoNavHost` y rutas con Compose).
- [x] Implementación de pantallas UI en Jetpack Compose (`CatalogoScreen`, `EquipoDetalleScreen`, `SolicitudFormScreen`, `MisSolicitudesScreen`, `SolicitudDetalleScreen`).
- [x] Gestión de estado reactivo mediante `PrestamoViewModel` y `PrestamoUiState` (con `StateFlow`).
- [x] Capa de persistencia y repositorio (`RealPrestamoRepository` con Room y `InMemoryPrestamoRepository` para tests).
- [x] Validaciones de dominio (`Validaciones.kt`: propósito, duración, ambiente).
- [x] Funcionalidad de cámara (`TakePicture`, `FileProvider`, permisos de cámara) y visor de evidencia fotográfica (`ACTION_VIEW`).
- [x] Sistema de notificaciones locales (`NotificationHelper`, canal de alta prioridad, permiso `POST_NOTIFICATIONS` en Android 13+).

---

## 4. Definition of Done (DoD)
1. **Compilación:** El código compila al 100% sin errores ni advertencias críticas de Gradle o lint.
2. **Arquitectura:** Se respeta la separación de capas (UI, ViewModel, Dominio, Datos/Room/API).
3. **Pruebas Automatizadas:** La suite de pruebas unitarias (`PrestamoViewModelTest`, `InMemoryPrestamoRepositoryTest`, `ValidacionesTest`) pasa de forma exitosa (`22/22 tests passed`).
4. **Flujos Funcionales:** El flujo completo (Catálogo ➔ Detalle ➔ Solicitar ➔ Mis Solicitudes) está operativo y verificado.
5. **Calidad y Documentación:** Cobertura de casos de prueba manuales de `17` casos con su respectiva bitácora de ejecución, plan de pruebas y matriz de riesgos actualizados.

---

## 5. Matriz de Riesgos Actualizada

| ID | Riesgo | Probabilidad | Impacto | Nivel | Tratamiento / Mitigación |
|:---|:---|:---|:---|:---|:---|
| R-01 | Intentar iniciar sesión con credenciales vacías o inválidas | Alta | Alta | Alto | Validación en ViewModel y feedback visual inmediato |
| R-02 | Visualizar un catálogo vacío sin feedback | Media | Media | Medio | Implementación de `ListadoUiState.Vacio` |
| R-03 | Inconsistencia en el filtrado por categoría | Media | Media | Medio | Reactividad mediante `StateFlow` |
| R-04 | Navegación a un detalle de equipo inexistente | Baja | Alta | Medio | Manejo de nulos y pantallas de error/fallback |
| R-05 | Solicitar un equipo cuya disponibilidad cambió | Alta | Alta | Alto | Validación transaccional en repositorio |
| R-06 | Envío de formulario con campos incompletos | Alta | Media | Alto | Validaciones de dominio (`ambienteValido`, `propositoValido`, `duracionValida`) |
| R-07 | Duplicidad de solicitudes por doble toque | Media | Alta | Alto | Bandera de procesamiento en curso (`isProcessing`) |
| R-08 | Visualización desactualizada de solicitudes | Media | Media | Medio | Observación reactiva con Flows desde la base de datos |
| R-09 | Aprobación o rechazo no autorizado por usuarios sin rol Admin | Baja | Alta | Alto | Restricción de vista y acciones según el rol del usuario |
| R-10 | Modificación o eliminación inconsistente de equipos en inventario | Media | Alta | Alto | Sincronización en el repositorio central del sistema |
| R-11 | Denegación de permiso de cámara o fallo al generar URI temporal | Media | Media | Medio | Solicitud de permisos en tiempo de ejecución y uso seguro de `FileProvider` |
| R-12 | Bloqueo de notificaciones locales en Android 13+ | Media | Media | Medio | Solicitud automática del permiso en `MainActivity` y canal `IMPORTANCE_HIGH` |

---

## 6. Matriz de Trazabilidad (HU vs. Casos de Prueba)

| Historia de Usuario | Casos de Prueba Asociados |
|:---|:---|
| HU-01 (Login / Logout) | TC-01, TC-02, TC-17 |
| HU-02 (Catálogo) | TC-03, TC-04 |
| HU-03 (Detalle) | TC-05 |
| HU-04 (Disponibilidad) | TC-06, TC-07 |
| HU-05 (Solicitud) | TC-08, TC-09 |
| HU-06 (Mis Solicitudes) | TC-10 |
| HU-07 & HU-08 (Aprobación/Rechazo) | TC-11, TC-12, TC-13 |
| HU-09 (Inventario) | TC-14 |
| HU-10 (Cámara / Visor de Evidencia) | TC-15 |
| HU-11 (Notificaciones Locales) | TC-16 |

---

## 7. Plan / Suite de Pruebas Manuales Actualizada (17 Casos Representativos)

| ID | HU / Componente | Precondición | Pasos de Prueba | Resultado Esperado |
|:---|:---|:---|:---|:---|
| **TC-01** | HU-01 | Usuario en Login | Ingresar correo válido y clave "123456", presionar Iniciar Sesión | Inicio exitoso y redirección al Catálogo |
| **TC-02** | HU-01 | Usuario en Login | Ingresar clave incorrecta, presionar Iniciar Sesión | Mensaje de error y permanencia en Login |
| **TC-03** | HU-02 | Catálogo cargado | Navegar a la pantalla de Catálogo | Visualización correcta de tarjetas de equipos con categoría y estado |
| **TC-04** | HU-02 | Catálogo con ítems | Seleccionar filtro de categoría | Filtrado reactivo de equipos en pantalla |
| **TC-05** | HU-03 | Catálogo visible | Pulsar sobre la tarjeta de un equipo | Apertura de pantalla de detalle con especificaciones |
| **TC-06** | HU-04 | Equipo "Disponible" | Pulsar el botón "Solicitar Préstamo" | Redirección al formulario de solicitud |
| **TC-07** | HU-04 | Equipo "Préstamo" | Visualizar detalle de equipo no disponible | Botón de solicitud deshabilitado |
| **TC-08** | HU-05 | Formulario abierto | Llenar ambiente, propósito válido (>10 carac.) y duración (1-8h), confirmar | Creación exitosa de la solicitud |
| **TC-09** | HU-05 | Formulario abierto | Intentar doble toque rápido en confirmar | Se procesa una única solicitud (evita duplicidad) |
| **TC-10** | HU-06 | Sesión activa | Navegar a "Mis Solicitudes" | Listado histórico con estados en tiempo real |
| **TC-11** | HU-07 | Sesión Encargado | Navegar a "Solicitudes Pendientes" | Visualización de solicitudes entrantes |
| **TC-12** | HU-08 | Solicitud pendiente | Pulsar "Aprobar" | Estado cambia a Aprobada y equipo a Reservado/Préstamo |
| **TC-13** | HU-08 | Solicitud pendiente | Pulsar "Rechazar" indicando razón | Estado cambia a Rechazada y equipo a Disponible |
| **TC-14** | HU-09 | Sesión Encargado | Registrar o editar un equipo en inventario | Actualización inmediata en el catálogo |
| **TC-15** | HU-10 | Solicitud aprobada | Pulsar "Adjuntar/Capturar Foto", elegir "Tomar Foto" o "Galería" y luego "Abrir Evidencia" | Captura exitosa, guardado en Room, cambio a DEVUELTA y apertura correcta de la imagen |
| **TC-16** | HU-11 | App abierta / permisos | Crear nueva solicitud de préstamo | Emisión inmediata de notificación local push en la barra de estado con alta prioridad |
| **TC-17** | HU-01 | Sesión activa | Presionar el botón de cerrar sesión | Finaliza la sesión y redirige al Login |

---

## 8. Bitácora de Ejecución de Pruebas

| ID Prueba | Ejecutador | Fecha | Estado (PASS / FAIL / BLOCKED) | Observaciones / Comentarios |
|:---|:---|:---|:---|:---|
| **TC-01** | QA Lead | 2026-09-23 | **PASS** | Credenciales validadas correctamente. |
| **TC-02** | QA Lead | 2026-09-23 | **PASS** | Mensaje de error mostrado vía Snackbar/UiState. |
| **TC-03** | QA Lead | 2026-09-23 | **PASS** | Listado renderizado de forma fluida con Compose. |
| **TC-04** | QA Lead | 2026-09-23 | **PASS** | Filtrado reactivo instantáneo vía StateFlow. |
| **TC-05** | QA Lead | 2026-09-23 | **PASS** | Navegación correcta mediante Navigation. |
| **TC-06** | QA Lead | 2026-09-23 | **PASS** | Transición limpia a formulario. |
| **TC-07** | QA Lead | 2026-09-23 | **PASS** | Restricción visual aplicada correctamente. |
| **TC-08** | QA Lead | 2026-09-23 | **PASS** | Validaciones de dominio superadas con éxito. |
| **TC-09** | QA Lead | 2026-09-23 | **PASS** | Protección con `isProcessing` efectiva. |
| **TC-10** | QA Lead | 2026-09-23 | **PASS** | Observador reactivo actualizado en tiempo real. |
| **TC-11** | QA Lead | 2026-09-23 | **PASS** | Filtro de solicitudes pendientes correcto. |
| **TC-12** | QA Lead | 2026-09-23 | **PASS** | Cambio de estado transaccional validado. |
| **TC-13** | QA Lead | 2026-09-23 | **PASS** | Razón de rechazo validada (>5 caracteres). |
| **TC-14** | QA Lead | 2026-09-23 | **PASS** | Inventario actualizado correctamente en Room. |
| **TC-15** | QA Lead | 2026-09-23 | **PASS** | Captura con cámara, galería y visor de evidencia validados. |
| **TC-16** | QA Lead | 2026-09-23 | **PASS** | Notificación local emitida con canal `IMPORTANCE_HIGH` y permisos Android 13+. |
| **TC-17** | QA Lead | 2026-09-23 | **PASS** | Cierre de sesión y limpieza de estado correctos. |

---

## 9. Defectos Encontrados y Registrados (Histórico de Corrección)

| Defecto ID | Descripción | Severidad | Estado | Solución Aplicada |
|:---|:---|:---|:---|:---|
| **DEF-01** | Doble toque en el formulario generaba solicitudes duplicadas. | Alta | **Resuelto** | Implementación de bandera `isProcessing` en ViewModel. |
| **DEF-02** | Excepción no controlada al intentar leer Context en tests unitarios para notificaciones. | Media | **Resuelto** | Bloque `try-catch` de protección y manejo de contexto no mockeado. |
| **DEF-03** | Supresión silenciosa de notificaciones en Android 13+. | Media | **Resuelto** | Inclusión de solicitud de permiso `POST_NOTIFICATIONS` en `MainActivity`. |

---

## 10. Evidencia de Confirmación / Regresión y Conclusión

- **Pruebas Unitarias TDD:** 100% de éxito en la suite de pruebas unitarias (`22/22 tests passed`).
- **Arquitectura MVVM + Jetpack Compose + Room:** Componentes desacoplados, UI declarativa reactiva y persistencia transaccional.
- **Nuevas Características Incorporadas:** Captura fotográfica con cámara para devoluciones, visor de evidencia y notificaciones locales push.
- **Resultado del Incremento:** **Versión 0.2.0 Estable, Documentada y Verificada**.

---

## 11. Cuestionario Técnico y Arquitectural de la Solución

### 1. Abra una HU y muestre un criterio de aceptación; siga la trazabilidad hasta el código y la prueba que lo valida.
- **HU-05 (Formulario de solicitud de préstamo):**
  - **Criterio de Aceptación:** El propósito del préstamo debe tener obligatoriamente entre 10 y 180 caracteres para poder ser enviado.
  - **Trazabilidad al Código:**
    - Regla de negocio en [Validaciones.kt](file:///C:/Users/MiguelFormacion.LenovoLOQ_MIGAN/AndroidStudioProjects/prestamolab-ctma-android-ramas-compartidas/app/src/main/java/com/example/miprestamoslab/domain/Validaciones.kt): `fun propositoValido(proposito: String): Boolean = proposito.trim().length in 10..180`
    - Validación en ViewModel en [PrestamoViewModel.kt](file:///C:/Users/MiguelFormacion.LenovoLOQ_MIGAN/AndroidStudioProjects/prestamolab-ctma-android-ramas-compartidas/app/src/main/java/com/example/miprestamoslab/ui/PrestamoViewModel.kt): `if (!propositoValido(proposito)) errores.add(...)`
  - **Trazabilidad a la Prueba:**
    - Prueba unitaria en [ValidacionesTest.kt](file:///C:/Users/MiguelFormacion.LenovoLOQ_MIGAN/AndroidStudioProjects/prestamolab-ctma-android-ramas-compartidas/app/src/test/java/com/example/miprestamoslab/ValidacionesTest.kt): `propositoValido_debeAceptarEntre10Y180Caracteres()`
    - Caso de prueba manual: **TC-08**.

### 2. Explique por qué Room se considera fuente local canónica en su solución.
- Se considera la **fuente canónica local (Single Source of Truth)** porque todos los estados del dominio, equipos y solicitudes se leen y escriben directamente a través de las entidades transaccionales de Room (`EquipoEntity`, `SolicitudPrestamoEntity`) mediante DAOs reactivos con `Flow`. Ningún componente de la UI accede a fuentes volátiles o temporales; Room garantiza la persistencia, consistencia relacional y disponibilidad *offline-first*.

### 3. ¿Qué diferencia existe entre Flow y StateFlow en el contexto del ViewModel?
- **Flow:** Es un flujo *frío (cold stream)*; no emite valores hasta que hay un colector activo y recalcula o reejecuta su lógica para cada nuevo suscriptor.
- **StateFlow:** Es un flujo *caliente (hot stream)* especializado en mantener estado; siempre retiene un valor actual cacheado (`value`), emite inmediatamente el último estado a cualquier nuevo suscriptor (ideal para Jetpack Compose con `.collectAsStateWithLifecycle()`) y requiere obligatoriamente un valor inicial. Se utiliza en `PrestamoViewModel` para mantener el `PrestamoUiState`.

### 4. Muestre un caso de error de red y explique cómo se representa en UiState.
- Cuando ocurre un fallo en una operación (como un error de creación de solicitud o login inválido), se representa en el estado de la UI mediante [PrestamoUiState.kt](file:///C:/Users/MiguelFormacion.LenovoLOQ_MIGAN/AndroidStudioProjects/prestamolab-ctma-android-ramas-compartidas/app/src/main/java/com/example/miprestamoslab/ui/PrestamoUiState.kt):
  - `operacionState = OperacionUiState.Fallida("Mensaje de error")`
  - `mensaje = "Mensaje de error"`
  - `guardando = false`
  La UI observa estos cambios reactivamente mediante `StateFlow` y despliega un `Snackbar` o texto de error descriptivo.

### 5. Seleccione un test automatizado y explique Arrange, Act y Assert.
- Analizando el test `crearSolicitud_conDatosValidos_debeCambiarEstadoEquipo` en [PrestamoViewModelTest.kt](file:///C:/Users/MiguelFormacion.LenovoLOQ_MIGAN/AndroidStudioProjects/prestamolab-ctma-android-ramas-compartidas/app/src/test/java/com/example/miprestamoslab/PrestamoViewModelTest.kt):
  - **Arrange (Preparar):** Se configura el ViewModel con un repositorio de prueba y un equipo disponible con ID 1 en estado `DISPONIBLE`.
  - **Act (Actuar):** Se invoca la función del ViewModel `viewModel.crearSolicitud(1, "Lab", "Propósito de prueba válido", "2", {})`.
  - **Assert (Afirmar):** Se comprueba mediante `assertEquals(OperacionUiState.Exitosa, uiState.operacionState)` y verificando el estado del equipo que la solicitud fue procesada correctamente.

### 6. ¿Qué parte del incremento fue desarrollada mediante TDD y qué aprendieron?
- Las **reglas de negocio y funciones de validación** (`Validaciones.kt`) y la lógica transaccional de los repositorios (`InMemoryPrestamoRepository`) fueron desarrolladas bajo **TDD (Test-Driven Development)**: primero se escribieron las pruebas unitarias fallidas (`ValidacionesTest`) y luego el código productivo para hacerlas pasar.
- **Aprendizaje:** Permitió diseñar interfaces limpias y puras, anticipando casos borde (cadenas vacías, límites numéricos) antes de construir las pantallas de la interfaz de usuario.

### 7. Muestre un defecto encontrado, su confirmación y la regresión seleccionada.
- **Defecto ID:** `DEF-01` (Doble toque rápido en el botón de confirmación del formulario de préstamo generaba solicitudes duplicadas).
- **Confirmación:** Detectado durante la ejecución manual de pruebas (**TC-09**).
- **Solución y Regresión:** Se introdujo la bandera de control `isProcessing` en `PrestamoViewModel` para bloquear llamadas concurrentes. La regresión se confirmó ejecutando exitosamente los tests unitarios del ViewModel y validando el caso **TC-09** (PASS).

### 8. ¿Qué permiso del dispositivo solicitaron y por qué cumple mínimo privilegio?
- **Permisos solicitados:** `CAMERA` y `POST_NOTIFICATIONS`.
- **Principio de menor privilegio:** No se solicitan de forma masiva al instalar la aplicación. `CAMERA` se solicita únicamente bajo demanda en tiempo de ejecución (`ActivityResultContracts.RequestPermission`) cuando el usuario pulsa específicamente el botón de adjuntar/capturar foto de devolución. `POST_NOTIFICATIONS` se solicita para habilitar el canal de alertas críticas de solicitudes nuevas.

### 9. ¿Qué quality gates utiliza su Pull Request?
- **Quality Gates:**
  1. Compilación exitosa de Gradle (`app:assembleDebug`).
  2. Aprobación del 100% de la suite de pruebas unitarias (`app:testDebugUnitTest`, 22/22 tests pasando).
  3. Ausencia de errores críticos de linter y advertencias de código obsoleto.
  4. Revisión de trazabilidad entre HU y casos de prueba.

### 10. ¿Qué riesgo residual permanece en el incremento actual?
- **Riesgo residual (R-12 / Conectividad remota):** Dado que la arquitectura está preparada con un repositorio *offline-first* y DTOs de API REST pero operando localmente con Room y *mocks*, el riesgo residual radica en la eventual sincronización de datos con un servidor backend en producción real ante escenarios de pérdida intermitente de red, lo cual requerirá la implementación posterior de un servicio de background sync (ej. WorkManager).
