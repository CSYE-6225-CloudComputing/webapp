# Cloud Application

## HealthCheckup Rest API

### About 
The health check API allows us to monitor the health of the application instance. It ensures the application is connected to the database or can be connected during the health check.

### Prerequisites 📋

Before you begin, ensure you have met the following requirements:

- JDK is installed on your machine 
- MySQL Server installed and running 
- Maven

### Running the Application Locally

To build and deploy the web application locally, follow these steps:

1. **Clone the repository**: 
   ```bash
   git clone git@github.com:cloud-computing-csye6225-neu/webapp.git
   ```   
2. **Configure the `application.properties` file** with your database settings.
   spring.datasource.url=
   spring.datasource.username=
   spring.datasource.password=

4. **Build the project using Maven**: 
   ```bash
   mvn clean install -DskipTests=true
   ```

5. **Run the application**.

### Endpoint Details

### `/healthz`

- **Method**: `GET
- **Example**: curl -X GET http://localhost:9001/healthz
- **Request**: 
  - No payload allowed. 
- **Expected Response**:
  - On successful database connection:
    - **Status**: `200 OK`
    - **Body**: No payload.  
  - On unsuccessful database connection:
    - **Status**: `503 Service Unavailable`
    - **Body**: No payload.  
  - If an unsupported HTTP method is used (e.g., POST/PUT/DELETE/OPTION/TRACE/PATCH etc.):
    - **Status**: `405 Method Not Allowed`
    - **Example**: curl -X POST http://localhost:9001/healthz
  