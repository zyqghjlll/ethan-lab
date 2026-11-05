# Facts Platform — Architecture Definition & Trade-offs

## 1. Architecture Overview

The Facts Platform is designed as a **data-intensive, event-centric system** that focuses on:

- Reliable high-concurrency ingestion
- Durable fact persistence (no data loss)
- Replayable asynchronous processing
- Clear separation between write (command) and read (query)
- Serving as a practical carrier for modern data-intensive system design

The platform adopts a **CQRS-style architecture** with explicit responsibility boundaries.

---

## 2. Application Structure

### 2.1 Command Side — `facts-platform-app`

**Primary responsibility**:
> Accept facts, guarantee durability and correctness, and drive domain workflows.

This application focuses on **DDD Command handling**, not complex querying.

#### Responsibilities
- External ingestion APIs
- Raw event persistence (`raw_event`)
- Fact identity generation (`factId`)
- Idempotency & ordering control
- State machine & lifecycle management
- Asynchronous processing pipelines
- B-class (process-oriented) queries

#### Typical Queries (B-Class)
- Query by `factId`
- Query processing status
- Fetch latest raw events
- Worker polling queries (NEW → PROCESSING)

These queries:
- Are simple
- Strongly coupled to write models
- Exist only to support command workflows

---

### 2.2 Query Side — `facts-query-app`

**Primary responsibility**:
> Provide high-performance, complex, user-facing read capabilities.

This application is **read-only** with respect to command data.

#### Responsibilities
- Complex filtering / searching
- Multi-dimensional queries
- Aggregations and analytics
- Low-latency access patterns
- Read model optimization (indexes, caches, projections)

#### Typical Queries (C-Class)
- Multi-condition searches
- Time-range analytics
- Sorting / pagination at scale
- Reporting & metrics queries

These queries:
- Are performance-sensitive
- Can tolerate eventual consistency
- Evolve independently from write models

---

## 3. Data Flow
External System
↓
Ingest API (facts-platform-app)
↓
raw_event (durable write)
↓
Async Processing / Domain Logic
↓
Fact Materialized
↓
Projection / Read Model
↓
facts-query-app

Key principle:
> **The write side owns the truth.  
The read side optimizes the view.**

---

## 4. Technology Choices

### 4.1 Persistence Choice on Command Side

**Selected**: **MyBatis / MyBatis-Plus**

#### Reasons
- Explicit SQL control
- Predictable write behavior
- Efficient batch operations
- Natural fit for event tables and state machines
- Easier performance analysis (indexes, explain, partitions)

The command side primarily handles:
- Append-heavy writes
- State transitions
- Idempotency checks
- Worker polling queries

These patterns benefit from **transparent SQL**, not ORM magic.

---

### 4.2 Why Not JPA on the Command Side

JPA was explicitly evaluated and rejected as the primary persistence mechanism.

#### Trade-offs Considered

| Aspect | JPA | MyBatis |
|------|-----|---------|
| SQL Transparency | ❌ Low | ✅ High |
| Batch Write Control | ❌ Weak | ✅ Strong |
| Flush Semantics | ❌ Implicit | ✅ Explicit |
| State Machine Updates | ❌ Awkward | ✅ Natural |
| Data-intensive Patterns | ❌ Risky | ✅ Predictable |

**Conclusion**:  
The command side is **data-flow oriented**, not object-graph oriented.  
JPA would introduce unnecessary uncertainty without providing meaningful benefits.

> **DDD is a design methodology, not an ORM requirement.**

Domain modeling is preserved at the domain layer; persistence is a technical concern.

---

### 4.3 Persistence Choice on Query Side

The query side is intentionally left flexible:

- MyBatis / jOOQ for SQL-centric queries
- Optional integration with:
    - OLAP databases
    - Search engines (e.g., ES)
    - Analytical stores

The query side optimizes for:
- Read performance
- Index design
- Cache strategies
- Query isolation

---

## 5. Domain-Driven Design Positioning

### Command Side
- Domain logic expressed via:
    - Aggregates
    - Domain services
    - Policies / rules
- Repositories are **interfaces**
- Infrastructure implements repositories via MyBatis

### Query Side
- No domain logic
- No aggregates
- Query models only (DTO / projections)

This ensures:
- Domain integrity remains on the write side
- Query side stays simple and replaceable

---

## 6. Consistency Model

- **Write side**: strong consistency within a transaction
- **Read side**: eventual consistency

Key guarantees:
- A fact is durable once accepted
- A fact can always be replayed
- Query results may lag but never invent data

---

## 7. Observability & Operability

Both applications emphasize:

- Structured logs
- Fact-centric tracing (`factId`)
- Queue lag & backlog visibility
- Hotspot detection
- Failure classification

Observability is treated as a **first-class capability**, not an afterthought.

---

## 8. Evolution Strategy

The architecture supports incremental evolution:

1. DB-only ingestion & processing
2. Background workers
3. Projection tables
4. Optional MQ / Outbox
5. Advanced query infrastructure

Each step is:
- Backward compatible
- Observable
- Reversible

No big-bang rewrites.

---

## 9. Architectural Trade-off Summary

### What We Gain
- Clear responsibility boundaries
- Predictable write performance
- Scalable read optimization
- Strong replay & audit guarantees
- A real playground for data-intensive system design

### What We Accept
- Eventual consistency on reads
- Slightly higher operational complexity
- Two applications instead of one

These trade-offs are **intentional and aligned with platform goals**.

---

## 10. One-Sentence Architecture Statement

> The Facts Platform separates truth from view:  
> the command side guarantees durable, replayable facts,  
> while the query side optimizes how those facts are read.