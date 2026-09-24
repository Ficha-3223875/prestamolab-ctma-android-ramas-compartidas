# Bitácora de ejecución de pruebas — PréstamoLab CTMA

Registra **resultados reales** de ejecución (sección 12 de la guía y regla de uso responsable de IA:
no se reportan PASS/FAIL inventados).

- **Cómo ejecutar:** `./gradlew testDebugUnitTest` (unitarias/automatizadas) y ejecución manual sobre emulador/dispositivo.
- **Estados:** `PASS` = cumplió · `FAIL` = no cumplió · `BLOCKED` = no ejecutable · `PENDIENTE` = aún no ejecutado.

## 1. Pruebas automatizadas

| Fecha | Suite / test | Resultado | Evidencia |
|---|---|---|---|
| 2026-09-24 | `ValidacionesTest` | PASS | `gradlew testDebugUnitTest` (BUILD SUCCESSFUL) |
| 2026-09-24 | `InMemoryPrestamoRepositoryTest` | PASS | `gradlew testDebugUnitTest` (BUILD SUCCESSFUL) |
| 2026-09-24 | `PrestamoViewModelTest` | PASS | `gradlew testDebugUnitTest` (BUILD SUCCESSFUL) |
| 2026-09-24 | `EstadoCargaTest` (Semana 7: Cargando/Contenido/Vacío/Error) | PASS | 5 tests, 0 fallos (`testDebugUnitTest`) |
| 2026-09-24 | `DtoMappingTest` (Semana 8: DTO ↔ dominio) | PASS | 7 tests, 0 fallos (`testDebugUnitTest`) |
| 2026-09-24 | `PrestamoApiServiceTest` (Semana 8: MockWebServer 200/404/500/timeout/JSON inválido) | PASS | 7 tests, 0 fallos (`testDebugUnitTest`) |
| 2026-09-24 | `EvidenciaTest` (Semana 9 / HU-13: URI + metadatos, validación de URI vacía, mensaje recuperable) | PASS | 5 tests, 0 fallos (`testDebugUnitTest`) |
| 2026-09-24 | `CatalogoScreenUiTest` (Semana 9 / prueba UI instrumentada) | PENDIENTE | Compila (`compileDebugAndroidTestKotlin` OK); requiere emulador/dispositivo para ejecutarse |

> **Total automatizado: 46 tests, 0 fallos** (`./gradlew testDebugUnitTest`, 24/09/2026, rama `feature/JuanPaniagua`).
> Quality gates de la entrega: `assembleDebug` ✅ · `testDebugUnitTest` 46/0 ✅ · `lintDebug` ✅ · `compileDebugAndroidTestKotlin` ✅.

## 2. Pruebas manuales (suite TC-01 … TC-15)

| TC | Descripción breve | Resultado | Fecha / observaciones |
|---|---|---|---|
| TC-01 | Login con credenciales válidas | PENDIENTE | — |
| TC-02 | Login con campos vacíos o clave errónea | PENDIENTE | — |
| TC-03 | Catálogo muestra imagen, nombre, categoría y estado | PENDIENTE | — |
| TC-04 | Filtro por categoría | PENDIENTE | — |
| TC-05 | Detalle del equipo | PENDIENTE | — |
| TC-06 | Solicitud con equipo "Disponible" | PENDIENTE | — |
| TC-07 | Botón deshabilitado con equipo no disponible | PENDIENTE | — |
| TC-08 | Formulario de solicitud válido | PENDIENTE | — |
| TC-09 | Doble pulsación no duplica la solicitud | PENDIENTE | — |
| TC-10 | Mis Solicitudes filtra por usuario | PENDIENTE | — |
| TC-11 | Panel de pendientes (solo administrador) | PENDIENTE | — |
| TC-12 | Aprobar solicitud → equipo "En préstamo" | PENDIENTE | — |
| TC-13 | Rechazar solicitud → equipo "Disponible" | PENDIENTE | — |
| TC-14 | Gestión de inventario (agregar/editar/estado) | PENDIENTE | — |
| TC-15 | Cierre de sesión | PENDIENTE | — |

### Casos nuevos derivados de esta rama

| TC | Descripción | Resultado |
|---|---|---|
| TC-16 | Navegación a **Gestión de Inventario** desde el catálogo (rol encargado) | PENDIENTE |
| TC-17 | Estados de carga: cargando → contenido / vacío / error + botón **Reintentar** | PENDIENTE |
| TC-18 | Sincronización con API: éxito, sin conexión y timeout mostrados como mensaje recuperable | PENDIENTE |
| TC-19 | Adjuntar evidencia fotográfica a una solicitud (URI persistida) | PENDIENTE |
| TC-20 | Lectura del sensor de luz ambiente sin solicitar permisos | PENDIENTE |

## 3. Defectos encontrados

| BUG | Descripción | Severidad | Estado |
|---|---|---|---|
| BUG-01 | `GestionInventarioScreen` existía pero no estaba registrada en `NavHost`: las HU-09…HU-12 no eran alcanzables | Alta | Corregido en esta rama (ruta `gestionInventario` + acceso desde el catálogo) |
| BUG-02 | Sin quality gates de CI: no existía `.github/workflows`, el merge a `main` no estaba protegido | Media | Corregido (build + unit tests + lint) |

## 4. Registros pendientes de ejecución manual

Los resultados manuales (tabla 2) deben cargarse con la fecha real y el dispositivo/emulador utilizado.
Si un caso falla, abrir el Issue `BUG-XX` correspondiente y registrar confirmación y regresión.
