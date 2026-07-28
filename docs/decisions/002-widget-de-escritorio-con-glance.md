# 002 — Widget de escritorio 1x1 con Glance

Fecha: 2026-07-26
Estado: aceptada

## Contexto

Se pide un widget de escritorio de 1x1 que, al pulsarlo, registre la cantidad por defecto igual
que el botón principal de la pantalla principal. Cada pulsación añade una ingesta. El widget no
muestra datos: solo el icono de la app con un distintivo «+».

Los widgets de Android se dibujan con `RemoteViews`, un mecanismo ajeno a Compose. La regla de
`CLAUDE.md` de «no usar XML para UI» no contempla este caso, así que había que elegir.

## Decisión

1. **Glance (`androidx.glance:glance-appwidget` 1.1.1)** en vez de `AppWidgetProvider` +
   `RemoteViews` con un layout XML. Glance ofrece una API declarativa equivalente a Compose y
   evita introducir layouts XML de UI en el proyecto. Se elige la 1.1.1 por ser la última
   estable (la 1.2.0 solo tiene `rc01` publicada).

2. **El widget no renderiza datos.** Solo el icono con el distintivo `ic_widget_add_badge`.
   Consecuencia importante: no hay nada que invalidar. No hace falta llamar a `updateAll()` tras
   registrar una ingesta ni `updatePeriodMillis`, y el widget no puede quedar desincronizado con la
   base de datos.

2.b **Todo se dimensiona en proporción a la celda, no en dp fijos** (añadido 2026-07-28). El
   tamaño de una celda 1x1 depende de la rejilla del launcher: en la rejilla 8x6 de Nova Launcher
   sobre un Galaxy S25 ronda los 45 dp, la mitad que en la rejilla por defecto. Con un distintivo
   de 22 dp fijos el «+» dominaba la celda. Por eso:
   - `SizeMode.Exact` + `LocalSize.current` para conocer el tamaño real de la celda.
   - Distintivo = 36 % del lado menor (acotado a 15–28 dp), radio de esquina = 22 %.
   - El arte se dibuja desde `drawable-nodpi/ic_widget_icon.png`, un recorte centrado de
     `ic_launcher_fg.png` (304 de 432 px, sin reescalado) que **elimina la zona de seguridad del
     icono adaptativo**. `ic_launcher_fg.png` mide 432 px pero su dibujo solo ocupa los 293 px
     centrales, porque el launcher recorta el 66 % central de la capa de primer plano. Al pintarlo
     tal cual en el widget, la gota quedaba a ~68 % del lado, más pequeña que en el icono de la app
     de al lado. Como bonus, el recorte se decodifica a 304×304 en vez de escalarse a 1296×1296
     (`drawable/` sin cualificador se interpreta como mdpi), unos 6,7 MB menos de bitmap en el
     proceso del launcher.
   - **Un cuadrado del lado menor con `ContentScale.Fit`, no `fillMaxSize` con `Crop`.** Una celda
     1x1 no es cuadrada: en la rejilla 8x6 mide ~45 × 57 dp. Llenando la celda entera, `Crop`
     escalaba el arte hasta cubrir el alto y recortaba la gota por los lados. El cuadrado centrado
     de lado `min(ancho, alto)` no puede recortar nada sea cual sea la proporción de la celda, y es
     también el rectángulo al que el redondeo de esquinas tiene sentido aplicarse.

3. **La lógica vive en el dominio, no en el widget.** `RecordDefaultIntakeUseCase` encapsula
   «lee la cantidad por defecto → registra → reprograma el recordatorio → devuelve los totales
   del día». `AddDefaultIntakeAction` es un cascarón sin lógica, de modo que el comportamiento del
   widget se puede testear con JUnit sin instrumentación.

4. **La precedencia de la cantidad por defecto se extrae a una función pura**,
   `resolveDefaultIntakeSize` (`usecase/IntakeDefaults.kt`), siguiendo el patrón de
   `ReminderWindow.kt`. La usan `HomeViewModel` (sobre flows) y `GetDefaultIntakeSizeUseCase`
   (lectura puntual para el widget), así que el botón de Home y el widget no pueden divergir.

5. **Hilt vía `@EntryPoint`.** Glance instancia los `ActionCallback` por reflexión, así que no hay
   inyección por constructor posible; se resuelve la dependencia con `EntryPointAccessors`
   sobre `SingletonComponent`. Es la única excepción al «constructor injection siempre», y está
   acotada a esa clase.

6. **Confirmación con `Toast`** (`+250 ml · 1250/2400 ml`). Sin ningún feedback, una pulsación no
   da señal de haber hecho nada e invita a pulsar dos veces. Los toasts de texto siguen
   permitidos desde background (la restricción de Android 11 afecta a los toasts personalizados).

## Alternativas descartadas

- **`RemoteViews` + layout XML**: cero dependencias nuevas, pero mete un layout XML de UI en un
  proyecto que es 100 % Compose, justo lo que `CLAUDE.md` marca en rojo.
- **Reutilizar `NotificationActionReceiver` con `ACTION_DRINK`**: la cantidad tendría que fijarse
  al construir el `PendingIntent`, no al pulsar, así que el widget registraría una cantidad
  obsoleta si el usuario cambia la medida por defecto. El `ActionCallback` lee la cantidad en el
  momento de la pulsación.
- **Mostrar el progreso o la cantidad en el widget**: descartado por decisión de producto (el
  widget es un botón, no un panel) y además obligaría a invalidarlo desde la app, la notificación
  y el propio widget.
- **Widget redimensionable**: `resizeMode="none"` y `targetCellWidth/Height=1`. Es un botón; a
  mayor tamaño no aporta nada.

## Consecuencias

- Dependencia nueva: `androidx.glance:glance-appwidget`. Arrastra `glance`,
  `glance-appwidget-proto`, `glance-appwidget-external-protobuf` y **`androidx.work:work-runtime`
  2.7.1**. Glance usa WorkManager internamente para sus actualizaciones; la regla de este repo de
  «no usar WorkManager» sigue en pie para **nuestro** código: los recordatorios continúan con
  `AlarmManager`. Si en algún momento el tamaño del APK importa, esto es lo primero que hay que
  medir al reconsiderar `RemoteViews`.
- Verificado en emulador (Android 17, API 37): el provider se registra como 1x1, el widget se
  dibuja en el escritorio y tres pulsaciones consecutivas registran 200 + 200 + 200 ml. La
  reprogramación de la alarma solo llega a `AlarmManager` si `SCHEDULE_EXACT_ALARM` está
  concedido, igual que en el resto de la app (`AlarmManagerReminderScheduler` sale con un warning
  si no lo está).
- Pulsar dos veces registra dos ingestas. No se añade antirrebote: es coherente con el botón de
  Home, y los registros se pueden borrar desde la lista de «Registros de hoy».
- `previewImage` en vez de `previewLayout` para la vista del selector de widgets, para no crear un
  layout XML solo para el preview. El `layer-list` del preview fija el tamaño de sus dos capas
  (96 dp y 35 dp) para mantener la misma proporción que el widget en cualquier densidad.
- `SizeMode.Exact` implica que el launcher puede pedir una recomposición al cambiar el tamaño de la
  celda. Es barato (no hay E/S en `Content`) y no rompe el «nada que invalidar»: sigue sin haber
  datos que refrescar.
- El hueco entre el borde del widget y el borde de la celda lo pone el launcher (en Nova, la opción
  de margen/padding de widgets). No se puede eliminar desde la app.
