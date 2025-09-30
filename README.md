# Spring Batch Demo

A minimal Kotlin + Spring Boot 3 application showcasing Spring Batch, backed by SQLite, with a built‑in interactive Spring Shell to control jobs.

## Prerequisites

- Java 24 (as configured in pom.xml)
- No need to install Maven: the project includes the Maven Wrapper (mvnw)

## Quick start

Run the app with the Maven Wrapper:

```shell
./mvnw spring-boot:run
```

On startup, you’ll drop into an interactive Spring Shell prompt. Type `help` to see available commands.

The application uses an in‑tmp SQLite database and log file. Each run uses a unique file name via random UUIDs set in application.properties.

## Available batch job

- exampleJob: a simple job with a single step that logs the received job parameters.

## Shell commands

The following commands are available (aliases in parentheses):

- List registered jobs:
    - `batch:jobs` (or `jobs`)
- Start a job by name (optionally with parameters `k=v,k2=v2`):
    - `batch:start --job-name exampleJob --params foo=bar,run=local` (or `start --job-name ...`)
- List job instances (most recent first):
    - `batch:instances --job-name exampleJob --count 10` (or `instances ...`)
- List executions for a given job instance id:
    - `batch:executions --instance-id 1` (or `executions ...`)
- Show the last (or currently running) execution for a job:
    - `batch:last --job-name exampleJob` (or `last ...`)

## Typical flow

1) See available jobs
    - `jobs`
2) Start the demo job with optional parameters
    - `start --job-name exampleJob --params greeting=Hello,user=World`
3) Inspect instances and executions
    - `instances --job-name exampleJob --count 5`
    - If an instance exists, get its id (e.g., `1`) and run:
    - `executions --instance-id 1`
4) Check the last execution status
    - `last --job-name exampleJob`

## Build and test

Build:

```shell
./mvnw -q -DskipTests package
```

Run tests:

```shell
./mvnw test
```

Run the packaged jar (after package):

```shell
java -jar target/spring-batch-demo-0.0.1-SNAPSHOT.jar
```

## Notes

- Spring Batch metadata schema is auto‑initialized for SQLite
  from [org/springframework/batch/core/schema-sqlite.sql](src/main/resources/org/springframework/batch/core/schema-sqlite.sql). Schemas can be found
  at https://github.com/spring-projects/spring-batch/blob/5.2.x/spring-batch-core/src/main/resources/org/springframework/batch/core. Just make sure to
  use the same version as the defined dependency.
- The job uses `RunIdIncrementer` so you can re‑start `exampleJob` without changing parameters.
