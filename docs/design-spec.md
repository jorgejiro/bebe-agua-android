# design-spec.md — Bebe Agua

> Especificación visual de referencia para Claude Code. Leer junto a CLAUDE.md antes de implementar cualquier pantalla o componente de UI.

---

## 1. Estética general

**Dirección**: "agua mineral nocturna" — oscuro, limpio, sin ornamentos. Sensación de instrumento de precisión, no de app infantil o gamificada. Sin mascota, sin corazones, sin puntos, sin animaciones de celebración.

**Modo**: oscuro como base en v1. El tema claro puede añadirse en v1.1. El tema del sistema (auto) se soporta desde el principio vía `isSystemInDarkTheme()` pero la paleta diseñada es la oscura.

**Tipografía**:
- UI general: `DM Sans` (Google Fonts) — pesos 300, 400, 500
- Números (ml, horas, fechas): `DM Mono` — pesos 400, 500. Da sensación de medidor/instrumento.
- En Compose: declarar ambas como `FontFamily` en `Type.kt` usando `downloadableFonts` o bundleando los `.ttf` en `res/font/`.

---

## 2. Paleta de colores

Declarar en `ui/theme/Color.kt` con estos nombres exactos.

```kotlin
// Fondos
val BackgroundDeep    = Color(0xFF0B1829)   // fondo del "shell" / status bar
val BackgroundMain    = Color(0xFF0F2440)   // fondo de pantalla
val BackgroundCard    = Color(0xFF0A1D35)   // cards, listas, ring center
val BackgroundElement = Color(0xFF0D2E52)   // botones secundarios, chips, iconos
val BackgroundNav     = Color(0xFF0D1F38)   // barra de navegación superior

// Acentos principales
val AccentPrimary     = Color(0xFF1570B0)   // azul medio — arco de progreso base, FAB
val AccentLight       = Color(0xFF60C8F5)   // cian — punta del arco, números principales, cantidades
val AccentGlow        = Color(0xFF38BDF8)   // dot del pill "próximo recordatorio"

// Bordes
val BorderSubtle      = Color(0xFF1A3550)   // borde de cards y listas
val BorderDefault     = Color(0xFF1E4A72)   // borde de elementos interactivos
val BorderStrong      = Color(0xFF1E3554)   // borde exterior del shell / nav

// Texto
val TextPrimary       = Color(0xFFC8E8F8)   // texto principal sobre fondo oscuro
val TextSecondary     = Color(0xFF7AADCF)   // títulos de sección, tab activo, subtítulos
val TextMuted         = Color(0xFF3C6E96)   // subtexto, labels secundarios
val TextDim           = Color(0xFF2A5070)   // texto casi invisible, separadores, menú ⋮
val TextOnAccent      = Color(0xFFFFFFFF)   // texto sobre FAB y botones de acción
val TextOnAccentSoft  = Color(0xFFA8D8F0)   // subtexto sobre FAB (ej. "200 ml" bajo el +)

// Semánticos
val SuccessGreen      = Color(0xFF3ACF7A)   // check de objetivo alcanzado en historial
val SuccessBg         = Color(0xFF0E4A2A)   // fondo del chip de check
val SuccessBorder     = Color(0xFF1A8A50)
val WarnYellow        = Color(0xFF8FBF00)   // advertencia permiso alarma exacta
val WarnBg            = Color(0xFF1A2800)
val WarnBorder        = Color(0xFF3A5500)
```

Aplicar en `Theme.kt` con `darkColorScheme`:
```kotlin
val BebeAguaDarkColorScheme = darkColorScheme(
    primary          = AccentPrimary,
    onPrimary        = TextOnAccent,
    primaryContainer = BackgroundElement,
    secondary        = AccentLight,
    background       = BackgroundMain,
    surface          = BackgroundCard,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    outline          = BorderDefault,
)
```

---

## 3. Dimensiones y formas

```kotlin
// Shape.kt
val BebeAguaShapes = Shapes(
    small  = RoundedCornerShape(8.dp),    // chips, badges, iconos pequeños
    medium = RoundedCornerShape(14.dp),   // cards, grupos de settings, records list
    large  = RoundedCornerShape(20.dp),   // pills (próximo recordatorio)
    extraLarge = RoundedCornerShape(50)   // FAB circular y ring center
)
```

Espaciado base: múltiplos de 4dp. Padding lateral de pantalla: `16.dp` o `20.dp`.

---

## 4. Pantalla Home (`HomeScreen`)

