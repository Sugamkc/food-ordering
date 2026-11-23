# Risk Assessment — FoodPal

| Version | Date | Author | Change |
| --- | --- | --- | --- |
| 1.0 | 2026-11 | Project team (KC Sugam, Paudel Roshan) | First version |

## 1. Purpose

This document lists the main risks of the FoodPal project, estimates how serious each one is, and defines how the team will prevent or handle it. It will be reviewed during the project and updated when new risks appear. The ratings below are proposed estimates; the team should confirm them.

## 2. Rating Method

Probability and impact are the team's estimates, rated Low, Medium or High.

| Probability \ Impact | Low | Medium | High |
| --- | --- | --- | --- |
| High | Medium | High | High |
| Medium | Low | Medium | High |
| Low | Low | Low | Medium |

Risk level = result of the table above.

## 3. Risk Register

| ID | Risk | Category | Probability | Impact | Level | Owner |
| --- | --- | --- | --- | --- | --- | --- |
| R-01 | Khalti secret key is reachable from the app build and could be extracted | Security | High | High | High | Sugam |
| R-02 | Khalti sandbox or API changes, or Khalti is unavailable | Technical | Medium | High | High | Sugam |
| R-03 | Firebase outage or free-tier limits are reached | Technical / Cost | Medium | Medium | Medium | Sugam |
| R-04 | Scope growth: the team keeps adding features after the mid-defence | Scope | High | Medium | High | Roshan |
| R-05 | Admin app and rider interface are too large for the remaining time | Schedule | Medium | High | High | Roshan |
| R-06 | A member is unavailable (exams, other courses, illness) in a team of two | Staffing | Medium | High | High | Roshan |
| R-07 | Lead is overloaded (management, app development, design) | Staffing | High | Medium | High | Roshan |
| R-08 | A key, `google-services.json` with private settings or a keystore is pushed to GitHub | Security | Medium | High | High | Both members |
| R-09 | Rider shortage in the pilot, especially at peak hours | Operations | Medium | High | High | Roshan |
| R-10 | Informal or wrong delivery addresses delay orders | Operations | Medium | Medium | Medium | Roshan |
| R-11 | Fast Delivery (10 to 15 minutes) is promised but not met in rush hour | Operations | Medium | Medium | Medium | Roshan |
| R-12 | Poor internet makes the app, payment or tracking fail | Technical | Medium | Low | Low | Sugam |
| R-13 | Food business licence or tax registration is missing before a real launch | Legal | Low | High | Medium | Roshan |
| R-14 | Cart totals are wrong (the tax rule and the Fast Delivery fee are still open points) | Quality | Medium | Medium | Medium | Roshan |
| R-15 | Bugs are found too late because formal tests and the SUS survey are not done yet | Quality | Medium | High | High | Sugam |
| R-16 | Documents become inconsistent with each other or with the code; competitor figures from 2021 are outdated | Quality | Medium | Medium | Medium | Sugam |
| R-17 | Git conflicts or lost work from poor branch handling | Process | Medium | Medium | Medium | Roshan |
| R-18 | Code quality problems from unchecked AI-generated code | Quality | Medium | Medium | Medium | Roshan |

## 4. Risk Response Plan

| ID | Prevention (before it happens) | Contingency (if it happens) |
| --- | --- | --- |
| R-01 | Move Khalti initiate and lookup calls to a Firebase Cloud Function. Keep the key only in `local.properties` until then. Do not publish the app before the move. | Create a new key at once and remove the old one from the build; tell the lead. |
| R-02 | Keep payment code in one module. Read the current Khalti documentation before changes. Test in the Khalti test environment. | Keep cash on delivery working; an unpaid Khalti order stays PENDING and the user can retry. |
| R-03 | Check current Firebase limits. Keep reads small (search filters the loaded list, no extra call). Structure collections by user and order. | Reduce reads, or move to a paid plan if the pilot needs it. |
| R-04 | Keep a fixed must-have list and a change log. New features need agreement of both members and a written change. | Move the change to the future-work list. |
| R-05 | Build the Cloud Function first, then the admin app, then the rider interface and push notifications. Set weekly goals in GitHub Issues. | Drop low-priority items first (push notifications, reports, rider live location). |
| R-06 | Every area has a backup person. Everything is kept in the shared repository. | Reassign tasks at the next meeting and reduce scope. |
| R-07 | Split tasks into small issues early. Sugam takes backend, payment and testing. | Move tasks to the other member; reduce scope. |
| R-08 | Add secrets to `.gitignore`. Check each pull request for secrets. | Treat the secret as exposed: create a new key and clean the history. Deleting the commit alone is not enough. |
| R-09 | Start with a small delivery zone. Arrange riders in advance with the team or a partner. | Pause new orders in the zone and cap Fast Delivery orders per hour. |
| R-10 | Offer GPS, map pin or saved address, and rider chat to ask for a landmark. | Rider calls or chats with the customer to confirm the pin. |
| R-11 | Cap Fast Delivery orders per hour. Show the estimate honestly and keep the fee visible. | Switch the order to Standard Delivery and tell the customer. |
| R-12 | Show a clear offline message and retry. Handle loading and error states on every screen. | Keep the order PENDING and retry payment lookup when the connection returns. |
| R-13 | Treat the app as a pilot until registration and licensing are done. Confirm requirements with a lawyer or the relevant office. | Delay public launch; continue as a demo prototype. |
| R-14 | Confirm the tax rule and the fast-fee total with the team. Cover totals with unit tests (TC04 to TC07). | Fix the domain-layer calculation and re-run the tests. |
| R-15 | Write test cases during requirements and design. Test each feature when it is merged. Plan the SUS survey with 10 to 15 users. | Reserve time before the deadline for a full test round and bug fixing. |
| R-16 | Use requirement IDs and a traceability matrix. Review documents before they are final. Refresh competitor figures from current pages. | Hold a consistency review of all documents before submission. |
| R-17 | Follow the Git rules in the Project Standards (branches, small pull requests). | Restore work from branch history and fix conflicts together. |
| R-18 | Every member must understand and test AI-generated code. Pull requests need one approval. | Rewrite or remove the problem code and add tests for it. |

## 5. Monitoring

- The risk register is reviewed at the weekly team meeting.
- New risks are added with the next free ID (R-19, R-20, ...). IDs are never reused.
- When a risk becomes real, the owner reports it to the lead and starts the contingency plan.
