# Facts Platform — Practice Goals

## 1. Overall Practice Objective

The Facts Platform is not built to deliver a specific business feature,
but to **practice and validate the design of modern data-intensive systems**.

The core objective is to build a system that can be:
- reasoned about,
- evolved incrementally,
- observed in production-like conditions,
- and used as a long-term architectural practice ground.

---

## 2. Primary Practice Goals

### 2.1 High-Concurrency Ingestion & Durable Write

Practice designing a system that can:

- Accept high-concurrency external inputs
- Respond quickly at the edge (fast ACK)
- Persist all incoming data durably
- Guarantee **no data loss**

Key focus:
- Short transactions
- Append-heavy write patterns
- Clear success semantics (accepted ≠ processed)

---

### 2.2 Fact-Centric Data Modeling

Practice treating **facts as first-class citizens**:

- Every input becomes a fact with a unique `factId`
- Raw facts are preserved in their original form
- Facts are immutable once accepted
- All downstream processing derives from raw facts

Key focus:
- Traceability
- Auditability
- Replayability

---

### 2.3 Idempotency, Ordering, and Correctness

Practice correctness under real-world conditions:

- Duplicate requests
- Out-of-order events
- Late-arriving data
- Concurrent writes

Key focus:
- Idempotency keys
- Ordering strategies
- State machine–driven lifecycle management
- DB constraints as the final line of defense

---

### 2.4 Asynchronous Processing & Eventual Consistency

Practice decoupling ingestion from processing:

- Write-side durability first
- Background processing pipelines
- Explicit eventual consistency model
- Clear separation of SLA between write and process

Key focus:
- Replay instead of manual backfill
- Failure isolation
- Controlled retries and dead-letter handling

---

### 2.5 CQRS-Oriented Architecture

Practice explicit separation of responsibilities:

- Command side:
    - Owns truth
    - Handles writes, workflows, and state transitions
    - Provides only B-class (process-oriented) queries
- Query side:
    - Optimizes for read performance
    - Supports complex, high-volume, low-latency queries
    - Accepts eventual consistency

Key focus:
- Clear boundaries
- Independent evolution of write and read models

---

### 2.6 Data-Intensive Query & Performance Design

Practice building systems that can:

- Serve large datasets efficiently
- Support multi-dimensional filtering, sorting, and pagination
- Handle hot keys, hot partitions, and slow queries

Key focus:
- Index design
- Partitioning strategies
- Caching and read-path optimization
- Performance observability

---

### 2.7 Observability as a First-Class Capability

Practice designing systems that are **explainable in production**:

- End-to-end tracing using `factId`
- Structured logs describing lifecycle events
- Metrics for throughput, latency, backlog, and failures
- Visibility into queue lag and hotspots

Key focus:
- Diagnose before optimizing
- Measure before scaling

---

### 2.8 Evolutionary Architecture

Practice evolving systems without rewrites:

- Start DB-only
- Add workers and projections
- Introduce MQ / Outbox only when justified
- Keep every step runnable and reversible

Key focus:
- Incremental change
- Architectural discipline
- Avoiding big-bang redesigns

---

## 3. Explicit Non-Goals

The platform intentionally does **not** aim to:

- Deliver a feature-complete business product
- Optimize prematurely for maximum scale
- Hide complexity behind frameworks
- Treat ORM or tooling choices as architectural goals

The focus is on **understanding trade-offs**, not eliminating them.

---

## 4. Success Criteria

This practice is considered successful if:

- The system can explain its own behavior under load
- Data issues can be traced back to raw facts
- Processing logic can be changed and replayed safely
- Performance bottlenecks can be identified with evidence
- Architectural decisions are documented and revisitable

---

## 5. One-Sentence Practice Statement

> The Facts Platform exists to practice building data-intensive systems
> that are durable, observable, replayable, and evolvable by design.