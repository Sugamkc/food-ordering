# System Request — FoodPal

| | |
| --- | --- |
| **Project name** | FoodPal: Authentic Nepali Food Delivered Fast (Android food ordering and delivery app) |
| **Project sponsor** | Course Instructor (confirm) |
| **Project team** | KC Sugam (2577525), Paudel Roshan (2577528) |
| **Project period** | [Add start date] to [add end date] (a 16-week plan is used; the mid-defence marks the end of the customer app) |
| **Date** | November 2026 |

## 1. Business Need

People in Nepal who want a quick local meal face four recurring problems:

1. Choice is hidden behind restaurants. Apps ask the user to pick a restaurant first, which is slow when the only goal is a plate of momo.
2. Delivery fees and time are uncertain, and a late lunch costs a worker the whole break.
3. Payment is inconsistent. Some places take only cash while many people already use mobile wallets.
4. Addresses are informal. Many streets have no numbers, so the rider needs a map pin and a way to talk to the customer.

FoodPal is a Nepali-first Android app that treats Nepali dishes as the product: a short fixed menu of 20 dishes in five categories (momo, dal bhat, chowmein, sel roti, lassi and tea), clear prices, two delivery speeds, two payment methods, a map-based address and a live line to the rider.

## 2. Business Requirements

The system should allow customers to:

- Register, log in, log out, and view or edit their profile and saved addresses.
- Browse five food categories and search dishes as they type.
- View dish details: photo, description, price, old price and discount, rating, preparation time and calories.
- Set a quantity, add dishes to the cart, and change or remove cart items.
- See subtotal, tax, delivery fee and total before paying.
- Receive two free Coca-Cola 250 ml bottles when the subtotal reaches Rs 1000, removed again if it falls below.
- Choose Standard Delivery (Rs 100, 25 to 30 minutes) or Fast Delivery (Rs 100 extra, 10 to 15 minutes).
- Choose the delivery destination by GPS, map pin or saved address.
- Pay by cash on delivery or Khalti; a Khalti order is marked PAID only after a verified lookup.
- View past and active orders, follow live order stages, chat with the rider, and rate a completed delivery.

The system should also allow administrators (planned) to:

- Add, edit, hide and delete dishes and categories.
- View and update incoming orders and assign riders.
- Create and end promotions and view sales and rating reports.

## 3. Business Value

**Tangible value** (expected, to be measured in testing)

- Faster ordering of a local meal: from home to a placed order in about six taps.
- Fewer delivery misunderstandings, because of the map pin and built-in rider chat.
- Higher order value, because of the Rs 1000 free-gift offer.
- Low development cost, using free or low-cost tiers (Kotlin, Android Studio, Firebase).

**Intangible value**

- Better user experience through clear prices, honest fees and live tracking.
- A local-food focus that serves students, office workers, families and visitors.
- Part-time delivery work and a sales channel for small kitchens.
- Team gains practical experience in Android development, payment integration and software engineering documentation.

## 4. Special Issues or Constraints

- **Platform:** Android only (Android 8.0 or newer), built with Kotlin in Android Studio.
- **Backend:** Firebase (Authentication, Firestore, Realtime Database) and Khalti ePayment; both depend on internet access and service availability.
- **Payment security:** The Khalti secret key must not stay in the app; payment calls are to move to a backend function (Firebase Cloud Function) and keys must not be committed to the public repository.
- **Legal:** Business registration, PAN or VAT registration and food business licensing are needed before a real launch; Nepal's Electronic Transactions Act, 2063 applies.
- **Scope:** Customer app first; administrator app, rider interface, push notifications and cancellation and refund rules are planned. Multi-restaurant onboarding, iOS and web ordering are out of scope.
- **Operations:** The first release is a small pilot delivery area with riders arranged by the team or a partner.
- **Time and team:** The project must be completed by a two-member student team within the academic schedule.
