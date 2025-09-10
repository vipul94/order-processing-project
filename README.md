# 🚀 Order Processing Project

## 📌 Overview
The **Order Processing Project** is a Spring Boot backend application that simulates an **order management system**.  
It integrates with **MySQL** for persistence, **Redis** for caching (read-through & write-through), and **AWS SQS** for message processing.

This project demonstrates:
- ✅ REST APIs for order creation & retrieval
- ✅ Redis-based caching
- ✅ Asynchronous message consumption from AWS SQS
- ✅ MySQL persistence layer

---

## 🛠️ Tech Stack
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA (MySQL)**
- **Redis (Jedis client)**
- **AWS SQS (via AWS SDK v2)**
- **Maven**

---

## 📂 Project Structure

order-processing-project/
│── src/main/java/com/orderManagement/orderProcessing/
│ ├── Controller/ # REST Controllers
│ ├── Entity/ # JPA Entities
│ ├── Repository/ # Spring Data Repositories
│ ├── Service/ # Business logic + Redis caching
│ ├── Consumer/ # AWS SQS Consumer
│ └── Config/ # Redis + AWS Configurations
│
│── src/main/resources/
│ ├── application.properties # App configuration
│
│── pom.xml # Maven dependencies
└── README.md # Project documentation


## ⚙️ Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/vipul94/order-processing-project.git
cd order-processing-project
```


### Make sure you have MySQL and Redis running locally:
mysql -u root -p

Data base schema

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(50) NOT NULL,
    total_amount INT NOT NULL,
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    items VARCHAR(500),
    order_status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE  CURRENT_TIMESTAMP
);
