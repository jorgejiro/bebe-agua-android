# 005 — Recordatorios que vibran sin sonar (y por qué eso obliga a estrenar canal)

- **Fecha**: 2026-08-21
- **Estado**: aceptada

## Contexto

El sonido del recordatorio molesta lo suficiente como para que la respuesta habitual sea poner el
móvil en modo vibración, y entonces se escapan llamadas y avisos de otras apps. Lo que se quiere es
lo contrario: móvil siempre en modo sonido y este recordatorio en concreto silencioso pero
perceptible, incluido el reloj emparejado.

Hasta la 1.3.0 el canal `reminders` se creaba así:

```kotlin
NotificationChannel("reminders", name, IMPORTANCE_DEFAULT).apply {
    enableVibration(false)   // sonido por defecto del sistema, sin vibración
}
```

## Decisión

El canal pasa a `setSound(null, null)` + `enableVibration(true)`, con la misma
`IMPORTANCE_DEFAULT` (no queremos aviso emergente, solo que se note).

Lo que no es evidente: **cambiar esos valores en el sitio donde ya estaban no sirve de nada**. El
sonido, la vibración y la importancia de un canal se congelan en su primera creación; a partir de
ahí pertenecen al usuario, y volver a llamar a `createNotificationChannel` con otros valores es un
no-op en todos los móviles que ya tienen el canal. Solo cambiaría el nombre y la descripción. Es
decir: el arreglo funcionaría en instalaciones nuevas y no haría absolutamente nada en las que ya
existen, que son justo las que tienen el problema.

Así que el cambio de comportamiento se hace **estrenando id de canal** y borrando el viejo:

```kotlin
manager.deleteNotificationChannel("reminders")   // el que sonaba
manager.createNotificationChannel(/* reminders_vibrate */)
```

El borrado no es cosmético: sin él, el usuario vería dos filas de «Recordatorios de agua» en los
ajustes de notificación del sistema, una de ellas muerta.

Historial de ids, que hay que mantener aquí para que el siguiente cambio de este tipo sepa qué
borrar:

| id | Versiones | Comportamiento |
|---|---|---|
| `reminders` | ≤ 1.3.0 | Sonido por defecto, sin vibración |
| `reminders_vibrate` | 1.3.1+ | Sin sonido, con vibración |

Como contrapartida se añade en Ajustes → Permisos la fila **Ajustes de notificación**, que abre el
canal con `ACTION_CHANNEL_NOTIFICATION_SETTINGS`. Es el único sitio donde Android permite devolver
el sonido o cambiar el patrón de vibración, precisamente porque esos ajustes son del usuario y no de
la app: ofrecer un interruptor propio dentro de la app sería mentir, porque no podría aplicarlo.

## Consecuencias

- El precio de estrenar id es real: **se pierden los ajustes manuales** que el usuario hubiera hecho
  sobre el canal antiguo (volumen, patrón, importancia, silenciado). Aquí es aceptable porque el
  ajuste que se pierde es justo el que se quiere cambiar.
- Cambiar otra vez este comportamiento por código costará otro id y otro borrado. No es gratis, así
  que conviene no volver a tocarlo sin motivo.
- El reenvío al reloj no cambia: lo hace el sistema (no marcamos `setLocalOnly`) y la vibración de la
  muñeca la decide el propio reloj.
- `ReminderChannelTest` (instrumentado) fija las dos propiedades y comprueba que el canal antiguo ya
  no está; una regresión aquí no se podría arreglar con una actualización, necesitaría otro id.

## Verificación

En emulador API 36, la ruta que importa es la de **actualización**, no la de instalación limpia:

1. Instalada la 1.3.0 → `mId='reminders'`, `mSound=content://settings/system/notification_sound`,
   `mVibrationEnabled=false`.
2. Actualizada encima a la 1.3.1 → `mId='reminders_vibrate'`, `mSound=null`,
   `mVibrationEnabled=true`, y el antiguo `reminders` con `mDeleted=true`.

Comprobado con `adb shell dumpsys notification --noredact`.
