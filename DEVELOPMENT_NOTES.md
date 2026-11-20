# Development Notes

## Overall approach

I built the service as a layered Spring Boot application: controller, service,
domain, repository, DTOs and a global exception handler. I worked bottom-up in
small commits. Each rule (EMI, risk band, interest rate, eligibility) is its own
class with its own unit tests, and a thin application service combines them. The
controller only receives the request, triggers validation and returns the result.

The rule classes hold no controller or database code, so their tests run without
starting Spring.

## Key design decisions

- DTOs are separate from the JPA entity. The API shape and the stored shape can
  change independently, and clients cannot set fields like the generated ID.
- BigDecimal for all money. The monthly rate is computed at DECIMAL128 precision
  and only the final EMI is rounded, to scale 2 with HALF_UP, as the brief
  requires. Rounding earlier would compound errors.
- Age plus tenure is compared in months (`age * 12 + tenureMonths > 780`) to
  avoid fractional years.
- All failing rules are reported together. The brief's sample rejection has two
  reasons, so the service collects every reason instead of stopping at the first.
- Nested validation. `@Valid` on the applicant and loan objects makes the field
  rules fire, and the exception handler returns every failed field in one HTTP
  400 response.
- Invalid enum values and malformed JSON are handled separately, because they
  fail before validation and throw a different exception.
- File-backed H2 for the application. Decisions survive restarts, which suits the
  audit requirement, and a reviewer needs no setup.
- In-memory H2 for tests. Tests get their own database, so they never write to the
  real data file and can run while the application is running.
- Enums are stored as strings, so reordering an enum later cannot corrupt stored
  rows.
- Shared policy constant. The minimum credit score of 600 was duplicated in two
  classes, so I moved it into `LendingPolicy` in a separate refactor commit.

## Trade-offs

- H2 with `ddl-auto=update` is quick but not production-grade. I chose speed and
  easy review over migrations.
- The rule classes and the application service have unit tests. The application
  service tests use a mocked repository, so they check the decision logic and
  that every decision is saved, but not the real database mapping. I checked the
  HTTP layer and the stored rows manually with curl and the H2 console, and there
  is no automated web-layer test.
- The invalid-enum error message is generic. Naming the exact bad field needs more
  work on the Jackson exception.
- When the credit score is below 600 there is no risk band, so no rate or EMI is
  calculated for that application.

## Assumptions

- The 60% and 50% EMI rules overlap. The brief rejects above 60% in one section
  and above 50% in another. I treat 50% as the real offer limit and give each band
  its own reason code: `EMI_EXCEEDS_60_PERCENT` above 60%, and
  `EMI_EXCEEDS_50_PERCENT` between 50% and 60%. Both reject.
- Rejected applications have a null risk band, as in the brief's sample
  rejection, and carry no offer.
- A loan of exactly 10,00,000 gets no size premium, since the brief says "greater
  than".
- Total payable is the rounded EMI multiplied by the tenure.
- HTTP status codes. The brief does not specify them, so a processed application
  returns 201 Created whether approved or rejected, and invalid input returns 400.
- The sample EMI in the brief does not match its formula. For 5,00,000 at 13.5%
  over 36 months, the formula gives about 16,967.64, which I confirmed
  independently with a spreadsheet PMT calculation. The brief's sample shows
  16,234.23 and a matching total. I followed the stated formula and treated the
  sample response as illustrative.

## Improvements with more time

- Replace `ddl-auto=update` with Flyway migrations and move to PostgreSQL, with
  credentials from environment variables, and turn the H2 console off.
- Add MockMvc integration tests for `POST /applications`.
- Return more specific messages for invalid enum values.
- Add `GET /applications/{id}` to retrieve a stored decision.
- Move rates, premiums and thresholds into configuration instead of constants.
- Add structured logging for each decision.