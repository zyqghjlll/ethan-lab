# ethan-lab

> A multi-tenant, event-driven platform for personal growth tracking — built on a reliable Kafka backbone with full observability and AI-powered behavioral analysis.

[![Java](https://img.shields.io/badge/Java-21-blue)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-event--driven-orange)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-database-blue)](https://www.postgresql.org/)
[![Prometheus](https://img.shields.io/badge/Prometheus-metrics-red)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-dashboards-yellow)](https://grafana.com/)
[![Docker](https://img.shields.io/badge/Docker-containerized-blue)](https://www.docker.com/)

---

## What problem does this solve?

Most productivity tools track what you *plan* to do. **ethan-lab** tracks what you *actually* did — and uses AI to tell you what it means.

It is a multi-tenant event-driven platform for personal growth tracking. Users record daily actions as events. The system reliably ingests them through a Kafka-backed pipeline, persists them, and asynchronously triggers AI analysis to surface behavioral patterns, risks, and trajectory projections.

The core question it answers: *given how I actually behave today, where am I headed?*

---

## Architecture

The system separates high-frequency event ingestion from slow AI processing into two independent pipelines.

Incoming user events are written to a `user-events` Kafka topic with guaranteed delivery and idempotent deduplication. A consumer persists events to PostgreSQL in real time. A scheduled job aggregates each user's daily events and publishes one analysis task per user to an `ai-analysis-jobs` topic. A second consumer processes these tasks at a controlled rate, calls the AI API, and writes results back to the database.

Users bring their own AI API key. Keys are AES-encrypted at rest. This keeps data sovereign to each user and eliminates platform-level API costs.

```
User writes daily event
        │
        ▼
┌───────────────────┐
│  REST API Layer   │
└────────┬──────────┘
         │
         ▼
┌───────────────────┐        ┌─────────────────────┐
│  Kafka Topic:     │        │  Consumer A          │
│  user-events      │───────▶│  Persist to          │
│  (high frequency) │        │  PostgreSQL           │
└───────────────────┘        └─────────────────────┘
                                        │
                              Scheduled daily aggregation
                                        │
                                        ▼
┌───────────────────┐        ┌─────────────────────┐
│  Kafka Topic:     │        │  Consumer B          │
│  ai-analysis-jobs │◀───────│  Rate-limited        │
│  (low frequency)  │        │  AI API caller        │
└───────────────────┘        └─────────────────────┘
         │
         ▼
  AI Analysis Result
  written to PostgreSQL
         │
         ▼
┌───────────────────┐
│  Prometheus +     │
│  Grafana          │
│  (observability)  │
└───────────────────┘
```

---

## Why this design?

Three decisions shaped this architecture.

**Decouple AI calls from the write path.** AI APIs are slow and rate-limited. Triggering analysis synchronously on every event write would bottleneck ingestion. A separate `ai-analysis-jobs` topic lets the write path stay fast and the AI path retry safely on failure with exponential backoff and dead-letter handling.

**Correctness over throughput.** In a reliability platform, processing the same event twice is worse than processing it late. The idempotent deduplication layer — backed by a database unique constraint — prioritizes correctness. Concurrent duplicate writes are caught at the database level, not just in application logic.

**Observability is first-class.** Prometheus metrics track event write latency, Kafka consumer lag, AI call success rate, duplicate detection rate, and dead-letter queue depth. Grafana dashboards make system health visible at a glance — because a platform you cannot observe is a platform you cannot trust.

---

## Multi-tenant design

Each event carries a `userId`. Load tests simulate 1,000 concurrent users writing events simultaneously. Kafka partitions by `userId` to preserve per-user ordering. The idempotency layer operates per-user to prevent cross-tenant interference.

Users supply their own AI API key via settings. Keys are AES-encrypted before storage. This means:
- The platform bears zero AI API cost
- User data is analyzed using their own credentials
- Data sovereignty stays with the user

---

## Observability

| Metric | Description |
|---|---|
| `event_write_latency_ms` | End-to-end write latency per event |
| `kafka_consumer_lag` | Consumer lag per topic and partition |
| `ai_call_success_rate` | AI API call success/failure ratio |
| `duplicate_detection_count` | Idempotent deduplication hits |
| `dlq_depth` | Dead-letter queue depth |

Grafana dashboards are pre-configured in `/docker/grafana`.

---

## Modules

| Module | Description |
|---|---|
| `libs/common-core` | Shared: message contracts, auth, common models |
| `apps/facts-platform-app` | Event ingestion service: receive, persist, publish |

---

## Tech stack

Java 21 · Spring Boot 3 · Apache Kafka · PostgreSQL · Redis · Docker · Prometheus · Grafana · k6 (load testing)

---

## Quick start

```bash
# Start infrastructure
docker-compose up -d

# Build and run
mvn -q -DskipTests clean install
```

---

## Progress

- [x] Project structure
- [x] API layer and persistence
- [x] Kafka event publishing
- [x] Idempotent consumption
- [ ] Multi-tenant load test (k6, in progress)
- [ ] AI analysis pipeline
- [ ] User API key management
- [ ] Grafana dashboard templates

---

## Notes

`/docs` contains architecture decision records (ADRs) — the reasoning behind key design choices, not just what was built.

---

*Built by a senior backend engineer with 12+ years of experience in distributed systems, as both a technical showcase and a tool used daily for personal growth tracking.*