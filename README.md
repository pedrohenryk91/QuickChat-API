# QuickChat-API

## Introduction

QuickChat-API is the backend service responsible for handling real-time messaging, user authentication, and data management for the QuickChat application.

## Setup

### Dependencies

Ensure you have the following installed and configured on your system before proceeding:

* **Java** [25]
* **Maven** [3.9]
* **Docker** and **Docker Compose**

### Execution

**1. Clone the repository**

```bash
git clone https://github.com/pedrohenryk91/QuickChat-API.git
cd QuickChat-API
```

**2. Configure the environment**
Create your local environment variables file by copying the provided example. Open the new `.env` file and adjust the values as needed.

```bash
cp .env.example .env
```

**3. Start the database**
Use Docker Compose to spin up the required database container in the background.

```bash
docker-compose up -d
```

**4. Build and run the application**
Download the project dependencies, compile the code, and start the API.

```bash
mvn clean install
mvn spring-boot:run
```

## API Documentation

Once the application is up and running, you can access the interactive Swagger API documentation. By default, it is available at the following endpoint:

* **<http://localhost:8080/api-docs>** *(Adjust the port if your `.env` configuration uses a different one)*
