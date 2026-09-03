# Matriz de Riesgos - PréstamoLab CTMA

| ID | Riesgo | Probabilidad | Impacto | Nivel | Tratamiento |
| --- | --- | ---: | ---: | --- | --- |
| R-01 | Intentar iniciar sesión con credenciales vacías o inválidas | Alta | Alta | Alto | Validación de campos y mensaje de error descriptivo |
| R-02 | Visualizar un catálogo vacío sin feedback al usuario | Media | Media | Medio | Estado de UI vacío (Empty state) explícito |
| R-03 | Inconsistencia en el filtrado por categoría o búsqueda por texto | Media | Media | Medio | Reactividad en el estado UI con StateFlow |
| R-04 | Navegación a un detalle de equipo inexistente o con ID inválido | Baja | Alta | Medio | Manejo de excepciones y redirección a catálogo |
| R-05 | Intentar solicitar un equipo cuya disponibilidad ha cambiado a "En préstamo" o "En mantenimiento" | Alta | Alta | Alto | Validación antes del envío y bloqueo del botón |
| R-06 | Envío de formulario de préstamo con campos requeridos incompletos | Alta | Media | Alto | Control de estado del botón de envío según validez |
| R-07 | Duplicidad de solicitudes enviadas por doble toque | Media | Alta | Alto | Deshabilitación del botón al presionar e inmutabilidad |
| R-08 | Visualización desactualizada del estado de las solicitudes del usuario | Media | Media | Medio | Actualización automática del UiState tras cambios |
| R-09 | Aprobación o rechazo no autorizado por usuarios sin rol Administrador | Baja | Alta | Alto | Restricción de vista y acciones según el rol del usuario |
| R-10 | Modificación o eliminación inconsistente de un equipo en el inventario | Media | Alta | Alto | Sincronización en el repositorio central del sistema |