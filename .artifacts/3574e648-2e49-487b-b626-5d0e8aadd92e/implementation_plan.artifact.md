# Integración de Funcionalidades HU_05, HU_07 y HU_15 en MiPrestamosLab

Este plan detalla la integración de las historias de usuario HU_05 (Búsqueda y Filtrado), HU_07 (Aprobar/Rechazar solicitudes) y HU_15 (Login) en el módulo `miprestamoslab` para consolidarlo como la base principal del proyecto.

## User Review Required

> [!IMPORTANT]
> Se consolidará todo el flujo bajo el paquete `com.example.miprestamoslab`. El `MainActivity` principal se actualizará para iniciar desde la pantalla de Login de `miprestamoslab`.

## Proposed Changes

### [Modelos]

#### [NEW] [Usuario.kt](file:///C:/Users/juang/AndroidStudioProjects/prestamolab-nuevo/app/src/main/java/com/example/miprestamoslab/model/Usuario.kt)
Creación del modelo de usuario y sus roles (Aprendiz, Encargado).

---

### [UI y Lógica de Negocio]

#### [MODIFY] [PrestamoUiState.kt](file:///C:/Users/juang/AndroidStudioProjects/prestamolab-nuevo/app/src/main/java/com/example/miprestamoslab/ui/PrestamoUiState.kt)
Agregar el estado del usuario autenticado.

#### [MODIFY] [PrestamoViewModel.kt](file:///C:/Users/juang/AndroidStudioProjects/prestamolab-nuevo/app/src/main/java/com/example/miprestamoslab/ui/PrestamoViewModel.kt)
Implementar la lógica de autenticación (HU_15) simulada con credenciales del SENA.

#### [NEW] [LoginScreen.kt](file:///C:/Users/juang/AndroidStudioProjects/prestamolab-nuevo/app/src/main/java/com/example/miprestamoslab/ui/screens/LoginScreen.kt)
Implementar el formulario de inicio de sesión con validaciones según HU_15.

#### [MODIFY] [CatalogoScreen.kt](file:///C:/Users/juang/AndroidStudioProjects/prestamolab-nuevo/app/src/main/java/com/example/miprestamoslab/ui/screens/CatalogoScreen.kt)
Implementar la barra de búsqueda y el selector de categorías (HU_05).

#### [MODIFY] [PrestamoNavHost.kt](file:///C:/Users/juang/AndroidStudioProjects/prestamolab-nuevo/app/src/main/java/com/example/miprestamoslab/ui/navigation/PrestamoNavHost.kt)
Integrar la pantalla de Login como destino inicial y manejar la navegación basada en roles.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/juang/AndroidStudioProjects/prestamolab-nuevo/app/src/main/java/com/example/prestamolab/MainActivity.kt)
Actualizar el punto de entrada principal para que utilice el NavHost de `miprestamoslab`.

## Verification Plan

### Automated Tests
- No se requieren pruebas automatizadas nuevas en este momento, pero se verificará la compilación.

### Manual Verification
1.  **Login (HU_15)**: Ingresar con un correo institucional y contraseña "123456". Verificar que redirige al catálogo.
2.  **Filtrado (HU_05)**: Buscar un equipo por nombre en el catálogo y filtrar por categoría.
3.  **Gestión (HU_07)**: Como encargado, aprobar una solicitud y rechazar otra pidiendo la razón.
