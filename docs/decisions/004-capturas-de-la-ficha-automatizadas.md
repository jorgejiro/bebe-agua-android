# 004 — Las capturas de la ficha se generan con un script, no a mano

- **Fecha**: 2026-08-11
- **Estado**: aceptada

## Contexto

Play pide capturas en tres formatos —teléfono, tablet de 7" y tablet de 10"— y la ficha está en dos
idiomas. Con siete escenas por idioma eso son **42 imágenes**, y hay que rehacerlas cada vez que cambie
una pantalla. Hechas a mano son 42 secuencias de navegación, con dos problemas que no avisan:

- **Una captura mala no da error.** Sale en negro o a medio pintar, se guarda y se sube.
- **Un idioma se cuela.** Es exactamente lo que pasó con el primer juego de capturas de
  «¡Aquí hay tomate!», que es de donde viene este pipeline.

Además, con una instalación nueva no hay nada que enseñar: el Historial está vacío y la pantalla
principal marca 0 ml.

## Decisión

Portar el pipeline de «¡Aquí hay tomate!» a este repo, en `docs/store-assets/generar-capturas/`, con las
escenas y la siembra de datos de esta app. Cuatro piezas: `sembrar_historial.py` construye la base de
datos, `ui.py` navega buscando texto con `uiautomator`, `tanda.py` hace las seis escenas de la app en un
idioma, `capturar.py` orquesta las dos tandas y coloca el widget, y `revisar.py` controla el juego
completo antes de subirlo. `todos.sh` hace los tres formatos de un tirón.

Las capturas van versionadas en `docs/store-assets/capturas/<idioma>/<formato>/`. El idioma va primero
porque la ficha de Play se sube idioma a idioma: así los tres formatos de un idioma se actualizan
juntos y sin tocar el otro.

## Consecuencias

- Rehacer las 42 capturas es un comando y unos minutos, así que dejar la ficha desactualizada ya no
  tiene excusa.
- **Las capturas se toman con el APK debug**, no con el de release: la siembra se copia con `run-as`, que
  solo funciona con una app debuggable. Con el APK de release el pase entero se completa sin un solo
  error y las catorce capturas salen con la app a 0 ml — el fallo silencioso que motiva `revisar.py`.
- **El recordatorio de la notificación se dispara moviendo el reloj** hasta la alarma pendiente, porque
  `ReminderReceiver` no está exportado y un `am broadcast` desde el shell se encola sin entregarse. Si
  algún día se cambia la forma de programar los recordatorios, esta escena es la primera que se rompe.
- El sistema del emulador se queda en inglés (no se puede cambiar sin root), así que en las capturas
  españolas la barra de estado y el nombre de la app dentro de la notificación salen en inglés. Es una
  limitación aceptada, anotada en el `README.md` del generador junto con la vía para resolverla si algún
  día molesta.
