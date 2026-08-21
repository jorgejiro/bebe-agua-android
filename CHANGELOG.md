# Changelog

Historial de versiones de Bebe Agua. Este archivo es la fuente de verdad del changelog del
repositorio; el que se muestra dentro de la app vive en los `string-array` `changelog_*` de
`app/src/main/res/values/strings.xml` y `values-es/strings.xml`, indexados desde
`ui/changelog/ChangelogCatalog.kt`.

Al publicar una versión nueva hay que tocar los tres sitios: este archivo, los dos
`string-array` (EN y ES) y el catálogo.

El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y las versiones
[Semantic Versioning](https://semver.org/lang/es/).

## [1.3.1] — 2026-08-21 (versionCode 11)

### Cambiado
- Los recordatorios **vibran y no suenan**. Así el móvil puede quedarse en modo sonido —sin perderse
  llamadas ni otros avisos— y el recordatorio de beber sigue notándose, en la muñeca incluida si hay
  un reloj emparejado.
- El sonido, la vibración y el aviso emergente de un canal de notificación pertenecen al usuario
  desde el momento en que el canal existe: volver a crearlo con otros valores no cambia nada en los
  móviles que ya lo tienen. Por eso el canal pasa a ser `reminders_vibrate` y el antiguo
  `reminders` se borra al arrancar, para no dejar dos filas de recordatorios en los ajustes del
  sistema. Efecto secundario: si habías tocado a mano el sonido o la vibración del canal antiguo,
  ese ajuste se pierde. Ver `docs/decisions/005-recordatorios-que-vibran-sin-sonar.md`.

### Añadido
- Fila **Ajustes de notificación** en Ajustes → Permisos: abre directamente el canal en los ajustes
  del sistema, que es el único sitio donde Android permite devolverle el sonido o cambiar el patrón
  de vibración.

## [1.3.0] — 2026-08-11 (versionCode 10)

### Añadido
- **Enviar comentarios** en Ajustes → Acerca de: abre la app de correo del usuario con la dirección
  del autor puesta y, en el asunto, el nombre y la versión instalada. La dirección no se imprime en
  la pantalla, se lee en el campo Para de la app de correo. Mismo planteamiento que en
  «¡Aquí hay tomate!».

### Cambiado
- La app se llama **¡Bebe agua!** (ES) / **Drink Water!** (EN). Cambia el nombre del lanzador, el de
  los ajustes del sistema y el de la ficha de Google Play. El package, el repositorio y los datos
  guardados no cambian.

## [1.2.1] — 2026-08-04 (versionCode 8)

### Corregido
- El widget de escritorio se quedaba indefinidamente en el indicador de carga al colocarlo y el
  toque no registraba nada, solo en la build de release: R8 (full mode) eliminaba los
  constructores que WorkManager y Glance instancian por reflexión. Añadidas las reglas
  `-keepclassmembers` correspondientes en `proguard-rules.pro`.
  Ver `docs/decisions/003-reglas-r8-para-reflexion-de-glance-y-workmanager.md`.

## [1.2.0] — 2026-07-28 (versionCode 7)

### Añadido
- Widget de escritorio de 1x1: un toque registra la cantidad por defecto sin abrir la app, igual
  que el botón principal de la pantalla principal. Implementado con Glance
  (ver `docs/decisions/002-widget-de-escritorio-con-glance.md`). El icono llena la celda y el
  distintivo «+» se escala con ella, para que también se vea bien en rejillas densas (8x6).
- Pantalla «Novedades» accesible desde Ajustes → Acerca de, con el changelog de cada versión y
  distintivo para la versión instalada.
- Sección «Acerca de» en Ajustes con la versión instalada (`versionName (versionCode)`).

### Cambiado
- Valores por defecto para instalaciones nuevas: objetivo diario 2100 → 2400 ml, fin de la
  franja horaria 23:00 → 21:00 y recordatorios al día 10 → 14. No afecta a instalaciones
  existentes, que conservan sus ajustes.
- Los valores por defecto viven ahora en `AppSettings` y los leen tanto `SettingsDataSource`
  como el onboarding, que antes los duplicaba.

## [1.1.0] — 2026-07-24 (versionCode 6)

### Añadido
- Opción «Saltar recordatorio tras beber»: un recordatorio que caiga dentro de la ventana de
  cortesía posterior a una ingesta no se envía y se pasa al siguiente horario de la agenda.
  Ventana configurable (5–120 min, 15 por defecto), desactivada por defecto.
  Ver `docs/decisions/001-ventana-de-cortesia-tras-ingesta.md`.

### Corregido
- El indicador «Próximo recordatorio» de la pantalla principal y la alarma realmente programada
  comparten el mismo cálculo (`ReminderWindow.kt`), así que ya no pueden divergir.

## [1.0] — 2026-05-26 (versionCode 5)

Primera versión publicada en Google Play.

### Corregido
- La última cantidad registrada se mantiene como cantidad por defecto también entre arranques.
- Avisos de lint que bloqueaban la build de release.

## [1.0.3] — 2026-05-09 (versionCode 4)

### Añadido
- Paso de «recordatorios al día» en la página de configuración del onboarding.

### Cambiado
- Objetivo diario por defecto de 1500 ml a 2100 ml (solo afecta a instalaciones nuevas).

### Corregido
- La página de configuración del onboarding es desplazable, así que todos los campos son
  accesibles en pantallas pequeñas.

## [1.0.2] — 2026-05-09 (versionCode 3)

### Cambiado
- El primer recordatorio del día se ancla a la hora de inicio configurada en lugar de repartirse
  desde el primer intervalo.

### Corregido
- Cuando ya no quedan horarios válidos hoy, el siguiente recordatorio se programa para mañana.
- El estado del permiso de alarmas exactas se refresca en el `onResume` de Ajustes.

### Interno
- Suite inicial de tests (unitarios e instrumentados).

## [1.0.1] — 2026-05-06 (versionCode 2)

Primera build instalable con el MVP completo.

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
- Icono adaptativo propio (gota de agua).
- Traducción a español e inglés, aplicada en caliente al cambiar de idioma. Todos los datos se
  guardan en el dispositivo (Room + DataStore), sin cuentas, sin nube, sin publicidad y sin
  tracking.
- Firma de release y minificación con R8.