### Layout (de arriba a abajo)
1. **Status bar** — gestionada por el sistema, fondo `BackgroundDeep`
2. **Barra de navegación** — 3 tabs: Casa · Historial · Ajustes
3. **Cuerpo** — fondo `BackgroundMain`, padding `20.dp` lateral

### 4.1 Barra de navegación superior
- Fondo: `BackgroundNav`
- Border bottom: `0.5.dp`, color `BorderStrong`
- Tab activo: texto `AccentLight`, indicador inferior `2.dp` color `AccentLight`, border-radius `2.dp` en los extremos superiores
- Tab inactivo: texto `TextMuted`
- Tab activo (Casa): punto circular `5.dp` de diámetro en `AccentLight` antes del texto
- Font: DM Sans 12sp, weight 500

### 4.2 Anillo de progreso circular
- Tamaño: `210.dp` × `210.dp`
- Pista de fondo: círculo stroke `9.dp`, color `BackgroundElement` con el arco lleno en `AccentPrimary` opacidad 30%
- Arco de progreso: gradiente lineal de `AccentPrimary` → `AccentLight`, `strokeCap = StrokeCap.Round`
- Punto inicial del arco (0%): círculo `5.5.dp` relleno `AccentPrimary` opacidad 50%
- Punto final del arco (progreso actual): círculo `5.5.dp` relleno `AccentLight` (brillante)
- Interior del anillo (`ring center`):
  - Fondo: `BackgroundCard`, forma circular
  - inset `22.dp` desde el borde del ring-wrap
  - Número consumido: DM Mono 32sp, weight 500, color `AccentLight`
  - Separador "/": incluido en la línea de total
  - Total objetivo: "/ 1500 ml", 12sp, color `TextMuted`, weight 400
  - Label: "objetivo diario", 9sp, uppercase, letter-spacing 1.2sp, color `TextDim`

### 4.3 Pill "Próximo recordatorio"
- Posición: centrado, debajo del anillo, margen top `10.dp`
- Fondo: `BackgroundElement`, borde `0.5.dp` `BorderDefault`, border-radius `20.dp`
- Padding: `5.dp` vertical, `14.dp` horizontal
- Contenido: dot `6.dp` `AccentGlow` · texto "Próximo recordatorio" `TextMuted` 11sp · hora `TextSecondary` 11sp weight 500

### 4.4 Área del FAB
- Layout: `Row`, centrado, gap `16.dp`, padding horizontal `16.dp`
- El FAB ocupa el centro; a su derecha el botón de cambiar medida; a su izquierda un spacer del mismo tamaño que el botón de cambiar medida (para centrar el FAB ópticamente)

**FAB principal (añadir ingesta)**
- Tamaño: `104.dp` × `104.dp`, forma circular
- Fondo: gradiente lineal `135°` de `Color(0xFF1A7FD4)` → `Color(0xFF0E4A8A)`
- Anillo exterior visible: `box-shadow` equivalente en Compose → `border` de `6.dp` con dos capas:
  - Capa 1 (separación): `4.dp` color `BackgroundMain` (crea el hueco visual)
  - Capa 2 (anillo): `2.dp` color `AccentPrimary`
  - Implementar con `Modifier.border(2.dp, AccentPrimary, CircleShape).padding(4.dp).background(BackgroundMain, CircleShape).padding(4.dp)`... o más limpio: dibujar el anillo en `Canvas` / con `drawWithContent`.
- Contenido centrado en columna:
  - Símbolo `+`: DM Sans 38sp, weight 300, color blanco, `lineHeight = 1`
  - Cantidad: "200 ml" (la última usada), DM Mono 12sp, weight 500, color `TextOnAccentSoft`
- Al pulsar: ripple circular blanco con alpha 20%, animación `scale 1.0 → 0.94 → 1.0` (spring)

**Botón cambiar medida**
- Tamaño: `52.dp` × `52.dp`
- Fondo: `BackgroundElement`, borde `0.5.dp` `BorderDefault`, border-radius `16.dp`
- Icono: SVG de dos círculos con flechas arriba/abajo (ver assets), 20×20dp
- Label: "medida", 9sp, color `TextMuted`, debajo del icono
- Al pulsar: abre `BottomSheet` con la lista de tamaños disponibles

### 4.5 Lista "Registros de hoy"
- Header: texto "Registros de hoy" `TextSecondary` 13sp weight 500 · botón `+` a la derecha (`22.dp`, `BackgroundElement`, border-radius `6.dp`)
- Contenedor: `BackgroundCard`, borde `0.5.dp` `BorderSubtle`, border-radius `14.dp`, overflow hidden
- Separadores entre items: `0.5.dp` `BorderSubtle`

