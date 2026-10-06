# JAVA PROGRAMMING PROJECT REPORT

## Magazine & Newspaper Subscription Management System

**Student Name:** Arman Choudhary 
**Enrollment No.:** 150096725075  
**Programme:** B.Tech CSE (2025–29)  
**Semester:** III  
**Subject:** Java Programming  
**University:** ITM Skills University  
**Academic Year:** 2026–27

---

## 1. Introduction

The Magazine & Newspaper Subscription Management System is a Java desktop application made for a publishing house. The main idea is simple: instead of keeping subscriber details, plans, deliveries and renewal dates separately, the system keeps them together in one application.

The application has a Swing GUI with seven main sections. A user can register subscribers, select a subscription plan, schedule deliveries, check renewals, see expiry alerts, search records and generate delivery reports.

The project is intentionally built with Java SE only. It does not need a database or any external library, so it is easy to compile and demonstrate in a Java Programming lab.

## 2. Problem Statement

A publishing house needs a system to manage subscriber details, subscription plans, and delivery schedules for multiple magazine and newspaper titles. The system should also track renewal dates and flag subscriptions that are close to expiry.

The application therefore needs to provide:

- Subscriber information management
- Magazine and newspaper publication records
- Monthly, Quarterly and Annual subscription plans
- Delivery scheduling and delivery history
- Renewal date tracking
- Expiry alerts
- Search and sorting of records
- A graphical subscription management interface

## 3. Objectives

The main objectives of the project are:

1. To maintain subscriber and publication information.
2. To manage different subscription plans.
3. To calculate subscription cost and renewal dates.
4. To schedule and track deliveries.
5. To identify subscriptions that are about to expire.
6. To provide searching and sorting features.
7. To demonstrate the required Java concepts through a working GUI application.

## 4. Modules

The application is divided into seven modules.

| Module | What it does |
|---|---|
| Subscriber Registration | Add, view, update and delete subscriber records |
| Plan Selection | Create subscriptions and generate invoices |
| Delivery Scheduling | Schedule deliveries and update delivery status |
| Renewal Tracking | View subscriptions according to renewal date or plan |
| Expiry Alerts | Show subscriptions expiring within 7, 15 or 30 days |
| Subscription Search | Search using subscriber name, ID or publication |
| Delivery Reports | Filter deliveries and create a simple summary report |

## 5. Java Concepts Required by the Case Study

All the concepts listed in the given case study are used in the project.

| Java Concept | How it is used |
|---|---|
| Classes & Objects | Subscriber, Publication, Subscription and Delivery are modelled as classes |
| Constructors | Constructors initialise the objects when records are created |
| Enum | `SubscriptionType` contains MONTHLY, QUARTERLY and ANNUAL |
| StringBuilder | Used for invoices, delivery labels and delivery reports |
| ArrayList | Stores subscribers, publications and subscriptions |
| LinkedList | Stores delivery history |
| HashMap | Maps subscriber ID to that subscriber's subscriptions |
| TreeMap | Keeps subscriptions grouped by renewal date in sorted key order |
| CRUD | Subscriber records have Create, Read, Update and Delete; subscriptions also have create, read, update/renew, cancel and permanent delete operations |
| Searching | Searches are provided for name, subscriber ID and publication |
| Sorting | Subscriptions can be sorted by renewal date and plan type |
| Swing | Provides the complete desktop GUI |
| Exception Handling | Custom exceptions and try-catch blocks handle invalid operations |
| Validation | Name, email, phone, address, IDs, dates and payment amount are checked |

## 6. Data Structures Used

### ArrayList

`ArrayList` is used for the main subscriber, publication and subscription lists. It is suitable because the number of records can grow and the GUI frequently needs to display these records.

### LinkedList

`LinkedList` stores delivery history. New delivery records are added at the end of the list and the history is then read when delivery tables and reports are generated.

### HashMap

The HashMap stores data in the following form:

`Subscriber ID -> List of subscriptions`

This makes it easy to find all subscriptions belonging to one subscriber.

### TreeMap

The TreeMap uses the subscription end date as its key:

`Renewal Date -> List of subscriptions`

TreeMap keeps its keys sorted, which is useful when looking at upcoming renewal dates.

## 7. Classes and Packages

```text
SubscriptionSystem/
└── src/
    ├── model/
    │   ├── Subscriber.java
    │   ├── Publication.java
    │   ├── Subscription.java
    │   ├── SubscriptionType.java
    │   └── Delivery.java
    │
    ├── exception/
    │   ├── InvalidSubscriptionException.java
    │   ├── InvalidDeliveryException.java
    │   └── ValidationException.java
    │
    ├── service/
    │   └── SubscriptionService.java
    │
    ├── util/
    │   ├── Validator.java
    │   └── InvoiceGenerator.java
    │
    ├── ui/
    │   ├── MainFrame.java
    │   ├── SubscriberPanel.java
    │   ├── PlanSelectionPanel.java
    │   ├── DeliveryPanel.java
    │   ├── RenewalPanel.java
    │   ├── ExpiryAlertPanel.java
    │   ├── SearchPanel.java
    │   └── DeliveryReportPanel.java
    │
    └── Main.java
```

