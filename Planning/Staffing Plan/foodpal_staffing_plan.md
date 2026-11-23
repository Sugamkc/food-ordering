# Staffing Plan — FoodPal

## 1. Project Period

[Add start date] to [add end date]. A 16-week plan is used: the customer app was completed for the mid-defence (week 11), and the admin app, rider interface, testing and final defence follow.

## 2. Team Structure

| Name | Student ID | Primary Role | Supporting Role |
| --- | --- | --- | --- |
| Paudel Roshan | 2577528 | Project Lead / Project Manager | Android Developer (screens, cart, checkout, tracking), UI Designer |
| KC Sugam | 2577525 | Backend Developer (Firebase rules, Khalti, Cloud Functions) | Quality Assurance (QA) and Documentation, Tester |

*The role split is a suggestion based on the project document. Confirm or swap it before submission.*

## 3. Roles and Responsibilities

### Paudel Roshan — Project Lead and Android Developer

- Plans and tracks the whole project; keeps the schedule, task board and supervisor contact.
- Makes final technical decisions and resolves conflicts.
- Develops the Android app in Kotlin: menu, cart, free-gift rule, checkout, tracking, chat and rating screens, and the planned admin app and rider interface.
- Designs the mockups, visual identity and assets.
- Reviews pull requests and manages the GitHub repository.
- Leads the SRS and design documents.

### KC Sugam — Backend Developer and QA / Documentation

- Designs the Firebase data structure (users, dishes, orders, addresses, chat, ratings).
- Writes the Firestore and Realtime Database security rules so each user reads and writes only their own data.
- Builds the Khalti payment integration (initiate, lookup, deep link return) and moves it to a Cloud Function so the secret key leaves the app.
- Prepares the test plan and test cases, tests each feature and reports bugs.
- Runs the SUS usability survey and records the results.
- Maintains document quality and consistency, and prepares the final report and presentation material with the team.

## 4. Shared Responsibilities (both members)

- Write and review project documents in their own area.
- Follow the project standards (Git rules, naming, code style).
- Join the weekly meeting and update task status on time.
- Test their own work before submitting it.
- Never commit the Khalti secret key, `google-services.json` with private settings, or any keystore.

## 5. Work Allocation by Phase

| Phase | Main documents / work | Lead person |
| --- | --- | --- |
| Planning | System request, feasibility, staffing plan, risk assessment | Roshan (both review; Sugam formats) |
| Analysis | Requirement gathering, SRS, requirement-to-test traceability | Roshan and Sugam |
| Design | Mockups, architecture, data model | Roshan (UI, architecture), Sugam (data model) |
| Implementation 1 | Customer app, Firebase, Khalti | Roshan (app), Sugam (backend, payment) |
| Implementation 2 | Admin app, rider screens, Cloud Function | Roshan (apps), Sugam (Cloud Function, rules) |
| Testing | Test plan, test cases, SUS survey, bug reports | Sugam |
| Final | Final report, demo, presentation | Both |

## 6. Communication Plan

- Weekly meeting with the supervisor; extra short meetings when a deadline is close.
- Shared task board for daily task status.
- GitHub repository with issues for bugs and ideas.
- Code and document changes go through GitHub branches and pull requests, reviewed before merging to the main branch.

## 7. Staffing Risks and Handling

| Risk | Handling |
| --- | --- |
| Team of two: the lead carries management, app development and design | Sugam takes backend, payment and testing; tasks are split into small issues early |
| A member is absent or busy (exams, other courses) | Every area has a backup person; documents and code are kept in the shared repository |
| Uneven skill levels (for example Cloud Functions or Firebase rules) | Pair work and short knowledge-sharing; start with small tasks |
| Scope growth after the mid-defence | Change log and a fixed must-have list; the Cloud Function and admin app come first |