**Item: próximo recordatorio** (siempre el primero)
- Fondo: `Color(0xFF0B2540)` (ligeramente más claro que card para distinguirlo)
- Icono reloj: `BackgroundCard`, stroke `TextDim`
- Hora: `TextMuted`
- Sublabel: "próximo recordatorio", `TextMuted` 11sp
- Cantidad: "— ml", color `TextDim`

**Item: ingesta registrada**
- Icono gota: `BackgroundElement`, stroke `AccentLight`, fill `AccentLight` 15% opacity
- Hora: `TextPrimary` 13sp weight 500
- Sublabel: "vaso de agua" (o el nombre del tamaño), `TextMuted` 11sp
- Cantidad: DM Mono 13sp weight 500 `AccentLight`
- Botón overflow `⋮`: color `TextDim`, abre menu con opción "Eliminar"

---

## 5. Pantalla Historial (`HistoryScreen`)

### 5.1 Navegación de mes
- Row con flechas `‹` `›` a los lados y el mes en el centro
- Flechas: `28.dp`, `BackgroundElement`, borde `BorderDefault`, border-radius `8.dp`
- Mes: `TextSecondary` 14sp weight 500

### 5.2 Mini gráfico de barras semanal
- Grid de 7 columnas iguales, altura total `72.dp`
- Barras alineadas al fondo, border-radius superior `4.dp`
- Colores:
  - Objetivo alcanzado: `AccentPrimary`
  - No alcanzado (parcial): `BackgroundElement`
  - Hoy: `AccentLight`
  - Sin datos: `BackgroundElement` opacidad 40%
- Número de día debajo de cada barra: 9sp `TextMuted` (hoy: `AccentLight`)

### 5.3 Tarjetas de resumen
- Grid 2 columnas, gap `8.dp`
- Cada tarjeta: `BackgroundCard`, borde `BorderSubtle`, border-radius `12.dp`, padding `12.dp` × `14.dp`
- Label: 10sp uppercase letter-spacing `0.8sp` `TextMuted`
- Valor: DM Mono 20sp weight 500 `AccentLight`
- Sub-valor: 10sp `TextDim`

### 5.4 Lista de días
- Cards individuales: `BackgroundCard`, borde `BorderSubtle`, border-radius `12.dp`
- Hoy: borde `AccentPrimary` `0.5.dp`
- Columna fecha: label día (10sp `TextMuted`) + número y mes (13sp weight 500 `TextPrimary`)
- Barra de progreso inline: altura `6.dp`, fondo `BackgroundElement`, fill `AccentPrimary` (hoy: `AccentLight`), border-radius `3.dp`
- Cantidad: DM Mono 12sp `AccentLight`
- Check objetivo alcanzado: círculo `16.dp`, fondo `SuccessBg`, borde `SuccessBorder`, checkmark `SuccessGreen`
- No alcanzado: círculo `16.dp` vacío, `BackgroundElement`, borde `BorderDefault`

---

## 6. Pantalla Ajustes (`SettingsScreen`)

### 6.1 Estructura general
- Fondo: `BackgroundMain`
- Secciones separadas visualmente, cada una con:
  - Label de sección: 9sp uppercase letter-spacing `1.2sp` `TextDim`, margen bottom `6.dp`
  - Grupo: `BackgroundCard`, borde `BorderSubtle`, border-radius `14.dp`, overflow hidden
  - Separadores internos: `0.5.dp` `BorderSubtle`

### 6.2 Fila de ajuste estándar (`SettingsRow`)
- Padding: `12.dp` vertical, `14.dp` horizontal
- Icono: `28.dp` × `28.dp`, `BackgroundElement`, border-radius `8.dp`, icono `14.dp` × `14.dp` stroke `TextMuted`
- Label: 13sp weight 500 `TextPrimary`
- Sublabel (opcional): 10sp `TextMuted`
- Valor actual: DM Mono 12sp `AccentLight`, fondo `BackgroundElement`, borde `BorderDefault`, border-radius `8.dp`, padding `4.dp` × `10.dp`
- Chevron `›`: `TextDim` 14sp

### 6.3 Toggle
- Track: `36.dp` × `20.dp`, border-radius `10.dp`
- Activado: fondo `AccentPrimary`, thumb blanco `14.dp` a la derecha
- Desactivado: fondo `BorderStrong`, thumb a la izquierda

### 6.4 Chips de tamaños de ingesta
- Dentro del grupo, con padding `10.dp` × `14.dp`
- Chip inactivo: `BackgroundElement`, borde `BorderDefault`, border-radius `20.dp`, DM Mono 12sp `TextSecondary`
- Chip activo (el seleccionado como defecto): fondo `AccentPrimary`, borde `AccentPrimary`, texto blanco
- Chip "añadir": borde dashed `BorderDefault`, texto `TextDim`, fondo transparente

