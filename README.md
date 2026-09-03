 # PréstamoLab CTMA

## 1. Descubrimiento y Product Goal
* **Problema:** Falta de visibilidad y control sobre la disponibilidad, préstamos activos y devoluciones de equipos y herramientas compartidas en los laboratorios del CTMA.
* **Usuarios:** Solicitante demo (aprendices e instructores) y Gestor simulado.
* **Necesidades:** Consultar catálogo en tiempo real, solicitar préstamos de forma rápida, validar reglas de uso y rastrear el estado de las solicitudes.
* **Restricciones:** Entorno de ejecución local/emulado, datos puramente sintéticos, prototipo sin persistencia remota en el MVP (InMemoryRepository).
* **Valor Esperado:** Optimizar la trazabilidad y reducir el tiempo de gestión de préstamos en el centro de formación.
* **Product Goal:** *Mejorar la trazabilidad y la consulta de préstamos de recursos de formación mediante una experiencia móvil intuitiva y confiable.*

---

## 2. Historias de Usuario y Criterios de Aceptación

### HU-01: Consultar Catálogo
* **Descripción:** Como usuario, quiero ver la lista de equipos con su disponibilidad para saber qué recursos puedo solicitar.
* **Criterio de Aceptación:** Dado un usuario en la pantalla principal, cuando la app carga, se muestra el listado de equipos con su estado (`DISPONIBLE`, `RESERVADO`, `PRESTADO`).

### HU-02: Registrar Solicitud
* **Descripción:** Como usuario, quiero llenar un formulario para solicitar un equipo disponible.
* **Criterio de Aceptación:** Dado un equipo `DISPONIBLE` y un formulario válido (ambiente no vacío, propósito entre 10 y 180 caracteres y duración entre 1 y 8 horas), cuando el usuario pulsa *Confirmar Solicitud*, se crea una sola solicitud en estado `SOLICITADA` y el equipo pasa a `RESERVADO`.

### HU-03: Validación de Formulario
* **Descripción:** Como usuario, quiero que la app valide mis datos para evitar errores de envío.
* **Criterio de Aceptación:** Dado un propósito de menos de 10 caracteres, cuando el usuario intenta enviar, la app inhabilita la acción y muestra un mensaje de error sin modificar el estado del equipo.

### HU-04: Evitar Duplicados
* **Descripción:** Como usuario, quiero evitar solicitudes duplicadas si presiono accidentalmente dos veces el botón de envío.
* **Criterio de Aceptación:** Dado un formulario válido en proceso de envío, cuando se registra la acción, la app bloquea reintentos simultáneos y genera únicamente una (1) solicitud.

### HU-05: Consultar Mis Solicitudes
* **Descripción:** Como usuario, quiero ver el historial de mis solicitudes para hacerles seguimiento.
* **Criterio de Aceptación:** Dado un usuario que ha realizado una solicitud, al navegar a "Mis Solicitudes", la solicitud aparece listada mostrando equipo, ambiente, propósito, duración y estado actual.

### HU-06: Cancelar Solicitud
* **Descripción:** Como usuario, quiero poder cancelar una solicitud activa que ya no necesite.
* **Criterio de Aceptación:** Dada una solicitud en estado `SOLICITADA`, cuando el usuario presiona *Cancelar Solicitud*, la solicitud cambia a estado `CANCELADA` y el equipo asociado vuelve a estar `DISPONIBLE` en el catálogo.

---

## 3. Matriz de Riesgos

| ID | Riesgo | Prob. | Impacto | Nivel | Cobertura / Estrategia |
|---|---|---|---|---|---|
| R-01 | Dos solicitudes activas reservan el mismo equipo | Alta | Alta | Crítico | TC de disponibilidad + prevención de duplicados. |
| R-02 | Datos fuera de rango son aceptados (propósito < 10 o duración > 8) | Alta | Media | Alto | Partición de equivalencia + valores límite. |
| R-03 | ID inexistente o nulo provoca cierre abrupto de la app | Media | Alta | Alto | Navegación negativa y manejo de nulos. |
| R-04 | El catálogo no refleja el cambio de estado tras crear/cancelar | Media | Alta | Alto | Pruebas de flujo de estado + regresión. |
| R-05 | Las acciones o botones desaparecen con tamaño de fuente al 1.5× | Media | Media | Medio | Prueba de accesibilidad y layouts adaptables. |

---


* **Sprint Goal:** Permita consultar un equipo disponible y registrar una solicitud de préstamo válida, manteniendo la disponibilidad coherente y demostrando su calidad mediante pruebas reproducibles.


## 👤 Contribuciones de Juan Daniel Paniagua Tapias

### 🚀 Historias de Usuario Desarrolladas

| Historia de Usuario | Commit / PR | Descripción de la Implementación |
| :--- | :--- | :--- |
| **HU-15: Login y Autenticación** | `f78ed7a` | Implementación de la pantalla de inicio de sesión, UI State en Compose y lógica para validación de credenciales de usuario. |
| **Gestión de Solicitudes Pendientes** | `1022fbc` (PR #17) | Integración de accesos directos y botones de navegación en el panel lateral (*Navigation Drawer*) para gestionar solicitudes pendientes. |

### 🛠️ Tareas Técnicas y Mantenimiento
* **Integración de Ramas & Control de Versiones (`8ba6278`, `36cf28c`):** Sincronización de cambios remotos, resolución de conflictos de *merge* e integración del Sprint 4 (módulo de gestión de inventario: HU10, HU11, HU12).