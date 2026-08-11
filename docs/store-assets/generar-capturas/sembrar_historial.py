#!/usr/bin/env python3
"""
Construye una base de datos con historial para que las capturas no salgan vacías.

    python3 sembrar_historial.py [directorio-de-salida]   # por defecto, junto a este script

Con una instalación nueva el Historial está en blanco y la pantalla principal marca 0 ml, y ninguna de
las dos se puede enseñar en una tienda. Aquí se generan 45 días de ingestas con forma creíble: casi
todos los días llegando al objetivo, alguno flojo, algún día en blanco, y **hoy a medias**, que es lo
normal a media mañana y además lo que deja el anillo de progreso a mitad de camino.

Dos cosas no son cosmética:

- **Hoy tiene que quedar por debajo del objetivo.** Si el día está completo, el anillo sale al 100 %,
  la escena pierde la gracia y encima `ReminderReceiver` no notifica —deja de recordar al alcanzar el
  objetivo—, así que la captura de la notificación se queda sin notificación.
- **Room valida la base al abrirla**: si `room_master_table` no lleva el `identityHash` del esquema
  compilado, lanza `IllegalStateException` y la app no arranca. El hash se lee de
  `app/schemas/…AppDatabase/1.json`, que es la fuente de verdad del esquema, así que al cambiar la base
  esto sigue cuadrando sin tocar nada.
"""
import json
import os
import random
import sqlite3
import sys
from datetime import date, datetime, timedelta, timezone

AQUI = os.path.dirname(os.path.abspath(__file__))
RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(AQUI)))   # …/docs/store-assets/generar-capturas
ESQUEMA = os.path.join(RAIZ, "app", "schemas",
                       "com.jjrapps.bebeagua.data.local.db.AppDatabase", "1.json")
BASE = "bebe_agua.db"
ZONA = "Europe/Madrid"
DESPLAZAMIENTO = timezone(timedelta(hours=2))     # verano en Madrid; solo afecta a las horas locales

OBJETIVO = 2400                                   # el mismo que trae la app de fábrica
MEDIDAS = (200, 250, 330, 500)


def identity_hash():
    with open(ESQUEMA, encoding="utf-8") as f:
        return json.load(f)["database"]["identityHash"]


def dia_de_ingestas(dia, total_objetivo, primera_hora=8, ultima_hora=21, paso=None):
    """Reparte [total_objetivo] ml en tragos de las medidas de la app, repartidos por la jornada."""
    filas, acumulado = [], 0
    hora = primera_hora
    while acumulado < total_objetivo and hora <= ultima_hora:
        medida = random.choice(MEDIDAS)
        momento = datetime(dia.year, dia.month, dia.day, hora,
                           random.choice([5, 12, 25, 38, 47, 55]), tzinfo=DESPLAZAMIENTO)
        filas.append((medida, int(momento.timestamp() * 1000), ZONA, dia.isoformat()))
        acumulado += medida
        hora += paso or random.choice([1, 1, 2])
    return filas


def sembrar(destino, hoy=None, dias=45, semilla=11):
    ruta = os.path.join(destino, BASE)
    if os.path.exists(ruta):
        os.remove(ruta)
    hoy = hoy or date.today()

    con = sqlite3.connect(ruta)
    con.executescript("""
    CREATE TABLE IF NOT EXISTS `intake` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
      `amount_ml` INTEGER NOT NULL, `timestamp_epoch_ms` INTEGER NOT NULL,
      `timezone_id` TEXT NOT NULL, `local_date` TEXT NOT NULL);
    CREATE INDEX IF NOT EXISTS `index_intake_local_date` ON `intake` (`local_date`);
    CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT);
    CREATE TABLE IF NOT EXISTS android_metadata (locale TEXT);
    """)
    con.execute("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, ?)",
                (identity_hash(),))
    con.execute("INSERT INTO android_metadata (locale) VALUES ('en_US')")
    con.execute("PRAGMA user_version = 1")

    random.seed(semilla)          # mismo historial en cada ejecución: las capturas son comparables
    filas = []
    for atras in range(dias - 1, -1, -1):
        dia = hoy - timedelta(days=atras)
        if atras == 0:
            # Hoy, a media mañana: unos cuantos tragos seguidos y el objetivo todavía lejos. El paso de
            # una hora es fijo para que la lista de «Registros de hoy» tenga varias filas y no dos.
            filas += dia_de_ingestas(dia, 1250, primera_hora=7, ultima_hora=13, paso=1)
            continue
        if random.random() < 0.08:                       # algún día sin registrar nada
            continue
        flojo = random.random() < 0.25                   # y algún día que se queda corto
        filas += dia_de_ingestas(dia, random.randint(1400, 1900) if flojo
                                 else random.randint(OBJETIVO, OBJETIVO + 400))

    con.executemany(
        "INSERT INTO intake (amount_ml, timestamp_epoch_ms, timezone_id, local_date) "
        "VALUES (?,?,?,?)", filas)
    con.commit()

    jornadas = con.execute("SELECT COUNT(DISTINCT local_date) FROM intake").fetchone()[0]
    logrados = con.execute(
        "SELECT COUNT(*) FROM (SELECT local_date, SUM(amount_ml) t FROM intake "
        "GROUP BY local_date HAVING t >= ?)", (OBJETIVO,)).fetchone()[0]
    de_hoy = con.execute("SELECT COALESCE(SUM(amount_ml), 0) FROM intake WHERE local_date = ?",
                         (hoy.isoformat(),)).fetchone()[0]
    con.close()
    print(f"{ruta}: {len(filas)} ingestas en {jornadas} días, {logrados} con el objetivo cumplido, "
          f"hoy {de_hoy}/{OBJETIVO} ml")
    return ruta


if __name__ == "__main__":
    destino = sys.argv[1] if len(sys.argv) > 1 else os.path.join(AQUI, "siembra")
    os.makedirs(destino, exist_ok=True)
    sembrar(destino)
