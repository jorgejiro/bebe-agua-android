# 007 — Widget 2x1 con el progreso de hoy

- **Fecha**: 2026-09-29
- **Estado**: aceptada

## Contexto

El widget 1x1 (ADR 002) es solo un botón y descarta mostrar datos porque obligaría a invalidarlo.
Se pide ahora un widget de 2x1 que, además del botón, muestre el progreso de hoy
(`1250 / 2400 ml`). Es el primer widget que renderiza datos, así que la premisa de «nada que
invalidar» del ADR 002 deja de valer para él.

## Decisión

1. **Widget separado, no un 1x1 redimensionable.** `DrinkWideWidget` + `DrinkWideWidgetReceiver` +
   su propio `appwidget-provider`. El 1x1 sigue sin datos y sin invalidarse jamás; quien quiere solo
   el botón no paga refrescos. El icono con distintivo se extrae a `DrinkIconWithBadge` y se comparte;
   la acción de toque (`AddDefaultIntakeAction`) también, y la regla R8 del ADR 003 ya la cubre.
   `resizeMode="horizontal"`: más ancho cabe, más alto no aporta nada.
2. **Invalidación desde los casos de uso.** `WidgetUpdater.refresh()` (interfaz de dominio,
   implementación Glance) se llama al final de `AddIntakeUseCase`, `DeleteIntakeUseCase` y
   `UpdateDailyGoalUseCase`. Son los puntos únicos por los que pasan Home, la acción de la
   notificación, el toque del widget (`RecordDefaultIntakeUseCase`) y el borrado desde el detalle del
   día, de modo que ningún ViewModel tiene que acordarse de refrescar. `GlanceWidgetUpdater` solo
   refresca el 2x1 y captura cualquier fallo (con Timber): el widget nunca puede romper un registro.
3. **Alarma inexacta a medianoche.** El texto debe reiniciarse al cambiar de día aunque nadie
   registre nada. `AlarmManager.setAndAllowWhileIdle(RTC, medianoche)` no necesita el permiso de
   alarmas exactas y unos minutos de retraso son irrelevantes. Request code 5 (0-4 ya en uso). La
   próxima medianoche se calcula con aritmética de calendario (`plusDays(1).atStartOfDay(zone)`), no
   sumando 24 h, para acertar en los días de cambio horario (23 h y 25 h); cubierto por tests con
   Europe/Madrid. Se programa al colocar el primer widget y en `BootReceiver`, y se cancela al quitar
   el último. El receptor también atiende `TIME_SET` y `TIMEZONE_CHANGED`, que mueven la medianoche.
4. **Sin `updatePeriodMillis` ni WorkManager propio.** `updatePeriodMillis="0"`: el mínimo del
   sistema es 30 min, es impreciso y despierta el dispositivo sin necesidad. WorkManager sigue
   descartado para nuestro código (solo lo usa Glance por dentro).
5. **Lectura de datos.** `provideGlance` obtiene el `Flow` de `GetTodaySummaryUseCase` vía
   `@EntryPoint` y el contenido lo **observa** con `collectAsState` (valor inicial leído antes, para
   no parpadear). Una lectura puntual con `first()` no sirve: `provideGlance` se ejecuta una vez por
   sesión, y `updateAll()` sobre una sesión viva solo recompone, así que el widget se quedaba en el
   valor viejo (visto en el emulador: la pulsación sumaba y el texto seguía en `0 / 2400 ml`). Si
   la lectura falla se muestra «Abre la app».
6. **Tamaños proporcionales.** Padding, lado del icono y tamaños de texto salen de `wideLayout(ancho,
   alto)` (funciones puras con tope), como `badgeSize` en el 1x1. Fondo con la paleta fija de la app.

## Alternativas descartadas

- **Hacer el 1x1 redimensionable y mostrar datos al ensancharlo**: obligaría a invalidar también el
  botón y mezclaría dos usos.
- **Refrescar desde ViewModels o receptores**: hay varios y se olvidaría alguno.
- **`updatePeriodMillis` o WorkManager periódico**: impreciso y más caro que una alarma por día.
- **Alarma exacta**: exigiría el permiso `SCHEDULE_EXACT_ALARM`, innecesario aquí.

## Consecuencias

- Cada registro, borrado o cambio de objetivo cuesta una actualización de Glance (barata: solo si
  hay un 2x1 colocado).
- Entre medianoche y la alarma inexacta el widget puede mostrar unos minutos el total de ayer.
- `DrinkWideWidgetReceiver` no usa Hilt (es un `GlanceAppWidgetReceiver`); la alarma usa
  `Clock.systemDefaultZone()` directamente.
- En rejillas muy densas (celda 2x1 de ~90 dp) el texto puede quedar apretado: el tamaño baja hasta
  10 sp y el texto es de una línea. Pendiente de comprobar en emulador.
