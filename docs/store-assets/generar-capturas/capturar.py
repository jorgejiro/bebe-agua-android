#!/usr/bin/env python3
"""
Juego completo de capturas para la ficha de Play, en el dispositivo conectado.

    python3 capturar.py <formato> [--sin-widget]

El formato es una de las claves de `PANTALLAS` —`telefono`, `tablet-7-pulgadas`,
`tablet-10-pulgadas`—, y las capturas quedan en `../capturas/<idioma>/<formato>`: la ficha de Play
se sube idioma a idioma, así que el idioma manda en el árbol y el formato va dentro.

Siete escenas por idioma, en español y en inglés: las cinco pantallas de la app, el recordatorio en la
sombra de notificaciones y el widget en el escritorio.

Dos cosas se preparan a propósito y no son cosmética:

- **El historial se siembra** con 45 días de ingestas (`siembra/`), porque con una instalación nueva el
  Historial sale vacío y la pantalla principal marca 0 ml. La siembra deja además **hoy por debajo del
  objetivo**, que es lo que hace que el anillo salga a media asta y que `ReminderReceiver` acepte
  notificar —al alcanzar el objetivo deja de recordar, y la captura de la notificación se quedaría sin
  notificación—.
- **El widget se coloca en la segunda página del escritorio.** En la primera, el launcher pinta «At a
  glance» con la fecha, que es texto del sistema y saldría en inglés en la ficha española; la segunda
  página no lo lleva y además queda el escritorio limpio.
"""
import os
import sys
import time

import tanda
import ui

PKG = "com.jjrapps.bebeagua"
LAUNCHER = "com.google.android.apps.nexuslauncher"

# La otra app del autor, que también trae widget de 1×1. Si está instalada aparece en la bandeja junto a
# la nuestra y el arrastre puede agarrar su vista previa. Se desinstala antes de colocar nada.
PKG_RIVAL = "com.jjrapps.aquihaytomate"

# Cómo se llama el widget en la bandeja. **En el idioma del sistema, no en el de la app**: la bandeja es
# del launcher, así que aquí no vale el nombre español aunque la tanda sea la española.
APP_EN_BANDEJA = "Drink Water"
BUSQUEDA_EN_BANDEJA = "drink"


def adb(*args):
    return ui.adb(*args)


def densidad():
    return int(adb("shell", "wm", "density").strip().split(":")[-1])


def dp(valor):
    return int(valor * densidad() / 160)


def ancho_pantalla():
    return tanda.ancho_pantalla()


def alto_pantalla():
    return tanda.alto_pantalla()


# Resolución y densidad que Play exige de cada formato. **No salen del AVD**: los
# tres `config.ini` dicen 2560×1600 @ 320, y los valores de verdad se fijan con `wm size` y `wm density`,
# que persisten en el emulador… pero no siempre los dos. A 320 dpi la tablet de 7" se queda en 540 dp de
# ancho en vez de 600, que es el umbral con el que Android decide que algo es una tablet: las capturas
# saldrían con el layout de un teléfono grande sin que nada fallara.
PANTALLAS = {
    "telefono": (1080, 2400, 420),
    "tablet-7-pulgadas": (1080, 1920, 288),
    "tablet-10-pulgadas": (1440, 2560, 288),
}


def fijar_pantalla(formato):
    """Aplica la resolución y la densidad que Play exige del formato que toca."""
    medidas = PANTALLAS.get(formato)
    if not medidas:
        print(f"aviso: '{formato}' no está en PANTALLAS, se deja la pantalla como esté")
        return
    ancho, alto, dpi = medidas
    adb("shell", "wm", "size", f"{ancho}x{alto}")
    adb("shell", "wm", "density", str(dpi))
    time.sleep(5)
    real = adb("shell", "wm", "size").strip().split(":")[-1].strip()
    print(f"pantalla: {formato} → {ancho}x{alto} @ {dpi} dpi "
          f"({round(ancho / (dpi / 160))} dp de ancho), efectiva {real}")


