# Requirements Traceability Matrix (RTM) — FoodPal

| Version | Date | Author | Change |
| --- | --- | --- | --- |
| 1.0 | 2026-11 | Project team (KC Sugam, Paudel Roshan) | First version: requirements, use cases, design, test case IDs |

## 1. Purpose

This matrix links every requirement in the FoodPal documentation (section 7 and 8) to its source, use case, design screen, test case, code and test result. It shows that nothing in the requirements is forgotten and nothing is built without a reason. It is updated in each phase.

## 2. How to Read It

- **Source:** O-1 to O-7 are the project objectives in section 3.2 of the FoodPal documentation (O-1 menu, O-2 pay and track, O-3 fast delivery, O-4 delivery destination, O-5 free gift, O-6 administrator tools, O-7 SUS usability evaluation). "Scope" means a feature in the scope lists of section 3.3 or 3.4.
- **Status:** *Built* or *Planned*, as in the requirement list.
- **Priority:** Must = "shall" in the requirement list; Should = "should".
- **Use case:** UC IDs are proposed in section 5 of this matrix. The team should confirm them.
- **Design:** SCR IDs are the 15 screens of the UI design (SCR-01 Welcome, SCR-02 Log in and sign up, SCR-03 Home, SCR-04 Category list, SCR-05 Dish details, SCR-06 Added to cart, SCR-07 Cart, SCR-08 Checkout, SCR-09 Map picker, SCR-10 Payment redirect, SCR-11 Payment confirmed, SCR-12 Orders, SCR-13 Tracking, SCR-14 Rider chat, SCR-15 Profile). "Pending" means the screen is not drawn yet (administrator and rider screens, push notifications).
- **Test case:** IDs are reserved now (TC-01 to TC-28 for FR-01 to FR-28, TC-29 to TC-40 for NFR-01 to NFR-12). The Test Plan defines them. The 15 sample test cases in section 14.2 of the FoodPal documentation are drafts and will be mapped to these IDs in the Test Plan.
- **Code and Result:** "-" means not recorded yet. Code = pull request or module, Result = Pass or Fail. Formal test results are planned, so every Result is "-".

## 3. Functional Requirements

| ID | Requirement | Priority | Status | Source | Use case | Design | Test case | Code | Result |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| FR-01 | Register with name, email and password | Must | Built | Scope | UC-01 | SCR-02 | TC-01 | - | - |
| FR-02 | Log in and log out | Must | Built | Scope | UC-01 | SCR-02, SCR-15 | TC-02 | - | - |
| FR-03 | Show the five categories on the home screen | Must | Built | O-1 | UC-02 | SCR-03 | TC-03 | - | - |
| FR-04 | List the dishes of a category | Must | Built | O-1 | UC-02 | SCR-04 | TC-04 | - | - |
| FR-05 | Filter dishes as the user types in the search bar | Must | Built | O-1 | UC-02 | SCR-03 | TC-05 | - | - |
| FR-06 | Show dish photo, description, price, old price, discount, rating, preparation time and calories | Must | Built | O-1 | UC-02 | SCR-05 | TC-06 | - | - |
| FR-07 | Set a quantity and add the dish to the cart | Must | Built | O-1 | UC-03 | SCR-05, SCR-06 | TC-07 | - | - |
| FR-08 | Change or remove cart items | Must | Built | O-1 | UC-03 | SCR-07 | TC-08 | - | - |
| FR-09 | Calculate subtotal, tax, delivery fee and total | Must | Built | O-1 | UC-03 | SCR-07 | TC-09 | - | - |
| FR-10 | Add two free Coca-Cola 250 ml at Rs 1000 or more; remove them below Rs 1000 | Must | Built | O-5 | UC-03 | SCR-07 | TC-10 | - | - |
| FR-11 | Offer Standard (Rs 100, 25 to 30 min) and Fast Delivery (Rs 100 extra, 10 to 15 min) | Must | Built | O-3 | UC-03 | SCR-07 | TC-11 | - | - |
| FR-12 | Choose the destination by GPS, map pin or saved address | Must | Built | O-4 | UC-03 | SCR-08, SCR-09 | TC-12 | - | - |
| FR-13 | Pay by cash on delivery or Khalti | Must | Built | O-2 | UC-03 | SCR-08 | TC-13 | - | - |
| FR-14 | Save each order as PENDING and change it to PAID after Khalti verification | Must | Built | O-2 | UC-03, UC-04 | SCR-10, SCR-11 | TC-14 | - | - |
| FR-15 | Verify a Khalti payment by lookup with the `pidx` before marking paid | Must | Built | O-2 | UC-04 | SCR-10 | TC-15 | - | - |
| FR-16 | Return the user to the app by deep link `foodpal://payment-success` | Must | Built | O-2 | UC-04 | SCR-10, SCR-11 | TC-16 | - | - |
| FR-17 | List the user's past and active orders | Must | Built | O-2 | UC-05 | SCR-12 | TC-17 | - | - |
| FR-18 | Show live order stages: confirmed, preparing, picked up, on the way | Must | Built | O-2 | UC-05 | SCR-13 | TC-18 | - | - |
| FR-19 | Let the customer and rider exchange chat messages on an order | Must | Built | O-2 | UC-06 | SCR-14 | TC-19 | - | - |
| FR-20 | Rate a completed delivery with stars and a note | Must | Built | O-2 | UC-07 | SCR-13 (rating popup) | TC-20 | - | - |
| FR-21 | View and edit the profile and saved addresses | Must | Built | Scope | UC-08 | SCR-15 | TC-21 | - | - |
| FR-22 | Administrator adds, edits, hides and deletes dishes and categories | Must | Planned | O-6 | UC-09 | Pending | TC-22 | - | - |
| FR-23 | Administrator views and updates incoming orders | Must | Planned | O-6 | UC-10 | Pending | TC-23 | - | - |
| FR-24 | Administrator assigns a rider to an order | Must | Planned | O-6 | UC-10 | Pending | TC-24 | - | - |
| FR-25 | Administrator creates and ends promotions | Must | Planned | O-6 | UC-11 | Pending | TC-25 | - | - |
| FR-26 | Send push notifications on status changes | Should | Planned | Scope | UC-05 | Pending | TC-26 | - | - |
| FR-27 | Let a customer cancel an order before preparation | Should | Planned | Scope | UC-05 | Pending (cancel button on SCR-12) | TC-27 | - | - |
| FR-28 | Show sales and rating reports to the administrator | Should | Planned | O-6 | UC-11 | Pending | TC-28 | - | - |

