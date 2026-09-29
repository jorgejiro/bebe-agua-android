# Feature: 2x1 home-screen widget with today's progress

## Objective

A second Glance widget, 2 cells wide × 1 high: app icon with the «+» badge (tap = log the default
amount, like the 1x1) plus a text with today's consumed vs goal (`1250 / 2400 ml`).

## Why

The 1x1 is only a button; with twice the room the user can see progress without opening the app.

## Scope

- In: `DrinkWideWidget` + receiver + `appwidget-provider` XML + preview + manifest + ES/EN
  strings; reuse `AddDefaultIntakeAction`; a `WidgetUpdater` abstraction called wherever today's
  total or the goal changes (add, delete, goal change, widget tap); midnight refresh (inexact alarm)
  so the text resets for the new day; docs (CLAUDE.md §2.8, ADR 002 amended or new ADR 007).
- Out: changing the 1x1 widget's look, charts, version bump.

## Constraints

- The 1x1 stays data-free; the 2x1 is the first widget that renders data, so it must be
  invalidated on every change (breaks ADR 002's "never invalidate" premise for this widget only).
- Size everything proportionally to the cell (`SizeMode.Exact` + `LocalSize`), no fixed dp.
- Hilt only via `@EntryPoint` inside Glance classes. R8 rule for `ActionCallback` already covers
  the shared action; verify with a release build.
- No WorkManager for our own scheduling.

## TDD

Not configured → off. Checks: unit tests for pure sizing/format helpers and the updater calls in
use cases, `./gradlew lint test`, release build installs and renders on the emulator.

## Delivery

Branch `feat/widget-2x1`, stacked on `feat/resumen-diario`. Push / PR / merge are the owner's
decision. Forecast ~500 authored lines, strategy `ask-on-risk`.

## Tasks

- [x] T1 — Widget + receiver + provider XML + preview + strings, content reads today's summary.
      Route: delegated (writer trigger). Commit: 57dc639
- [x] T2 — `WidgetUpdater` + calls at every change point + midnight refresh + tests + docs.
      Route: delegated (writer trigger). Commit: eb3e196

## Progress / evidence

- T1: `./gradlew lint test`: BUILD SUCCESSFUL (new `DrinkWideWidgetTest`, 8 tests). Icon+badge
  extracted to shared `DrinkIconWithBadge`; provider is `resizeMode="horizontal"` (a taller widget
  adds nothing; width may grow). Card uses the app's fixed palette (`BackgroundMain`), like the app
  has no dynamic/light variant for the widget. Manual emulator check pending (narrow grids).

- T2: `./gradlew lint test`: BUILD SUCCESSFUL (new `WidgetRefreshUseCasesTest`, `WidgetMidnightTest`
  incl. DST 2026-03-29 / 2026-10-25). `./gradlew assembleDebug assembleRelease`: BUILD SUCCESSFUL.
  Goal path: new `UpdateDailyGoalUseCase` used by `SettingsViewModel` and `OnboardingViewModel`.
  TIME_SET/TIMEZONE_CHANGED handled by the midnight receiver (trivial). Docs: CLAUDE.md 2.8/4,
  ADR 007.

## Next step

Manual emulator check: place the 2x1 (debug and release build), log/delete intakes, change goal,
change date past midnight, narrow grid text fit. Push / PR are the owner's decision.
