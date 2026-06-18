# ethan-lab

> A lab for practicing engineering **judgment** on an event-driven backend: how to define correctness, how to make failures observable, and how to decide what to build — and what to deliberately leave bare.

[![Java](https://img.shields.io/badge/Java-21-blue)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-event--driven-orange)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-database-blue)](https://www.postgresql.org/)
[![Prometheus](https://img.shields.io/badge/Prometheus-metrics-red)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-dashboards-yellow)](https://grafana.com/)

---

## What this is

ethan-lab is not a product. It's a working lab where I sharpen the part of
engineering that doesn't show up in a framework tutorial: **deciding what
"correct" means, proving it, and judging what's worth building.**

The business scenario is intentionally as small as possible — **ingest
messages**: receive a message, persist it, publish it, consume it. Nothing more.
A simple scenario is a feature here, not a limitation: it keeps the spotlight on
the method, not on business complexity.

The method is deliberate:

1. Define correctness precisely — as conservation laws and a per-event state machine.
2. Load-test until something violates the definition.
3. Keep the implementation bare on purpose, so problems surface instead of being pre-empted.
4. Understand the failure, then decide the fix.

The interesting part isn't any single fix. It's building checks that catch
**any** violation of correctness — whether or not I foresaw the cause — and
knowing exactly where my local implementation stops and a production one would
begin.

→ **Why this method works:** [docs/why-this-method.md](docs/why-this-method.md)
→ **What "correct" means here:** [docs/message-pipeline-correctness.md](docs/message-pipeline-correctness.md)

---

## The pipeline

A small asynchronous pipeline running locally on Docker Compose:

```
HTTP → PostgreSQL → Kafka → Consumer
```

Driven by k6 load tests, watched through Prometheus + Grafana. Idempotency is
currently an in-memory check — deliberately bare, so that correctness laws can
expose what a naive implementation misses (e.g. why an outbox is eventually
needed) rather than hiding it behind a mechanism added too early.

This is verification for a **local, manually-driven** setup. Production-grade
reconciliation (incremental, continuous) is different machinery at scale —
documented as such, not pretended.

---

## What's being demonstrated

Less "I can wire up Kafka," more:

- **Defining correctness** as checkable conservation laws (no loss, no wrong duplication), separating the aggregate view from the per-event state machine, and knowing why `distinct` keys — not totals — are the right unit.
- **Making correctness observable** — Grafana displays the verdict; it doesn't compute it. The judgment lives in the counter arithmetic or the reconciliation query.
- **Deciding scope** — what to build now, what to leave bare to surface problems, and where the local approach diverges from production.
- **Reasoning in the open** — `/docs` holds the reasoning behind each choice, including a written defense of the method itself after I doubted it.

---

## Docs

| Doc | What it covers |
|---|---|
| [`why-this-method.md`](docs/why-this-method.md) | Why bare-implementation + correctness checks is a valid way to surface problems — written as a doubt resolved, not a claim asserted |
| [`message-pipeline-correctness.md`](docs/message-pipeline-correctness.md) | The correctness model: conservation laws + per-event state machine |
| [`where correctness can be judged.md`](docs/where%20correctness%20can%20be%20judged.md) | Why Prometheus/Redis detach from truth under crashes and code change; why only state inside the DB's transaction boundary is a stable foundation; CDC vs outbox; the verdict/sentinel separation and what Grafana is actually for |
| [`architecture.md`](docs/architecture.md) | Persistence and write-path trade-offs |
| [`practice-goals.md`](docs/practice-goals.md) | The practice goals behind the platform: high-concurrency ingestion, fact-centric modeling, idempotency, CQRS, observability, and evolutionary architecture |
| [`debugging-kafka-broker-stale-znode.md`](docs/debugging-kafka-broker-stale-znode.md) | Debugging a Kafka broker that wouldn't start after inactivity — stale ZooKeeper znode root cause and fix |

`/docs` is closer to a set of architecture decision records than feature docs —
the reasoning, not just the result.

---

## Tech stack

Java 21 · Spring Boot 3 · Apache Kafka · PostgreSQL · Docker · Prometheus · Grafana · k6

---

## Quick start

```bash
docker-compose up -d
mvn -q -DskipTests clean install
```

---

## Status

Real, now:
- [x] Message ingestion: REST → persist → publish
- [x] Kafka consumer with in-memory idempotency (deliberately bare)
- [x] k6 load tests
- [x] First-pass Grafana dashboard
- [x] Correctness model defined (laws + state machine)
- [x] Law 1 (ingress conservation) check + Grafana residual panel

Roadmap — all method-driven, listed as intent, not achievement:
- [ ] Law 2 / Law 3 correctness checks
- [ ] Outbox / Inbox — added when a correctness check exposes the need, not before
- [ ] Per-run metric isolation
- [ ] Grafana auto-provisioning on `docker-compose up`

---

*Built by a backend engineer with 12+ years in distributed systems — as a place to practice and show how I think about correctness, observability, and what's worth building.*