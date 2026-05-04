# CLAUDE.md — Bebe Agua

> Documento guía para Claude Code. Léelo antes de cualquier tarea no trivial. Si algo aquí entra en conflicto con la petición del usuario, pregunta antes de ejecutar.

---

## 1. Visión del producto

**Bebe Agua** es una app Android nativa para registrar la ingesta diaria de agua y recibir recordatorios periódicos. Pensada para uso personal del autor y publicación en Google Play.

### Principios de diseño
- **Simple y directa.** Una pantalla principal donde la acción de "registrar ingesta" es el elemento dominante.
- **Sin fricción.** Un toque registra la cantidad por defecto. La cantidad por defecto es siempre la última usada.
- **Recordatorios bien hechos.** Solo dentro de la franja horaria configurada por el usuario, dejan de enviarse al alcanzar el objetivo, y se reinician al registrar manualmente.
- **Sin cuentas, sin nube, sin ads, sin tracking.** Todo local en el dispositivo.
- **Bonita y moderna.** Material 3 expressive, animaciones sutiles, tipografía y color cuidados.

### Referencia visual
La app tomada como referencia visual (no funcional) por el usuario es *Water Tracker — Drink Water Reminder* (`watertracker.waterreminder.watertrackerapp.drinkwater`). Nos interesa específicamente:
- El círculo grande con progreso `bebido/objetivo`.
- El botón central de añadir ingesta con la cantidad por defecto visible.
- El botón secundario para cambiar la medida por defecto.
- La lista de "Registros de hoy" debajo.

**No nos interesa**: la sección superior de "consejos", la mascota animada, ni elementos estilo gamificación con corazones/recompensas.

---

## 2. Funcionalidad (alcance v1)

### 2.1 Pantalla principal (Casa)
- Círculo de progreso central mostrando `consumido / objetivo` en ml.
- Botón grande central: **registrar ingesta con la cantidad por defecto** (la última usada). Un solo toque.
- Botón secundario adyacente: **cambiar la cantidad por defecto** (abre selector con todas las medidas configuradas).
- Sección "Registros de hoy" con lista de ingestas (hora + cantidad), ordenadas descendente. Cada item permite eliminar (long-press o menú overflow).
- Indicador de la próxima notificación programada ("Próximo recordatorio: 09:30").

### 2.2 Pantalla Historial
- Vista por días con total diario y barra de progreso vs objetivo.
- Posibilidad de ver el detalle de un día (lista de ingestas).
- (v1.1, no v1) Gráfico semanal/mensual.

### 2.3 Pantalla Configuración
- **Objetivo diario** (ml). Por defecto: 1500 ml.
- **Hora de inicio del día**. Por defecto: 08:00.
- **Hora de fin del día**. Por defecto: 23:00.
- **Número de recordatorios al día** (slider entre N_min y N_max calculados según ventana horaria). El usuario decide más recordatorios = ingestas más pequeñas, o menos = más grandes.
- **Lista editable de tamaños de ingesta**. Por defecto: `[200 ml]`. El usuario puede añadir, editar y eliminar (mínimo siempre debe quedar uno).
- **Tamaño por defecto al iniciar**: el último usado (no se configura, se infiere).
- **Idioma**: Auto / Español / English.
- **Tema**: Auto / Claro / Oscuro.
- **Vista previa de horarios de recordatorio calculados** con posibilidad de ajustar la hora de cada uno individualmente.
- **Permisos**: estado de permiso de notificaciones y de alarmas exactas, con botón para abrir ajustes del sistema si están denegados.

### 2.4 Notificaciones
- Tap en la notificación → abre la app en la pantalla principal (deep link, no relanzar).
- Acción rápida en la notificación: "He bebido 200 ml" (o lo que sea la medida por defecto actual) que registra sin abrir la app.
- Acción rápida: "Posponer 15 min".
- Solo se envían entre `horaInicio` y `horaFin`.
- Dejan de enviarse al alcanzar el objetivo diario.
- Al registrar manualmente una ingesta, se reprograma el siguiente recordatorio desde ese momento (no se acumulan).
- A medianoche (en zona horaria local) se reinician contadores y se reprograman los recordatorios del día siguiente.

