# Publicación en F-Droid

## Objetivo

Dejar ¡Bebe agua! lista para entrar en el catálogo principal de F-Droid: el repositorio cumple sus
requisitos, la receta para `fdroiddata` está escrita y validada, y el merge request preparado.

## Por qué

La app es MIT, no usa red ni servicios de Google, y el público de F-Droid es justo el que valora eso.
Se replica lo ya hecho con Sleep Noise (`fdroiddata!50449`).

## Alcance y restricciones

- F-Droid compila desde el código y **firma con su clave**. Una instalación de Play y una de F-Droid
  no pueden actualizarse entre sí. Sin builds reproducibles en esta primera entrada.
- El merge request a `gitlab.com/fdroid/fdroiddata` se abre con `glab` (usuario `jorgejiro`), desde
  el fork `jorgejiro/fdroiddata`, solo tras confirmación de Jorge.
- TDD: no aplica (cambios de build y metadatos). Checks: `./gradlew lint test`, `assembleRelease`
  sin `keystore.properties` y `fdroid lint` sobre la receta.

## Tareas

- [x] T1 · Build apto para F-Droid: `signingConfig` opcional (compila sin `keystore.properties`),
  `dependenciesInfo` fuera del APK/AAB y sin el plugin foojay. Ruta: inline (2 ficheros mecánicos).
- [x] T2 · Metadatos fastlane en `fastlane/metadata/android/{en-US,es-ES}`: título, descripciones,
  changelog del versionCode 11, icono, gráfico y capturas. Ruta: delegada (lectura de la ficha de
  Play + copias).
- [x] T3 · Receta `docs/fdroid/com.jjrapps.bebeagua.yml`, validada con `fdroid lint`, guía
  `docs/fdroid/LEEME.md` y nota en `CLAUDE.md`. Ruta: inline.
- [x] T4 · Rama `com.jjrapps.bebeagua` en el fork de `fdroiddata` con la receta y merge request.
  Ruta: inline (`glab`).

## Progreso

- T1 · `272aca3`. `assembleRelease` sin `keystore.properties` genera `app-release-unsigned.apk`;
  `./gradlew lint test` en verde.
- T2 · `940e3a1`. Textos dentro de los límites (corta 64/75, larga 1174/1404, changelog 368/388).
  Sin `icon.png` ni `featureGraphic.png`: no existen en el repo (son opcionales; F-Droid toma el
  icono del APK). La descripción ES se copió tal cual de la ficha de Play, que no lleva tildes.
- T3 · `b42af6f`. `fdroid lint` (fdroidserver 2.4.5, con `config/categories.yml` de fdroiddata)
  sin avisos; `fdroid rewritemeta` no cambia nada. Categorías: Habit Tracker, Sports & Health.
- T4 · `main` subido a GitHub. Rama `com.jjrapps.bebeagua` del fork `jorgejiro/fdroiddata`
  (desde su `master` `3db245b`), commit `6df8a1a` `New app: Drink Water!` con la receta idéntica a
  la del repo.
- Sin verificar: el build real en la CI de F-Droid.

## Siguiente paso

Merge request abierto el 2026-09-28: https://gitlab.com/fdroid/fdroiddata/-/merge_requests/50455.
Firma de F-Droid, sin builds reproducibles. Falta que pase la CI (al abrirlo aún no había pipeline)
y la revisión.