### 6.5 Sección de recordatorios
- Header con icono reloj + "Recordatorios al día" + badge DM Mono del número actual
- Slider: track `4.dp`, fondo `BackgroundElement`, fill `AccentPrimary`, thumb `14.dp` `AccentLight`, borde `2.dp` `BackgroundMain`
- Labels del slider: "menos" / "más", 10sp `TextMuted`
- Lista de horas calculadas: DM Mono 13sp `TextSecondary` + cantidad sugerida 11sp `TextMuted` + botón "editar" 10sp `TextDim`

### 6.6 Banner de advertencia de permisos
- Aparece solo si el permiso `SCHEDULE_EXACT_ALARM` está denegado
- Fondo `WarnBg`, borde `WarnBorder`, border-radius `10.dp`
- Dot `6.dp` `WarnYellow` · texto 11sp `WarnYellow` · botón "Activar": 10sp `Color(0xFFB8E000)`, fondo `Color(0xFF2A3E00)`, borde `Color(0xFF4A6A00)`, border-radius `6.dp`

---

## 7. Componentes reutilizables

| Componente | Descripción |
|---|---|
| `ProgressRing` | Anillo SVG/Canvas con gradiente, punto inicial y punto de progreso |
| `NextReminderPill` | Pill con dot, texto y hora |
| `AddIntakeFab` | FAB circular con anillo exterior, `+` y cantidad |
| `ChangeSizeFab` | Botón cuadrado con icono flechas y label |
| `IntakeRecord` | Item de lista con icono, hora, sublabel, cantidad y overflow |
| `UpcomingRecord` | Variante del item anterior para el próximo recordatorio |
| `DayRow` | Fila de historial con fecha, barra de progreso y check |
| `SettingsRow` | Fila de ajuste con icono, labels, valor y chevron |
| `SettingsToggleRow` | Variante de SettingsRow con toggle |
| `SizeChip` | Chip de tamaño de ingesta, activo/inactivo/añadir |
| `SectionLabel` | Label de sección en ajustes (uppercase, muted) |
| `PermissionWarningBanner` | Banner amarillo de permiso denegado |
| `WeekBarChart` | Mini gráfico de barras semanal para historial |

---

## 8. Notificación del sistema

**Canal**: `reminders`, importancia `IMPORTANCE_DEFAULT` (suena, no hace vibración larga).

**Layout de la notificación**:
- Título: "¿Has bebido agua?" (ES) / "Time to drink water!" (EN)
- Texto: "Llevas X ml de Y ml hoy" / "You've had X ml of Y ml today"
- Icono pequeño: gota monocroma (vector drawable, color blanco sobre transparente)
- Icono grande: icono de la app

**Acciones**:
1. Acción primaria "Beber X ml" — registra la ingesta por defecto y cancela la notificación
2. Acción secundaria "Posponer 15 min" — reprograma para 15 minutos después

---

## 9. Transiciones y animaciones

- Tap en FAB: `scale` spring `0.94` → `1.0`, duración ~150ms
- Aparición de items en lista: `fadeIn` + `slideInVertically` staggered (delay 40ms por item)
- Cambio de progreso en el anillo: animación `animateFloatAsState` con `spring(dampingRatio = 0.8f)`
- Navegación entre tabs: sin animación de shared element en v1, transición `fadeIn/fadeOut` simple
- BottomSheet de tamaños: estándar Material 3 (`ModalBottomSheet`)

---

## 10. Assets necesarios

Pendiente de crear (sugerencia: usar `ImageVector` en Compose o vectores en `res/drawable/`):

| Asset | Formato sugerido | Notas |
|---|---|---|
| Icono app (adaptativo) | `ic_launcher_foreground.xml` | Gota estilizada cian sobre fondo transparente |
| Icono notificación | `ic_notification.xml` | Gota monocroma blanca, tamaño 24dp |
| Icono gota (lista) | `ImageVector` o `ic_drop.xml` | Con fill semitransparente |
| Icono cambiar medida | `ImageVector` | Dos círculos con flechas arriba/abajo |
| Icono reloj | `ImageVector` | Para próximo recordatorio y ajustes |
| Icono escudo | `ImageVector` | Para sección de permisos en ajustes |
| Icono sol/luna | `ImageVector` | Para ajuste de tema |
| Icono idioma | `ImageVector` | Para ajuste de idioma |
