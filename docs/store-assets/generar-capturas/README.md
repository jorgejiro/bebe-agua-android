# Generar las capturas de la ficha de Play

Automatiza el juego completo de capturas: **siete escenas × dos idiomas × tres formatos = 42 imágenes**,
en `docs/store-assets/capturas/<idioma>/<formato>/`.

**El idioma manda en el árbol y el formato va dentro** (`capturas/es/telefono/`), porque la ficha de Play
se sube idioma a idioma: así los tres formatos de un idioma están juntos y se puede actualizar un idioma
sin tocar el otro.

Está aquí versionado porque estas capturas hay que rehacerlas cada vez que cambie una pantalla, y hacerlas
a mano son 42 secuencias de navegación con el riesgo de que se cuele una en el idioma equivocado.

Es un puerto del mismo pipeline de «¡Aquí hay tomate!», con las escenas y la siembra de esta app.

## Uso

Con un emulador arrancado y **el APK debug instalado** —no el de release: la siembra de la base de datos
se copia con `run-as`, y `run-as` solo funciona con una app debuggable. Con el APK de release el pase
entero se completa sin un solo error y las catorce capturas salen con la app a 0 ml:

```bash
python3 sembrar_historial.py                  # una vez: la base de datos con historial
python3 capturar.py telefono                  # las 14 capturas del dispositivo conectado (7 × 2 idiomas)
python3 revisar.py                            # al final: control de las 42 antes de subirlas
```

O de un tirón los tres formatos, arrancando y apagando cada emulador por su cuenta:

```bash
./todos.sh
```

**Pasa siempre `revisar.py` antes de subir.** Comprueba las dimensiones exactas, que `es` y `en` no sean
idénticas —un idioma colado—, que ninguna escena esté a medio pintar y que estén las 42. Devuelve código
de salida 1 si algo falla, así que sirve tal cual en un script.

`capturar.py` recibe el **formato** (`telefono`, `tablet-7-pulgadas`, `tablet-10-pulgadas`) y reparte las
capturas en `../capturas/<idioma>/<formato>/`; con `--capturas=DIR` se cambia la raíz.

`capturar.py --sin-widget` salta la colocación del widget, para cuando ya está puesto en el escritorio.

Los tres emuladores se crearon a mano en `~/.android/avd` (no hay `avdmanager` instalado), con las
resoluciones que Play exige. **Estas medidas no salen del AVD**: los `config.ini` dicen otra cosa, y las
de verdad las fija `capturar.py` con `wm size` y `wm density` en cada pase —ver `PANTALLAS`—, para no
depender de lo que haya quedado guardado en el emulador:

| AVD | Resolución | Densidad | En dp | Aspecto |
|---|---|---|---|---|
| `Medium_Phone` | 1080 × 2400 | 420 | 411 × 914 | 9:20 |
| `Tablet7` | 1080 × 1920 | 288 | 600 × 1067 | **9:16** |
| `Tablet10` | 1440 × 2560 | 288 | 800 × 1422 | **9:16** |

Play pide **9:16 exacto** para las capturas de tablet, y lados de 320–3840 px en la de 7" y de
1080–7680 px en la de 10". Las densidades de 288 dpi no son casualidad: dejan la tablet pequeña en 600 dp
de ancho y la grande en 800 dp, que son los dos umbrales con los que Android decide que algo es una
tablet.

## Las siete escenas

| Fichero | Qué enseña |
|---|---|
| `01-registrar-agua` | La pantalla principal: el anillo a media asta y los registros del día |
| `02-elegir-medida` | El selector de cantidad, abierto desde el botón secundario |
| `03-historial` | Los últimos días, con la racha y las medias |
| `04-objetivo-y-recordatorios` | Ajustes desde arriba: objetivo, franja horaria, recordatorios y horarios |
| `05-medidas-y-permisos` | El final de Ajustes: medidas, idioma, permisos y Acerca de |
| `06-recordatorio-en-la-notificacion` | El aviso en la sombra, con sus dos acciones rápidas |
| `07-widget-en-el-escritorio` | El widget de 1×1 en la pantalla de inicio |

## Lo que hace y por qué

- **`sembrar_historial.py`** — 45 días de ingestas en un `bebe_agua.db` que se copia con `run-as`. Con una
  instalación nueva el Historial sale vacío y la principal marca 0 ml. Copia el `identityHash` del
  esquema exportado, sin el cual Room se niega a abrir la base. Deja **hoy por debajo del objetivo**, que
  es lo que pone el anillo a media asta y lo que hace que el recordatorio se envíe.
- **`ui.py`** — localiza los elementos por texto con `uiautomator dump` y toca su centro. Tocar por
  coordenadas falla la mitad de las veces y además no sobrevive al cambio de resolución.
- **`tanda.py`** — las seis primeras escenas en un idioma, las cinco de la app y la de la notificación. El
  idioma se pone por la pantalla de Ajustes,
  no con `cmd locale set-app-locales`: la app aplica su propio ajuste al arrancar y sobreescribe el del
  sistema.
- **`capturar.py`** — orquesta las dos tandas y coloca el widget arrastrándolo, que es la única forma: no
  hay orden de `adb` que fije un widget en el escritorio.

