# Publicación en F-Droid

F-Droid no admite subir APK: compila la app desde este repositorio con una **receta** que vive en
[`fdroiddata`](https://gitlab.com/fdroid/fdroiddata), y la firma con su propia clave. Publicar es
abrir un merge request allí con la receta.

| Pieza | Dónde |
|---|---|
| Receta, validada con `fdroid lint` y en el formato de `fdroid rewritemeta` | [`com.jjrapps.bebeagua.yml`](com.jjrapps.bebeagua.yml) |
| Ficha: título, descripciones, changelog, icono, gráfico y capturas | `fastlane/metadata/android/{en-US,es-ES}/` — F-Droid la lee del repositorio |

## Enviar la app

1. En el fork [`jorgejiro/fdroiddata`](https://gitlab.com/jorgejiro/fdroiddata), crear una rama
   `com.jjrapps.bebeagua` desde `fdroid/fdroiddata:master` y añadir la receta como
   `metadata/com.jjrapps.bebeagua.yml`. No hace falta clonar (el repo pesa varios GB): basta con la
   API de GitLab o `glab`.
2. Commit: `New app: Drink Water!`.
3. Abrir el merge request contra `fdroid/fdroiddata:master` y rellenar la checklist de la plantilla.
   La CI del merge request compila la app: si falla, el log dice por qué.
4. Contestar a los revisores. La revisión la hacen voluntarios y puede tardar semanas.

## Lo que hay que saber

- **La firma es la de F-Droid, no la tuya.** Quien instala desde Play no puede actualizar desde
  F-Droid, ni al revés, sin desinstalar antes. Para firmar con la clave propia hacen falta builds
  reproducibles, y es un trabajo aparte.
- **La receta de la 1.3.1 apunta al commit del tag `v1.3.1`, no al de la versión.** El commit de
  la versión es anterior a quitar lo que F-Droid rechaza: la firma obligatoria (sin
  `keystore.properties` el build fallaba), el bloque de dependencias cifrado para Google
  (`dependenciesInfo`) y el plugin foojay. Por eso `v1.3.1` se puso sobre ese commit. La app es la
  misma: solo cambia la configuración del build. Sin ese tag, `checkupdates` falla en la CI del
  merge request: el último tag tendría un `versionCode` menor que el de la receta.
- **Mientras el MR de alta siga abierto, la receta lleva un solo build: el de la última versión.**
  Al actualizar el MR a una versión nueva, se sustituye el build anterior en vez de añadir otro
  (el revisor lo pidió en !50455: «Remove the old version»).
- **Las versiones siguientes se publican solas.** Con `UpdateCheckMode: Tags` y
  `AutoUpdateMode: Version`, F-Droid detecta cada tag `vX.Y.Z` nuevo, lee `versionCode` y
  `versionName` de `app/build.gradle.kts` y añade el build. Basta con etiquetar cada release.
- **El changelog de cada versión** va en `fastlane/metadata/android/<idioma>/changelogs/<versionCode>.txt`,
  con un máximo de 500 caracteres. Sirve el mismo texto que las notas de Play
  (`docs/play-release-notes.md`).
- **La ficha de fastlane es una copia de la de Play** (`docs/play-store-publication-texts.md` y
  `docs/store-assets/capturas/<idioma>/telefono/`). Si cambian los textos o las capturas, hay que
  volver a copiarlos aquí.
- **Nada en el build puede descargar herramientas por su cuenta.** El escáner de F-Droid rechaza el
  plugin `foojay-resolver` y borra `gradle-daemon-jvm.properties` antes de compilar. Su servidor
  (Debian trixie) ya trae JDK 21, así que no hace falta pedirlo.
