# Feature: end-of-day summary notification

## Objective

At the end of the day (default 23:00, time configurable, can be disabled in Settings) post a
notification with what the user drank vs the daily goal. Tapping it opens a new day-detail screen
for that date (total, goal, intakes), which also opens from History rows.

## Why

Closing the day with a clear "you drank X of Y" is useful feedback, independent of reminders
(which stop once the goal is reached).

## Scope

- In: `AppSettings` fields `dailySummaryEnabled` (default true) and `dailySummaryMinutes`
  (default 23:00 = 1380), DataStore + repository, a second alarm slot (own action, request code,
  receiver), new notification channel `daily_summary`, rescheduling on boot / onboarding finish /
  settings change / after firing, day-detail route `day/{date}` (screen + ViewModel + UiState),
  notification content intent carrying the date, clickable History rows, Settings UI (switch + time
  picker), ES/EN strings, tests, docs (CLAUDE.md, ADR 006).
- Out: version bump / changelog entry (done at release time), reminder logic changes.

## Constraints

- Summary is posted even if the goal was reached (it is a summary, not a reminder).
- Exact-alarm denied → same silent skip as reminders (`canScheduleExactAlarms()`).
- Date in the intent is the local date at firing time, so tapping after midnight still shows the
  summarized day.
- Channel sound/vibration freezes on first creation; pick its behaviour once.
- No hardcoded strings; EN + ES for every string; `@PreviewLightDark` on public composables.

## TDD

Not configured for this project (no session/project setting) → off. Checks: new unit tests for the
scheduling use case and day-detail ViewModel, then `./gradlew lint test` (JAVA_HOME = Android
Studio JBR).

## Delivery

Branch `feat/resumen-diario` from `main`. Push / PR / merge are the owner's decision. Forecast
~900 authored lines (over the ~400 heuristic): strategy `ask-on-risk`, chain question deferred to
delivery since the owner has historically merged straight to `main`.

## Tasks

- [x] T1 — Settings fields + DataStore/repo, summary scheduler (interface + AlarmManager impl),
      `ScheduleDailySummaryUseCase`, `DailySummaryReceiver`, channel, boot/onboarding hooks, unit
      tests. Route: delegated (writer trigger: 2+ non-trivial files).
- [x] T2 — Day-detail screen + route, notification tap → detail, History rows clickable, tests.
      Route: delegated (writer trigger).
- [ ] T3 — Settings UI (switch + time), reschedule on change, strings ES/EN, CLAUDE.md + ADR 006.
      Route: delegated (writer trigger).

## Progress / evidence

### T1 (done)
- Route: delegated writer. Deviation: `MainViewModel` also calls `ScheduleDailySummaryUseCase` on app
  start (only once onboarding is done) so users updating the app get the alarm scheduled without a
  boot or settings change; the use case is idempotent. Extra helper: `DailySummaryNotificationFactory`.
- `./gradlew lint test compileDebugAndroidTestKotlin`: BUILD SUCCESSFUL (new
  `ScheduleDailySummaryUseCaseTest`, 5 tests, pass).
- Commit: 4961584

### T2 (done)
- Route: delegated writer. Added `GetDaySummaryUseCase` (date-parameterised sibling of
  `GetTodaySummaryUseCase`) so the ViewModel holds no business logic. The pending date from the
  notification lives in `MainViewModel` (`pendingSummaryDate`, `onSummaryDateReceived`,
  `consumePendingSummaryDate`); `MainActivity` only forwards the extra (onCreate only when
  `savedInstanceState == null`, and onNewIntent).
- `./gradlew lint test compileDebugAndroidTestKotlin`: BUILD SUCCESSFUL (new
  `DayDetailViewModelTest` 6 tests, `MainViewModelTest` 4 tests, pass).
- Commit: T2_HASH

## Next step

T3.
