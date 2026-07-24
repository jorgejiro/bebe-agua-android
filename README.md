# Bebe Agua

A native Android app to track your daily water intake and receive timely reminders — no accounts, no cloud, no ads.

## Features

### Home screen
- Large progress circle showing consumed vs. daily goal (ml).
- One-tap button to log the default intake (always the last used amount).
- Secondary button to switch the default intake size from your configured list.
- "Today's records" list with timestamp and amount, newest first. Long-press or overflow menu to delete a record.
- Next scheduled reminder displayed inline.

### History
- Day-by-day list covering the last 30 days: total intake, progress bar vs. goal, and goal-reached indicator.
- Summary stats: daily average, best day, and current streak.

### Settings
- **Daily goal** — stepper + keyboard input, default 1500 ml.
- **Reminder window** — configurable start and end time (default 08:00–23:00).
- **Reminders per day** — slider to set how many reminders fire within the window.
- **Calculated schedule** — preview of exact reminder times derived from your settings.
- **Intake sizes** — editable list of preset amounts; add, edit, or delete entries (minimum one must remain).
- **Language** — Español / English (follows system locale by default).
- **Permissions** — inline status for notification permission and exact-alarm permission, with direct links to system settings.
- **About** — installed version and a **What's new** screen with the changelog of every release (see [CHANGELOG.md](CHANGELOG.md)).

### Reminders
- Delivered via `AlarmManager.setExactAndAllowWhileIdle` — fires at the exact scheduled time.
- Notification actions: **Drink X ml** (logs intake without opening the app) and **Snooze 15 min**.
- Reminders stop automatically once the daily goal is reached.
- Only fire within the configured time window.
- Logging an intake reschedules the next reminder from that moment.
- Reminders are rescheduled on device reboot via `BOOT_COMPLETED`.

### Onboarding
- First-launch flow: welcome screen, goal and time-window setup, and proactive permission requests.

## Tech stack

| Layer | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + UDF (ui / domain / data) |
| DI | Hilt |
| Local storage | Room (intake records) + DataStore (settings) |
| Background | AlarmManager exact alarms + BroadcastReceiver |
| Notifications | NotificationManagerCompat |
| Concurrency | Coroutines + Flow |
| Min SDK | 31 (Android 12) |

## Requirements

- Android 12 (API 31) or higher.
- **Notifications** permission (Android 13+).
- **Schedule exact alarms** permission — requested on first launch; required for on-time reminders.

## Building

```bash
./gradlew assembleDebug
```

## License

Personal / private project. All rights reserved.
