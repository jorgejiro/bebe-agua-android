# Changelog

Historial de versiones de Bebe Agua. Este archivo es la fuente de verdad del changelog del
repositorio; el que se muestra dentro de la app vive en los `string-array` `changelog_*` de
`app/src/main/res/values/strings.xml` y `values-es/strings.xml`, indexados desde
`ui/changelog/ChangelogCatalog.kt`.

Al publicar una versión nueva hay que tocar los tres sitios: este archivo, los dos
`string-array` (EN y ES) y el catálogo.

El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y las versiones
[Semantic Versioning](https://semver.org/lang/es/).

## [1.1.0] — 2026-07-24 (versionCode 6)

### Añadido
- Opción «Saltar recordatorio tras beber»: un recordatorio que caiga dentro de la ventana de
  cortesía posterior a una ingesta no se envía y se pasa al siguiente horario de la agenda.
  Ventana configurable (5–120 min, 15 por defecto), desactivada por defecto.
  Ver `docs/decisions/001-ventana-de-cortesia-tras-ingesta.md`.
- Pantalla «Novedades» accesible desde Ajustes → Acerca de, con el changelog de cada versión.

### Corregido
- El indicador «Próximo recordatorio» de la pantalla principal y la alarma realmente programada
  comparten el mismo cálculo (`ReminderWindow.kt`), así que ya no pueden divergir.

## [1.0] — 2026-05-26 (versionCode 5)

Primera versión publicada en Google Play.

### Añadido
- Pantalla principal con anillo de progreso, registro de la cantidad por defecto en un toque,
  selector de medida y lista de registros del día con borrado.
- Historial de los últimos 30 días con totales diarios, media, mejor día y racha actual.
- Recordatorios exactos (`setExactAndAllowWhileIdle`) dentro de la franja horaria configurada,
  que dejan de enviarse al alcanzar el objetivo diario y se reprograman al registrar una ingesta.
- Acciones rápidas en la notificación: registrar la medida por defecto o posponer 15 minutos.
- Reprogramación de recordatorios tras reiniciar el dispositivo.
- Ajustes: objetivo diario, franja horaria, recordatorios al día, tamaños de ingesta e idioma,
  con estado de los permisos de notificaciones y alarmas exactas.
- Onboarding en el primer arranque.
- Traducción a español e inglés. Todos los datos se guardan en el dispositivo (Room + DataStore),
  sin cuentas, sin nube, sin publicidad y sin tracking.
