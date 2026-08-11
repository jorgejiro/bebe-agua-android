# AGENTS.md — Bebe Agua

> Documento guía para Codex. Léelo antes de cualquier tarea no trivial. Si algo aquí entra en conflicto con la petición del usuario, pregunta antes de ejecutar.

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

## 2. Funcionalidad (estado actual v1.2.0)

### 2.1 Pantalla principal (Casa) ✅ Implementada
- Círculo de progreso central mostrando `consumido / objetivo` en ml.
- Botón grande central: **registrar ingesta con la cantidad por defecto** (la última usada). Un solo toque.
- Botón secundario adyacente: **cambiar la cantidad por defecto** (abre selector con todas las medidas configuradas).
- Sección "Registros de hoy" con lista de ingestas (hora + cantidad), ordenadas descendente. Cada item permite eliminar.
- Indicador de la próxima notificación programada ("Próximo recordatorio: 09:30").

### 2.2 Pantalla Historial ✅ Implementada
- Vista por días (últimos 30) con total diario y barra de progreso vs objetivo.
- Streaks y estadísticas básicas.
- (v1.1, no v1) Gráfico semanal/mensual.

### 2.3 Pantalla Configuración ✅ Implementada
- **Objetivo diario** (ml). Por defecto: 2400 ml.
- **Hora de inicio del día**. Por defecto: 08:00.
- **Hora de fin del día**. Por defecto: 21:00.
- **Número de recordatorios al día** (slider entre N_min y N_max calculados según ventana horaria). Por defecto: 14.
- **Lista editable de tamaños de ingesta**. Por defecto: `[200 ml]`. El usuario puede añadir, editar y eliminar (mínimo siempre debe quedar uno).
- **Tamaño por defecto al iniciar**: el último usado (no se configura, se infiere).
- **Idioma**: Auto / Español / English.
- **Tema**: Auto / Claro / Oscuro.
- **Vista previa de horarios de recordatorio calculados**.
- **Permisos**: estado de permiso de notificaciones y de alarmas exactas, con botón para abrir ajustes del sistema si están denegados. El estado de `canScheduleExactAlarms()` se refresca en `onResume`.
- **Acerca de**: **Enviar comentarios** —correo al autor con el nombre y la versión en el asunto; la dirección **no se imprime en la pantalla**, se ve en la app de correo al abrirse—, versión instalada (`versionName (versionCode)`) y acceso a la pantalla **Novedades** (changelog).

### 2.4 Pantalla Onboarding ✅ Implementada
- Flujo de bienvenida al primer arranque.
- Configura objetivo diario y ventana horaria.
- Solicita permisos (notificaciones, alarmas exactas).

### 2.5 Notificaciones ✅ Implementadas
- Tap en la notificación → abre la app en la pantalla principal.
- Acción rápida en la notificación: "He bebido X ml" (medida por defecto actual) que registra sin abrir la app.
- Acción rápida: "Posponer 15 min".
- Solo se envían entre `horaInicio` y `horaFin`.
- Dejan de enviarse al alcanzar el objetivo diario.
- Al registrar manualmente una ingesta, se reprograma el siguiente recordatorio desde ese momento.
- `BootReceiver` reprograma los recordatorios tras reinicio del dispositivo.

### 2.6 Cálculo de recordatorios
Dada la ventana `[horaInicio, horaFin]` y `N` recordatorios elegidos por el usuario:
- Distribuir `N` puntos uniformemente en la ventana.
- Cantidad sugerida por recordatorio = `objetivoDiario / N` (redondeado a la medida disponible más cercana, solo informativo en la notificación).
- Si el usuario registra manualmente, el "próximo recordatorio" se desplaza para que no salga inmediatamente después.

### 2.7 Pantalla Novedades (changelog) ✅ Implementada
- Se abre desde Configuración → Acerca de → **Novedades** (ruta `changelog`, no es una pestaña de la navegación superior; mientras está abierta se mantiene resaltada la pestaña de Configuración).
- Lista de versiones de más reciente a más antigua: nombre de versión, fecha de publicación, distintivo "Actual" para el `versionCode` instalado y los cambios en viñetas.
- **Tres sitios que hay que mantener sincronizados** al publicar una versión:
  1. `CHANGELOG.md` en la raíz (fuente de verdad del repo).
  2. Los `string-array` `changelog_<version>` en `values/strings.xml` y `values-es/strings.xml` (texto traducido que ve el usuario).
  3. `ui/changelog/ChangelogCatalog.kt` (versión, `versionCode`, fecha y referencia al array).
