#!/usr/bin/env python3
"""
Una tanda de capturas de la app: mismas escenas, un dispositivo y un idioma.

    python3 tanda.py <directorio> <es|en>

Las escenas son las cinco pantallas de la app más la notificación. La del widget no está aquí porque
hay que arrastrarlo en el launcher, y la hace `capturar.py`:

    01-registrar-agua        04-objetivo-y-recordatorios
    02-elegir-medida         05-medidas-y-permisos
    03-historial             06-recordatorio-en-la-notificacion

Todo se navega buscando el texto en pantalla, nunca por coordenadas fijas: el mismo script tiene que
valer para el teléfono y para las dos tablets, que tienen resoluciones distintas.
"""
import os
import re
import sys
import time

import ui

PKG = "com.jjrapps.bebeagua"

TEXTOS = {
    "es": {"inicio": "Inicio", "historial": "Historial", "ajustes": "Ajustes",
           "idioma": "Idioma", "eleccion": "Español",
           "registros": "Registros de hoy", "medida": "medida", "selector": "Seleccionar cantidad",
           "media": "Media diaria", "racha": "Racha actual",
           "objetivo": "Objetivo diario", "franja": "Hora de inicio",
           "recordatorios": "Recordatorios al día", "horarios": "Horarios calculados",
           "acerca": "Acerca de", "notificacion": "¿Has bebido agua?"},
    "en": {"inicio": "Home", "historial": "History", "ajustes": "Settings",
           "idioma": "Language", "eleccion": "English",
           "registros": "Today's records", "medida": "measure", "selector": "Select amount",
           "media": "Daily average", "racha": "Current streak",
           "objetivo": "Daily goal", "franja": "Start time",
           "recordatorios": "Reminders per day", "horarios": "Calculated schedule",
           "acerca": "About", "notificacion": "Time to drink water!"},
}


def adb(*args):
    return ui.adb(*args)


def abrir_app():
    adb("shell", "am", "start", "-n", f"{PKG}/.MainActivity")
    time.sleep(2.5)


def alto_pantalla():
    return int(adb("shell", "wm", "size").strip().split(":")[-1].split("x")[1])


def ancho_pantalla():
    return int(adb("shell", "wm", "size").strip().split(":")[-1].split("x")[0])


def deslizar_arriba(veces=4):
    """Sube al principio de una lista larga, para que la captura no salga a media pantalla."""
    x, alto = ancho_pantalla() // 2, alto_pantalla()
    for _ in range(veces):
        adb("shell", "input", "swipe", str(x), str(int(alto * 0.35)), str(x), str(int(alto * 0.85)), "250")
        time.sleep(0.6)


def deslizar_abajo(veces=1):
    x, alto = ancho_pantalla() // 2, alto_pantalla()
    for _ in range(veces):
        adb("shell", "input", "swipe", str(x), str(int(alto * 0.80)), str(x), str(int(alto * 0.30)), "250")
        time.sleep(0.6)


def ir_a(pestana):
    """Las pestañas se tocan con [exacto]: «Inicio» también está dentro de «Hora de inicio»."""
    ui.tocar(pestana, exacto=True)
    time.sleep(1.5)


def poner_idioma(idioma):
    """
    Por la pantalla de Ajustes, que es el camino real: la app aplica su propio ajuste al arrancar y
    sobreescribe el del sistema, así que `cmd locale set-app-locales` no sirve.

    Se toca siempre la opción del idioma **destino**, que nunca coincide con el valor que la fila de
    Ajustes muestra detrás del diálogo —ese es el idioma de partida—, así que no hay ambigüedad.
    """
    t, otro = TEXTOS[idioma], TEXTOS["en" if idioma == "es" else "es"]
    abrir_app()
    if ui.buscar(t["ajustes"], exacto=True) and not ui.buscar(otro["ajustes"], exacto=True):
        print(f"  idioma ya en {idioma}")
        ir_a(t["ajustes"])
        return

    ir_a(otro["ajustes"])
    for _ in range(8):                      # el idioma está a media lista
        if ui.buscar(otro["idioma"], exacto=True):
            break
        deslizar_abajo()
    ui.tocar(otro["idioma"], exacto=True)
    time.sleep(1.5)
    ui.tocar(t["eleccion"], exacto=True)
    time.sleep(3)
    print(f"  idioma → {idioma}")


def esperar(texto, limite=25, exacto=False):
    """
    Espera a que el texto esté en pantalla antes de capturar.

    Con un `sleep` fijo salen capturas a medio pintar o en negro: una tablet no tarda lo mismo que un
    teléfono en componer, y tras un `force-stop` la pantalla principal tarda todavía más. Esperar por
    contenido es lo único que aguanta el cambio de dispositivo.
    """
    fin = time.time() + limite
    while time.time() < fin:
        if ui.buscar(texto, exacto=exacto):
            time.sleep(0.5)          # un pelín más: que acabe de asentarse el primer fotograma
            return True
        time.sleep(0.7)
    raise SystemExit(f"no apareció en pantalla: {texto!r}")


def escena(nombre, destino, esperando=None, reposo=0.0, exacto=False):
    if esperando:
        esperar(esperando, exacto=exacto)
    if reposo:
        time.sleep(reposo)
    ruta = os.path.join(destino, nombre + ".png")
    ui.captura(ruta)
    print(f"  {nombre}.png")


def alarma_pendiente():
    """El `origWhen` en milisegundos de la alarma que la app tiene programada, o None."""
    volcado = adb("shell", "dumpsys", "alarm")
    for linea in volcado.splitlines():
        if "Alarm{" in linea and PKG in linea:
            m = re.search(r"origWhen (\d+)", linea)
            if m:
                return int(m.group(1))
    return None