### 2.5 Cálculo de recordatorios
Dada la ventana `[horaInicio, horaFin]` y `N` recordatorios elegidos por el usuario:
- Distribuir `N` puntos uniformemente en la ventana.
- Cantidad sugerida por recordatorio = `objetivoDiario / N` (redondeado a la medida disponible más cercana, solo informativo en la notificación).
- El usuario puede sobrescribir manualmente cada hora individual desde Configuración.
- Si el usuario registra manualmente, el "próximo recordatorio" se desplaza para que no salga inmediatamente después.

---

## 3. Stack técnico

**Vinculante** (no cambiar sin justificarlo):

| Capa | Decisión |
|---|---|
| Lenguaje | Kotlin 2.3.10+ |
| UI | Jetpack Compose con BOM `2026.04.01` o superior estable |
| Material | Material 3 (`androidx.compose.material3`) |
| `minSdk` | 31 (Android 12) |
| `targetSdk` | 36 (Android 16, último estable en build) |
| `compileSdk` | igual a `targetSdk` |
| Build | Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`) |
| AGP | última estable compatible con la BOM elegida |
| Arquitectura | MVVM + UDF (Unidirectional Data Flow), capas: `ui` / `domain` / `data` |
| DI | Hilt |
| Persistencia | **Room** para registros de ingesta (consultas por día, agregaciones); **DataStore (Preferences)** para ajustes |
| Navegación | Navigation 3 (Nav3) si está estable en el momento del setup; si no, Navigation Compose 2.x |
| Background | **`AlarmManager.setExactAndAllowWhileIdle()`** + `BroadcastReceiver` para los recordatorios. **No usar WorkManager** para los recordatorios (granularidad mínima de 15 min, no garantiza hora exacta). Sí usar WorkManager si aparece alguna tarea de mantenimiento periódica no crítica. |
| Notificaciones | `NotificationManagerCompat` + canal dedicado `reminders` con importancia `IMPORTANCE_DEFAULT` |
| Concurrencia | Coroutines + Flow |
| i18n | Recursos `strings.xml` (`values/`, `values-es/`); cambio de idioma en runtime con `AppCompatDelegate.setApplicationLocales` (App Locales API, Android 13+) |
| Tests | JUnit5 + Turbine para Flows + Compose UI Test |
| Logs | `Timber` (solo en debug) |
| Backup | `android:allowBackup="false"` en v1 (decisión del usuario: solo local, sin nube) |

### Permisos requeridos
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

> **Importante sobre alarmas exactas:** desde Android 14 `SCHEDULE_EXACT_ALARM` está denegada por defecto para apps que no son de tipo reloj/calendario. Hay que solicitarla al usuario en el primer arranque con `Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM` y comprobar `AlarmManager.canScheduleExactAlarms()` antes de programar. **No declarar `USE_EXACT_ALARM`**: Google Play la rechazaría para una app de hidratación (está restringida a alarm clock / calendar genuinos).

> **Boot persistence:** al recibir `ACTION_BOOT_COMPLETED` hay que reprogramar los recordatorios del día.

---

## 4. Estructura del proyecto

```
app/
  build.gradle.kts
  src/main/
    AndroidManifest.xml
    kotlin/com/jorgejiro/bebeagua/
      BebeAguaApplication.kt
      MainActivity.kt
      di/                        # módulos Hilt
      data/
        local/
          db/                    # Room: AppDatabase, DAOs, entidades
          datastore/             # DataStore: SettingsDataSource
        repository/              # Implementaciones de los repositorios
      domain/
        model/                   # Modelos de dominio puros (Intake, DaySummary, ReminderConfig...)
        repository/              # Interfaces
        usecase/                 # AddIntakeUseCase, GetTodaySummaryUseCase, ScheduleRemindersUseCase...
      ui/
        theme/                   # Color.kt, Type.kt, Theme.kt (Material 3, dynamic color)
        common/                  # Composables reutilizables
        home/                    # HomeScreen + HomeViewModel
        history/
        settings/
        navigation/              # NavGraph
      reminder/
        ReminderScheduler.kt     # Encapsula AlarmManager
        ReminderReceiver.kt      # BroadcastReceiver
        BootReceiver.kt
        NotificationFactory.kt
      util/
    res/
      values/strings.xml
      values-es/strings.xml
      mipmap-*/                  # Iconos adaptativos
      drawable/