- El test unitario `ChangelogCatalogTest` falla si el `versionCode` compilado no tiene entrada en el catálogo; el instrumentado `ChangelogResourcesTest` falla si falta el array en ES o EN.
- No se muestra automáticamente al actualizar: es solo consultable.

---

## 3. Stack técnico

**Vinculante** (no cambiar sin justificarlo):

| Capa | Decisión |
|---|---|
| Lenguaje | Kotlin **2.3.21** |
| UI | Jetpack Compose con BOM **`2026.04.01`** |
| Material | Material 3 (`androidx.compose.material3`) |
| `minSdk` | 31 (Android 12) |
| `targetSdk` | 36 (Android 16) |
| `compileSdk` | 36 |
| Build | Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`) |
| AGP | **9.2.1** |
| KSP | **2.3.7** (para Hilt y Room; no usar kapt) |
| Arquitectura | MVVM + UDF (Unidirectional Data Flow), capas: `ui` / `domain` / `data` |
| DI | Hilt **2.59.2** |
| Persistencia | **Room 2.7.1** para registros de ingesta; **DataStore Preferences 1.1.4** para ajustes |
| Navegación | **Navigation Compose 2.9.0** (Nav3 no estaba maduro; se descartó) |
| Background | **`AlarmManager.setExactAndAllowWhileIdle()`** + `BroadcastReceiver`. **No usar WorkManager** para recordatorios. |
| Notificaciones | `NotificationManagerCompat` + canal `reminders` con `IMPORTANCE_DEFAULT` |
| Concurrencia | Coroutines **1.10.2** + Flow |
| i18n | Recursos `strings.xml` (`values/`, `values-es/`); cambio de idioma en runtime con `AppCompatDelegate.setApplicationLocales` |
| Tests | **JUnit 4** + MockK **1.13.9** + Turbine **1.2.0** + Compose UI Test |
| Logs | `Timber 5.0.1` (solo en debug) |
| Backup | `android:allowBackup="false"` |

### Versiones de librerías clave (ver `gradle/libs.versions.toml` para la lista completa)
| Librería | Versión |
|---|---|
| Compose BOM | 2026.04.01 |
| Hilt | 2.59.2 |
| Room | 2.7.1 |
| DataStore | 1.1.4 |
| Navigation Compose | 2.9.0 |
| Hilt Navigation Compose | 1.2.0 |
| Coroutines | 1.10.2 |
| Timber | 5.0.1 |
| MockK | 1.13.9 |
| Turbine | 1.2.0 |

### Permisos requeridos
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

> **Importante sobre alarmas exactas:** desde Android 14 `SCHEDULE_EXACT_ALARM` está denegada por defecto. Se solicita al usuario en onboarding con `Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM` y se comprueba `AlarmManager.canScheduleExactAlarms()` antes de programar. El estado se refresca en `onResume` de Settings. **No declarar `USE_EXACT_ALARM`**: Google Play la rechazaría para una app de hidratación.

> **Boot persistence:** al recibir `ACTION_BOOT_COMPLETED` y `ACTION_LOCKED_BOOT_COMPLETED` el `BootReceiver` reprograma el siguiente recordatorio.

---

## 4. Estructura del proyecto

```
app/
  build.gradle.kts
  src/main/
    AndroidManifest.xml
    java/com/jjrapps/bebeagua/
      BebeAguaApplication.kt
      MainActivity.kt
      di/
        DatabaseModule.kt
        DataStoreModule.kt
        RepositoryModule.kt
      data/
        local/
          db/
            AppDatabase.kt         # Room v1, entidad única intake
            IntakeDao.kt
            IntakeEntity.kt
            LocalDateConverter.kt
            DayTotal.kt
          datastore/
            SettingsDataSource.kt
        repository/
          IntakeRepositoryImpl.kt
          SettingsRepositoryImpl.kt
      domain/
        model/
          Intake.kt
          AppSettings.kt
          DaySummary.kt
          DayHistory.kt
        repository/
          IntakeRepository.kt
          SettingsRepository.kt
          ReminderScheduler.kt    # interfaz
        usecase/
          AddIntakeUseCase.kt
          DeleteIntakeUseCase.kt
          GetTodaySummaryUseCase.kt
          GetDailyHistoryUseCase.kt
          CalculateReminderTimesUseCase.kt
          ScheduleRemindersUseCase.kt
          ObserveSettingsUseCase.kt
      ui/
        theme/
          Color.kt
          Type.kt
          Shape.kt
          Theme.kt
        common/
          IntakeRecordItem.kt
          ProgressRing.kt
        home/
          HomeScreen.kt
          HomeViewModel.kt
          HomeUiState.kt
        history/
          HistoryScreen.kt
          HistoryViewModel.kt
          HistoryUiState.kt
        changelog/
          ChangelogScreen.kt
          ChangelogViewModel.kt
          ChangelogUiState.kt
          ChangelogCatalog.kt       # catálogo estático de versiones + @ArrayRes de sus cambios
        settings/
          SettingsScreen.kt
          SettingsViewModel.kt
          SettingsUiState.kt
        onboarding/
          OnboardingScreen.kt
          OnboardingViewModel.kt
        main/
          MainViewModel.kt        # gestiona el estado de onboarding completado
        navigation/
          NavGraph.kt
          Screen.kt               # sealed class con rutas Home / History / Settings / Changelog
      reminder/
        AlarmManagerReminderScheduler.kt   # implementación producción
        NoOpReminderScheduler.kt           # stub para tests
        ReminderReceiver.kt
        NotificationActionReceiver.kt      # maneja acciones de notificación
        BootReceiver.kt
        NotificationFactory.kt
    res/
      values/strings.xml
      values-es/strings.xml
      mipmap-*/
      drawable/
