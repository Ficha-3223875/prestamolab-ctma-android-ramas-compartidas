# Definition of Done (DoD) — PréstamoLab CTMA

Compromiso de calidad del incremento. Una Historia de Usuario **no está terminada por existir una pantalla**:
debe cumplir criterios de aceptación, integrarse a la arquitectura, superar las pruebas acordadas y esta DoD.

Fuente: Guía de Aprendizaje Integradora parte 2 (secciones 2, 5, 7 y 12).

## DoD del Incremento

- [ ] La HU tiene criterios de aceptación verificables y está vinculada a un Issue de GitHub.
- [ ] El código está en una rama `feature/...` y entra mediante Pull Request (nunca directo a `main`).
- [ ] El PR referencia la HU y permite rastrear HU → CA → Riesgo → TC → PR → Resultado → Bug en `docs/MATRIZ_TRAZABILIDAD.md`.
- [ ] Se respetan las capas: **Compose UI no accede a Room/Retrofit**; la UI solo emite eventos y representa `UiState`.
- [ ] El `ViewModel` expone `UiState` observable vía `StateFlow` y se recolecta con `collectAsStateWithLifecycle`.
- [ ] Room es la fuente local canónica; el Repository es el único punto de acceso a los datos.
- [ ] Las operaciones asíncronas usan corrutinas/`Flow` respetando ciclo de vida, cancelación y manejo de errores.
- [ ] Los estados de carga, contenido, vacío y error están modelados en el `UiState` (no hay pantallas "colgadas").
- [ ] Existen pruebas automatizadas pertinentes (unitarias y/o de integración) y **pasan localmente**.
- [ ] `./gradlew testDebugUnitTest`, `./gradlew assembleDebug` y `./gradlew lintDebug` pasan (quality gates de CI).
- [ ] GitHub Actions ejecuta build + unit tests + lint en el PR y está en verde.
- [ ] Los riesgos asociados están en `docs/RIESGOS.md` y los casos de prueba en `docs/PLAN_PRUEBAS.md`.
- [ ] No hay secretos, tokens ni datos sensibles en texto plano ni en el historial de Git.
- [ ] El README/documentación del incremento refleja cambios de arquitectura, ejecución o limitaciones.
- [ ] Se actualiza la versión del incremento (ver `docs/VERSIONES.md`) y queda identificable con tag/commit.

## Definition of Done adicional por capability

| Capability | Condición extra |
|---|---|
| Persistencia (Semana 6) | Migraciones de Room declaradas; datos sobreviven al reinicio de la app |
| Asincronía (Semana 7) | Estados Loading/Empty/Error cubiertos por prueba; sin `runBlocking` en la UI |
| API (Semana 8) | DTO ↔ dominio ↔ Entity mapeados; timeouts y errores 401/404/5xx manejados; pruebas con MockWebServer |
| Capacidad del dispositivo (Semana 9) | Permiso solicitado solo cuando se usa (mínimo privilegio), URI/metadatos persistidos, privacidad documentada |
| CI (Semana 8-9) | Quality gates bloquean el merge; artefactos de reporte disponibles |

## Reglas de transparencia

- No se reportan PASS/FAIL inventados: cada resultado registrado en `docs/BITACORA_PRUEBAS.md` corresponde a una ejecución real.
- El código generado o asistido por IA se documenta en el README (propósito y verificación) según la sección 14 de la guía.
