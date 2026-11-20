# Loan Decision Engine

A Spring Boot REST service that evaluates loan applications against lending rules,
returns an approval with a single offer or a rejection with reasons, and stores
every decision for audit.

## Requirements

- Java 25
- No separate Maven install needed (the Maven wrapper is included)

## Run

```
./mvnw spring-boot:run
```

The service starts on http://localhost:8080. Decisions are stored in a file-backed
H2 database under `./data`, which is created on first run.

## Test

```
./mvnw test
```

Tests use a separate in-memory H2 database, so they can run while the application
is running and never touch the data in `./data`.

## API

`POST /applications`

```
curl -X POST http://localhost:8080/applications \
  -H "Content-Type: application/json" \
  -d '{"applicant":{"name":"Rahul","age":30,"monthlyIncome":75000,"employmentType":"SALARIED","creditScore":720},"loan":{"amount":500000,"tenureMonths":36,"purpose":"PERSONAL"}}'
```

- `201` with `APPROVED` and a single offer, or `REJECTED` with `rejectionReasons`
- `400` with a list of messages for invalid input

Rejection reasons: `CREDIT_SCORE_BELOW_MINIMUM`, `AGE_TENURE_LIMIT_EXCEEDED`,
`EMI_EXCEEDS_60_PERCENT`, `EMI_EXCEEDS_50_PERCENT`.

## Inspecting stored decisions

With the application running, open http://localhost:8080/h2-console and connect with
JDBC URL `jdbc:h2:file:./data/loandb`, user `sa`, and an empty password. Decisions
are in `LOAN_APPLICATIONS` and rejection reasons are in
`APPLICATION_REJECTION_REASONS`.

## Design notes

See [DEVELOPMENT_NOTES.md](DEVELOPMENT_NOTES.md) for the approach, design decisions,
trade-offs and assumptions.