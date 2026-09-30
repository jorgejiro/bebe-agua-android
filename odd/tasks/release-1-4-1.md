# Feature: 1.4.1 — reminder tap opens Home + drop ACCESS_NETWORK_STATE

## Objective

Fix the reminder notification tap so it always lands on Home, and release 1.4.1 (versionCode 13)
to Google Play production and the F-Droid MR !50455, together with the manifest fix 4810d6d.

## Problem

Tapping the end-of-day summary opens the day detail on top of Home. The next reminder notification
opens `MainActivity` with no target (`SINGLE_TOP | CLEAR_TOP` → `onNewIntent`, no extra), so the
nav stack stays on the day detail (History tab highlighted) instead of Home.

## Scope

- In: reminder content intent carries an explicit "open Home" request; `MainActivity` forwards it;
  `MainViewModel` exposes a single pending navigation request (Home | DayDetail(date)); `NavGraph`
  pops to the start destination for Home; unit tests. Release chores as in `release-1-4-0.md`.
- Out: launcher-icon behavior (resuming the last screen is standard Android).

## Constraints

- Owner authorized (2026-09-30): fix the bug, "genera release, publica versión en Play Store, y
  actualiza el MR dando las gracias al revisor".
- F-Droid MR keeps a single build (replace 1.4.0 with 1.4.1).

## TDD

Off (no project TDD configuration). Checks: `./gradlew lint test`; manual check on emulator.

## Tasks

- [x] T1 — Reminder tap navigates to Home. Route: delegated (writer trigger: 4+ non-trivial files).
- [x] T2 — Version bump 1.4.1 (vc 13) + all release texts + recipe. Route: delegated (writer trigger).
- [x] T3 — Commit, tag `v1.4.1`, push; `fastlane subir`. Route: inline (bounded actions).
- [x] T4 — Update the F-Droid MR recipe to 1.4.1, reply to the reviewer, check the pipeline. Route: inline.

## Progress / evidence

- Pre-work: 4810d6d `fix(manifest)` removes ACCESS_NETWORK_STATE (merged from androidx.work);
  `./gradlew lint test` green.
- T1 done: a6feebd (reminder intent carries `EXTRA_OPEN_HOME`; `PendingNavigation` Home | DayDetail).
  Emulator (release APK): summary intent → day detail; reminder intent → Home, Home tab highlighted.
  RDD assess: medium, under budget. `./gradlew lint test` green.
- T2 done: versionCode 13 / 1.4.1; CHANGELOG, `changelog_1_4_1` (EN/ES), `ChangelogCatalog`, fastlane
  `13.txt` (es-ES 271 chars, en-US 222 chars, both <= 500), `docs/play-release-notes.md` (3 subsections),
  CLAUDE.md (title + roadmap), F-Droid recipe. `./gradlew lint test`: BUILD SUCCESSFUL.
- T3 done: release commit 1f0e53d, tag `v1.4.1`, recipe commit 2ea6586; `main` and tag pushed.
  `fastlane subir track:production release_status:completed` (2026-09-30 16:27): "Successfully
  finished the upload to Google Play".
- T4 done: MR branch commit 8a805de0 (recipe 1.4.1/13, single build); reviewer reply posted (note
  3931304263); pipeline 2897699025: success.

## Next step

Wait for Play review and for F-Droid testing/merge of !50455.
