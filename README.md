# File Upload Service

## Overview
Spring Boot REST API for uploading and retrieving files.

## Features
- Upload files (JPG, PNG, PDF)
- Max size: 5MB
- Retrieve file by ID
- Unique file storage
- Swagger documentation

## Run
mvn spring-boot:run

## Swagger
http://localhost:8080/swagger-ui.html

## Endpoints
POST /files/upload  
GET /files/{id}
