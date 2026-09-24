# Control de versiones del incremento — PréstamoLab CTMA

Convención de la guía (sección 3.3): identificar de forma reproducible qué commit/tag corresponde a cada entrega.

| Versión | Semana | Incremento esperado | Estado en esta rama | Evidencia |
|---|---|---|---|---|
| v0.1.0 | Base (guía 1) | Línea base heredada: Compose + ViewModel + UiState + Repository InMemory + Navigation | ✅ Integrado | Historial previo a `feature/JuanPaniagua` |
| v0.2.0 | Semana 5 | Consolidación del incremento heredado + corte de calidad (riesgos, trazabilidad, casos manuales) | ✅ Integrado | `docs/RIESGOS.md`, `docs/PLAN_PRUEBAS.md`, `docs/MATRIZ_TRAZABILIDAD.md` |
| v0.3.0 | Semana 6 | Persistencia local: Room + DAO + DataStore + Scrum formal | ✅ Integrado | `data/local/*`, `RoomPrestamoRepository`, commit `7dd07ee` |
| v0.4.0 | Semana 7 | Reactivo/asíncrono: corrutinas, Flow, estados Loading/Content/Empty/Error, métricas | 🔄 En curso | `PrestamoUiState` + `PrestamoViewModel` |
| v0.5.0 | Semana 8 | Sincronización con API (Retrofit/OkHttp/DTO) + pruebas automatizadas + CI | 🔄 En curso | `.github/workflows/ci.yml`, capa `data/remote` |
| v0.6.0 | Semana 9 | Capacidades del dispositivo, seguridad, E2E, calidad no funcional | ⬜ Pendiente | — |

## Cómo etiquetar

```bash
git tag -a v0.3.0 -m "Incremento Semana 6: persistencia local Room + DataStore"
git push origin v0.3.0
```

> **Regla obligatoria:** cada entrega semanal debe quedar identificable por un tag (o, en su defecto,
> por el SHA del commit mergeado a `main`), para poder reproducir qué código correspondió a cada entregable.

## Commits de referencia en esta rama

| SHA | Descripción |
|---|---|
| `7dd07ee` | feat: persistencia local con Room y DataStore (Semana 6, v0.3.0) |
| `45dac76` | test: agregar y refactorizar pruebas unitarias personalizadas |
| `379591c` | docs: actualizar README con contribuciones de Juan Paniagua |
