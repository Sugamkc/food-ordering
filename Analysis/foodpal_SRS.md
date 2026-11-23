# Software Requirements Specification (SRS) — FoodPal

| Version | Date | Author | Change |
| --- | --- | --- | --- |
| 1.0 | 2026-11 | Project team (KC Sugam, Paudel Roshan) | First version |

## 1. Introduction

### 1.1 Purpose

This document specifies the requirements of FoodPal, a Nepali-first food ordering and delivery Android app. It describes what the system must do and the qualities it must have. It is the basis for the Design, Implementation and Testing phases.

### 1.2 Scope

FoodPal lets a customer order Nepali dishes, pay by cash on delivery or Khalti, follow the order live, chat with the rider and rate the delivery. It lets administrators (planned) manage the menu, orders, riders and promotions.

**In scope:** customer accounts, menu and search, dish details, cart with the free-gift rule, delivery speed, delivery destination (GPS, map pin, saved address), cash and Khalti payment, orders, live tracking, rider chat, rating, and the planned administrator functions.

**Out of scope:** multi-restaurant marketplace onboarding and settlement, iOS and web ordering, international payments, and languages other than English at this stage.

### 1.3 Definitions

| Term | Meaning |
| --- | --- |
| Customer | The person ordering food |
| Rider | The courier who delivers the order |
| Administrator | The restaurant or platform operator who manages menu, orders, riders and promotions |
| Order | A saved request with items, fees, total, payment method and destination |
| PENDING / PAID | Order payment states; a Khalti order becomes PAID only after a verified lookup |
| Khalti | Nepali wallet payment provider (ePayment API with initiate and lookup calls) |
| `pidx` | The payment identifier returned by Khalti |
| Standard / Fast Delivery | Delivery speeds: 25 to 30 minutes, or 10 to 15 minutes with a rush fee |
| Free gift | Two Coca-Cola 250 ml bottles added at a subtotal of Rs 1000 or more |
| FR / NFR / UC | Functional Requirement / Non-Functional Requirement / Use Case |
| O-1 to O-7 | Project objectives listed in section 3.2 of the FoodPal documentation |

### 1.4 References

- Planning documents: System Request, Feasibility Analysis, Staffing Plan, Project Standards, Risk Assessment.
- FoodPal Project Documentation (mid-defence version).
- Khalti ePayment API documentation; Firebase documentation.
- Structure follows the common IEEE-style SRS outline.

## 2. Overall Description

### 2.1 Product Perspective

FoodPal is a new standalone Android app with a backend-as-a-service architecture. The app holds the screens and business rules. Firebase supplies identity (Authentication), data (Cloud Firestore) and live status and chat (Realtime Database). Khalti handles wallet payment. Android location services and a map SDK provide GPS and the map pin. The administrator app, the rider interface and a Cloud Function that will hold the Khalti secret key are planned.

### 2.2 User Classes

| User | Description | Main needs |
| --- | --- | --- |
| Customer | Office or shift worker, student, family or visitor | Find a dish quickly, see the real price and time, pay the usual way, track the order |
| Rider | Courier (planned rider interface) | Exact pin and a quick way to ask the customer for a landmark |
| Administrator | Operator (planned admin app) | Keep menu and orders accurate, assign riders, run offers |

### 2.3 Operating Environment

Android smartphone running Android 8.0 or newer, with an internet connection. Location permission is needed only for GPS delivery.

### 2.4 Constraints

- Kotlin and Android Studio.
- Firebase and a Khalti merchant account.
- Free or low-cost Firebase tiers during development.
- Prices in Nepalese rupees (Rs).
- A small two-member student team and a short academic schedule.

### 2.5 Assumptions and Dependencies

