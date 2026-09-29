# Feature: publish to Google Play with fastlane supply

## Objective

Upload ¡Bebe agua!'s Play listing (texts, screenshots in three formats) and future AABs from this
repo with `fastlane supply`, the same way Constanza does, and refresh the ES/EN listing texts.

## Why

The listing already lives in `fastlane/metadata/android/` (added for F-Droid). Uploading texts and
42 screenshots by hand in Play Console is slow and error-prone.

## Scope

- In: `fastlane/Appfile`, `fastlane/Fastfile` (lanes `validar`, `ficha`, `subir`), `.gitignore`
  for the key and report, refreshed ES/EN title/short/full descriptions (and their copy in
  `docs/play-store-publication-texts.md`), tablet screenshots copied into
  `sevenInchScreenshots/` and `tenInchScreenshots/`, Spanish runbook section.
- Out: icon and feature graphic (Play keeps the ones already uploaded; supply leaves missing images
  untouched), App content questionnaires, Data safety, a new AAB release.

## Constraints

- Shared key `~/.config/play/jjrmobileapps.json` (service account
  `play-publisher@jjrmobileapps.iam.gserviceaccount.com`), never in the repo.
- Listing texts limits: title 30, short 80, full 4000 characters.
- `fastlane/metadata` is also read by F-Droid.
- Uploading to Play (`fastlane ficha`) is outward-facing: confirm with the owner first.

## TDD

Off — tooling/config/copy only. Checks: `fastlane lanes`, `fastlane validar`, character limits,
screenshot copies byte-equal to `docs/store-assets/capturas/`.

## Tasks

- [x] T1 — Appfile, Fastfile, `.gitignore`; validate against the API. Route: inline (mechanical
      copy of Constanza's files).
- [x] T2 — Refresh ES/EN listing texts + tablet screenshot copies + runbook. Route: delegated
      (writer trigger: 2+ non-trivial files).
- [ ] T3 — `fastlane validar` then, after owner confirmation, `fastlane ficha`. Route: inline,
      bounded action.

## Progress / evidence

- T1: `fastlane lanes` lists `validar`, `ficha`, `subir`. `fastlane validar` (2026-09-29):
  "Successfully validated the upload to Google Play" — the service account already has access to
  `com.jjrapps.bebeagua`. Keystore points to `bebeagua.jks` (the accepted upload key).
- T2 (a2e868a): texts within limits (es full 2505, en full 2364, short 75/64, titles 11/12); 28 tablet
  copies and 14 phone screenshots byte-equal to `docs/store-assets/capturas/` (cmp). Runbook added to
  `docs/play-store-publication-texts.md`, CLAUDE.md bullet, capture README documents the copy step.
  Dynamic color claim omitted from the listing: not found in `ui/theme` (CLAUDE.md says otherwise).

## Next step

T3.
