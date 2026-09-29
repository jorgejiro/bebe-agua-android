# Textos para Google Play - ¡Bebe agua!

Documento de trabajo para rellenar la ficha de Google Play Console y preparar el paso de beta interna a produccion.

> Nota: la app no tiene cuentas, nube, anuncios, analitica ni tracking. Toda la informacion de ingestas y ajustes se guarda localmente en el dispositivo.

---

## Checklist para publicar en produccion

1. Verificar que la beta interna instala y funciona correctamente.
2. Probar en un dispositivo real o emulador:
   - primer arranque y onboarding;
   - permiso de notificaciones;
   - permiso de alarmas exactas;
   - registrar ingesta desde la app;
   - accion rapida de notificacion "Beber X ml";
   - accion "Posponer 15 min";
   - cambio de idioma ES/EN;
   - historial tras varios registros.
3. Ejecutar antes de generar la release:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew lint test
```

4. Si hay emulador o dispositivo conectado:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew connectedDebugAndroidTest
```

5. Generar un nuevo `.aab` firmado con la upload key vigente.
6. Subir el `.aab` a **Production** o promover la release ya validada desde **Internal testing**.
   Pegar las notas de la version (es-ES y en-US) desde
   [`play-release-notes.md`](play-release-notes.md).
7. Revisar:
   - ficha principal;
   - paises/regiones;
   - categoria;
   - datos de seguridad;
   - clasificacion de contenido;
   - declaracion de permisos sensibles, si Play Console la solicita;
   - precio: gratis.
8. Enviar a revision.

---

## Subir a Google Play con fastlane

Los textos y las capturas de la ficha se suben con `fastlane supply` (ver `fastlane/Fastfile` y
`fastlane/Appfile`) desde `fastlane/metadata/android/`, que es la fuente de verdad: si cambias un
texto, cámbialo ahí y en este documento. La cuenta de servicio ya tiene acceso a la app.

- **Clave**: `~/.config/play/jjrmobileapps.json` (ruta por defecto del `Appfile`) o cualquier otra
  exportada como `SUPPLY_JSON_KEY`. Nunca dentro del repositorio.
- `fastlane validar`: valida la ficha contra la API sin publicar nada.
- `fastlane ficha`: sube textos y capturas (móvil, tablet 7" y tablet 10"), sin binario.
- `fastlane subir track:internal`: compila y sube el AAB firmado como borrador (`release_status`
  `draft` por defecto), con las notas `changelogs/<versionCode>.txt` de cada idioma. Con
  `images:true` resube también las capturas.
- **Sigue siendo manual**: los cuestionarios de «Contenido de la app» y «Seguridad de los datos».
- Las capturas de `fastlane/.../images/` son copias byte a byte de
  `docs/store-assets/capturas/`; ver el README de `generar-capturas/` para regenerarlas y copiarlas.

---

## Ficha principal - App name

### Espanol

¡Bebe agua!

### English

Drink Water!

---

## Ficha principal - Short description

Limite habitual: 80 caracteres.

### Espanol

Registra tu agua, recibe recordatorios que vibran. Sin cuentas ni anuncios.

### English

Track water intake and get timely reminders. No accounts or ads.

---

## Ficha principal - Full description

### Espanol

¡Bebe agua! es una app sencilla para registrar el agua que bebes cada día y recibir recordatorios a lo largo de la jornada, sin cuentas, sin anuncios y sin enviar nada fuera de tu móvil.

La pantalla principal está pensada para ser rápida: un toque registra la cantidad que usaste la última vez. Si hoy quieres otra, cambia de medida entre las que tengas configuradas o escribe una cantidad distinta con «Otra cantidad…». Un círculo de progreso muestra lo bebido frente a tu objetivo, y debajo tienes los registros de hoy, que puedes borrar si te equivocas.

Los recordatorios se reparten de forma uniforme entre la hora de inicio y la de fin que elijas, con el número de avisos que prefieras. Dejan de llegar cuando alcanzas el objetivo, se recalculan cada vez que registras agua y en la pantalla principal ves cuándo será el siguiente. Vibran sin sonar, así que puedes tener el móvil en modo sonido sin que te molesten; si llevas un reloj emparejado, el aviso también le llega. Si lo prefieres, puedes activar que se salte el aviso que caiga justo después de beber.

