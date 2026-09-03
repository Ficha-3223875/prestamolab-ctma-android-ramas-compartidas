# PréstamoLab CTMA

## 1. Descubrimiento y Product Goal
* **Problema:** Falta de visibilidad y control sobre la disponibilidad, préstamos activos y devoluciones de equipos y herramientas compartidas en los laboratorios del CTMA.
* **Usuarios:** Solicitante demo (aprendices e instructores) y Gestor simulado.
* **Necesidades:** Consultar catálogo en tiempo real, solicitar préstamos de forma rápida, validar reglas de uso y rastrear el estado de las solicitudes.
* **Restricciones:** Entorno de ejecución local/emulado, datos puramente sintéticos, prototipo sin persistencia remota en el MVP (InMemoryRepository).
* **Valor Esperado:** Optimizar la trazabilidad y reducir el tiempo de gestión de préstamos en el centro de formación.
* **Product Goal:** *Mejorar la trazabilidad y la consulta de préstamos de recursos de formación mediante una experiencia móvil intuitiva y confiable.*

---

### Historias de Usuario (Sprint 4 - Gestión de Inventario)

| ID | Historia de Usuario | Descripción | Criterios de Aceptación |
| :--- | :--- | :--- | :--- |
| **HU 10** | Registrar Nuevo Equipo | Permitir al encargado agregar nuevos equipos al catálogo. | • Formulario exclusivo para el Encargado.<br>• Validar campos obligatorios (Nombre, Categoría, Descripción).<br>• Insertar el nuevo equipo con estado "Disponible". |
| **HU 11** | Editar Información de Equipo | Permitir al encargado actualizar datos de equipos existentes. | • Modificar nombre, categoría y descripción.<br>• Reflejar los cambios inmediatamente en el estado del repositorio.<br>• Mantener la integridad de los IDs. |
| **HU 12** | Gestión de Estado e Inhabilitación | Cambiar el estado administrativo de los equipos del inventario. | • Permitir cambiar a "En Mantenimiento" o "Dado de Baja".<br>• Validar que el equipo NO esté prestado actualmente al inhabilitarlo.<br>• Permitir reactivar equipos a estado "Disponible". |

### Riesgos Asociados al Sprint 4

| ID Riesgo | Riesgo | HU Asociada |
| :--- | :--- | :--- |
| **R-08** | Inconsistencia en stock por no liberar equipo devuelto o inhabilitado | HU 12 (Gestión de Estado e Inhabilitación) |
| **R-09** | Acceso no autorizado a vistas administrativas de inventario | HU 10 (Registrar Nuevo Equipo) |
| **R-15** | Desincronización de datos de inventario al trabajar en memoria sin API | HU 12 (Gestión de Estado e Inhabilitación) |

### 4. Sprint Planning y DoD

* **Sprint Goal:** Permita consultar un equipo disponible y registrar una solicitud de préstamo válida, manteniendo la disponibilidad coherente y demostrando su calidad mediante pruebas reproducibles.