def notificacion(destino, idioma):
    """
    El recordatorio con sus dos acciones rápidas, en la sombra desplegada.

    **El aviso se dispara moviendo el reloj**, no con un `am broadcast` al receptor: `ReminderReceiver`
    no está exportado, así que un broadcast desde el shell se encola y no se entrega nunca —se comprobó
    mirando que la alarma programada no se movía—. Adelantar el reloj hasta la alarma pendiente hace que
    salte la de verdad, con su texto y sus dos acciones, que es justo lo que se quiere enseñar.

    El adelanto es momentáneo y no hace falta que dure: el emulador vuelve a sincronizar el reloj con el
    del Mac a los pocos segundos —ni siquiera con `auto_time` a 0—, pero la notificación ya está
    publicada y ahí se queda. Por eso tampoco se intenta fijar una hora bonita para las capturas.

    El receptor solo notifica si el día está por debajo del objetivo, que es lo que deja preparado
    `sembrar_historial.py`.
    """
    # La sombra limpia primero: el emulador trae de fábrica el aviso «Set a screen lock» del centro de
    # seguridad, y en la captura sale debajo del nuestro como si fuera parte de la app.
    adb("shell", "cmd", "statusbar", "expand-notifications")
    time.sleep(2)
    for etiqueta in ("Clear all", "Borrar todo"):
        if ui.buscar(etiqueta, exacto=True):
            ui.tocar(etiqueta, exacto=True)
            time.sleep(1.5)
            break
    adb("shell", "cmd", "statusbar", "collapse")
    time.sleep(1.5)

    for _ in range(4):
        cuando = alarma_pendiente()
        if cuando and cuando > int(time.time() * 1000):
            break
        abrir_app()                     # al arrancar y al guardar ajustes la app reprograma
        time.sleep(2)
    else:
        raise SystemExit("la app no tiene ningún recordatorio programado que disparar")

    adb("shell", "cmd", "alarm", "set-time", str(cuando + 3000))

    fin = time.time() + 30
    while time.time() < fin:
        if PKG in adb("shell", "dumpsys", "notification", "--noredact"):
            break
        time.sleep(1)
    else:
        raise SystemExit("el recordatorio no llegó a publicarse")

    adb("shell", "cmd", "statusbar", "expand-notifications")
    time.sleep(2.5)
    if not ui.buscar(TEXTOS[idioma]["notificacion"]):
        adb("shell", "cmd", "statusbar", "expand-notifications")
        time.sleep(2.5)
    if not ui.buscar(TEXTOS[idioma]["notificacion"]):
        raise SystemExit("la notificación no está en la sombra")
    ui.captura(os.path.join(destino, "06-recordatorio-en-la-notificacion.png"))
    print("  06-recordatorio-en-la-notificacion.png")
    adb("shell", "cmd", "statusbar", "collapse")
    time.sleep(1.5)


def tanda(destino, idioma):
    t = TEXTOS[idioma]
    os.makedirs(destino, exist_ok=True)
    poner_idioma(idioma)

    # 1 · La pantalla principal: el anillo a media asta y los registros del día.
    ir_a(t["inicio"])
    escena("01-registrar-agua", destino, esperando=t["registros"])

    # 2 · El selector de medida, que es el segundo botón de la pantalla principal.
    #
    # Con reposo: la hoja modal tiene su propia animación de entrada, que no se apaga con los
    # `animation_scale` del sistema, y la captura la pillaba a mitad de camino —con el título cortado por
    # el borde curvo de la hoja—.
    ui.tocar(t["medida"], exacto=True)
    escena("02-elegir-medida", destino, esperando=t["selector"], reposo=1.2)
    adb("shell", "input", "keyevent", "KEYCODE_BACK")
    time.sleep(1.5)

    # 3 · El historial de los últimos días, con la racha y las medias.
    ir_a(t["historial"])
    deslizar_arriba()
    escena("03-historial", destino, esperando=t["media"])

    # 4 · Ajustes desde arriba: objetivo diario, franja horaria, recordatorios al día y los horarios que
    # la app calcula. En el teléfono entra todo eso de una vez.
    ir_a(t["ajustes"])
    deslizar_arriba()
    escena("04-objetivo-y-recordatorios", destino, esperando=t["objetivo"], exacto=True)

    # 5 · El final de Ajustes: medidas, idioma, permisos y Acerca de.
    #
    # Antes esta escena era «los recordatorios», y salía **idéntica** a la anterior: en el teléfono la
    # sección de recordatorios ya entra en la primera pantalla, así que la condición de deslizar no se
    # cumplía nunca y se subían dos veces la misma captura. Enseñar la mitad de abajo sí aporta.
    # Se desliza hasta el final de la lista, no hasta que «Acerca de» asome: en una tablet cabe casi todo
    # Ajustes de una vez, y con la condición de «está en pantalla» volveríamos a la captura duplicada.
    for _ in range(8):
        seccion = ui.buscar(t["acerca"], exacto=True)
        if seccion and seccion[0]["centro"][1] < alto_pantalla() * 0.6:
            break
        deslizar_abajo()
    escena("05-medidas-y-permisos", destino, esperando=t["acerca"], exacto=True)

    # 6 · El recordatorio en la sombra de notificaciones.
    notificacion(destino, idioma)

    # Y la app se queda en la pantalla principal, que es donde conviene dejarla.
    abrir_app()
    ir_a(t["inicio"])


if __name__ == "__main__":
    tanda(sys.argv[1], sys.argv[2])
