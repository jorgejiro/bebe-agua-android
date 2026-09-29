# 006 — Resumen de fin de día: alarma, receiver y canal propios

- **Fecha**: 2026-09-29
- **Estado**: aceptada

## Contexto

Los recordatorios dejan de enviarse al alcanzar el objetivo, así que quien lo cumple no recibe
ningún cierre del día. Se quiere una notificación a última hora (23:00 por defecto, configurable y
desactivable) con lo bebido frente al objetivo, que al pulsarla abra el detalle de ese día.

## Decisión

- **Alarma, receiver y canal separados** de los recordatorios. La alarma de recordatorios es una
  sola ranura (`ReminderScheduler`, request code 0) que se recalcula constantemente; meter ahí el
  resumen la haría competir con el siguiente recordatorio. Con ranura propia (`DailySummaryScheduler`,
  request code 3, acción `ACTION_DAILY_SUMMARY_ALARM`) cada una se reprograma sin pisar a la otra.
  El canal `daily_summary` (vibra y no suena, como los recordatorios) permite al usuario silenciar o
  cambiar solo el resumen desde los ajustes del sistema. Su comportamiento se congela al crearlo, por
  lo que se ha elegido ya bien a la primera.
- **Se envía aunque se haya alcanzado el objetivo.** Es un resumen, no un recordatorio: el caso en
  que más sentido tiene es justo el de haberlo cumplido. El texto cambia («Objetivo cumplido: …»).
- **La fecha viaja en el intent** (`EXTRA_SUMMARY_DATE`, ISO) y se calcula al disparar la alarma. Si
  el usuario pulsa la notificación pasada la medianoche, sigue viendo el día resumido y no «hoy».
- **Pantalla de detalle del día** (`day/{date}`), reutilizada desde las filas de Historial, en lugar
  de mandar al usuario a Casa o al Historial: el resumen enlaza con los datos exactos que resume.
  `MainActivity` solo reenvía el extra a `MainViewModel`, que lo expone una vez y se consume tras
  navegar para que girar el móvil no repita la navegación.
- **Programación idempotente**: `ScheduleDailySummaryUseCase` solo depende de los ajustes y del reloj
  (siguiente ocurrencia estrictamente posterior a ahora) y se llama desde arranque de la app, boot,
  onboarding, cambios de ajustes y el propio receiver. Sin permiso de alarmas exactas se omite en
  silencio, igual que los recordatorios.

## Consecuencias

- Un canal y una ranura de alarma más que mantener.
- Los usuarios que actualizan reciben el resumen sin tocar nada (se programa al abrir la app), pero
  antes de la primera apertura tras actualizar no hay alarma.
