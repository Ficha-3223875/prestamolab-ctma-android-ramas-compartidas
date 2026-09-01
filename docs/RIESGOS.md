| ID | Riesgo | Probabilidad | Impacto | Nivel | Tratamiento |
| :--- | :--- | ---: | ---: | :--- | :--- |
| **R-01** | Intentar solicitar un equipo que no está disponible o ya fue prestado | Alta | Alta | Alto | Validación lógica en UI/ViewModel y deshabilitación de botones |
| **R-02** | Creación de solicitudes duplicadas por múltiples clics rápidos | Media | Alta | Alto | Bloqueo de UI (debounce) al presionar "Solicitar" |
| **R-03** | Error de navegación hacia un detalle o préstamo inexistente/nulo | Media | Media | Medio | Manejo de estados recuperables y vistas de fallback/error |
| **R-04** | Intento de login con credenciales inválidas o incompletas | Alta | Alta | Alto | Validación de campos en tiempo real y mensajes claros de error |
| **R-05** | Falla al filtrar por categoría dejando la lista de equipos vacía sin retroalimentación | Media | Baja | Bajo | Mostrar mensaje explícito de "No se encontraron equipos" |
| **R-06** | Intento de registrar entrega física de una solicitud que no está APROBADA | Media | Alta | Alto | Control estricto de máquina de estados (`APROBADA` → `ENTREGADA`) |
| **R-07** | Intento de registrar devolución de un equipo no marcado como ENTREGADO | Media | Alta | Alto | Restricción de botones según el estado actual en el flujo |
| **R-08** | Inconsistencia en el stock total al no liberar el equipo devuelto | Alta | Alta | Alto | Actualización automática de `EstadoEquipo.DISPONIBLE` en el repositorio |
| **R-09** | Acceso no autorizado a vistas administrativas por parte de usuarios comunes | Baja | Alta | Alto | Control de roles y permisos en la navegación global |
| **R-10** | Pérdida de estado al rotar la pantalla o cambiar de pestaña en Android | Alta | Media | Medio | Uso de `ViewModel` y `rememberSaveable` en componentes Jetpack Compose |
| **R-11** | Intentar aprobar/rechazar solicitudes ya procesadas previamente | Media | Media | Medio | Deshabilitación de acciones según estado actual de la solicitud |
| **R-12** | Consultar historial de préstamos sin registros disponibles | Baja | Baja | Bajo | Renderizado de estado vacío (*Empty State*) en `LazyColumn` |
| **R-13** | Incompatibilidad de paquetes entre ramas del equipo (`ui` vs `ui/theme`) | Alta | Media | Medio | Estandarización del árbol de paquetes y resolución en Pull Requests |
| **R-14** | Intento de devolución parcial sin registrar observaciones del estado del equipo | Media | Media | Medio | Campos obligatorios para observaciones al momento de la devolución |
| **R-15** | Desincronización de datos al trabajar sin persistencia real (en memoria) | Alta | Media | Medio | Centralización del estado mediante repositorio único en memoria |