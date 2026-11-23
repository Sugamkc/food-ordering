# Project Standards — FoodPal

| Version | Date | Author | Change |
| --- | --- | --- | --- |
| 1.0 | 2026-11-23 | Paudel Roshan, KC Sugam | First version |

## 1. Purpose

These standards keep documents, code and team work consistent. All members, and any AI tool they use, must follow them.

## 2. Documentation Standards

### 2.1 File naming

- Planning documents: number prefix, words joined with underscores, for example `1_System_Request.md`, `4_Project_Standards.md`.
- Other documents: `SRS.md`, `RTM.md`, `Test_Plan.md`, `Design_UI.md`, `FoodPal-Documentation.md`.
- Format: Markdown (`.md`), kept inside the correct SDLC folder (Planning, Analysis, Design, Implementation) under `docs/`.

### 2.2 Document structure

- Every document starts with a title and a version table (as at the top of this file).
- Use numbered headings (1., 1.1) and tables for lists of items.
- Requirement IDs follow one pattern: FR-01 (functional), NFR-01 (non-functional), UC-01 (use case), TC-01 (test case). IDs are never reused or renumbered.
- Mark every number as *measured*, *sourced* or *assumed*. Never publish survey or test numbers that were not collected.
- Currency is shown in Nepalese rupees as `Rs`.

### 2.3 Review

- Every document is reviewed by at least one other member before it is final.
- KC Sugam checks format and consistency; Paudel Roshan checks technical content.

## 3. Git and GitHub Standards

### 3.1 Branches

| Branch | Use | Example |
| --- | --- | --- |
| `main` | Stable, always working. No direct commits | |
| `feature/<short-name>` | New features | `feature/admin-app` |
| `fix/<short-name>` | Bug fixes | `fix/khalti-return` |
| `docs/<short-name>` | Documents | `docs/srs` |

### 3.2 Commit messages

Format: `type: short description` (present tense, under 70 characters).

| Type | Use for |
| --- | --- |
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Document change |
| `refactor` | Code change without behavior change |
| `test` | Tests |
| `chore` | Setup, config, cleanup |

Example: `feat: add free gift rule to cart`

### 3.3 Pull requests

- Work is merged into `main` only through a pull request (PR).
- A PR needs at least one approval from the other member.
- The PR title follows the commit format; the description says what changed and how it was tested.
- Keep PRs small (one feature or one document).

### 3.4 Tasks

- Every task is a GitHub Issue with an owner. Branches and PRs refer to the issue number.
- Tag the mid-defence and final builds, for example `v0.1-mid-defence`.

### 3.5 Repository hygiene

- `.gitignore` must include: `local.properties`, `build/`, `.gradle/`, `.idea/`, `*.keystore`, and any file holding the Khalti key or private Firebase settings.
- No large or generated files, and no personal data, in the repository.

## 4. Coding Standards (Kotlin and Android)

### 4.1 Naming

| Item | Style | Example |
| --- | --- | --- |
| Classes and objects | PascalCase | `CartRepository`, `KhaltiClient` |
| Functions and variables | camelCase | `loadDishes`, `cartItems` |
| Constants | UPPER_SNAKE_CASE | `GIFT_MIN_SUBTOTAL` |
| Packages | lowercase | `com.foodpal.ui` |
| Resource files and IDs | snake_case | `ic_dish_momo`, `strings.xml` |

### 4.2 Structure

- Package by layer inside `app/src/main/java/com/foodpal/`: `ui` (screens), `data` (Firebase repositories), `payment` (Khalti client and deep link handler), `domain` (cart rules, free gift rule, totals), `util`.
- Business rules (cart totals, free gift at Rs 1000, delivery fee) live in `domain`, not in screens.
- Payment code stays in one module so a Khalti API change touches one place.

### 4.3 Code quality

- No hardcoded user-visible text; use `strings.xml`.
- No unused code, and no commented-out blocks left in `main`.
- Functions do one thing and stay short and readable.
- Comments explain why, not what. Public classes have a short description.
- Handle error, loading and empty states (no internet, empty cart, payment failure, location denied) on every screen that loads data.
- Format code with Android Studio's default formatter (Code > Reformat Code) before each commit.

### 4.4 UI

- Follow the FoodPal visual identity: dark background (`#1C0A08`), orange accent (`#FF5733`), navy (`#2C4558`) and cream (`#FDF3E4`) surfaces, pill-shaped buttons, Roboto type.
- Use orange for large or bold text only; small text on light surfaces uses a darker orange or navy.
- Touch targets are at least 48 dp; text stays readable at large sizes.
- Every fee, tax and total is shown in the cart before payment.
- Prevent errors: no order with an empty cart or without a destination.

## 5. Security Standards

- The Khalti secret key and other secrets live only in `local.properties` (not committed). The app reads them through `BuildConfig` during development.
- Before public release, Khalti initiate and lookup calls move to a Firebase Cloud Function so the secret key leaves the app.
- An order is never marked PAID without a verified Khalti lookup using the `pidx`.
- Firestore and Realtime Database rules limit each user to their own cart, addresses and orders.
- Location is requested only when the user picks GPS delivery.
- If a key is committed by mistake, tell the lead immediately and create a new key; deleting the commit is not enough.
- Use sample data in tests; no real personal data of users.
- Keep HTTPS for every network call.

## 6. Testing Standards

- Each requirement has at least one test case (see the RTM).
- Payment tests use the Khalti test environment.
- A feature is done only when: it works on a device or emulator, it has passed its test case, its PR is approved, and it is merged into `main`.
- Bugs are reported as GitHub Issues with steps to reproduce.
- Usability is measured with the System Usability Scale (SUS); the target is a score of 68 or higher.

## 7. AI Tool Usage Standards

- Team members may use AI tools, but are responsible for all code and text they submit. Understand it, test it and fix it before committing.
- Start each AI session with the shared project context so all AI tools follow the same decisions.
- Do not paste API keys, the Khalti secret key or private data into AI tools.
- AI-generated code must follow the standards in this document; reformat or correct it if not.
- Project decisions are made by the team, not by an AI tool. A new decision is shared with both members.

## 8. Communication Standards

- Weekly meeting with the supervisor and the team; a short update in the team chat when a task starts, is blocked or is finished.
- Decisions that change scope, features or documents are written down (in the relevant document, the change log or an Issue) and announced to the team.
- Scope changes after the mid-defence are logged with date, reason and decision.
- Reply to messages about blocking issues within one day.
