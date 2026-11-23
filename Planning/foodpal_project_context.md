Project: FoodPal

A Nepali-first food ordering and delivery Android app ("Authentic Nepali Food Delivered Fast"). It is a university semester project done by a two-member team, and it must follow the academic software engineering (SDLC) approach with formal documents.

**Team:** KC Sugam (2577525), Paudel Roshan (2577528).

## Tech

- Android (8.0 or newer), Kotlin, Android Studio
- Android views (not Jetpack Compose), Roboto type
- Firebase: Authentication, Cloud Firestore, Realtime Database; Cloud Functions (planned)
- Khalti ePayment API; Android location services and a map SDK
- JUnit, Android emulator and real devices for testing
- GitHub for version control

## Core features

1. **Account:** register, log in, log out, edit profile and saved addresses (email and password, Firebase Authentication).
2. **Menu:** 20 Nepali dishes in 5 categories (Momo, Dal Bhat, Chowmein, Sel Roti, Lassi and Tea) with live search; dish details show photo, price, old price and discount, rating, preparation time and calories.
3. **Cart:** quantity edit, subtotal, tax, delivery fee, total; Fast Delivery switch; free gift of two Coca-Cola 250 ml when the subtotal reaches Rs 1000 (removed if it falls below).
4. **Delivery:** Standard (Rs 100, 25 to 30 minutes) or Fast (Rs 100 extra, 10 to 15 minutes); destination by GPS, map pin or saved address.
5. **Payment:** cash on delivery or Khalti. The order is saved as PENDING and becomes PAID only after a verified Khalti lookup with the `pidx`; the app returns by deep link `foodpal://payment-success`.
6. **Orders:** order history, live status stages (confirmed, preparing, picked up, on the way), customer and rider chat, star rating with a note.

## Status (decided)

- **Built:** the customer app, covering all six features above.
- **Planned:** administrator app (menu, orders, riders, promotions, reports), rider interface, push notifications, cancellation and refund rules, and a Firebase Cloud Function that holds the Khalti secret key.
- **Out of scope:** multi-restaurant marketplace, iOS and web ordering, international payments, languages other than English for now.
- **Evidence still to collect:** formal test results, the SUS usability score and the survey.

## Architecture (decided)

Client and backend-as-a-service. Layers inside `com.foodpal`: `ui` (screens), `data` (Firebase repositories), `payment` (Khalti client and deep link handler), `domain` (cart rules, free gift rule, totals), `util`. Business rules live in `domain`, not in screens. Order items copy the dish name and price at order time, so later menu edits never change an old order.

## Screens (15, from the document)

Welcome, Log in and sign up, Home, Category list, Dish details, Added to cart, Cart, Checkout, Map picker, Payment redirect, Payment confirmed, Orders, Tracking, Rider chat, Profile.

## UI identity (decided)

Dark background `#1C0A08`, orange accent `#FF5733`, navy `#2C4558`, cream `#FDF3E4`, grey `#EBEBEB`, pill-shaped buttons, rounded cards, Roboto, food photography on every dish. Orange only for large or bold text. Touch targets at least 48 dp. Prices in Nepalese rupees (`Rs`). Every fee is visible in the cart before payment.

## Open points (do not decide alone; ask the team)

- The exact tax rule (the sample cart showed Rs 2 on Rs 2250).
- Whether the total Fast Delivery fee is Rs 200 (Rs 100 standard plus Rs 100 rush).

## Document process and order

Repo folders: Planning (System Request, Feasibility Analysis, Staffing Plan, Project Standards, Risk Assessment), Analysis (SRS), Design, Implementation. Order: System Request, Feasibility Analysis, Staffing Plan, Project Standards, Risk Assessment, SRS, Design, Implementation. The main documentation file is `FoodPal-Documentation`, with the requirement IDs FR-01, NFR-01, UC-01 and TC-01. File names: `foodpal_system_request.md`, `foodpal_feasibility_analysis.md`, `foodpal_staffing_plan.md`, `foodpal_project_standards.md`, `foodpal_risk_assessment.md`.

## Rules for the AI

1. Stay consistent with this context. Do not change decisions above unless asked.
2. Never put API keys in committed code. The Khalti secret key and other secrets go in `local.properties` (gitignored), and the Khalti calls move to a Cloud Function before public release. Remind the user if they paste a key.
3. Use proper academic software engineering terms and document structure.
4. Do not invent facts the user has not given (deadlines, grading, course details, costs, survey or test numbers). If a fact is needed, ask one short question.
5. Explain in simple language. Give one step at a time and wait before the next step. When code is needed, put it directly in the chat.
6. If the user mentions a new decision, remind them to share it with their teammate so everyone's AI stays in sync.