## 4. Non-Functional Requirements

| ID | Category | Requirement | Source | Design / where handled | Test case | Code | Result |
| --- | --- | --- | --- | --- | --- | --- | --- |
| NFR-01 | Performance | Menu and search results appear in under 2 seconds on a mid-range phone with 4G | O-1 (find a dish quickly) | SCR-03, SCR-04; data layer | TC-29 | - | - |
| NFR-02 | Reliability | An order is never marked PAID without a verified Khalti lookup | O-2 | Payment module; SCR-10 | TC-30 | - | - |
| NFR-03 | Usability | A first-time user places an order in under 3 minutes; SUS score of 68 or higher | O-7 | All screens | TC-31 | - | - |
| NFR-04 | Security | Passwords are handled by Firebase Authentication; Firestore rules limit a user to their own data | Constraint (Firebase) | Architecture; security rules | TC-32 | - | - |
| NFR-05 | Security | The Khalti secret key is not stored in the app; payment calls move to a backend function | Constraint (payment security) | Architecture (Cloud Function, planned) | TC-33 | - | - |
| NFR-06 | Compatibility | Runs on Android 8.0 or newer | Constraint (platform) | Architecture | TC-34 | - | - |
| NFR-07 | Availability | A clear message is shown when offline; backend depends on Firebase availability | Constraint (internet) | State screens | TC-35 | - | - |
| NFR-08 | Maintainability | Layered structure with separate UI, data and payment packages | Project Standards | Architecture | TC-36 | - | - |
| NFR-09 | Scalability | Firestore collections are structured by user and order so the pilot can grow without redesign | Constraint (pilot area) | Data model | TC-37 | - | - |
| NFR-10 | Accessibility | Text is readable at default and large sizes; touch targets are at least 48 dp | O-7 | UI design | TC-38 | - | - |
| NFR-11 | Privacy | Location is used only when the user chooses GPS delivery | O-4 | SCR-08, SCR-09 | TC-39 | - | - |
| NFR-12 | Localization | Prices are in Nepalese rupees (Rs); Nepali language support is planned | Constraint (Nepal context) | UI design | TC-40 | - | - |

## 5. Use Case to Requirement

| Use case | Name | Requirements |
| --- | --- | --- |
| UC-01 | Register and log in | FR-01, FR-02 |
| UC-02 | Browse and search the menu | FR-03, FR-04, FR-05, FR-06 |
| UC-03 | Place an order | FR-07 to FR-14 |
| UC-04 | Pay with Khalti | FR-14, FR-15, FR-16 |
| UC-05 | Track an order | FR-17, FR-18, FR-26, FR-27 |
| UC-06 | Chat with the rider | FR-19 |
| UC-07 | Rate a delivery | FR-20 |
| UC-08 | Manage profile and saved addresses | FR-21 |
| UC-09 | Manage menu (administrator) | FR-22 |
| UC-10 | Manage orders and riders (administrator) | FR-23, FR-24 |
| UC-11 | Manage promotions and reports (administrator) | FR-25, FR-28 |

## 6. Risk to Requirement

| Risk | What reduces it |
| --- | --- |
| R-01 Khalti secret key reachable from the app | NFR-05, NFR-02 |
| R-02 Khalti API change or outage | FR-14, FR-15, NFR-02 |
| R-03 Firebase outage or limits | NFR-07, NFR-09 |
| R-04 Scope growth | Priorities (Must, Should) in this matrix |
| R-05 Admin app too large for the time | FR-22 to FR-28 kept as Planned, built in priority order |
| R-09 Rider shortage | FR-24 |
| R-10 Wrong delivery addresses | FR-12, FR-19 |
| R-11 Fast Delivery not met in rush hour | FR-11 |
| R-12 Poor internet | NFR-07 |
| R-14 Wrong cart totals | FR-09, FR-10, FR-11 |
| R-15 Late testing | NFR-03, test case IDs TC-01 to TC-40 |

## 7. Status Summary

| Item | Count |
| --- | --- |
| Functional requirements | 28 (Must 25, Should 3) |
| Non-functional requirements | 12 |
| Functional requirements Built | 21 (FR-01 to FR-21) |
| Functional requirements Planned | 7 (FR-22 to FR-28) |
| Functional requirements with at least one screen | 21 (FR-01 to FR-21) |
| Functional requirements with no screen yet | 7 (FR-22 to FR-28) |
| Requirements with code recorded in this matrix | 0 (to be filled) |
| Requirements formally tested | 0 (formal tests are planned) |