## Detalles que costaron una iteración cada uno

- **La notificación se dispara moviendo el reloj**, con `cmd alarm set-time` hasta el `origWhen` de la
  alarma pendiente. Un `am broadcast` contra `ReminderReceiver` **no vale**: el receptor no está
  exportado, así que el broadcast se encola y no se entrega nunca. Y no da error, simplemente no pasa
  nada; se vio comparando la alarma programada antes y después, que no se movía.
- **Y el reloj no se puede dejar fijo.** Se intentó poner una hora bonita e igual para los dos idiomas:
  el emulador vuelve a sincronizar con el reloj del Mac a los pocos segundos, incluso con `auto_time` y
  `auto_time_zone` a 0, y `set-time` no protesta —el cambio se deshace solo—. Da igual para lo que
  importa, porque el adelanto solo tiene que durar lo justo para que la alarma salte y la notificación
  quede publicada; lo único que se fija es el **formato de 24 horas**, que en la ficha española sí canta.
- **Esperar por contenido, nunca por tiempo.** Con un `sleep` fijo las capturas salen a medio pintar, y
  una tablet no tarda lo mismo que un teléfono en componer.
- **Y esperar por una subcadena no es esperar.** «Inicio» es la pestaña de la pantalla principal **y**
  está dentro de «Hora de inicio», en Ajustes: sin `exacto=True` el script toca la fila equivocada. Las
  pestañas se tocan siempre con coincidencia exacta.
- **Una captura mala no da error**, sale negra y se sube. De ahí `revisar.py`.
- **Fuera el `-wal` al sembrar.** Sustituir el fichero principal de una base SQLite sin borrar el diario
  deja a Room aplicando por encima un WAL que no corresponde, y la pantalla sale a cero.
- **`pm clear` del launcher antes de colocar el widget.** No hay orden de `adb` que borre un widget, así
  que sin esto se acumulan los de los pases anteriores y quedan además los que el launcher trae de
  fábrica.
- **El widget va en la segunda página del escritorio.** En la primera, el launcher pinta «At a glance»
  con la fecha en el idioma del sistema, que no se puede cambiar sin root, y saldría en inglés en la
  ficha española.
- **El widget colocado no tiene texto**: es el icono con el «+», así que se localiza por su
  `content-desc`, que el launcher rellena con la etiqueta del proveedor («Log a drink»).
- **La bandeja de widgets habla el idioma del sistema, no el de la app.** Se busca «Drink Water» aunque
  la tanda sea la española.
- **El long press necesita que el dedo se quede quieto de verdad.** `input swipe` con origen y destino
  iguales no arrastra: hay que usar `input motionevent` con esperas dentro del dispositivo.
- **Este widget no se queda en blanco.** No pinta datos, así que —al revés que el de «¡Aquí hay
  tomate!»— no hay que reabrir la app antes de ir al escritorio.
- **La sombra se vacía antes de disparar el recordatorio.** El emulador trae de fábrica el aviso «Set a
  screen lock» del centro de seguridad, y en la captura salía justo debajo del nuestro, como si fuera
  parte de la app.
- **Del marco de redimensión se sale con HOME**, no tocando un hueco. El toque a ciegas en la parte de
  abajo caía en el buscador de la tablet y lo abría, y con el buscador delante el widget «no aparecía» en
  la jerarquía: el pase lo dejaba en la primera página, la del «At a glance» y el Calendar con su
  «Sign in».
- **Y hay que esperar al widget recién soltado.** En la tablet de 7" el launcher tarda en asentarlo, así
  que la búsqueda inmediata tampoco lo encontraba.
- **El menú del escritorio tarda más de dos segundos en la tablet de 10".** Con la comprobación inmediata
  el pase moría en «no encontrado: 'Widgets'» aunque el hueco del long press fuera bueno.
- **El widget se suelta algo por encima de la mitad de la pantalla.** A un tercio de la altura, la tablet
  de 10" lo encajaba en la primera fila de la rejilla, pegado a la barra de estado.
- **Cada tanda cabe de sobra en diez minutos, el pase entero no.** De ahí `--solo-preparar`,
  `--idioma=es|en` y `--solo-widget`: sirven para trocear el trabajo cuando quien lanza el script tiene
  un límite de tiempo por orden, y para rehacer una sola escena sin repetir las otras trece.
- **Con una sola medida el selector de cantidad no cuenta nada.** La app trae `[200 ml]` de fábrica, así
  que la preparación añade 330 y 500 desde Ajustes, por la interfaz: son cuatro toques y así no hay que
  replicar el `preferences_pb` del DataStore, que cambiaría en cuanto se toque `SettingsDataSource`.

## Limitación conocida

El **sistema** del emulador está en inglés y no se puede cambiar sin root, así que en las capturas
españolas la barra de estado, los ajustes rápidos y el nombre de la app **dentro de la notificación**
salen en inglés («Drink Water!»), aunque el texto del aviso y la app estén en español. Es lo mismo que
pasa en «¡Aquí hay tomate!». Si algún día molesta, la vía es arrancar el emulador con
`-prop persist.sys.locale=es-ES` y hacer un pase por idioma, con el coste de arrancar seis veces.
