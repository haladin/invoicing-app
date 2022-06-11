# InvoicingApp

## 1. Prerequisites

    - Java 11
    - NPM 8.1.2+
    - Angular CLI 13.2.5 (npm install -g angular-cli@13.2.5 )
    - Docker - optional

## 2. Building

Run `./build.sh` from current directory. This script will build both frontend and backend or you can build them manually.

### 2.1. Building backend manually 

```
cd backend
./gradlew clean build
```

### 2.2. Building frontend manually

```
cd invoicing_app
rm -rf dist
ng build
```

## 3. Running the app

After successful build, application can be start with `./start.sh` script. Client application is running on port 8080 by default (http://localhost:8080)

### 3.1. Starting backend separately  

```
cd backend
./gradlew bootRun
```

### 3.1. Starting frontend separately  

```
cd invoicing_app
ng serve
```

By default angular is hosting application on port 4200

## 4. Using Docker

Application can be started using pre-build docker image:
```
docker run -p 8080:8080 haladin/invoicing-app:v0.0.1
```
or using the script:
```
./run_docker.sh
```

## 5. Using only API 

API can be tested with following curl example:
```
curl --location --request POST 'localhost:8080/api/invoices/' \
--form 'file=@"./data.csv"' \
--form 'currencies="EUR:1,USD:0.94,GBP:1.17,BGN:0.51"' \
--form 'outputCurrency="GBP"'
```

Optional property `--form 'filterByVat="1234"'`