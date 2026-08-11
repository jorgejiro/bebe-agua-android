#!/usr/bin/env python3
"""
Utilidad de automatización para las capturas de la tienda.

Localiza los elementos por texto o por descripción con `uiautomator dump` y luego toca su centro. Tocar
a ciegas con coordenadas falla la mitad de las veces —está anotado en docs/estado-del-proyecto.md— y
además se rompe en cuanto cambia la resolución, que es justo lo que hacemos aquí: teléfono y dos tablets.

    python3 ui.py cap <ruta.png>          captura la pantalla
    python3 ui.py dump                    volca el árbol de la UI (texto y descripciones)
    python3 ui.py tap <texto>             toca el elemento cuyo texto o descripción coincida
    python3 ui.py wait <texto> [segundos]  espera a que aparezca
"""
import os
import re
import subprocess
import sys
import time

ADB = os.path.expanduser("~/Library/Android/sdk/platform-tools/adb")
SERIE = os.environ.get("SERIE_ADB")


def adb(*args, binario=False):
    orden = [ADB] + (["-s", SERIE] if SERIE else []) + list(args)
    r = subprocess.run(orden, capture_output=True)
    if binario:
        return r.stdout
    return r.stdout.decode("utf-8", "replace")


def captura(ruta):
    datos = adb("exec-out", "screencap", "-p", binario=True)
    if len(datos) < 1000:
        raise RuntimeError(f"captura vacía ({len(datos)} bytes)")
    with open(ruta, "wb") as f:
        f.write(datos)
    return len(datos)


def arbol():
    """El XML de la jerarquía. Se reintenta: uiautomator falla si la UI está animando."""
    for _ in range(4):
        adb("shell", "uiautomator", "dump", "/sdcard/ui.xml")
        xml = adb("shell", "cat", "/sdcard/ui.xml")
        if "<hierarchy" in xml:
            return xml
        time.sleep(1)
    raise RuntimeError("uiautomator no devolvió la jerarquía")


NODO = re.compile(r'<node[^>]*?>')


def nodos(xml):
    fuera = []
    for n in NODO.findall(xml):
        attr = dict(re.findall(r'(\w+(?:-\w+)?)="([^"]*)"', n))
        caja = attr.get("bounds", "")
        m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", caja)
        if not m:
            continue
        x1, y1, x2, y2 = map(int, m.groups())
        fuera.append({
            "texto": attr.get("text", ""),
            "desc": attr.get("content-desc", ""),
            "clase": attr.get("class", ""),
            "clicable": attr.get("clickable", "false"),
            "centro": ((x1 + x2) // 2, (y1 + y2) // 2),
            "caja": (x1, y1, x2, y2),
        })
    return fuera


def buscar(aguja, xml=None, exacto=False):
    """
    El primer nodo cuyo texto o descripción coincida, prefiriendo la coincidencia exacta.

    `exacto=True` descarta las parciales, y hace falta cuando la aguja es una palabra que también
    aparece dentro de otro texto de la pantalla: «SIGUIENTE» es el botón del onboarding **y** el
    comienzo de la línea «SIGUIENTE: ENFOQUE · 25 MIN» del temporizador, así que sin esto se toca la
    línea de la pantalla equivocada y el bucle no avanza.
    """
    xml = xml or arbol()
    aguja_b = aguja.lower()
    exactos, parciales = [], []
    for n in nodos(xml):
        for campo in (n["texto"], n["desc"]):
            if not campo:
                continue
            if campo.lower() == aguja_b:
                exactos.append(n)
            elif not exacto and aguja_b in campo.lower():
                parciales.append(n)
    return (exactos + parciales)[:1]


def esperar(aguja, limite=20, exacto=False):
    """
    Espera a que [aguja] aparezca y devuelve sus nodos, o None si no llega a tiempo.

    Existe porque un `sleep` fijo seguido de un `tocar` es la forma más fácil de que el pipeline se
    rompa al cambiar de dispositivo: la bandeja de widgets de la tablet de 10" tarda más de tres
    segundos en pintarse y el «Search» no estaba todavía. Es la misma regla que ya seguían las
    capturas —esperar por contenido, nunca por tiempo— aplicada también a la navegación.
    """
    fin = time.time() + limite
    while True:
        halladas = buscar(aguja, exacto=exacto)
        if halladas:
            return halladas
        if time.time() >= fin:
            return None
        time.sleep(1)


def tocar(aguja, limite=20, exacto=False):
    halladas = esperar(aguja, limite=limite, exacto=exacto)
    if not halladas:
        raise SystemExit(f"no encontrado: {aguja!r}")
    x, y = halladas[0]["centro"]
    adb("shell", "input", "tap", str(x), str(y))
    return x, y


if __name__ == "__main__":
    orden = sys.argv[1]
    if orden == "cap":
        print(captura(sys.argv[2]), "bytes")
    elif orden == "dump":
        filtro = sys.argv[2].lower() if len(sys.argv) > 2 else ""
        for n in nodos(arbol()):
            etiqueta = n["texto"] or n["desc"]
            if etiqueta and (not filtro or filtro in etiqueta.lower()):
                print(f'{etiqueta[:58]:60} {n["caja"]} clicable={n["clicable"]}')
    elif orden == "tap":
        print("tocado en", tocar(sys.argv[2]))
    elif orden == "wait":
        limite = float(sys.argv[3]) if len(sys.argv) > 3 else 15
        fin = time.time() + limite
        while time.time() < fin:
            if buscar(sys.argv[2]):
                print("visible"); raise SystemExit(0)
            time.sleep(1)
        raise SystemExit(f"no apareció: {sys.argv[2]!r}")
