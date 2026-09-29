# Feature: release 1.4.0 (Play production + F-Droid MR)

## Objective

Publish 1.4.0 (versionCode 12) with the end-of-day summary, the day-detail screen and the 2x1
widget: to Google Play production (sent to review) and update the pending F-Droid MR !50455.

## Scope

- In: version bump, `CHANGELOG.md`, `changelog_1_4_0` arrays EN/ES, `ChangelogCatalog`,
  fastlane changelogs `12.txt` (en-US, es-ES, ≤ 500 chars), `docs/play-release-notes.md` block (3
  subsections), CLAUDE.md version/roadmap, Play/F-Droid full description mentioning the new
  features (`docs/play-store-publication-texts.md` + fastlane copies), F-Droid recipe
  (`docs/fdroid/com.jjrapps.bebeagua.yml` + MR branch), tag `v1.4.0`, `fastlane subir`.
- Out: new screenshots unless a captured scene visibly changed (checked in T1).

## Constraints

- Owner authorized (2026-09-29): "prepara todo lo necesario y envía a publicar, y actualiza el MR
  en F-Droid". Track: production, `release_status: completed`.
- Upload key `bebeagua.jks`. Release must build without `keystore.properties` (F-Droid).

## TDD

Off (release chores). Checks: `./gradlew lint test` (ChangelogCatalogTest), character limits,
`fastlane validar`, `fastlane subir` result, F-Droid MR pipeline.

## Tasks

- [x] T1 — Version bump + all release texts + recipe. Route: delegated (writer trigger).
- [x] T2 — Commit, tag `v1.4.0`, push; `fastlane subir track:production release_status:completed`.
      Route: inline (bounded actions).
- [x] T3 — Update F-Droid MR branch recipe to 1.4.0 and check pipeline. Route: inline.

## Progress / evidence

- T1 (delegated writer, 2026-09-29): versionCode 12 / versionName 1.4.0; CHANGELOG, `changelog_1_4_0` EN/ES,
  ChangelogCatalog, fastlane `12.txt` (es-ES 471, en-US 469 chars), Play notes block (3 subsections),
  full descriptions (es-ES 2996, en-US 2825 chars; fastlane copies identical to the doc), CLAUDE.md,
  F-Droid recipe (commit is `COMMIT_PLACEHOLDER`, parent replaces it). `./gradlew lint test`: BUILD SUCCESSFUL.
  Screenshots: no regeneration needed (scenes 4/5 unlikely to show the new Daily summary card).
- T2: release commit be068cb (RDD assess: medium, under budget), tag `v1.4.0` pushed with `main`.
  `fastlane subir track:production release_status:completed` (2026-09-29 16:58): "Successfully
  finished the upload to Google Play" — AAB 12 + changelogs en-US/es-ES + listing texts, sent to
  review.
- T3: recipe 1.4.0 → commit be068cbf…; fork branch commit 7e57c1eb on MR !50455; update comment
  posted for the reviewer (note 3924442027). Pipeline 2893837949: success (fdroid build, checkupdates, lint, rewritemeta, check apk, all green).
- Reviewer follow-up (2026-09-29): linsui asked to "Remove the old version" and said the MR is
  mostly ready, pending testing (long queue); update the MR on any new release. 1.3.1 build removed
  (fork commit 742ead90), reply posted (note 3925633645), pipeline 2894512772: success. Rule
  recorded in `docs/fdroid/LEEME.md`.

## Next step

Wait for Play review and for F-Droid testing/merge of !50455. If a new version ships first, replace
the build in the MR recipe (single build while the MR is open).
