# 🔨 The Auction House — Real-Time Bidding Platform

![License](https://img.shields.io/badge/license-MIT-blue)
![Status](https://img.shields.io/badge/status-active-brightgreen)

**The Auction House** is a full-stack bidding platform that enables real-time auctions with secure user participation. Built with a robust **Spring Boot** backend and a responsive **Angular** frontend, it features high-concurrency bidding logic, JWT-secured transactions, and automated notification systems.

-----

## 📌 Features

### **User & Security**

  * ✅ **Secure Authentication:** JWT-based login and registration.
  * ✅ **Email Verification:** OTP-based account activation and password recovery.
  * ✅ **Profile Management:** Edit profile details and track personal activity.
  * ✅ **Role-Based Access:** Distinct flows for Bidders and Auction Listers.
  * ✅ **Secure Authentication**: JWT-based login and registration with HttpOnly cookies or local storage.
  * ✅ **Email Service System (SMTP**): Integrated email engine for sending real-time OTP verification codes and password reset links.
  * ✅ **Profile Management**: Edit profile details and track personal activity with a smooth, reactive UI.

### **Bidding & Auctions**

  * ✅ **Real-Time Bidding**: Live updates on bid status and current highest offers using asynchronous polling or WebSockets.
  * ✅ **Fully Responsive Design**: A "Mobile-First" approach ensuring a seamless experience across desktops, tablets, and smartphones.
  * ✅ **Automated Notifications**: Instant email alerts via SMTP for bid outbids, auction wins, and account security updates.
  * ✅ **Advanced Search:** Filter and find auctions with real-time query processing.
  * ✅ **Single Product View:** Detailed insights, countdowns, and bid history.
  * ✅ **Watchlist:** Save favorite items for later.
  * ✅ **Winner Workflow:** Successful bidders can complete in-app purchases upon winning.

### **Dashboard & Management**

  * ✅ **My Auctions:** Listers can manage their postings and update auction status.
  * ✅ **My Bids & Wins:** Bidders can track active bids and finalized purchases.
  * ✅ **Notification System:** Get alerted on bid outbids, wins, and auction closures.

-----

## 🛠️ Technologies Used

| Layer          | Stack                                           |
| -------------- | ----------------------------------------------- |
| **Frontend** | Angular (TypeScript), HTML5, CSS/Tailwind       |
| **Backend** | Spring Boot 3 (Java), Spring Security           |
| **Auth** | JWT (JSON Web Tokens), OTP via Email            |
| **Database** | MySQL                                           |
| **Messaging** | JavaMailSender (SMTP) for OTP & Alerts |
| **ORM** | Spring Data JPA (Hibernate)                     |
| **Payments** | Integrated Checkout Gateway                     |
| **Tools** | Maven, IntelliJ IDEA, VS Code                   |

-----

## 📁 Project Structure

### **Backend (Spring Boot)**

```text
server/src/main/java/com/auctionhouse/
│
├── config/         # Security & JWT Configurations
├── controllers/    # REST API Endpoints
├── dto/            # Data Transfer Objects (Request/Response)
├── entity/         # Database Models (JPA Entities)
├── repository/     # Data Access Layer
├── service/        # Business Logic & Bidding Engine
└── util/           # Helpers (OTP, Date formatters)
```

### **Frontend (Angular)**

```text
client/src/app/
│
├── components/     # UI Components (Home, Auction Details, Profile)
├── services/       # API integration & Auth Services
├── guards/         # Route Protection (AuthGuards)
├── models/         # TypeScript Interfaces
└── environments/   # API Base URLs
```

-----

## 🚀 Getting Started

### **1. Clone the Project**

```bash
git clone https://github.com/sasindi22/TheAuctionHouse.git
```

### **2. Backend Setup (IntelliJ IDEA)**

1.  Open the `server` folder in IntelliJ.
2.  Update `src/main/resources/application.properties` with your **MySQL** credentials and **SMTP** settings (for OTP).
3.  Run `Maven Install` to download dependencies.
4.  Run the Spring Boot application.

### **3. Frontend Setup (VS Code)**

1.  Open the `client` folder in VS Code.
2.  Run `npm install` to install dependencies.
3.  Start the development server:
    ```bash
    ng serve
    ```
4.  Access the app at `http://localhost:4200`.

-----

## 👨‍💻 Author

Created by **Thedara Sasindi**  
*Ungergraduate Full‑stack Software Engineering*  
GitHub: <https://github.com/sasindi22>  
Email: thedarasasindi@gmail.com

-----

> *This project was developed for academic purposes, focusing on clean code, SOLID principles, and full-stack integration.*