### Important classes

**Subscriber.java**  
Stores subscriber ID, name, email, phone and address.

**Publication.java**  
Stores publication ID, title, type, frequency and monthly price.

**Subscription.java**  
Connects a subscriber with a publication and stores plan type, start date, end date, amount and active status.

**SubscriptionType.java**  
The enum contains the three plans required by the case study. It also stores the duration and discount used for cost calculation.

**Delivery.java**  
Stores delivery ID, related subscription, delivery date, status and address.

**SubscriptionService.java**  
Contains the main business logic, collections, CRUD operations, searching, sorting, renewal and expiry handling.

## 8. Working of the System

### 8.1 Subscriber Registration

The user enters the subscriber's name, email, phone number and address. The application generates an ID such as `SUB001`. Before saving the record, the service checks the entered information through the Validator class.

The subscriber table also supports selecting an existing row and changing the information. A delete option is provided as well.

### 8.2 Plan Selection

The user selects:

- Subscriber
- Publication
- Plan type
- Start date

The plan type comes from the `SubscriptionType` enum. The application calculates the amount using the publication's monthly price and the duration of the selected plan.

For example, the three available plans are:

- Monthly — 1 month
- Quarterly — 3 months
- Annual — 12 months

After a subscription is created, a simple invoice is shown using `StringBuilder`.

### 8.3 Delivery Scheduling

The user selects a subscription, enters a delivery date and chooses a status such as Scheduled, Pending, Delivered or Failed. The address is filled from the subscriber record.

Each delivery is stored in the `LinkedList` delivery history. The user can also update the status and view a formatted delivery label.

### 8.4 Renewal Tracking

Every subscription has an end date. The Renewal section can display the records in renewal-date order or plan-type order.

`Comparable` is used for the natural renewal-date order and `Comparator` is used for plan-type sorting.

The system also allows a selected subscription to be renewed. Its end date is extended according to the selected plan.

### 8.5 Expiry Alerts

The expiry section checks how many days are left before the end date. The user can select:

- 7 days
- 15 days
- 30 days

Subscriptions are shown from the most urgent to the least urgent. This directly addresses the requirement to flag expiring subscriptions.

### 8.6 Subscription Search

The search module supports:

- Subscriber name
- Subscriber ID
- Publication title

Name and publication searches support partial text, while subscriber ID search can use the HashMap for a direct lookup.

### 8.7 Delivery Reports

The report section accepts a date range and an optional delivery status. It displays matching delivery records and creates a short summary using `StringBuilder`.

The summary includes total deliveries and counts for Delivered, Scheduled, Pending and Failed records.

## 9. CRUD Operations

CRUD means Create, Read, Update and Delete.

### Subscriber CRUD

**Create:** Add a new subscriber.  
**Read:** Display and find subscriber records.  
**Update:** Change subscriber details.  
**Delete:** Remove a subscriber record.

### Subscription CRUD

**Create:** Create a new subscription.  
**Read:** Display and search subscriptions.  
**Update:** Renew a subscription by changing its dates and status.  
**Delete:** Cancel a subscription or permanently remove a selected subscription.

## 10. Searching and Sorting

### Searching

The service contains separate methods for searching subscribers by name and ID and subscriptions by publication.

### Sorting by renewal date

`Subscription` implements `Comparable<Subscription>`. Its `compareTo()` method compares the end dates.

```java
@Override
public int compareTo(Subscription other) {
    return this.endDate.compareTo(other.endDate);
}
```

### Sorting by plan type

A `Comparator` compares the duration of the selected plan. Therefore Monthly comes before Quarterly and Annual.

## 11. Enum Implementation

The plan types are represented by an enum instead of using random strings.

```java
public enum SubscriptionType {
    MONTHLY("Monthly", 1, 1.0),
    QUARTERLY("Quarterly", 3, 0.95),
    ANNUAL("Annual", 12, 0.85);
}
```

The enum also contains a method to calculate the total cost.

This makes the plan selection simple and avoids accepting an invalid plan name.

## 12. StringBuilder Implementation

The case study specifically asks for StringBuilder. It is used in `InvoiceGenerator` for creating formatted invoices and delivery labels. It is also used in the delivery report.

A simple example is:

```java
StringBuilder invoice = new StringBuilder();
invoice.append("SUBSCRIPTION INVOICE\n");
invoice.append("Subscriber: ").append(subscription.getSubscriber().getName()).append("\n");
invoice.append("Amount: Rs. ").append(subscription.getAmount()).append("\n");
```

This is easier to read than joining many separate strings with `+`.

## 13. Validation and Exception Handling

