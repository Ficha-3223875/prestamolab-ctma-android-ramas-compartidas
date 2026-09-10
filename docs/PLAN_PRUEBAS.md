# Plan de Pruebas - PréstamoLab CTMA

| ID | HU/CA | Técnica | Precondición | Pasos | Esperado |
| --- | --- | --- | --- | --- | --- |
| TC-01 | HU-01 / CA-01.1 | Caso de uso | Usuario en pantalla Login | Ingresar correo y clave válidos, presionar "Iniciar Sesión" | Inicio exitoso y redirección al Catálogo |
| TC-02 | HU-01 / CA-01.2 | Negativa | Usuario en pantalla Login | Dejar campos vacíos o clave errónea, presionar "Iniciar Sesión" | Mensaje de error y permanencia en la pantalla |
| TC-03 | HU-02 / CA-02.1 | Caso de uso | Existen equipos cargados | Ingresar a la pantalla de Catálogo | Lista desplegada con imagen, nombre, categoría y estado |
| TC-04 | HU-02 / CA-02.2 | Límite | Múltiples categorías cargadas | Seleccionar una categoría en el filtro | Solo se muestran los equipos pertenecientes a la categoría |
| TC-05 | HU-03 / CA-03.1 | Caso de uso | Catálogo visible | Seleccionar una tarjeta de equipo | Apertura del detalle con especificaciones completas y estado |
| TC-06 | HU-04 / CA-04.1 | Equivalencia | Equipo "Disponible" seleccionado | Presionar el botón "Solicitar Préstamo" | Redirección al formulario de solicitud prellenado |
| TC-07 | HU-04 / CA-04.2 | Negativa | Equipo "En préstamo" seleccionado | Visualizar el detalle del equipo | Botón "Solicitar Préstamo" deshabilitado |
| TC-08 | HU-05 / CA-05.1 | Caso de uso | Formulario abierto | Diligenciar motivo y fecha de devolución válida, presionar "Confirmar" | Solicitud creada en estado "Pendiente" y confirmación |
| TC-09 | HU-05 / CA-05.2 | Error guessing | Formulario abierto | Presionar rápidamente dos veces el botón de confirmación | Se genera únicamente una (1) solicitud en el sistema |
| TC-10 | HU-06 / CA-06.1 | Caso de uso | Usuario autenticado | Navegar a la sección "Mis Solicitudes" | Muestra listado histórico con estado actual de cada solicitud |
| TC-11 | HU-07 / CA-07.1 | Caso de uso | Sesión iniciada como Admin | Navegar a "Solicitudes Pendientes" | Muestra las solicitudes recibidas de todos los usuarios |
| TC-12 | HU-08 / CA-08.1 | Caso de uso | Admin en solicitud pendiente | Presionar el botón "Aprobar" | Estado cambia a "Aprobada" y equipo a "En préstamo" |
| TC-13 | HU-08 / CA-08.2 | Caso de uso | Admin en solicitud pendiente | Presionar el botón "Rechazar" | Estado cambia a "Rechazada" y equipo se mantiene disponible |
| TC-14 | HU-09 / CA-09.1 | Caso de uso | Sesión iniciada como Admin | Navegar a "Gestión de Inventario" | Muestra la lista completa de equipos con opción de registro/edición |
| TC-15 | HU-10 / CA-10.1 | Caso de uso | Usuario en cualquier pantalla | Presionar el botón de cerrar sesión en la barra superior | Finaliza la sesión y redirige al Login |