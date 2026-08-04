# 003 — Reglas R8 para la reflexión de Glance y WorkManager

- **Fecha**: 2026-08-04
- **Estado**: aceptada

## Contexto

En la 1.2.0 publicada en Play, el widget de escritorio se quedaba indefinidamente en el
layout de carga (spinner en Nova Launcher, placeholder de recarga en Pixel Launcher) nada
más colocarlo. En debug funcionaba perfectamente, así que nunca se vio durante el
desarrollo: solo falla con `isMinifyEnabled = true`.

Logcat de la build minificada al colocar el widget:

```
E WM-InputMerger: java.lang.InstantiationException:
    java.lang.Class<androidx.work.OverwritingInputMerger> has no zero argument constructor
E WM-WorkerWrapper: Could not create Input Merger androidx.work.OverwritingInputMerger
```

La cadena causal:

1. Glance no dibuja el widget en el `BroadcastReceiver`: encola la composición en un
   `SessionWorker` de **WorkManager** (por eso Glance arrastra WorkManager como dependencia
   transitiva; nosotros no lo usamos directamente).
2. WorkManager instancia por reflexión (`Class.newInstance()`) el `InputMerger` de cada
   worker; el de por defecto es `OverwritingInputMerger`.
3. `work-runtime` **2.7.1** (la versión que fija `glance-appwidget` 1.1.1) trae la regla
   consumer `-keep class * extends androidx.work.InputMerger` **sin** especificación de
   miembros. En el *full mode* de R8 (por defecto desde AGP 8) una regla `-keep` sin
   miembros ya no conserva implícitamente el constructor sin argumentos.
4. R8 elimina el constructor, el `SessionWorker` muere antes de componer nada y el widget
   no publica jamás sus `RemoteViews`: el launcher muestra para siempre el
   `initialLayout` de carga.

El mismo patrón afecta a los `ActionCallback` de Glance: la regla consumer de
`glance-appwidget` 1.1.1 (`-keep public class * extends ...action.ActionCallback`) tampoco
conserva el constructor, y Glance los instancia por reflexión al pulsar el widget. Es decir,
incluso renderizando bien, el *tap* habría fallado en release.

## Decisión

Añadir en `app/proguard-rules.pro` dos reglas `-keepclassmembers` que conservan el
constructor sin argumentos de:

- toda clase que extienda `androidx.work.InputMerger`, y
- toda clase que extienda `androidx.glance.appwidget.action.ActionCallback`.

Se descartó forzar una versión moderna de `work-runtime` (2.11.x corrige la regla) porque
supone añadir una dependencia directa que no usamos, solo para heredar una regla de una
línea que podemos declarar nosotros. Cuando Glance estable suba su dependencia de
WorkManager, la primera regla quedará redundante e inocua.

## Consecuencias

- El widget renderiza y responde al toque en builds minificadas. Verificado en emulador
  (API 36, Pixel Launcher) instalando el APK de release y colocando el widget.
- Cualquier funcionalidad futura que use WorkManager directamente queda también protegida.
- **Lección**: las funcionalidades que dependen de reflexión (widgets Glance, workers,
  callbacks) hay que probarlas con la build de release (`assembleRelease` instalado a mano)
  antes de publicar, no solo en debug.
