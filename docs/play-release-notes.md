# Novedades para Google Play — ¡Bebe agua!

Textos de **«Novedades»** («What's new») listos para copiar y pegar en Play Console al crear la
release: *Producción → Crear nueva versión → Notas de la versión*, una pestaña por idioma
(`es-ES` y `en-US`).

- **Límite de Google Play: 500 caracteres por idioma.** Cada bloque de abajo indica los que ocupa.
- La ficha permanente (nombre, descripciones, capturas, privacidad, cuestionarios) está en
  [`play-store-publication-texts.md`](play-store-publication-texts.md). Este archivo es solo el
  texto que cambia en cada publicación.
- Las viñetas salen del `CHANGELOG.md` y de los `string-array` `changelog_*`, pero **no son el mismo
  texto**: aquí se escribe para alguien que aún no tiene la versión, así que se omite lo interno y se
  añade el recordatorio de que la app sigue sin cuentas ni seguimiento.
- Al publicar una versión nueva, añade su bloque arriba y deja los anteriores como historial.
- **Cada bloque lleva SIEMPRE tres subsecciones**, en este orden: `### es-ES (N caracteres)`,
  `### en-US (N caracteres)` y `### Formato con etiquetas de idioma`. La tercera repite los dos
  textos envueltos en `<es-ES>` y `<en-US>` dentro de un único bloque, que es el formato que Play
  Console acepta de una sola pegada para todos los idiomas. Sin ella hay que copiar idioma por
  idioma, así que un bloque con solo las dos primeras está incompleto.

---

## 1.3.1 (versionCode 11) — 2026-08-21

### es-ES (388 caracteres)

```text
Novedades de la versión 1.3.1

• Los recordatorios ahora vibran en vez de sonar. Así puedes dejar el móvil en modo sonido, sin perderte llamadas, y el aviso de beber se sigue notando (también en el reloj, si tienes uno emparejado).
• Nueva fila «Ajustes de notificación» en Ajustes → Permisos, por si prefieres devolverle el sonido.

Sin cuentas, sin nube, sin anuncios y sin seguimiento.
```

### en-US (368 caracteres)

```text
What's new in 1.3.1

• Reminders now vibrate instead of making a sound. You can leave your phone in ring mode, so you do not miss calls, and the reminder still gets through — on your watch too, if you have one paired.
• New "Notification settings" row in Settings → Permissions, in case you would rather put the sound back.

No accounts, no cloud, no ads, no tracking.
```

### Formato con etiquetas de idioma

```xml
<es-ES>
Novedades de la versión 1.3.1

• Los recordatorios ahora vibran en vez de sonar. Así puedes dejar el móvil en modo sonido, sin perderte llamadas, y el aviso de beber se sigue notando (también en el reloj, si tienes uno emparejado).
• Nueva fila «Ajustes de notificación» en Ajustes → Permisos, por si prefieres devolverle el sonido.

Sin cuentas, sin nube, sin anuncios y sin seguimiento.
</es-ES>
<en-US>
What's new in 1.3.1

• Reminders now vibrate instead of making a sound. You can leave your phone in ring mode, so you do not miss calls, and the reminder still gets through — on your watch too, if you have one paired.
• New "Notification settings" row in Settings → Permissions, in case you would rather put the sound back.

No accounts, no cloud, no ads, no tracking.
</en-US>
```

---

## 1.3.0 (versionCode 10) — 2026-08-11

### es-ES (327 caracteres)

```text
Novedades de la versión 1.3.0

• La app ahora se llama «¡Bebe agua!».
• Nuevo «Enviar comentarios» en Ajustes → Acerca de: abre tu app de correo para escribir al autor, con la versión instalada ya en el asunto. Si algo no funciona o echas algo en falta, cuéntalo por ahí.

Sin cuentas, sin nube, sin anuncios y sin seguimiento.
```

### en-US (308 caracteres)

```text
What's new in 1.3.0

• The app is now called Drink Water!
• New "Send feedback" in Settings → About: it opens your email app to write to the author, with the installed version already in the subject. If something is broken or missing, that is the place to say so.

No accounts, no cloud, no ads, no tracking.
```

### Formato con etiquetas de idioma

```xml
<es-ES>
Novedades de la versión 1.3.0

• La app ahora se llama «¡Bebe agua!».
• Nuevo «Enviar comentarios» en Ajustes → Acerca de: abre tu app de correo para escribir al autor, con la versión instalada ya en el asunto. Si algo no funciona o echas algo en falta, cuéntalo por ahí.

Sin cuentas, sin nube, sin anuncios y sin seguimiento.
</es-ES>
<en-US>
What's new in 1.3.0

• The app is now called Drink Water!
• New "Send feedback" in Settings → About: it opens your email app to write to the author, with the installed version already in the subject. If something is broken or missing, that is the place to say so.

No accounts, no cloud, no ads, no tracking.
</en-US>
```

---

## 1.2.1 (versionCode 8) — 2026-08-04

### es-ES (275 caracteres)

```text
Novedades de la versión 1.2.1

• Corregido: el widget de escritorio se quedaba cargando indefinidamente al añadirlo y el toque no registraba nada. Ya se muestra correctamente y un toque registra tu cantidad por defecto.

Sin cuentas, sin nube, sin anuncios y sin seguimiento.
```

### en-US (224 caracteres)

```text
What's new in 1.2.1

• Fixed: the home screen widget was stuck on a loading spinner when added and taps did nothing. It now renders correctly and one tap logs your default amount.

No accounts, no cloud, no ads, no tracking.
```

### Formato con etiquetas de idioma

```xml
<es-ES>
Novedades de la versión 1.2.1

• Corregido: el widget de escritorio se quedaba cargando indefinidamente al añadirlo y el toque no registraba nada. Ya se muestra correctamente y un toque registra tu cantidad por defecto.

Sin cuentas, sin nube, sin anuncios y sin seguimiento.
</es-ES>
<en-US>
What's new in 1.2.1

• Fixed: the home screen widget was stuck on a loading spinner when added and taps did nothing. It now renders correctly and one tap logs your default amount.

No accounts, no cloud, no ads, no tracking.
</en-US>
```

---

## 1.2.0 (versionCode 7) — 2026-07-28

### es-ES (468 caracteres)

```text
Novedades de la versión 1.2.0

• Nuevo widget de escritorio de 1x1: un toque registra tu cantidad por defecto sin abrir la app.
• Nueva pantalla «Novedades» en Ajustes → Acerca de, con los cambios de cada versión.
• Ajustes muestra la versión instalada.
• Nuevos valores por defecto al instalar: objetivo de 2400 ml, franja de 08:00 a 21:00 y 14 recordatorios al día. Si ya usabas la app, tus ajustes no cambian.

Sin cuentas, sin nube, sin anuncios y sin seguimiento.
```

### en-US (414 caracteres)

```text
What's new in 1.2.0

• New 1x1 home screen widget: one tap logs your default amount without opening the app.
• New "What's new" screen in Settings → About, with the changes in every version.
• Settings now shows the installed version.
• New defaults on install: 2400 ml goal, an 08:00-21:00 window and 14 reminders a day. If you already use the app, your settings stay.

No accounts, no cloud, no ads, no tracking.
```

### Formato con etiquetas de idioma

Es el formato que espera la API de publicación (`releaseNotes`) y sirve si automatizas la subida en
vez de pegar en cada pestaña:

```xml
<es-ES>
Novedades de la versión 1.2.0

• Nuevo widget de escritorio de 1x1: un toque registra tu cantidad por defecto sin abrir la app.
• Nueva pantalla «Novedades» en Ajustes → Acerca de, con los cambios de cada versión.
• Ajustes muestra la versión instalada.
• Nuevos valores por defecto al instalar: objetivo de 2400 ml, franja de 08:00 a 21:00 y 14 recordatorios al día. Si ya usabas la app, tus ajustes no cambian.

Sin cuentas, sin nube, sin anuncios y sin seguimiento.
</es-ES>
<en-US>
What's new in 1.2.0

• New 1x1 home screen widget: one tap logs your default amount without opening the app.
• New "What's new" screen in Settings → About, with the changes in every version.
• Settings now shows the installed version.
• New defaults on install: 2400 ml goal, an 08:00-21:00 window and 14 reminders a day. If you already use the app, your settings stay.

No accounts, no cloud, no ads, no tracking.
</en-US>
```

---

## 1.0.1 (versionCode 2) — 2026-05-06, primera publicación

Se conserva como referencia del tono usado en la publicación inicial.

### es-ES

```text
• Registro rápido de la ingesta diaria de agua.
• Recordatorios configurables dentro de tu franja horaria.
• Historial de los últimos días y estadísticas básicas.
• Acciones rápidas desde la notificación.
• Español e inglés.
```

### en-US

```text
• Quick daily water intake logging.
• Configurable reminders within your chosen time window.
• Recent history and basic statistics.
• Quick actions from notifications.
• Spanish and English.
```
