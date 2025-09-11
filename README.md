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

```
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
|── Makefile # Run commands
└── README.md # Project documentation
```

## ⚙️ Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/vipul94/order-processing-project.git
cd order-processing-project
```
---


### Resources needed to setup:
```
1) MySql - Persistence layer
2) SQS - Messaging queue
3) Redis - Caching[Read-through-cache, Write-through-cache]
```

- **Run commands in make file to setup the resources**

---


### API Description
```
1) To create order in sync.

curl --location 'http://localhost:8080/api/v1/add/order' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{
    "customer_name": "customer2",
    "total_amount": 150.0,
    "items": [
        {
            "item_name": "pizza",
            "quantity": 3,
            "price": 50.0,
            "total_amount": 150.0
        }
    ],
    "order_time": "2025-09-10 16:48:23"
}'

2) To get the orders by customer name with pegenated with querying on DB.

curl --location 'http://localhost:8080/api/v1/fetch/orders/pagenation?customerName=customer2&page=0&size=2' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json'

3) Fetch order status by order_id.

curl --location 'http://localhost:8080/api/v1/fetch/order/status?orderId=7'

4) Update order status in Sync

curl --location --request PATCH 'http://localhost:8080/api/v1/update/order/status/sync?orderId=6' \
--header 'Content-Type: application/json' \
--data '{
    "orderStatus": "SUCCESS"
}'

5) Update order status Async using SQS queue.
 
curl --location --request PATCH 'http://localhost:8080/api/v1/update/order/status/async?orderId=7' \
--header 'Content-Type: application/json' \
--data '{
    "orderStatus": "SUCCESS"
}'
```