The application uses three custom exception classes:

- `InvalidSubscriptionException`
- `InvalidDeliveryException`
- `ValidationException`

The `Validator` class checks the main input fields.

| Input | Validation |
|---|---|
| Name | Cannot be empty; letters, spaces and common name characters are allowed |
| Email | Checked with an email pattern |
| Phone | 10-digit Indian mobile number beginning with 6–9 |
| Address | Cannot be empty and must have enough characters |
| Subscriber ID | Must follow `SUB001` style |
| Publication ID | Must follow `PUB001` style |
| Date | Must be a valid `YYYY-MM-DD` date |
| Payment amount | Must be greater than 0 and below the defined limit |

For example, if the user enters an invalid email, the exception is caught and an error message is shown instead of allowing bad data into the list.

## 14. GUI Design

The GUI is made using Java Swing.

The main window uses a `JTabbedPane` with these tabs:

1. Subscribers
2. Plan Selection
3. Delivery
4. Renewals
5. Expiry Alerts
6. Search
7. Delivery Reports

Common Swing components used in the project include:

- `JFrame`
- `JPanel`
- `JTabbedPane`
- `JTable`
- `JTextField`
- `JComboBox`
- `JButton`
- `JOptionPane`
- `JScrollPane`

The interface is kept simple so that it is easy to understand during a lab demonstration.

## 15. Sample Data

The program loads sample data when it starts so that all modules can be demonstrated immediately.

The current sample data contains:

- 5 subscribers
- 5 publications
- 7 subscriptions
- 10 delivery records

Some subscriptions are deliberately placed close to their expiry date. This makes the Expiry Alerts and Renewal Tracking modules easy to demonstrate.

## 16. Testing

The source code was compiled successfully with the Java compiler. The service layer was also tested for sample-data loading, searching, subscription creation, cancellation and deletion.

| Test | Expected Result | Result |
|---|---|---|
| Load sample subscribers | 5 subscribers loaded | PASS |
| Load sample subscriptions | 7 subscriptions loaded | PASS |
| Load delivery history | 10 deliveries loaded | PASS |
| Search subscriber by name | Rahul Sharma is found | PASS |
| Search by publication | The Hindu subscription is found | PASS |
| Create subscription | New subscription is added | PASS |
| Cancel subscription | Status becomes inactive | PASS |
| Delete subscription | Record is removed | PASS |
| Invalid email | Validation error is shown | PASS |
| Invalid phone | Validation error is shown | PASS |
| Invalid delivery address | Delivery error is shown | PASS |
| Renewal sorting | Records are ordered by end date | PASS |

## 17. Advantages

1. Easy graphical interface.
2. Covers the complete subscription workflow in one application.
3. Uses all the Java concepts mentioned in the case study.
4. Uses collections according to the type of data being stored.
5. Includes validation and exception handling.
6. Sample data makes the project easy to demonstrate.
7. No external library or database is required.

## 18. Limitations

1. Data is stored only in memory, so it is lost after the program closes.
2. The application is intended for a single user.
3. It does not send real email or SMS notifications.
4. Payment is represented by validated subscription amount; there is no real payment gateway.
5. Reports are displayed inside the application and are not exported to PDF or Excel.

## 19. Future Scope

The project can later be improved by:

- Connecting it to MySQL or PostgreSQL using JDBC.
- Adding login and user roles.
- Sending renewal reminders through email or SMS.
- Adding online payment integration.
- Exporting reports to PDF or CSV.
- Adding a web version using Spring Boot.
- Adding analytics for subscription and delivery trends.

## 20. Conclusion

The Magazine & Newspaper Subscription Management System provides a practical example of how Java can be used to solve a small real-world management problem.

The project covers subscriber registration, subscription plans, delivery scheduling, renewal tracking, expiry alerts, searching and reporting. More importantly, the required Java concepts are not only mentioned in the report but are actually used in the source code.

The application helped demonstrate object-oriented programming, collections, enum, StringBuilder, CRUD operations, searching, sorting, Swing GUI, validation and exception handling in one project.

## 21. References

1. Oracle Java Documentation — Java SE API.
2. Oracle Java Tutorials — Swing and Collections Framework.
3. Herbert Schildt, *Java: The Complete Reference*.
4. Cay S. Horstmann, *Core Java Volume I — Fundamentals*.

## 22. How to Run the Project

### Step 1 — Open the project folder

```bash
cd SubscriptionSystem
```

### Step 2 — Compile

```bash
mkdir -p bin
javac -d bin src/model/*.java src/exception/*.java src/util/*.java src/service/*.java src/ui/*.java src/Main.java
```

### Step 3 — Run

```bash
java -cp bin Main
```

The application opens with sample data already loaded.

---

**Project:** Magazine & Newspaper Subscription Management System  
**Language:** Java  
**GUI:** Java Swing  
**Semester:** III  
**Subject:** Java Programming