def preparar():
    """Permisos, historial sembrado, sin animaciones y onboarding pasado."""
    adb("shell", "pm", "grant", PKG, "android.permission.POST_NOTIFICATIONS")
    adb("shell", "appops", "set", PKG, "SCHEDULE_EXACT_ALARM", "allow")
    # Reloj de 24 horas: el sistema del emulador está en inglés y por defecto pinta «2:57» en la barra de
    # estado, que en la ficha española canta. La hora en sí no se puede fijar —ver `tanda.notificacion`—,
    # pero el formato sí.
    adb("shell", "settings", "put", "system", "time_12_24", "24")
    for clave in ("window_animation_scale", "transition_animation_scale", "animator_duration_scale"):
        adb("shell", "settings", "put", "global", clave, "0")

    semilla = os.path.join(os.path.dirname(os.path.abspath(__file__)), "siembra", "bebe_agua.db")
    adb("shell", "am", "force-stop", PKG)
    adb("push", semilla, "/data/local/tmp/seed.db")
    adb("shell", f"run-as {PKG} mkdir -p databases")
    adb("shell", f"run-as {PKG} sh -c 'cat /data/local/tmp/seed.db > databases/bebe_agua.db'")
    # **Y fuera el WAL y el shm de la sesión anterior.** Sustituir el fichero principal de una base
    # SQLite sin borrarlos deja a Room aplicando por encima un diario que no corresponde, y la pantalla
    # sale a cero con la captura hecha y subida, sin que nada falle.
    adb("shell", f"run-as {PKG} sh -c 'rm -f databases/bebe_agua.db-wal databases/bebe_agua.db-shm'")
    print("preparado: permisos, historial sembrado (sin WAL viejo), animaciones apagadas")

    tanda.abrir_app()
    for _ in range(8):                       # el onboarding, si aparece, son varias páginas
        for etiqueta in ("Empezar", "Get started", "Siguiente", "Next"):
            if ui.buscar(etiqueta, exacto=True):
                ui.tocar(etiqueta, exacto=True)
                time.sleep(1.2)
                break
        else:
            break

    anadir_medidas()


# Las medidas que se ven en la ficha. De fábrica la app trae una sola, `[200 ml]`, y con una sola medida
# el selector de cantidad —la escena 02— enseña una lista de un elemento y «Otra cantidad…», que no
# cuenta nada de lo que hace la app.
MEDIDAS = (330, 500)


def anadir_medidas():
    """
    Configura un par de medidas más desde Ajustes, si no están ya.

    Por la interfaz y no escribiendo el DataStore a mano: son cuatro toques y así no hay que replicar el
    formato del `preferences_pb`, que cambiaría en cuanto se toque `SettingsDataSource`.
    """
    tanda.ir_a("Ajustes") if ui.buscar("Ajustes", exacto=True) else tanda.ir_a("Settings")
    for medida in MEDIDAS:
        for _ in range(8):
            if ui.buscar("+ Añadir tamaño") or ui.buscar("+ Add size"):
                break
            tanda.deslizar_abajo()
        if ui.buscar(f"{medida} ml", exacto=True):
            continue
        ui.tocar("+ Añadir tamaño") if ui.buscar("+ Añadir tamaño") else ui.tocar("+ Add size")
        time.sleep(1.2)
        adb("shell", "input", "text", str(medida))
        time.sleep(0.8)
        ui.tocar("Añadir") if ui.buscar("Añadir", exacto=True) else ui.tocar("Add", exacto=True)
        time.sleep(1.5)
    print(f"medidas configuradas: 200 y {', '.join(str(m) for m in MEDIDAS)} ml")


def limpiar_escritorio():
    """
    Escritorio virgen antes de colocar el widget.

    `pm clear` del launcher es la única forma de quitar lo que dejó un pase anterior: no hay orden de
    `adb` que borre un widget, así que sin esto se acumulan y encima quedan los widgets que el launcher
    trae de fábrica, como el de Calendar con su «Sign in», que no tienen nada que hacer en la ficha.
    """
    adb("shell", "pm", "uninstall", PKG_RIVAL)
    adb("shell", "pm", "clear", LAUNCHER)
    time.sleep(3)
    adb("shell", "input", "keyevent", "KEYCODE_HOME")
    time.sleep(5)
    print("escritorio limpio")