| ID | Assumption |
| --- | --- |
| A-01 | Standard Delivery costs Rs 100; Fast Delivery adds Rs 100. The team must confirm that the total Fast Delivery fee is Rs 200. |
| A-02 | The tax rule is not final (the sample cart showed Rs 2 on Rs 2250) and must be confirmed. |
| A-03 | The first release covers a small pilot delivery area; riders are arranged by the team or a partner. |
| A-04 | The menu has 20 dishes in five categories with one kitchen model. |
| A-05 | The UI language is English. |
| A-06 | Firebase and Khalti are available and their free tiers or test environment are enough for development and the demo. |
| A-07 | Administrator and rider accounts are created by the team (no public sign-up for these roles). |

## 3. Specific Requirements

Priority: Must = "shall" (mandatory for the final project), Should = "should" (desirable). Status shows *Built* or *Planned*. Source points to a project objective (O-1 to O-7) or to the scope lists in section 3.3 and 3.4 of the FoodPal documentation.

### 3.1 Functional Requirements

#### Account and Profile

| ID | Requirement | Priority | Status | Source |
| --- | --- | --- | --- | --- |
| FR-01 | The system shall let a new user register with name, email and password. | Must | Built | Scope |
| FR-02 | The system shall let a registered user log in and log out. | Must | Built | Scope |
| FR-21 | The system shall let the user view and edit the profile and saved addresses. | Must | Built | Scope |

#### Menu and Dish Details

| ID | Requirement | Priority | Status | Source |
| --- | --- | --- | --- | --- |
| FR-03 | The system shall show the five categories on the home screen. | Must | Built | O-1 |
| FR-04 | The system shall list the dishes of a category. | Must | Built | O-1 |
| FR-05 | The system shall filter dishes as the user types in the search bar. | Must | Built | O-1 |
| FR-06 | The system shall show a dish's photo, description, price, old price, discount, rating, preparation time and calories. | Must | Built | O-1 |

#### Cart, Offer and Delivery Speed

| ID | Requirement | Priority | Status | Source |
| --- | --- | --- | --- | --- |
| FR-07 | The system shall let the user set a quantity and add the dish to the cart. | Must | Built | O-1 |
| FR-08 | The system shall let the user change or remove cart items. | Must | Built | O-1 |
| FR-09 | The system shall calculate subtotal, tax, delivery fee and total. | Must | Built | O-1 |
| FR-10 | The system shall add two Coca-Cola 250 ml free when the subtotal is Rs 1000 or more and remove them if it falls below. | Must | Built | O-5 |
| FR-11 | The system shall offer Standard Delivery (Rs 100, 25 to 30 minutes) and Fast Delivery (Rs 100 extra, 10 to 15 minutes). | Must | Built | O-3 |

#### Checkout and Payment

| ID | Requirement | Priority | Status | Source |
| --- | --- | --- | --- | --- |
| FR-12 | The system shall let the user choose the delivery destination by GPS, map pin or saved address. | Must | Built | O-4 |
| FR-13 | The system shall let the user pay by cash on delivery or Khalti. | Must | Built | O-2 |
| FR-14 | The system shall save each order as PENDING and change it to PAID after Khalti verification. | Must | Built | O-2 |
| FR-15 | The system shall verify a Khalti payment by a lookup with the payment identifier (`pidx`) before marking an order paid. | Must | Built | O-2 |
| FR-16 | The system shall return the user to the app after payment through the deep link `foodpal://payment-success`. | Must | Built | O-2 |

#### Orders, Tracking, Chat and Rating

| ID | Requirement | Priority | Status | Source |
| --- | --- | --- | --- | --- |
| FR-17 | The system shall list the user's past and active orders. | Must | Built | O-2 |
| FR-18 | The system shall show live order stages: confirmed, preparing, picked up, on the way. | Must | Built | O-2 |
| FR-19 | The system shall let the customer and rider exchange chat messages on an order. | Must | Built | O-2 |
| FR-20 | The system shall let the user rate a completed delivery with stars and a note. | Must | Built | O-2 |

#### Administrator and Extra Functions