gradle/
  libs.versions.toml             # Catálogo de versiones
```

---

## 5. Base de datos (Room)

### Tabla `intake`
| Columna | Tipo | Notas |
|---|---|---|
| `id` | INTEGER PK autogen | |
| `amount_ml` | INTEGER NOT NULL | |
| `timestamp_epoch_ms` | INTEGER NOT NULL | UTC |
| `timezone_id` | TEXT NOT NULL | p.ej. `Europe/Madrid`, para reagrupar correctamente por día local |
| `local_date` | TEXT NOT NULL | `YYYY-MM-DD` derivado, indexado |

Index: `local_date`.

DAO con queries:
- `observeIntakesForDate(date: LocalDate): Flow<List<IntakeEntity>>`
- `observeTotalForDate(date: LocalDate): Flow<Int>`
- `getDailyTotalsBetween(start: LocalDate, end: LocalDate): List<DayTotal>`
- `insert(intake)`, `delete(id)`

Migrations: documentadas y testeadas en `androidTest/`.

---

## 6. Flujo de notificaciones (resumen técnico)

1. Al cambiar settings o registrar ingesta → `ScheduleRemindersUseCase` recalcula los próximos recordatorios del día actual.
2. Solo se programa **el siguiente** recordatorio (no la lista entera) usando `setExactAndAllowWhileIdle`. Cuando se dispara, el receiver:
   - Comprueba si hoy ya se alcanzó el objetivo → si sí, no notifica y programa el de mañana.
   - Comprueba si estamos en ventana horaria → si no, programa el siguiente válido.
   - Lanza notificación.
   - Programa el siguiente recordatorio.
3. `BootReceiver` reprograma el siguiente recordatorio al arrancar el dispositivo.
4. Acción "He bebido X ml" en la notificación → registra ingesta vía repositorio y reprograma.

---

## 7. Convenciones de código

- **Idioma del código y comentarios técnicos**: inglés. Strings de UI: recursos i18n.
- **Nomenclatura de archivos Compose**: `HomeScreen.kt`, `HomeViewModel.kt`, `HomeUiState.kt`. Una pantalla = un archivo de screen + un archivo de viewmodel + un archivo de state.
- **Estado de pantalla**: `sealed interface XxxUiState` con `Loading`, `Success(data)`, `Error`. Eventos de una vez (snackbars, navegación) vía `Channel<UiEvent>`.
- **Inyección**: constructor injection siempre. Nada de `@Inject lateinit`.
- **No usar `LiveData`**, ni `RxJava`. Solo Flow/StateFlow.
- **No usar XML para UI**. Todo Compose. La única excepción tolerable es el splash con la API `androidx.core.splashscreen`.
- **Previews**: cada Composable público con al menos un `@Preview`. Usar `@PreviewLightDark`.

---

## 8. Cómo trabajar en este repo (instrucciones para Claude Code)

### Antes de cada tarea
1. Lee este archivo entero. Si la tarea contradice algo aquí, pregunta antes de tirar adelante.
2. Mira el último commit y el estado de `git status` para no pisar cambios.
3. Si se va a tocar UI, abre `Theme.kt`, `Color.kt`, `Type.kt` antes de inventarte estilos.

### Al hacer cambios
- Los strings van en `strings.xml`. **Nunca hardcodees strings en Composables.**
- Si añades un string, añade también la traducción en `values-es/strings.xml` (o EN si el base es ES).
- Si tocas el schema de Room → escribe la migration y el test de migration.
- Si tocas algo de notificaciones → manualmente prueba en emulador con Android 12, 14 y 16 (la lógica de permisos cambia).
- No añadas dependencias sin justificar y sin actualizar `libs.versions.toml`.

### Al terminar una tarea
- Ejecuta `./gradlew lint detekt test` y deja todo en verde.
- Resume en un commit con [Conventional Commits](https://www.conventionalcommits.org/): `feat(home): ...`, `fix(reminder): ...`, `refactor(data): ...`.
- Si has tomado decisiones técnicas no triviales (cambio de stack, librería nueva, workaround), añade una entrada en `docs/decisions/NNN-titulo.md` (ADR ligero).

### Lo que NO hacer (rojo)
- No usar WebView, Cordova, Capacitor, React Native, Flutter ni KMP para la UI. Esto es Android nativo en Compose.
- No mezclar XML layouts con Compose.
- No escribir lógica de negocio en Composables ni en `Activity`. Va en `ViewModel` o en `UseCase`.
- No usar `runBlocking` fuera de tests.
- No subir secretos, claves de Play Console, ni `keystore` al repo.
- No declarar `USE_EXACT_ALARM` (rechazada por Play para esta categoría).
- No añadir analítica de terceros, crash reporting con telemetría, ni SDKs de marketing en v1.

---

## 9. Roadmap

**v1.0 (MVP, Play Store)**
- Pantalla principal con registro de ingesta y progreso.
- Tamaños de ingesta personalizables.
- Configuración (objetivo, ventana horaria, número de recordatorios, idioma, tema).
- Recordatorios con `setExactAndAllowWhileIdle`.
- Acciones rápidas en notificación (registrar / posponer).
- Persistencia con Room.
- i18n ES/EN.
- Material 3 con dynamic color.

**v1.1**
- Historial visual (gráficos semanal/mensual).
- Widget de pantalla principal con botón rápido de registro.
- Export/import de datos en JSON.

**v1.2 (eventual)**
- Wear OS companion.
- Recordatorios "inteligentes" (saltarse el siguiente si has bebido más de la cuota esperada).

---

## 10. Branding y assets

- **Nombre**: Bebe Agua (ES) / Drink Water (EN).
- **Package**: `com.jorgejiro.bebeagua` (ajustar si Jorge prefiere otro dominio).
- **Paleta base**: azules acuáticos. Definir colores semilla en `Theme.kt` y dejar que Material 3 dynamic color tome el control en Android 12+.
- **Icono adaptativo**: gota estilizada sobre fondo claro. Pendiente de generar con el flujo de Claude Design.
- **Capturas para Play**: 5 capturas (Home, Home con muchas ingestas, Historial, Settings, Notificación).

---

## 11. Preguntas abiertas

Cosas a decidir antes/durante el desarrollo (Claude Code: si te topas con una de estas, **pregunta a Jorge** antes de inventar una respuesta):

1. ¿Acción de eliminar registro reduce el contador del día (sí, asumido) o lo deja como "histórico"?
2. ¿Los recordatorios deben silenciarse si el dispositivo está en modo "No molestar"? (Por defecto sí, no hacer override del DND.)
3. ¿Qué pasa si el usuario registra ingesta a las 03:00 fuera de la ventana? Se acepta y suma al día anterior si todavía no ha pasado la hora final del día anterior; si no, suma al día actual. **Por simplicidad v1: siempre suma al día local del timestamp.**
4. ¿Permitir registros con cantidades arbitrarias además de las medidas predefinidas? Recomendado: sí, vía un campo "Otra cantidad…" en el selector.
