# Contributing to SplitMate

## 1. Jira-First Development

Every development unit must have a Jira issue before implementation begins.

The required workflow is:

```text
Jira Issue
→ Branch
→ TDD RED
→ Development
→ TDD GREEN
→ Refactor
→ Test & Coverage
→ Commit
→ Push
→ Pull Request
→ CI / Review
→ Merge
→ Branch Cleanup
→ Jira Done
```

### Rules

* Create the Jira issue before creating a feature branch.
* Move the Jira issue to `In Progress` when implementation begins.
* Every feature branch must contain the Jira issue key.
* Every commit must contain the Jira issue key.
* Every pull request must contain the Jira issue key.
* Do not develop directly on `main`.
* Create feature branches from the latest clean `main`.
* Never intentionally switch branches with uncommitted work.
* Keep each Jira issue small enough to be independently implemented and tested.
* Move the Jira issue to `Done` only after the Definition of Done has been satisfied.

## 2. Test-Driven Development (TDD)

SplitMate follows TDD for business logic and other behavior where automated tests are practical.

The standard cycle is:

1. **RED** — Write a test that describes the expected behavior. The test must initially fail.
2. **GREEN** — Write the minimum production code required to make the test pass.
3. **REFACTOR** — Improve the implementation without changing its behavior.
4. Run the relevant test suite again.
5. Verify coverage and add missing edge/error-path tests where appropriate.

### TDD Rules

* Write the test before the production implementation.
* Tests must describe observable behavior, not implementation details.
* A test should have a clear reason for existing.
* Avoid tests written only to increase code coverage.
* Every bug fix should include a regression test when practical.
* Refactoring must preserve existing behavior and keep the test suite passing.
* Tests must be deterministic and independently repeatable.

### What We Test

#### Unit Tests

Unit tests are primarily used for:

* Service/business logic
* Validation rules
* Calculations
* Utility functions
* Domain behavior
* Security-related logic that can be isolated

Unit tests should be fast and isolated from external systems.

#### Integration Tests

Integration tests are used when multiple application components must work together, including:

* Spring application context
* REST controllers with application services
* PostgreSQL/database behavior
* Repository behavior
* Authentication and authorization flows
* External integrations where practical

Integration tests should verify real component interaction rather than duplicate unit-test behavior.

## 3. Test Coverage Standards

High test coverage is an engineering goal, but coverage percentage alone is not considered proof of quality.

We prioritize:

1. Behavioral coverage
2. Branch coverage
3. Edge-case coverage
4. Error-path coverage
5. Regression coverage
6. Line coverage

### Target Coverage

The following are engineering targets:

| Area                      |                                       Target |
| ------------------------- | -------------------------------------------: |
| Service / business logic  |                                      90–95%+ |
| Controllers / API layer   |                                      85–95%+ |
| Security / authentication |                                         90%+ |
| Overall backend           |                                         90%+ |
| Repositories              |              Meaningful integration coverage |
| Frontend                  | High coverage for business-critical behavior |

These are targets rather than reasons to write artificial tests.

### Required Test Cases

Where applicable, tests should cover:

* Normal/success scenarios
* Boundary values
* Invalid input
* Missing input
* Empty values
* Authorization failures
* Authentication failures
* Not-found scenarios
* Conflict scenarios
* Database failures
* External-service failures
* Duplicate requests
* Regression scenarios

### Coverage Enforcement

Coverage will eventually be measured automatically by CI.

CI should fail when coverage falls below agreed thresholds for areas where enforcement is practical.

A coverage threshold must never replace meaningful behavioral testing.

## 4. Selenium UI / E2E Testing

SplitMate uses Selenium for browser-based end-to-end testing of important user workflows.

Selenium tests are separate from unit and integration tests.

### Test Responsibilities

#### Unit Tests

Verify isolated business behavior.

Example:

> An expense with a negative amount is rejected.

#### Integration Tests

Verify application components work together.

Example:

> The expense API correctly persists an expense and its splits in PostgreSQL.

#### Selenium E2E Tests

Verify complete user workflows through the UI.

Example:

> A user logs in, creates a group, adds an expense, splits it, and verifies the resulting balance.

### E2E Project Location

Browser automation tests will live separately from the backend and frontend:

```text
splitmate/
├── backend/
├── frontend/
├── e2e-tests/
├── .github/
└── docker-compose.yml
```

### Selenium Principles

* Use Selenium WebDriver for browser automation.
* Prefer the Page Object Model for reusable page interactions.
* Use stable locators such as IDs or dedicated test attributes.
* Prefer explicit waits over arbitrary sleep statements.
* Keep tests independent and repeatable.
* Each test should create or prepare its own required data.
* Avoid depending on the execution order of tests.
* Capture screenshots and useful diagnostics when an E2E test fails.
* E2E tests should focus on important user journeys rather than individual UI implementation details.
* Avoid duplicating every unit or integration test as a Selenium test.

### Initial E2E Workflows

The initial Selenium suite should eventually cover:

1. User registration
2. User login
3. Creating a group
4. Adding a friend
5. Creating an expense
6. Equal expense splitting
7. Viewing balances
8. Recording a settlement
9. Complete expense workflow from login to settlement

Additional workflows can be added as SplitMate features grow.

## 5. Git Branch and Commit Conventions

Every development task must be traceable to its Jira issue.

### Branch Naming

Feature branches must follow:

```text
feature/<JIRA-KEY>-<short-description>
```

Example:

```text
feature/SPLIT-10-user-registration
```

Bugfix branches must follow:

```text
bugfix/<JIRA-KEY>-<short-description>
```

Example:

```text
bugfix/SPLIT-25-invalid-expense-validation
```

### Commit Messages

Every commit related to a Jira task must include the Jira key.

Format:

```text
<JIRA-KEY>: <short description>
```

Example:

```text
SPLIT-10: implement user registration
```

### Pull Requests

Pull request titles must also include the Jira key.

Example:

```text
SPLIT-10: implement user registration
```

### Traceability

A development change should be traceable through:

```text
Jira Issue
    ↓
Git Branch
    ↓
Commit
    ↓
Pull Request
    ↓
CI
    ↓
Merge
```

This makes it possible to identify which Jira requirement produced each code change.