| ID | Requirement | Priority | Status | Source |
| --- | --- | --- | --- | --- |
| FR-22 | The administrator shall add, edit, hide and delete dishes and categories. | Must | Planned | O-6 |
| FR-23 | The administrator shall view and update incoming orders. | Must | Planned | O-6 |
| FR-24 | The administrator shall assign a rider to an order. | Must | Planned | O-6 |
| FR-25 | The administrator shall create and end promotions. | Must | Planned | O-6 |
| FR-26 | The system should send push notifications on status changes. | Should | Planned | Scope |
| FR-27 | The system should let a customer cancel an order before preparation. | Should | Planned | Scope |
| FR-28 | The system should show sales and rating reports to the administrator. | Should | Planned | O-6 |

### 3.2 Non-Functional Requirements

Numbers marked as target are goals to be verified in testing.

| ID | Category | Requirement |
| --- | --- | --- |
| NFR-01 | Performance | Menu and search results shall appear in under 2 seconds on a mid-range phone with 4G (target). |
| NFR-02 | Reliability | An order shall never be marked PAID without a verified Khalti lookup. |
| NFR-03 | Usability | A first-time user shall be able to place an order in under 3 minutes (target); the SUS score shall be 68 or higher (target). |
| NFR-04 | Security | Passwords shall be handled by Firebase Authentication; Firestore rules shall limit a user to their own data. |
| NFR-05 | Security | The Khalti secret key shall not be stored in the app; payment calls shall move to a backend function. |
| NFR-06 | Compatibility | The app shall run on Android 8.0 or newer. |
| NFR-07 | Availability | The backend depends on Firebase availability; the app shall show a clear message when offline. |
| NFR-08 | Maintainability | Code shall follow a layered structure with separate UI, data and payment packages, and the Project Standards. |
| NFR-09 | Scalability | Firestore collections shall be structured by user and order so the pilot can grow without redesign. |
| NFR-10 | Accessibility | Text shall be readable at default and large sizes; touch targets shall be at least 48 dp. |
| NFR-11 | Privacy | Location shall be used only when the user chooses GPS delivery. |
| NFR-12 | Localization | Prices shall be in Nepalese rupees (Rs); Nepali language support is planned. |

### 3.3 External Interface Requirements

| Type | Description |
| --- | --- |
| User interface | Android app following the FoodPal visual identity (dark background, orange accent, navy and cream surfaces, pill-shaped buttons, Roboto). 15 customer screens from Welcome to Profile; administrator and rider screens are planned. |
| Hardware | Phone GPS and internet connection. |
| Software | Firebase Authentication, Cloud Firestore, Realtime Database, Cloud Functions (planned), Khalti ePayment API, Android location services and a map SDK. |
| Communication | HTTPS for every network call; deep link `foodpal://payment-success` for the Khalti return. |

### 3.4 Data Requirements (logical)

| Entity | Main data |
| --- | --- |
| USER | userId, name, email, phone, role (customer, rider, administrator), createdAt |
| ADDRESS | addressId, userId, label, latitude, longitude, text |
| CATEGORY | categoryId, name, iconUrl |
| FOOD_ITEM | foodId, categoryId, name, description, price, oldPrice, discountPercent, rating, prepMinutes, calories, imageUrl, available |
| CART_ITEM | userId, foodId, quantity, isGift |
| ORDER | orderId, userId, status, deliverySpeed, subtotal, tax, deliveryFee, total, paymentMethod, addressId, createdAt |
| ORDER_ITEM | orderId, foodName, unitPrice, quantity, isGift (copies name and price at order time) |
| PAYMENT | paymentId, orderId, method, pidx, status, amount |
| RIDER and DELIVERY | riderId, userId, vehicle, available; deliveryId, orderId, riderId, stage, etaMinutes |
| CHAT_MESSAGE | messageId, orderId, senderId, text, sentAt |
| REVIEW | reviewId, orderId, stars, note |
| PROMOTION | promoId, rule, giftFoodId, minSubtotal |