Desde la propia notificación puedes registrar la cantidad habitual o posponer el aviso 15 minutos. También hay dos widgets para el escritorio: uno de 1×1 y otro de 2×1 que además muestra el progreso de hoy. Con una pulsación registran tu cantidad habitual sin abrir la app.

Al final del día puedes recibir un resumen en una notificación (a las 23:00 por defecto, con la hora configurable y desactivable) con lo que has bebido frente a tu objetivo. Además, cada día del historial se abre en una pantalla de detalle con sus registros, donde también puedes borrar los que quieras.

El historial recoge los últimos 30 días con el total de cada jornada, la media, el mejor día y tu racha actual. Puedes elegir el idioma (automático, español o inglés) y el tema (automático, claro u oscuro), consultar las novedades de cada versión dentro de la app y escribir al autor desde Ajustes.

Tu privacidad: no hay cuentas, nube, anuncios ni seguimiento. Los registros y los ajustes se guardan solo en tu dispositivo. El código es abierto, con licencia MIT.

Funciones principales:

- Registro de agua con un toque, con la última cantidad usada.
- Medidas configurables y opción de introducir otra cantidad.
- Círculo de progreso diario y lista de registros de hoy.
- Recordatorios repartidos en tu franja horaria, con número de avisos a tu gusto.
- Aviso que vibra sin sonar, compatible con relojes emparejados.
- Opción de saltar el recordatorio justo después de beber.
- Acciones rápidas en la notificación: registrar o posponer 15 minutos.
- Widgets de escritorio de 1×1 y 2×1 (con el progreso de hoy) para registrar con una pulsación.
- Resumen de fin de día en una notificación, a la hora que elijas.
- Historial de 30 días con media, mejor día y rachas, y detalle de cada día.
- Idioma automático, español o inglés; tema automático, claro u oscuro.
- Sin cuentas, sin anuncios y sin seguimiento: los datos no salen de tu dispositivo.

### English

Drink Water! is a simple app for logging the water you drink each day and getting reminders throughout the day, with no accounts, no ads and nothing sent off your phone.

The home screen is built for speed: one tap logs the amount you used last time. If you want something different today, switch between the sizes you have set up or type a custom amount with "Other amount…". A progress ring shows what you have drunk against your goal, and below it you will find today's records, which you can delete if you make a mistake.

Reminders are spread evenly between the start and end times you choose, with as many alerts as you like. They stop once you reach your goal, are recalculated every time you log water, and the home screen shows when the next one is due. They vibrate without making a sound, so your phone can stay in sound mode without being disturbed; if you wear a paired watch, the alert reaches it too. If you prefer, you can turn on skipping the reminder that would land right after you drink.

From the notification itself you can log your usual amount or snooze the alert for 15 minutes. There are also two home screen widgets, a 1x1 and a 2x1 that also shows today's progress. One tap logs your usual amount without opening the app.

At the end of the day you can get a summary notification (11:00 PM by default, with a configurable time, and you can turn it off) with what you drank against your goal. Each day in the history also opens a detail screen with its entries, where you can delete any you like.

History covers the last 30 days with each day's total, the average, your best day and your current streak. You can pick the language (automatic, Spanish or English) and the theme (automatic, light or dark), read what is new in each version inside the app, and email the author from Settings.

Your privacy: there are no accounts, no cloud, no ads and no tracking. Your records and settings are stored only on your device. The code is open source under the MIT license.

Main features:

