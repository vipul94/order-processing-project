# Makefile for Order Processing Project

# 🐳 Docker (Redis + LocalStack)
up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

# 🗄️ Database Setup (LOCAL MySQL, not Docker)
db-init:
	mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS order_processing; USE order_processing; \
	CREATE TABLE IF NOT EXISTS orders ( \
	    order_id INT AUTO_INCREMENT PRIMARY KEY, \
	    customer_name VARCHAR(50) NOT NULL, \
	    total_amount INT NOT NULL, \
	    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, \
	    items VARCHAR(500), \
	    order_status VARCHAR(20), \
	    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, \
	    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP \
	); \
	CREATE INDEX idx_customer_name ON orders (customer_name);"

db-indexes:
	mysql -u root -p -e "USE order_processing; SHOW INDEXES FROM orders;"

# Start Redis using brew
redis-install:
	brew install redis

redis-start:
	brew services start redis

redis-stop:
	brew services stop redis

# 🔧 LocalStack + AWS SQS Setup
aws-configure:
	aws configure set aws_access_key_id test
	aws configure set aws_secret_access_key test
	aws configure set region us-east-1

sqs-create:
	aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name order_processing_queue

sqs-list:
	aws --endpoint-url=http://localhost:4566 sqs list-queues

sqs-send:
	aws --endpoint-url=http://localhost:4566 sqs send-message \
	    --queue-url http://localhost:4566/000000000000/order_processing_queue \
	    --message-body "Hello LocalStack SQS!"

sqs-receive:
	aws --endpoint-url=http://localhost:4566 sqs receive-message \
	    --queue-url http://localhost:4566/000000000000/order_processing_queue

sqs-dlq:
	aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name order_processing_dlq
	aws --endpoint-url=http://localhost:4566 sqs set-queue-attributes \
	    --queue-url http://localhost:4566/000000000000/order_processing_queue \
	    --attributes '{"RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:000000000000:order_processing_dlq\",\"maxReceiveCount\":\"3\"}"}'