def buscar_en_bandeja():
    """
    Deja la bandeja de widgets mostrando solo nuestra app.

    Las esperas por contenido están por tres fallos distintos de la tablet de 10": la bandeja tarda en
    pintarse —y allí «Search» solo existe como `content-desc`—, el campo de búsqueda tarda en coger el
    foco, así que un `input text` inmediato se pierde y la lista se queda sin filtrar, y el filtrado
    tarda bastante más que el tecleo. Si aun así no cumple, se recorre la lista a mano.
    """
    ui.tocar("Search", limite=25)
    if not ui.esperar("Back", limite=10):
        time.sleep(1.5)
    adb("shell", "input", "text", BUSQUEDA_EN_BANDEJA)
    if ui.esperar(APP_EN_BANDEJA, limite=25):
        return

    print("  el buscador no filtró; recorriendo la bandeja")
    adb("shell", "input", "keyevent", "KEYCODE_ESCAPE")
    time.sleep(1.5)
    ancho, alto = ancho_pantalla(), alto_pantalla()
    for _ in range(12):
        if ui.buscar(APP_EN_BANDEJA):
            return
        adb("shell", "input", "swipe", str(ancho // 2), str(int(alto * 0.7)),
            str(ancho // 2), str(int(alto * 0.35)), "400")
        time.sleep(1.2)
    raise SystemExit("el widget no aparece en la bandeja, ni buscando ni recorriéndola")


def nodo_del_widget(limite=0):
    """
    El widget ya puesto en el escritorio. No tiene texto —es el icono con el «+»—, así que se busca por
    su `content-desc`, que el launcher rellena con la etiqueta del proveedor.

    Con [limite] espera a que aparezca, y hace falta justo después de soltarlo: en la tablet de 7" el
    launcher tarda en asentar el widget recién colocado, la búsqueda inmediata no lo encontraba y se
    quedaba en la primera página, la que tiene el «At a glance» y el widget de Calendar con su «Sign in».
    """
    for aguja in ("Log a drink", "Registrar ingesta", APP_EN_BANDEJA):
        hallado = ui.esperar(aguja, limite=limite) if limite else ui.buscar(aguja)
        if hallado:
            return hallado
    return None


def colocar_widget():
    """
    Añade el widget de 1×1 y lo deja en la segunda página del escritorio.

    Todo por gestos, porque no hay forma de fijar un widget por línea de órdenes. El arrastre se hace con
    `motionevent` y esperas dentro del propio dispositivo: un `input swipe` no mantiene el dedo quieto el
    tiempo que el launcher necesita para entender que es un long-press y no un toque.
    """
    limpiar_escritorio()
    ancho, alto = ancho_pantalla(), alto_pantalla()
    centro_x = ancho // 2

    adb("shell", "input", "keyevent", "KEYCODE_HOME")
    time.sleep(2)
    # El long press tiene que caer en hueco libre, y dónde está el hueco depende de lo que traiga
    # preinstalado cada launcher: en la tablet grande la zona de arriba está ocupada.
    # Y hay que darle tiempo: en la tablet de 10" el menú del escritorio tarda más de dos segundos en
    # aparecer, y con la comprobación inmediata el pase moría en «no encontrado: 'Widgets'» aunque el
    # hueco fuera bueno.
    for fraccion in (0.65, 0.5, 0.45, 0.8, 0.3):
        y = int(alto * fraccion)
        adb("shell", "input", "swipe", str(centro_x), str(y), str(centro_x), str(y), "900")
        if ui.esperar("Widgets", limite=8):
            break
        adb("shell", "input", "keyevent", "KEYCODE_ESCAPE")
        time.sleep(1)
    ui.tocar("Widgets")
    buscar_en_bandeja()

    # Abrir la ficha de la app. En el teléfono la fila se despliega en el sitio; en una tablet la bandeja
    # tiene dos paneles y la vista previa aparece en el de la derecha. Tocar la fila vale para los dos, y
    # si no basta se prueba el chevron del extremo derecho.
    titulo = ui.esperar(APP_EN_BANDEJA, limite=20)
    if not titulo:
        raise SystemExit("el widget no aparece en la bandeja")
    ui.tocar(APP_EN_BANDEJA)
    time.sleep(2.5)

    etiqueta = ui.esperar("1 × 1", limite=8) or ui.buscar("1 x 1") or ui.buscar("Log a drink")
    if not etiqueta:
        adb("shell", "input", "tap", str(titulo[0]["caja"][2] + dp(30)), str(titulo[0]["centro"][1]))
        etiqueta = ui.esperar("1 × 1", limite=10) or ui.buscar("1 x 1") or ui.buscar("Log a drink")
    if not etiqueta:
        raise SystemExit("no encuentro la ficha del widget en la bandeja")
    x = etiqueta[0]["centro"][0]
    y = etiqueta[0]["caja"][1] - dp(70)

    # Se suelta centrado y algo por encima de la mitad, en fracciones de la pantalla y no en dp fijos:
    # los dp caen dentro del escritorio en el teléfono pero pegados al borde superior en las tablets. A un
    # tercio de la altura la tablet de 10" lo encajaba en la primera fila, justo debajo de la barra de
    # estado, y la captura quedaba fea.
    destino_x, destino_y = centro_x, int(alto * 0.45)
    adb("shell", f"input motionevent DOWN {x} {y}; sleep 1.2; "
                 f"input motionevent MOVE {x + 2} {y - 2}; sleep 0.3; "
                 f"input motionevent MOVE {x} {y - dp(30)}; sleep 0.2; "
                 f"input motionevent MOVE {int(x * 0.8)} {y - dp(60)}; sleep 0.2; "
                 f"input motionevent MOVE {destino_x} {destino_y}; sleep 0.5; "
                 f"input motionevent UP {destino_x} {destino_y}")
    time.sleep(3)
    # Fuera el marco de redimensión con HOME, no con un toque en un hueco: el toque a ciegas en la parte
    # de abajo caía en el buscador de la tablet y lo abría, y con el buscador encima el widget «no
    # aparecía» en la jerarquía.
    adb("shell", "input", "keyevent", "KEYCODE_HOME")
    time.sleep(2)

    # A la segunda página, para dejar atrás el «At a glance» del launcher.
    puesto = nodo_del_widget(limite=20)
    if puesto:
        px, py = puesto[0]["centro"]
        adb("shell", f"input motionevent DOWN {px} {py}; sleep 1.2; "
                     f"input motionevent MOVE {px + dp(15)} {py}; sleep 0.3; "
                     f"input motionevent MOVE {int(ancho * 0.5)} {py}; sleep 0.3; "
                     f"input motionevent MOVE {ancho - 20} {py}; sleep 1.5; "
                     f"input motionevent MOVE {ancho - 10} {py}; sleep 1.5; "
                     f"input motionevent MOVE {centro_x} {py}; sleep 0.8; "
                     f"input motionevent UP {centro_x} {py}")
        time.sleep(3)
    else:
        print("  aviso: no localizo el widget colocado; se queda en la primera página")
    print("widget colocado en el escritorio")


def capturar_widget(destino):
    """
    El escritorio con el widget puesto.

    Este widget no pinta datos —es el icono con el «+»—, así que no hay que reabrir la app para que se
    republique: no puede quedarse en blanco por un `force-stop`. Lo que sí hay que hacer es buscarlo:
    HOME deja la primera página y el widget vive en la segunda.
    """
    adb("shell", "input", "keyevent", "KEYCODE_HOME")
    time.sleep(3)
    ancho, alto = ancho_pantalla(), alto_pantalla()
    for _ in range(3):
        if nodo_del_widget():
            break
        adb("shell", "input", "swipe", str(int(ancho * 0.8)), str(alto // 2),
            str(int(ancho * 0.2)), str(alto // 2), "300")
        time.sleep(1.5)
    else:
        raise SystemExit("el widget no está en ninguna página del escritorio")

    time.sleep(1)
    ui.captura(os.path.join(destino, "07-widget-en-el-escritorio.png"))
    print("  07-widget-en-el-escritorio.png")


# Raíz del árbol de capturas: `<idioma>/<formato>` dentro de esta carpeta.
CAPTURAS = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "capturas")


if __name__ == "__main__":
    formato = sys.argv[1]
    capturas = next((a.split("=", 1)[1] for a in sys.argv if a.startswith("--capturas=")), CAPTURAS)
    # Los pases se pueden trocear, que es útil cuando quien lanza el script tiene un límite de tiempo por
    # orden: `--solo-preparar` deja la pantalla, los datos y el widget listos, y luego cada
    # `--idioma es|en` hace su tanda sin volver a tocar nada de eso.
    solo_preparar = "--solo-preparar" in sys.argv
    idiomas = [a.split("=")[1] for a in sys.argv if a.startswith("--idioma=")] or ["es", "en"]

    if solo_preparar or "--sin-preparar" not in sys.argv:
        fijar_pantalla(formato)
        preparar()
        if "--sin-widget" not in sys.argv:
            colocar_widget()
    if solo_preparar:
        raise SystemExit(0)

    for idioma in idiomas:
        destino = os.path.join(capturas, idioma, formato)
        os.makedirs(destino, exist_ok=True)
        print(f"── {idioma} ──")
        # `--solo-widget` rehace nada más la escena del escritorio, que no depende del idioma de la app y
        # es la única que hay que repetir cuando se recoloca el widget.
        if "--solo-widget" not in sys.argv:
            tanda.tanda(destino, idioma)
        capturar_widget(destino)
