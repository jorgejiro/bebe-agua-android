#!/usr/bin/env bash
# Las 42 capturas de un tirón: los tres formatos, arrancando y apagando cada emulador.
#
#     ./todos.sh [ruta-al-apk-debug]
#
# El APK **debug**, no el de release: la siembra de la base de datos se copia con `run-as` y eso solo
# funciona con una app debuggable. Con el de release el pase se completa sin un error y las capturas
# salen con la app a 0 ml.
set -euo pipefail

AQUI="$(cd "$(dirname "$0")" && pwd)"
SDK="$HOME/Library/Android/sdk"
ADB="$SDK/platform-tools/adb"
APK="${1:-$AQUI/../../../app/build/outputs/apk/debug/app-debug.apk}"

# Cada pase es «AVD formato»; las capturas de cada formato quedan en ../capturas/<idioma>/<formato>.
declare -a PASES=(
  "Medium_Phone telefono"
  "Tablet7 tablet-7-pulgadas"
  "Tablet10 tablet-10-pulgadas"
)

python3 "$AQUI/sembrar_historial.py"

for pase in "${PASES[@]}"; do
  set -- $pase
  avd=$1; formato=$2
  echo "══ $avd → $formato ══"

  # A que el AVD anterior haya cerrado de verdad. Sin esta espera el emulador aborta con «Running
  # multiple emulators with the same AVD»: `adb emu kill` vuelve enseguida, pero el proceso tarda en
  # morir, y el pase se quedaba colgado esperando un arranque que no iba a llegar.
  while pgrep -f "qemu-system.*$avd" >/dev/null 2>&1; do sleep 2; done

  ( nohup "$SDK/emulator/emulator" -avd "$avd" -no-boot-anim > "/tmp/emu-$avd.log" 2>&1 & )
  espera=0
  until [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
    sleep 3
    espera=$((espera + 3))
    if [ $espera -gt 300 ]; then
      echo "el emulador $avd no arrancó en cinco minutos; mira /tmp/emu-$avd.log"
      exit 1
    fi
  done
  sleep 5

  "$ADB" uninstall com.jjrapps.bebeagua >/dev/null 2>&1 || true
  "$ADB" install -r "$APK" >/dev/null

  python3 -u "$AQUI/capturar.py" "$formato"

  "$ADB" emu kill || true
  sleep 8
done

python3 "$AQUI/revisar.py"