- One-tap water logging with the last amount you used.
- Configurable sizes and a custom amount option.
- Daily progress ring and today's list of records.
- Reminders spread across your time window, with the number of alerts you choose.
- Alerts that vibrate without sound, forwarded to a paired watch.
- Option to skip the reminder right after you drink.
- Quick notification actions: log your amount or snooze for 15 minutes.
- 1x1 and 2x1 home screen widgets (the latter with today's progress) to log with a single tap.
- End-of-day summary notification at the time you choose.
- 30-day history with average, best day and streaks, plus a detail screen for each day.
- Automatic, Spanish or English language; automatic, light or dark theme.
- No accounts, no ads, no tracking: your data never leaves your device.

---

## Ficha principal - Category

Categoria recomendada:

Health & Fitness

Alternativa si Play Console ofrece subcategorias o etiquetas:

- Hydration
- Water tracker
- Reminders
- Health habits

---

## Ficha principal - Tags sugeridos

Usar solo si Play Console los ofrece y encajan con las opciones disponibles:

- Health & fitness
- Habit tracker
- Reminder
- Water tracker

---

## Ficha principal - Promotional text / Release tagline

### Espanol

Una forma sencilla y privada de acordarte de beber agua durante el dia.

### English

A simple and private way to remember to drink water throughout the day.

---

## Novedades de esta version

El texto de "Novedades" cambia en cada publicacion, asi que vive en su propio archivo, con el
recuento de caracteres frente al limite de 500 de Play y una seccion por version:
[`play-release-notes.md`](play-release-notes.md).

Lo que habia aqui eran las notas de la primera publicacion; estan conservadas en ese archivo.

---

## Capturas - Orden recomendado

Las capturas se generan automaticamente, en español y en ingles y en los tres formatos que pide Play
(telefono, tablet de 7" y tablet de 10"): ver `store-assets/generar-capturas/README.md`. Quedan en
`store-assets/capturas/<idioma>/<formato>/` —el idioma primero, que es como Play pide los recursos: uno
por ficha de idioma— y este es el orden en que se suben:

1. `01-registrar-agua` — la pantalla principal, con el anillo a media asta y los registros del dia.
2. `02-elegir-medida` — el selector de cantidad.
3. `03-historial` — los ultimos dias, con racha y medias.
4. `04-objetivo-y-recordatorios` — Ajustes: objetivo, franja horaria, recordatorios y horarios.
5. `05-medidas-y-permisos` — el final de Ajustes: medidas, idioma, permisos y Acerca de.
6. `06-recordatorio-en-la-notificacion` — el aviso con sus dos acciones rapidas.
7. `07-widget-en-el-escritorio` — el widget de 1x1 en la pantalla de inicio.

---

## Capturas - Textos cortos opcionales

Si anades texto sobre las capturas, usar frases breves.

### Espanol

- Registra agua con un toque.
- Sigue tu objetivo diario.
- Configura recordatorios a tu ritmo.
- Revisa tu historial reciente.
- Sin cuentas, anuncios ni tracking.

### English

- Log water with one tap.
- Follow your daily goal.
- Set reminders your way.
- Review your recent history.
- No accounts, ads, or tracking.

---

## Feature graphic - Texto sugerido

Si preparas una imagen promocional de 1024 x 500, sugerencia de copy:

### Espanol

¡Bebe agua!

Recordatorios simples para mantenerte hidratado.

### English

Drink Water!

Simple reminders to stay hydrated.

---

## Data safety - Respuestas sugeridas

Estas respuestas asumen que la app mantiene el estado actual: sin nube, sin cuentas, sin ads, sin analytics, sin crash reporting externo y sin SDKs de terceros que recojan datos.

### Does your app collect or share any of the required user data types?

No.

### Is all user data collected by your app encrypted in transit?

No aplica, porque la app no transmite datos de usuario fuera del dispositivo.

### Do you provide a way for users to request that their data is deleted?

No aplica para datos remotos, porque no hay cuenta ni servidor. Los datos locales se pueden eliminar desinstalando la app. Si en el futuro anades export/import, cuenta o sincronizacion, revisar esta respuesta.

### Privacy policy summary

¡Bebe agua! does not collect, share, or sell personal data. Water intake records and settings are stored locally on the user's device and are not transmitted to the developer or third parties.

---

## Privacy Policy - Texto base

Puedes publicarlo como pagina simple si Play Console te pide una URL de politica de privacidad. Ajusta la fecha antes de publicarlo.

### English

# Privacy Policy for Drink Water!

Effective date: 2026-05-26

Drink Water! is a personal hydration reminder app. The app is designed to work without accounts, cloud services, advertising, analytics, or tracking.

## Data stored on your device

The app stores your water intake records and app settings locally on your device. This information is used only to show your daily progress, history, and reminders.

## Data collection

The app does not collect personal data and does not send your water intake records or settings to the developer or to third parties.

## Data sharing

The app does not share or sell user data.

## Permissions

The app may request notification permission to send hydration reminders. It may also request exact alarm permission so reminders can be delivered at the configured time.

## Data deletion

Because the data is stored locally, you can delete it by clearing the app data from Android settings or uninstalling the app.

## Contact

For questions about this privacy policy, contact the developer through the email listed on Google Play.

### Espanol

# Politica de privacidad de ¡Bebe agua!

Fecha de entrada en vigor: 2026-05-26

¡Bebe agua! es una app personal de recordatorios de hidratacion. La app esta disenada para funcionar sin cuentas, servicios en la nube, publicidad, analitica ni tracking.

## Datos guardados en el dispositivo

La app guarda tus registros de ingesta de agua y ajustes localmente en tu dispositivo. Esta informacion se usa solo para mostrar tu progreso diario, historial y recordatorios.

## Recogida de datos

La app no recoge datos personales y no envia tus registros de agua ni tus ajustes al desarrollador ni a terceros.

## Comparticion de datos

La app no comparte ni vende datos de usuario.

## Permisos

La app puede solicitar permiso de notificaciones para enviar recordatorios de hidratacion. Tambien puede solicitar permiso de alarmas exactas para entregar los recordatorios a la hora configurada.

## Eliminacion de datos

Como los datos se guardan localmente, puedes eliminarlos borrando los datos de la app desde los ajustes de Android o desinstalando la app.

## Contacto

Para preguntas sobre esta politica de privacidad, contacta con el desarrollador mediante el email indicado en Google Play.

---

## App content - Content rating

Respuestas esperadas para el cuestionario, segun el estado actual de la app:

- No violencia.
- No contenido sexual.
- No lenguaje ofensivo.
- No apuestas.
- No compras dentro de la app.
- No contenido generado por usuarios.
- No interaccion social.
- No ubicacion compartida.
- No navegador web ni enlaces externos dentro de la app.

Resultado esperado: apta para todos o clasificacion equivalente baja, dependiendo del pais.

---

## App content - Target audience

Audiencia recomendada:

- 13+ o adultos/general audience.

No posicionarla especificamente para ninos. Aunque la app sea segura y simple, no esta disenada como app infantil ni incluye controles o politicas especificas para menores.

---

## App content - Ads

Does your app contain ads?

No.

---

## App content - App access

All app functionality is available without signing in. No credentials are required to review the app.

---

## App content - Permissions declaration

La app usa:

- `POST_NOTIFICATIONS`: para enviar recordatorios de hidratacion.
- `SCHEDULE_EXACT_ALARM`: para programar recordatorios puntuales dentro de la franja horaria elegida por el usuario.
- `RECEIVE_BOOT_COMPLETED`: para reprogramar recordatorios despues de reiniciar el dispositivo.

Texto sugerido si Play Console pide explicar `SCHEDULE_EXACT_ALARM`:

### English

The app uses exact alarms to deliver hydration reminders at the times configured by the user. Reminders are only scheduled within the user's selected time window and stop once the daily water goal is reached. The app does not use `USE_EXACT_ALARM`.

### Espanol

La app usa alarmas exactas para enviar recordatorios de hidratacion a las horas configuradas por el usuario. Los recordatorios solo se programan dentro de la franja horaria elegida y se detienen al alcanzar el objetivo diario. La app no usa `USE_EXACT_ALARM`.

---

## Declaracion corta para soporte o revision

### English

Drink Water! is a local-only hydration reminder app. It does not require accounts, does not show ads, does not use analytics, and does not transmit user data off the device. Exact alarms are used only to deliver user-configured hydration reminders on time.

### Espanol

¡Bebe agua! es una app local de recordatorios de hidratacion. No requiere cuentas, no muestra anuncios, no usa analitica y no transmite datos del usuario fuera del dispositivo. Las alarmas exactas se usan solo para enviar puntualmente los recordatorios configurados por el usuario.