## 4. Use Cases

### 4.1 Use Case List

| ID | Use case | Actor |
| --- | --- | --- |
| UC-01 | Register and log in | Customer |
| UC-02 | Browse and search the menu | Customer |
| UC-03 | Place an order | Customer |
| UC-04 | Pay with Khalti | Customer, Khalti |
| UC-05 | Track an order | Customer |
| UC-06 | Chat with the rider | Customer, Rider |
| UC-07 | Rate a delivery | Customer |
| UC-08 | Manage profile and saved addresses | Customer |
| UC-09 | Manage menu | Administrator |
| UC-10 | Manage orders and riders | Administrator |
| UC-11 | Manage promotions and reports | Administrator |

### 4.2 Main Use Cases

**UC-03 Place an order**

- Actor: Customer. Precondition: Customer is logged in.
- Main flow: (1) Customer adds dishes to the cart. (2) System recalculates subtotal, tax, delivery fee and total, and adds or removes the free gift at Rs 1000. (3) Customer chooses Standard or Fast Delivery. (4) Customer chooses the destination by GPS, map pin or saved address. (5) Customer chooses cash or Khalti. (6) System saves the order as PENDING and confirms it.
- Alternate flow: Empty cart or no destination → system blocks the order and shows a message. Location permission denied → system offers the map pin or a saved address. Payment fails → order stays PENDING and the user can retry.
- Related: FR-07 to FR-14, NFR-11.

**UC-04 Pay with Khalti**

- Actor: Customer, Khalti. Precondition: The order is saved as PENDING.
- Main flow: (1) System calls Khalti initiate with the amount in paisa, order id and return URL, and receives a payment URL and `pidx`. (2) Customer pays in Khalti. (3) Khalti returns to `foodpal://payment-success`. (4) System calls lookup with the `pidx`. (5) If the status is Completed, the order becomes PAID.
- Alternate flow: Customer closes Khalti, or the status is not Completed → the order stays PENDING and the customer is told.
- Related: FR-13 to FR-16, NFR-02, NFR-05.

**UC-05 Track an order**

- Actor: Customer. Precondition: An order exists.
- Main flow: (1) Customer opens Orders. (2) System lists active and past orders. (3) Customer opens an order. (4) System shows the live stage with placed time and estimate.
- Alternate flow: No rider yet → system shows "Kitchen Preparing".
- Related: FR-17, FR-18, FR-26, FR-27.

**UC-06 Chat with the rider**

- Actor: Customer, Rider. Precondition: An order exists.
- Main flow: (1) Customer opens the chat on the order. (2) Customer sends a message. (3) System shows the rider's reply.
- Alternate flow: Rider offline → the message is stored and delivered later.
- Related: FR-19.

**UC-07 Rate a delivery**

- Actor: Customer. Precondition: The delivery is completed.
- Main flow: (1) System shows the rating popup. (2) Customer chooses stars and adds a note. (3) System stores the rating on the order.
- Alternate flow: Customer skips → no rating is stored.
- Related: FR-20.

**UC-09 to UC-11 Administrator use cases (planned)**

- Actor: Administrator. Precondition: Administrator is logged in.
- Main flow: The administrator manages dishes and categories (UC-09), updates order status and assigns riders (UC-10), and creates or ends promotions and views reports (UC-11).
- Alternate flow: A required field is missing → system shows an error and does not save.
- Related: FR-22 to FR-25, FR-28.

## 5. Requirements Summary

| Type | Count | Must | Should | Built | Planned |
| --- | --- | --- | --- | --- | --- |
| Functional (FR-01 to FR-28) | 28 | 25 | 3 | 21 | 7 |
| Non-functional (NFR-01 to NFR-12) | 12 | | | | |

Priorities may change only through a team decision recorded in this document. The Requirements Traceability Matrix (RTM) links each requirement to design, code and test cases.
