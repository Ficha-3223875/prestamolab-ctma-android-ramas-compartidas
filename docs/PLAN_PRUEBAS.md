# Plan de Pruebas - Sprint 4

| ID | HU/CA | Técnica | Precondición | Pasos | Esperado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-10** | HU-10 / CA-10.1 | Caso de uso | Usuario autenticado como Encargado | Llenar formulario de nuevo equipo y guardar | Equipo guardado con estado "Disponible" |
| **TC-11** | HU-10 / CA-10.2 | Negativa | Formulario de registro abierto | Dejar campos obligatorios vacíos y guardar | Mensaje de error, no permite registrar |
| **TC-12** | HU-11 / CA-11.1 | Caso de uso | Existe un equipo en inventario | Editar datos del equipo y guardar | Cambios reflejados en el catálogo |
| **TC-13** | HU-12 / CA-12.1 | Caso de uso | Equipo en estado "Disponible" | Cambiar estado a "En Mantenimiento" | Equipo inhabilitado para préstamos |
| **TC-14** | HU-12 / CA-12.2 | Negativa | Equipo en estado "Prestado" | Intentar cambiar estado a "Mantenimiento" | Bloqueo con mensaje explicativo |