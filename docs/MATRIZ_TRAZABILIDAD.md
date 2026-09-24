# Matriz de Trazabilidad — PréstamoLab CTMA

Relaciona cada Historia de Usuario (HU) con su implementación en código, sus criterios de aceptación (CA), las pruebas automatizadas y los casos de prueba manuales (TC) del plan.

| HU | Criterio de aceptación | Código fuente | Prueba automatizada | TC manual |
|---|---|---|---|---|
| HU-01 Autenticación | CA-01.1, CA-01.2, CA-01.3 | `ui/PrestamoViewModel.kt` → `login()`; `data/local/SesionDataStore.kt` | `PrestamoViewModelTest` | TC-01, TC-02 |
| HU-02 Catálogo y filtrado | CA-02.1, CA-02.2, CA-02.3 | `ui/screens/CatalogoScreen.kt`; `data/repository/*` → `equipos` | — | TC-03, TC-04 |
| HU-03 Detalle del equipo | CA-03.1, CA-03.2 | `ui/screens/EquipoDetalleScreen.kt` | — | TC-05 |
| HU-04 Validación de disponibilidad | CA-04.1, CA-04.2 | `domain/Validaciones.kt`; `data/repository/RoomPrestamoRepository.kt` → `crearSolicitud` | `InMemoryPrestamoRepositoryTest` | TC-06, TC-07 |
| HU-05 Formulario de solicitud | CA-05.1, CA-05.2, CA-05.3 | `ui/screens/SolicitudFormScreen.kt`; `ui/PrestamoViewModel.kt` → `crearSolicitud` | `ValidacionesTest`; `InMemoryPrestamoRepositoryTest` | TC-08, TC-09 |
| HU-06 Mis solicitudes | CA-06.1, CA-06.2 | `ui/screens/MisSolicitudesScreen.kt` | — | TC-10 |
| HU-07 Panel pendientes admin | CA-07.1, CA-07.2 | `ui/screens/SolicitudesPendientesScreen.kt` | — | TC-11 |
| HU-08 Aprobación y rechazo | CA-08.1, CA-08.2 | `ui/PrestamoViewModel.kt` → `aprobarSolicitud`/`rechazarSolicitud`; repositorio | `InMemoryPrestamoRepositoryTest` | TC-12, TC-13 |
| HU-09 Gestión de inventario | CA-09.1, CA-09.2 | `ui/screens/GestionInventarioScreen.kt`; repositorio → `agregar/editar/cambiarEstado` | `InMemoryPrestamoRepositoryTest` | TC-14 |
| HU-10 Cierre de sesión | CA-10.1, CA-10.2 | `ui/PrestamoViewModel.kt` → `logout()`; `SesionDataStore.limpiarSesion` | `PrestamoViewModelTest` | TC-15 |

## Reglas de negocio → validación

| Regla | Implementación |
|---|---|
| Solo prestar equipos `DISPONIBLE` | `RoomPrestamoRepository.crearSolicitud` (`IllegalStateException` si no disponible) |
| Prevenir duplicados por doble clic | `PrestamoViewModel`: guard `guardando` en todas las mutaciones; `enabled = !guardando` en UI |
| IDs inexistentes sin crash | `RoomPrestamoRepository` devuelve `Result.failure`; pantallas muestran fallback con botón Volver |
| Guardar imágenes como URI, no Bitmap/Base64 | `EvidenciaEntity` persiste `uri` + metadatos (Semana 9); nunca se almacena el Bitmap en la base |
| No exponer secretos en código plano | Sin claves ni tokens en el repositorio; la URL por ambiente se inyecta con `BuildConfig.BASE_URL` (Gradle), no en el código |
| La UI no conoce Retrofit ni Room | `PrestamoNavHost` y las pantallas solo reciben `UiState` y emiten eventos; Retrofit vive en `data/remote` y Room en `data/local` |
| Room es la fuente local canónica | La sincronización remota escribe en Room (`SincronizadorRemoto` → DAO) y la UI siempre lee de Room |

## Persistencia (Semana 6)

| Capa | Archivo |
|---|---|
| Room (fuente única de verdad) | `data/local/PrestamoDatabase.kt`, `PrestamoDao.kt`, `EquipoEntity.kt`, `SolicitudEntity.kt` |
| DataStore (preferencias de sesión) | `data/local/SesionDataStore.kt` |
| Repositorio Room | `data/repository/RoomPrestamoRepository.kt` |
| Repositorio en memoria (unit tests) | `data/repository/InMemoryPrestamoRepository.kt` |