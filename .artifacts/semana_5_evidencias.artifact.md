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
