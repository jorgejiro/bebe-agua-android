# Textos para Google Play - Bebe Agua

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

## Ficha principal - App name

### Espanol

Bebe Agua

### English

Drink Water

---

## Ficha principal - Short description

Limite habitual: 80 caracteres.

### Espanol

Registra el agua que bebes y recibe recordatorios para llegar a tu objetivo

### English

Track water intake and get timely reminders. No accounts or ads.

---

## Ficha principal - Full description

### Espanol

Bebe Agua es una app sencilla para registrar tu ingesta diaria de agua y recibir recordatorios periódicos durante el día.

La pantalla principal esta pensada para ser rápida: un toque registra la cantidad que usas normalmente, y puedes cambiarla cuando quieras desde tus medidas configuradas o introducir otra cantidad. El progreso diario se muestra en un circulo claro con el total bebido frente a tu objetivo.

Puedes configurar tu objetivo diario, la hora de inicio y fin de los recordatorios, y cuantos avisos quieres recibir al día. La app calcula los horarios de forma uniforme dentro de tu franja horaria y deja de recordarte beber cuando ya has alcanzado el objetivo.

Tambien incluye historial de los últimos dias, estadísticas basicas, rachas, soporte para Español e Inglés, y acciones rápidas desde la notificación para registrar agua o posponer el aviso.

Privacidad total: sin cuentas, sin nube, sin anuncios y sin tracking. No se guardan datos personales, y los registros de ingesta se guardan localmente en tu dispositivo.

Funciones principales:

- Registro rápido de agua con un solo toque.
- Objetivo diario configurable.
- Recordatorios dentro de la franja horaria que elijas.
- Acciones rápidas desde la notificación.
- Historial diario y estadísticas básicas.
- Tamaño de ingesta configurable.
- Idioma Español e Inglés.
- Datos guardados sólo en el dispositivo (privacidad total).

### English

Drink Water is a simple app for tracking your daily water intake and receiving periodic reminders throughout the day.

The home screen is built for quick use: one tap logs your usual amount, and you can change it whenever you want from your configured sizes or enter a custom amount. Your daily progress is shown in a clear circular view with the amount consumed against your goal.

You can configure your daily goal, reminder start and end time, and how many reminders you want per day. The app calculates reminder times evenly within your selected window and stops reminding you once you reach your daily goal.

It also includes recent history, basic statistics, streaks, Spanish and English support, and quick notification actions to log water or snooze a reminder.

Simple privacy: no accounts, no cloud, no ads, and no tracking. Your records stay stored locally on your device.

Main features:

- One-tap water logging.
- Configurable daily goal.
- Reminders within your chosen time window.
- Quick actions from notifications.
- Daily history and basic statistics.
- Configurable intake sizes.
- Spanish and English language support.
- Data stored only on your device.

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

### Espanol

- Registro rapido de ingesta diaria de agua.
- Recordatorios configurables dentro de tu franja horaria.
- Historial de los ultimos dias y estadisticas basicas.
- Acciones rapidas desde la notificacion.
- Soporte para Espanol e Ingles.

### English

- Quick daily water intake logging.
- Configurable reminders within your chosen time window.
- Recent history and basic statistics.
- Quick actions from notifications.
- Spanish and English support.

---

## Capturas - Orden recomendado

1. Home con progreso diario visible.
2. Home con varios registros en "Registros de hoy".
3. Historial con estadisticas y dias registrados.
4. Ajustes con objetivo, horario y recordatorios.
5. Notificacion con acciones rapidas.

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

Bebe Agua

Recordatorios simples para mantenerte hidratado.

### English

Drink Water

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

Bebe Agua does not collect, share, or sell personal data. Water intake records and settings are stored locally on the user's device and are not transmitted to the developer or third parties.

---

## Privacy Policy - Texto base

Puedes publicarlo como pagina simple si Play Console te pide una URL de politica de privacidad. Ajusta la fecha antes de publicarlo.

### English

# Privacy Policy for Drink Water

Effective date: 2026-05-26

Drink Water is a personal hydration reminder app. The app is designed to work without accounts, cloud services, advertising, analytics, or tracking.

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

# Politica de privacidad de Bebe Agua

Fecha de entrada en vigor: 2026-05-26

Bebe Agua es una app personal de recordatorios de hidratacion. La app esta disenada para funcionar sin cuentas, servicios en la nube, publicidad, analitica ni tracking.

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

Drink Water is a local-only hydration reminder app. It does not require accounts, does not show ads, does not use analytics, and does not transmit user data off the device. Exact alarms are used only to deliver user-configured hydration reminders on time.

### Espanol

Bebe Agua es una app local de recordatorios de hidratacion. No requiere cuentas, no muestra anuncios, no usa analitica y no transmite datos del usuario fuera del dispositivo. Las alarmas exactas se usan solo para enviar puntualmente los recordatorios configurados por el usuario.
