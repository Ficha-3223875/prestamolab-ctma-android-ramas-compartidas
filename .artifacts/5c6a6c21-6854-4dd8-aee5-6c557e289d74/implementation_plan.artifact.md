# Corrección de Problemas de Entrada de Texto en el Formulario de Solicitud

El usuario reporta que no puede introducir texto en el formulario de solicitud de equipo usando el emulador. Tras analizar el código, se han identificado causas potenciales tanto a nivel de implementación de Compose como de configuración del entorno.

## Causa Probable del Error

1.  **Efectos Secundarios en la Composición:** En `PrestamoNavHost.kt`, la función `viewModel.cargarEquipo(equipoId)` se llama directamente dentro del bloque `composable`. Esto provoca que el equipo se cargue en cada recomposición del NavHost, lo cual puede interrumpir el foco de los campos de texto si ocurre un ciclo de actualización de estado.
2.  **Falta de Desplazamiento (Scrolling):** El formulario en `SolicitudFormScreen.kt` usa `imePadding()` pero no está dentro de un contenedor con scroll. Si el teclado cubre los campos de texto y el usuario no puede verlos, puede parecer que no está escribiendo.
3.  **Configuración del Emulador:** Si el teclado físico del ordenador no funciona, suele deberse a que la opción "Hardware keyboard" está desactivada en la configuración del AVD (Android Virtual Device).

## Cambios Propuestos

### Componente de Navegación

#### [MODIFY] [PrestamoNavHost.kt](file:///C:/Users/MiguelFormacion.LenovoLOQ_MIGAN/AndroidStudioProjects/MiPrestamosLab/app/src/main/java/com/example/miprestamoslab/ui/navigation/PrestamoNavHost.kt)
- Envolver la llamada a `viewModel.cargarEquipo(equipoId)` en un `LaunchedEffect` para asegurar que solo se ejecute cuando el ID del equipo cambie o al entrar en la pantalla, evitando efectos secundarios durante la recomposición.

### Componente de UI

#### [MODIFY] [SolicitudFormScreen.kt](file:///C:/Users/MiguelFormacion.LenovoLOQ_MIGAN/AndroidStudioProjects/MiPrestamosLab/app/src/main/java/com/example/miprestamoslab/ui/screens/SolicitudFormScreen.kt)
- Añadir `Modifier.verticalScroll(rememberScrollState())` a la `Column` del formulario para permitir el desplazamiento cuando el teclado esté visible.
- Asegurar que el `imePadding()` funcione correctamente con el scroll.

## Verificación Plan

### Manual Verification
- Abrir el emulador y navegar a la pantalla de solicitud de un equipo.
- Verificar que al hacer clic en un campo de texto, el teclado aparece y permite la entrada.
- Verificar que el formulario permite hacer scroll cuando el teclado está desplegado.
- **Recomendación al usuario:** Si el teclado físico sigue sin funcionar, ir a `Extended Controls` (...) -> `Settings` -> `General` -> `Send keyboard shortcuts to...` y asegurarse de que la opción `Hardware keyboard` esté habilitada en la configuración del AVD dentro del AVD Manager.

### Automated Tests
- Ejecutar la aplicación y comprobar que no hay bucles de recomposición en los logs al abrir el formulario.
