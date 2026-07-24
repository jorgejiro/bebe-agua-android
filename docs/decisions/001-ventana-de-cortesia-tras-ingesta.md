# 001 — Ventana de cortesía tras registrar una ingesta

Fecha: 2026-07-24
Estado: aceptada

## Contexto

Si el usuario registra una ingesta justo antes de que toque un recordatorio, la notificación
llega igualmente y resulta redundante. Se pide una opción, **desactivada por defecto**, que
salte el siguiente recordatorio cuando cae dentro de los N minutos posteriores a la ingesta
(N = 15 por defecto, configurable).

## Decisión

1. **Se salta el recordatorio, no se pospone.** El siguiente recordatorio programado es el
   primer horario de la agenda posterior a `últimaIngesta + ventana`. No se desplaza toda la
   agenda: los horarios calculados siguen siendo los mismos.

2. **La lógica es idempotente, derivada del estado, no de un flag de llamada.**
   `ScheduleRemindersUseCase` no recibe un parámetro tipo `afterIntake`; consulta la última
   ingesta del día y aplica el corte. Así, cualquier invocación (Home, acción de notificación,
   `ReminderReceiver`, `BootReceiver`, cambios en ajustes) converge en el mismo horario y la
   alarma programada nunca diverge de lo que muestra la pantalla principal.

3. **Red de seguridad en `ReminderReceiver`.** Una alarma ya en vuelo cuando el usuario bebe
   puede dispararse antes de que la reprogramación surta efecto; el receiver comprueba la
   ventana de cortesía y, si aplica, no notifica (pero sí reprograma).

4. **`Clock` inyectado por Hilt** (`di/ClockModule.kt`) en `ScheduleRemindersUseCase`, para
   poder testear el cálculo con un reloj fijo en vez de depender de `LocalTime.now()`.

5. **Cruce de medianoche.** `reminderCutoff` devuelve `null` cuando el desplazamiento pasa de
   las 00:00, y el caso se resuelve programando el primer recordatorio de mañana. Esto también
   corrige un bug latente del snooze de 15 min cerca de medianoche, que antes podía calcular
   una hora anterior a `now` y programar una alarma en el pasado.

## Alternativas descartadas

- **Posponer el recordatorio N minutos** en vez de saltarlo: acumula desplazamientos si el
  usuario bebe varias veces seguidas y desalinea la agenda respecto a la vista previa de
  ajustes.
- **Parámetro `afterIntake` en el use case**: más simple, pero hace que el resultado dependa de
  quién llama; al cambiar los ajustes desde Settings la alarma y el indicador de Home divergían.

## Consecuencias

- `AppSettings` gana `skipImminentReminder` (default `false`) y `skipImminentWindowMinutes`
  (default 15, rango 5–120). Ambos en DataStore, sin migración de Room.
- Si la ventana de cortesía es mayor que el intervalo entre recordatorios, se salta más de uno.
  Es el comportamiento esperado de la opción.
