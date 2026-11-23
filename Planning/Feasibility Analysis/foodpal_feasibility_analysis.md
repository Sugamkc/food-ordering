# Feasibility Analysis — FoodPal

## 1. Technical Feasibility

**Familiarity with the application.** The team is building a food ordering and delivery app. The core patterns (menu, search, cart, checkout, order history) are common and well understood. Wallet payment (Khalti), live tracking and rider chat were the newer parts, and the customer flow has already been built and demonstrated end to end, which is the strongest evidence of technical feasibility.

**Familiarity with technology**

| Technology | Purpose | Assessment |
| --- | --- | --- |
| Kotlin, Android Studio | App development | Mature, well documented, widely used; the team already uses them |
| Firebase Authentication | Sign up and log in | Ready service, no custom password code |
| Cloud Firestore | Users, dishes, orders, addresses, ratings | Structured documents suit the data |
| Firebase Realtime Database | Live order status and chat | Low latency, suits tracking and chat |
| Android location services and map SDK | GPS and map pin for delivery | Standard APIs |
| Khalti ePayment API | Wallet payment | Documented initiate and lookup calls |
| Firebase Cloud Functions (planned) | Keep the Khalti secret key off the phone | Needed before public release |

**Project size.** Medium for a two-member student team. Main modules: authentication, menu and search, cart and free-gift rule, checkout and destination, Khalti payment, orders, tracking, rider chat and rating. The planned administrator app and rider interface are the remaining work. Size is controlled by a fixed menu of 20 dishes in five categories and one kitchen model.

**Compatibility.** The app targets Android 8.0 or newer only. It depends on Firebase and Khalti over the internet. No iOS, web or multi-restaurant integration is planned.

**Technical risk areas**

- Dependence on third-party services (Firebase, Khalti) and on internet quality. Mitigation: clear offline messages and retries.
- Khalti sandbox or API changes. Mitigation: keep payment code in one module and read current documentation.
- The Khalti secret key is currently reachable from the app build. Mitigation: move initiate and lookup calls to a Cloud Function.

**Conclusion:** Technically feasible and demonstrated for the customer app. The main risk is payment-key security, which has a defined fix.

## 2. Economic Feasibility

All amounts below are planning assumptions and must be replaced with real quotes before the final defence.

**Development costs (one-time)**

| Item | Assumption |
| --- | --- |
| Development laptops and phones | Already owned, no cost |
| Google Play developer registration | One-time fee of USD 25, only if the app is published |
| Design assets and fonts | No cost (open fonts, own photos) |
| Testing devices and data | Small cost, to be estimated by the team |
| Khalti merchant onboarding | To be confirmed against current terms |
| Labor | Student team time, no salary cost |

**Operational costs (pilot, if deployed)**

- Firebase: free tier expected at pilot scale; paid use if volume grows.
- Map and SMS usage: small.
- Part-time riders (two suggested): the largest running cost.
- Marketing through social media: small.

**Revenue model.** A delivery fee of Rs 100 per order (Rs 200 for Fast Delivery) plus a margin on food. Food margin depends on the agreement with kitchens and is the main unknown.

**Benefits**

- Tangible: low development cost; delivery income and food margin if deployed; larger orders encouraged by the Rs 1000 free-gift rule (to be measured).
- Intangible: a Nepali-first service with clear prices, part-time delivery work, a sales channel for small kitchens, and practical learning in Android, payment integration and software engineering.

**Conclusion:** Economically feasible as a pilot. Development cost is low, and profitability at scale needs real cost data.

## 3. Legal Feasibility

- **Business registration:** A food delivery business in Nepal needs company or firm registration and a PAN or VAT registration with the Inland Revenue Department.
- **Food licensing:** Kitchens need a food business licence and hygiene compliance from the local government and the Department of Food Technology and Quality Control.
- **Electronic transactions:** Online contracts and records fall under Nepal's Electronic Transactions Act, 2063 (2008).
- **Payments:** Wallet payments go through Khalti; the app never stores card or wallet credentials.
- **Privacy:** Collect only name, email, phone and address, state this in a privacy policy, and ask permission for location.
- **Consumer rights:** Show prices, taxes and fees before payment and provide a way to complain.
- **App store terms:** Follow Google Play policies on payments, data safety and content.

This is general planning information, not legal advice; confirm with a lawyer or the relevant office before launch.

**Conclusion:** Legally feasible with registration, food licensing and tax setup.

## 4. Organizational and Operational Feasibility

**Stakeholders**

| Stakeholder | Interest |
| --- | --- |
| Course instructor (sponsor) | Project meets course requirements and the software engineering process |
| Development team (KC Sugam, Paudel Roshan) | Complete the project and learn |
| Customers (students, office workers, families, visitors) | Quick local food, clear prices, easy payment, live tracking |
| Riders | Clear pickup and drop point, chat with the customer |
| Kitchens and administrators | Accurate menu and orders (administrator app planned) |

**Fit with user behaviour.** Users already use smartphones, mobile data and wallets, so ordering through an app needs no new habit.

**Daily operation.** The team needs kitchens or partner restaurants, riders, and one person to watch orders. Kitchen and rider staff need a half-day orientation on the admin and rider screens.

**Support.** A phone number and in-app chat handle problems. The first month focuses on a small delivery zone to keep delivery times promised.

**Operational risks.** Rush-hour demand, rider shortages and wrong addresses. Mitigation: map pin, rider chat and a cap on Fast Delivery orders per hour.

**Conclusion:** Organizationally and operationally feasible in a small pilot zone.

## 5. Schedule Feasibility

- The customer app was completed for the mid-defence (end of increment one).
- Remaining work: administrator app, rider interface, backend function for the Khalti key, testing and the SUS usability survey.
- A 16-week plan is used: topic and research (weeks 1 to 2), requirements (3 to 4), design (5 to 6), customer app build (7 to 10), mid-defence (11), admin app and rider screens (12 to 14), testing and survey (15), final report and defence (16).
- Features are prioritised: the customer app is done; the Cloud Function and admin app come first, then the rider interface and push notifications.

**Conclusion:** Feasible if the scope in the planned list is held and tasks are divided early between the two team members.

## 6. Social and Environmental Feasibility

The app creates part-time delivery work and gives home cooks and small kitchens a way to sell. Delivery by motorbike adds traffic and emissions; batching nearby orders is a future improvement.

## 7. Feasibility Summary

| Dimension | Verdict | Key risk |
| --- | --- | --- |
| Technical | Feasible, demonstrated | Third-party service outages; secret key in the app |
| Economic | Feasible for a pilot | Unknown food margin |
| Legal | Feasible with registration | Food licence and tax setup |
| Organizational and operational | Feasible in a small zone | Rider supply at peak hours |
| Schedule | Feasible if scope holds | Size of the admin app |

## 8. Overall Conclusion

FoodPal is feasible technically, economically, legally, organizationally and in schedule, under these conditions:

- The Khalti secret key is moved to a Cloud Function before public release.
- Business registration and food licensing are completed before a real launch.
- The first release stays in a small pilot area with a fixed 20-dish menu.
- Every fee and tax is shown before payment.
- Features are prioritised so a working core stays delivered.

**Recommendation:** Proceed with the project.
