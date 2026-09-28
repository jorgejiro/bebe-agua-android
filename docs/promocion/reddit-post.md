# Reddit post — Drink Water! / ¡Bebe agua!

Two versions of the same post: English for international subreddits and Spanish for
Spanish-speaking ones. Reddit text posts accept this Markdown as is (switch the editor to
"Markdown mode"). Attach 2–4 screenshots from `docs/store-assets/capturas/<lang>/telefono/` as an
image post or in the rich-text editor, since Reddit does not render relative image links.

---

## English

### Title (pick one)

- I couldn't find a water reminder app that did what I needed, so I built my own — free, no ads, no account, source on GitHub
- [Dev] Drink Water!: a minimal water tracker with reminders that respect your schedule and stop when you hit your goal

### Body

Hi everyone!

I wanted a simple habit: drink enough water during the day. So I tried a bunch of the water tracker
apps on Google Play. And none of them really fit what I needed:

- Most were full of **ads**, upsells, or wanted me to **create an account** just to log a glass of water.
- Many added things I didn't want: tips feeds, animated mascots, hearts, rewards and other
  gamification.
- Reminders were the worst part: they rang **outside my hours**, kept nagging **after I had already
  reached my goal**, or fired **five minutes after I had just had a drink**.
- The cheap ones **rang** instead of vibrating, so I had to choose between muting my phone or
  being interrupted.

So I built my own. It's called **Drink Water!** (*¡Bebe agua!* in Spanish), and it does one thing
well.

**What it does**

- 💧 **One-tap logging.** A big progress ring shows how much you've drunk vs. your daily goal. The
  main button logs your usual amount (always the last size you used). You can also pick another
  size or type any custom amount.
- ⏰ **Reminders that behave.**
  - They only fire inside the time window you set (e.g. 08:00–21:00).
  - You choose how many per day, and they're spread evenly.
  - They **stop automatically once you reach your goal**.
  - Logging water pushes the next reminder forward.
  - Optionally, a reminder that falls within a grace window after a drink is skipped.
- 📳 **Vibrate, don't ring.** Your phone can stay in sound mode. If you wear a smartwatch, the
  vibration reaches your wrist.
- 🔔 **Act from the notification:** "Drink 250 ml" logs it without opening the app, and "Snooze
  15 min" is there for when now isn't a good moment.
- 🏠 **1x1 home screen widget:** one tap logs your default amount.
- 📊 **History:** last 30 days with daily totals, average, best day and streak.
- 🎨 Material 3 with dynamic color, light/dark theme, English and Spanish.

**What it doesn't do**

No account, no cloud, no ads, no analytics, no trackers, no in-app purchases. Everything stays on
your phone.

**Open source**

The full source code is on GitHub: https://github.com/jorgejiro/bebe-agua-android

It's native Android: Kotlin, Jetpack Compose, Material 3, Room, DataStore, Hilt, and exact
alarms for the reminders. So if you're curious about how it works, or you're learning Compose,
feel free to dig in.

**Get it**

Google Play: https://play.google.com/store/apps/details?id=com.jjrapps.bebeagua
(Android 12 or newer.)

**Missing something?**

I built it for my own needs, but I'd love it to be useful for more people. If you try it and miss
a feature, write to me at **jjrmobileapps@gmail.com** (or open an issue on GitHub). I'll gladly
review every request. Feedback of any kind is very welcome, including the critical kind. 🙂

Thanks for reading, and stay hydrated! 💦

---

## Español

### Título (elige uno)

- No encontré ninguna app de recordatorios para beber agua que se ajustara a lo que necesitaba, así que hice la mía: gratis, sin anuncios, sin cuentas y con el código en GitHub
- [Dev] ¡Bebe agua!: registro de agua minimalista con recordatorios que respetan tu horario y paran al llegar al objetivo

### Cuerpo

¡Hola a todos!

Quería adquirir un hábito sencillo: beber suficiente agua durante el día. Probé bastantes apps de
Google Play para registrar el agua, y ninguna se ajustaba a lo que necesitaba:

- La mayoría estaban llenas de **anuncios** y ofertas de pago, o me pedían **crear una cuenta**
  solo para apuntar un vaso de agua.
- Muchas añadían cosas que no quería: consejos, mascotas animadas, corazones, recompensas y demás
  gamificación.
- Lo peor eran los recordatorios: sonaban **fuera de mi horario**, seguían insistiendo **cuando ya
  había llegado al objetivo** o saltaban **cinco minutos después de haber bebido**.
- Muchas **sonaban** en vez de vibrar, así que tenía que elegir entre silenciar el móvil o que me
  interrumpieran.

Así que hice la mía. Se llama **¡Bebe agua!** (*Drink Water!* en inglés) y hace una sola cosa, y
la hace bien.

**Qué hace**

- 💧 **Registro con un toque.** Un gran anillo de progreso muestra lo que llevas bebido frente a tu
  objetivo diario. El botón principal registra tu cantidad habitual (siempre la última que usaste).
  También puedes elegir otra medida o escribir una cantidad cualquiera.
- ⏰ **Recordatorios que se portan bien.**
  - Solo llegan dentro de la franja horaria que elijas (por ejemplo, de 08:00 a 21:00).
  - Tú decides cuántos quieres al día, y se reparten de forma uniforme.
  - **Dejan de llegar en cuanto alcanzas el objetivo**.
  - Al registrar agua, el siguiente recordatorio se aplaza.
  - Si quieres, se salta el recordatorio que caiga poco después de haber bebido.
- 📳 **Vibran, no suenan.** El móvil puede seguir con sonido. Si llevas reloj inteligente, la
  vibración te llega a la muñeca.
- 🔔 **Acciones desde la notificación:** «He bebido 250 ml» lo registra sin abrir la app, y
  «Posponer 15 min» está para cuando no es buen momento.
- 🏠 **Widget 1x1 en el escritorio:** un toque registra tu cantidad por defecto.
- 📊 **Historial:** últimos 30 días con el total diario, la media, el mejor día y la racha.
- 🎨 Material 3 con color dinámico, tema claro u oscuro, en español e inglés.

**Qué no hace**

Nada de cuentas, nube, anuncios, analíticas, rastreadores ni compras dentro de la app. Todo se queda
en tu móvil.

**Código abierto**

El código fuente completo está en GitHub: https://github.com/jorgejiro/bebe-agua-android

Es Android nativo: Kotlin, Jetpack Compose, Material 3, Room, DataStore, Hilt y alarmas exactas
para los recordatorios. Si tienes curiosidad por cómo funciona o estás aprendiendo Compose, puedes
curiosear lo que quieras.

**Descárgala**

Google Play: https://play.google.com/store/apps/details?id=com.jjrapps.bebeagua
(Requiere Android 12 o superior).

**¿Echas algo en falta?**

La hice para cubrir mis necesidades, pero me encantaría que le fuera útil a más gente. Si la pruebas
y echas en falta alguna funcionalidad, escríbeme a **jjrmobileapps@gmail.com** (o abre un issue en
GitHub). Revisaré con gusto cada propuesta de mejora. Cualquier comentario es bienvenido, también
las críticas. 🙂

¡Gracias por leer, y a hidratarse! 💦