gradle/
  libs.versions.toml
```

---

## 5. Base de datos (Room)

**Versión actual de la base de datos: 1**

### Tabla `intake`
| Columna | Tipo | Notas |
|---|---|---|
| `id` | INTEGER PK autogen | |
| `amount_ml` | INTEGER NOT NULL | |
| `timestamp_epoch_ms` | INTEGER NOT NULL | UTC |
| `timezone_id` | TEXT NOT NULL | p.ej. `Europe/Madrid` |
| `local_date` | TEXT NOT NULL | `YYYY-MM-DD` derivado, indexado |

Index: `local_date`.

DAO con queries:
- `observeIntakesForDate(date: LocalDate): Flow<List<IntakeEntity>>`
- `observeTotalForDate(date: LocalDate): Flow<Int>`
- `getDailyTotalsBetween(start: LocalDate, end: LocalDate): List<DayTotal>`
- `insert(intake)`, `delete(id)`

Al subir la versión del schema → escribir migration + test en `androidTest/`.

---

## 6. Flujo de notificaciones (resumen técnico)

1. Al cambiar settings o registrar ingesta → `ScheduleRemindersUseCase` recalcula y programa solo **el siguiente** recordatorio con `setExactAndAllowWhileIdle`.
2. Cuando se dispara `ReminderReceiver`:
   - Comprueba si hoy ya se alcanzó el objetivo → si sí, no notifica y programa el de mañana.
   - Comprueba si estamos en ventana horaria → si no, programa el siguiente válido.
   - Lanza notificación vía `NotificationFactory`.
   - Programa el siguiente recordatorio.
3. `BootReceiver` (escucha `BOOT_COMPLETED` y `LOCKED_BOOT_COMPLETED`) reprograma el siguiente recordatorio al arrancar.
4. `NotificationActionReceiver` gestiona:
   - Acción "beber X ml" → llama al repositorio + reprograma.
   - Acción "posponer 15 min" → reprograma con offset.

---

## 7. Convenciones de código

- **Idioma del código y comentarios técnicos**: inglés. Strings de UI: recursos i18n.
- **Package raíz**: `com.jjrapps.bebeagua`.
- **Nomenclatura de archivos Compose**: `HomeScreen.kt`, `HomeViewModel.kt`, `HomeUiState.kt`. Una pantalla = un archivo de screen + un archivo de viewmodel + un archivo de state.
- **Estado de pantalla**: `sealed interface XxxUiState` con `Loading`, `Success(data)`, `Error`. Eventos de una vez (snackbars, navegación) vía `Channel<UiEvent>`.
- **Inyección**: constructor injection siempre. Nada de `@Inject lateinit`.
- **No usar `LiveData`**, ni `RxJava`. Solo Flow/StateFlow.
- **No usar XML para UI**. Todo Compose. La única excepción tolerable es el splash con la API `androidx.core.splashscreen`.
- **Previews**: cada Composable público con al menos un `@Preview`. Usar `@PreviewLightDark`.

---

## 8. Cómo trabajar en este repo (instrucciones para Codex)

### Antes de cada tarea
1. Lee este archivo entero. Si la tarea contradice algo aquí, pregunta antes de tirar adelante.
2. Mira el último commit y el estado de `git status` para no pisar cambios.
3. Si se va a tocar UI, abre `Theme.kt`, `Color.kt`, `Type.kt`, `Shape.kt` antes de inventarte estilos.

### Al hacer cambios
- Los strings van en `strings.xml`. **Nunca hardcodees strings en Composables.**
- Si añades un string, añade también la traducción en `values-es/strings.xml` (o EN si el base es ES).
- Si subes `versionCode`/`versionName` → actualiza `CHANGELOG.md`, los `string-array` `changelog_*` (EN y ES) y `ChangelogCatalog.kt`.
- Si tocas el schema de Room → escribe la migration y el test de migration. Incrementa `AppDatabase.VERSION`.
- Si tocas algo de notificaciones → manualmente prueba en emulador con Android 12, 14 y 16 (la lógica de permisos cambia).
- No añadas dependencias sin justificar y sin actualizar `libs.versions.toml`.

### Al terminar una tarea
- Ejecuta `./gradlew lint test` y deja todo en verde.
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

**v1.0 — MVP ✅ Publicado** (`versionCode 2`, `versionName 1.0.1`)
- [x] Pantalla principal con registro de ingesta y progreso.
- [x] Pantalla historial (últimos 30 días, streaks).
- [x] Pantalla configuración (objetivo, ventana horaria, recordatorios, idioma, tema, permisos).
- [x] Onboarding en primer arranque.
- [x] Recordatorios exactos con `setExactAndAllowWhileIdle`.
- [x] Acciones rápidas en notificación (registrar / posponer).
- [x] Boot persistence.
- [x] Persistencia con Room + DataStore.
- [x] i18n ES/EN.
- [x] Material 3 con dynamic color.
- [x] Suite de tests (40 tests: unitarios + instrumentados).
- [x] Release signing + R8 minification.

**v1.1 — Cerrada** (`versionCode 6`, `versionName 1.1.0`)
- [x] Ventana de cortesía tras registrar una ingesta.

**v1.2 — En desarrollo** (`versionCode 7`, `versionName 1.2.0`)
- [x] Changelog consultable dentro de la app (Configuración → Acerca de → Novedades).
- [ ] Gráfico semanal/mensual en historial.
- [ ] Widget de pantalla principal con botón rápido de registro.
- [ ] Export/import de datos en JSON.

**v1.3 — Cerrada** (`versionCode 9`, `versionName 1.3.0`)
- [x] Nombre de la app con exclamaciones: ¡Bebe agua! / Drink Water!
- [x] Enviar comentarios al autor por correo desde Ajustes → Acerca de.

**v1.4 (eventual)**
- [ ] Wear OS companion.
- [ ] Recordatorios "inteligentes" (saltarse el siguiente si has bebido más de la cuota esperada).

---

## 10. Branding y assets

- **Nombre**: ¡Bebe agua! (ES) / Drink Water! (EN). El repo, el package y el `rootProject.name` siguen siendo `Bebe Agua` / `bebeagua`.
- **Package**: `com.jjrapps.bebeagua`.
- **Paleta base**: azules acuáticos. Colores semilla en `Theme.kt`; Material 3 dynamic color en Android 12+.
- **Icono adaptativo**: gota estilizada sobre fondo claro.
- **Capturas para Play**: 5 capturas (Home, Home con muchas ingestas, Historial, Settings, Notificación).

---

## 11. Preguntas abiertas

Cosas a decidir antes/durante el desarrollo (Codex: si te topas con una de estas, **pregunta a Jorge** antes de inventar una respuesta):

1. ~~¿Acción de eliminar registro reduce el contador del día?~~ **Resuelto: sí, reduce el contador.**
2. ¿Los recordatorios deben silenciarse si el dispositivo está en modo "No molestar"? (Por defecto sí, no hacer override del DND.)
3. ~~¿Qué pasa si el usuario registra ingesta fuera de la ventana horaria?~~ **Resuelto: siempre suma al día local del timestamp.**
4. ¿Permitir registros con cantidades arbitrarias además de las medidas predefinidas? Recomendado: sí, vía un campo "Otra cantidad…" en el selector. Pendiente de implementar.
