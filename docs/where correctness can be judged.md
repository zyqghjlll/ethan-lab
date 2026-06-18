# Where correctness can be judged

A design note on a question that turned out to be deeper than the tool I started
with: **where does a correctness check have to live so that it stays valid no
matter how the code changes, how requests interleave, or where a process
crashes?**

This continues [correctness-model.md](correctness-model.md) (what "correct"
means) and [why-this-method.md](why-this-method.md) (why a bare implementation).
Those define the conservation laws and the detector. This note is about the
*foundation* a detector must stand on to be trustworthy. It is about **mechanism
correctness** — does the data flow without loss, duplication, or stuck state —
not business correctness. The unit under discussion is an **event** moving
through the pipeline, not an order or any business meaning attached to it.

## The premise: judge an invariant, not the code

I don't want to verify "did the code execute as I expected." Code changes, has
bugs, and under concurrency and crashes it walks paths I never planned. Anything
anchored to the code's execution is as fragile as the code.

So I verify something more stable — an **invariant on state**. For the pipeline
the invariant is:

> Every event that enters the system must end in a **known terminal state** —
> consumed, or some recorded, queryable failure — and **no event may ever sit
> in an unknown state or vanish.**

Success is terminal. Failure is terminal too, *as long as it is recorded and
queryable*. The only thing forbidden is an event that is neither succeeded nor
in any known failed state — unaccounted for. That, and only that, is
incorrectness.

This definition is what makes verification stable:

- I assert only the **final state**, so the middle can be anything → survives
  code changes.
- Terminal state is an attribution, not an order of events → survives
  concurrency.
- Terminal state must be **persisted** (still there after a crash) → survives
  crashes.

And the conservation laws are just this invariant made countable: "every event
has a known terminal state, none missing" is the same statement as "the terminal
counts sum to the entry count, residual zero." A non-zero residual *is* an event
sitting in no known state.

**One honest boundary.** This guarantee has an entry edge. An event must first
be persisted into the source of truth before it can have a trackable terminal
state. Before it lands (still in memory or on the wire), a crash loses it with no
record, and that reliability depends on upstream retry (the client resending),
not on my system. So precisely: *once an event enters the source of truth, it
will always have a known terminal state and will never silently vanish.* Getting
it into the source of truth must be early and as reliable as possible, but the
backstop for that first hop lives upstream.

## What I reached for first, and why each failed

With that premise as the test, I can put each candidate up against it — code
change, concurrency, crash — and watch it fall.

### Prometheus + Grafana

My first instinct was the obvious one: instrument the stages with counters,
scrape them with Prometheus, visualize in Grafana. Watch the residuals, watch
throughput.

It fails the premise on two counts. **It couples to the code.** A counter sits
on an execution path; it measures *the process*, not the event's terminal
state. Refactor the path and the instrumentation breaks — the opposite of
code-independent. **And it detaches from the truth.** I saw this directly: with
the publish-failure injected inside the transaction, the `persisted` counter
incremented and then the transaction rolled back — counters don't roll back. The
dashboard showed `reached = persisted`, a clean conservation, while the actual
row count in the DB was ~30% lower. A *false* conservation. The metric had
detached from the persisted state. A counter in memory also dies on a crash. So:
fails code-change, fails crash.

### Redis as a middle layer

If direct instrumentation couples to code, maybe a middle layer decouples it:
have the business write each event's state to Redis, expose Redis through
Prometheus, visualize in Grafana. `business → Redis → Prometheus → Grafana`.

But this is no different from `business → Prometheus → Grafana`. Each arrow is a
cross-process write, and each one can detach. The chain didn't remove the
detachment problem — it added a hop, so it has *more* points to detach at, not
fewer. Worse, the state in Redis is itself a copy written by a second operation
(write DB, then write Redis — two operations, not one), so it can already
disagree with the DB the instant one succeeds and the other doesn't. Using a copy
that can disagree with the truth to *judge* the truth is circular.

### The root cause

Counters, Redis, Prometheus, Grafana — they are all **derived copies
downstream of the DB.** They sample, they lag, they live in other processes, and
none of them shares the DB's transaction. The realization underneath all of it:
**you cannot atomically write two processes (dual write).** Any state kept
outside the DB's transaction boundary can detach from it. Adding more middleware
below the source of truth only manufactures more copies that can detach — it
never makes the judgment more reliable.

## Where it can actually be judged

The only thing that never detaches is **state persisted in the same transaction
as the business operation.** So a stable correctness check must be built there:

- Each event has a row with a **state** field (persisted / sent / consumed /
  failed), and **state changes commit in the same transaction as the business
  write.**
- Verification is an **independent, read-only reconciler** over that state —
  a job, a service, even a SQL script — checking the conservation laws against
  the state distribution. It lives **outside** the business code and depends only
  on the *meaning* of the state field, not on how the code got there.

This is verification that is **state-based, not process-based** — the same
instinct as "a unit test should assert behavior, not implementation." Refactor
the business code however you like; as long as the state semantics hold, the
reconciler doesn't change a line.

This also collapses the instrumentation problem. I don't sprinkle counters
through the code; I only need the state, at the one place it is persisted, to be
written correctly and in the same transaction. That is not extra
instrumentation — it is state management the business should be doing anyway. The
dependency on business code shrinks from "many scattered, brittle probes" to "one
stable contract: every state change is persisted atomically with the data."

## Two designs that satisfy the foundation

What's interesting is that the same foundation — *anchor state to the DB's
transaction consistency* — has two well-known implementations, and before I knew
the second I'd already reasoned my way toward the first.

**Binlog / CDC (e.g. Debezium).** The database's own change log is a stream of
committed changes, strictly consistent with the transaction (a rolled-back
transaction never appears in the binlog). Reading it requires **zero business
code instrumentation** — the business just writes the DB normally; the log is
produced by the database. A crash of the capture process does **not** lose data:
the change is already durable in the binlog, and the consumer resumes from its
last committed offset — the same way an outbox poller resumes from the outbox
table. Its real boundaries lie elsewhere: it captures *row changes*, not domain
intent (so `before`/`after` must be reasoned back into meaning, and full
before-images need DB config like `REPLICA IDENTITY FULL`); and it depends on
binlog retention (if the consumer is down longer than retention, the log is
gone). This is the *most* code-independent option — even more than outbox, which
still asks you to write an outbox row.

**Outbox.** Write the business data and an outbox record in one DB transaction,
then an independent poller relays the outbox to Kafka, retrying until it
succeeds. It turns an impossible atomic cross-system write (DB + Kafka) into a
possible single-system transaction (two tables in one DB) plus a retryable async
relay. The "intent to send" survives in the source of truth, so a failed publish
is retried, not lost.

They differ in trade-off — CDC is zero-instrumentation but needs infrastructure
(binlog enabled, a Debezium-class component, row-changes translated into domain
events); outbox is lighter infrastructure but lightly intrusive (one extra write
in the transaction). But they are reliable for the *same* reason: both keep the
pending record in the source of truth's durable log and resume by offset — the
capture/relay process crashing is only delay, never loss. That shared root is the
whole point — **the line between reliable and unreliable is whether the pending
state lives inside the DB's durability and transaction boundary, or outside it
in a process that can detach.** Outbox and CDC are inside. Counters, Redis,
Prometheus are outside.

## Transport vs verification — and why CDC fits verification best

Outbox and CDC both honor the foundation, but they serve two *different*
purposes, and conflating them hides a trap.

**Outbox is mainly transport** — it guarantees a message gets out (retried, not
lost). **CDC is mainly capture of truth** — it turns every committed change into
a stream, faithfully, without the business code's cooperation.

That difference matters for *verification specifically*. If I verify correctness
using the business's own outbox/inbox, the verifier and the verified are the same
code, same process, same author. A bug in how state is written, or a refactor
that moves a state update to the wrong place, corrupts both at once and
consistently — the verifier can't catch the very thing it depends on. **A check
built on state the business actively writes cannot verify whether that writing is
itself correct.**

CDC escapes this. It reads the binlog — a stream the database produces
automatically, which the business code can neither forge nor forget nor change by
refactoring. As long as the business writes the DB at all, the change is
captured. So CDC can be an **independent verification center, decoupled from and
resilient to business-code change** — the most thorough form of the
"low-dependency, change-proof verification" this whole note is chasing.

This does not make CDC zero-dependency. It still depends on a **state-semantics
contract**: the business must express state in DB fields, and the legal
transitions (the state machine) are still mine to define. And its reach is the DB
interior — it cannot see Kafka or downstream directly. But under the premise that
**the DB is the single source of truth**, that reach is enough: any cross-system
step that matters is forced to leave a trace in the DB through the
**outbox/inbox gateways** of the pipeline. A failed DB insert leaves no row; a
failed publish leaves an outbox row stuck short of terminal. Either way the
event's state is *incomplete in the DB*, and CDC sees it. So CDC's job is exactly
to **guard the correctness of those outbox/inbox gateways** — the entry and exit
of the whole pipeline — and through them, the whole flow. The detector's logic
changes only when the *definition of correctness* changes, never when the
business *implementation* changes — which is the entire point.

This is the same idea as **the log is the source of truth**: state is a
derivation of the log, and the log — here, the DB's change log — is the fact.
CDC is just consuming that fact directly.

## Two parallel channels — keep them separate

There is a tempting wrong turn: `event → DB → CDC → Prometheus → Grafana`. That
routes the judge (CDC) into the sentinel's pipe. The moment CDC's reading is fed
through sampled, cross-process Prometheus, it becomes a derived copy again — the
detachment problem returns, and the verdict degrades back into perception. **A
verification center's output is an authoritative verdict; it must not flow back
through the sampled observability pipe, or the judge is demoted to a derived
observation.**

So the two channels run **in parallel, never in series**:

```
verdict (judge):    event → DB → CDC → reconciliation result
sentinel (alert):   event → Micrometer → Prometheus → Grafana
```

The verdict channel produces a definite ruling ("event N stuck in SENT past T —
suspected loss"), written to its own authoritative place (a result table, a log,
an alert). If I want to *display* the verdict, Grafana reads that result table
directly — showing an already-decided fact, not sampling a flowing metric. The
sentinel channel stays as it is: fast, continuous, approximate.

## So what is Grafana for

Not nothing — but not the judge.

The reconciler over DB state is the **judge** (it can rule on truth, because it
reads the source of truth). Prometheus and Grafana are the **sentinel** — fast,
continuous, time-spanning perception: spot an anomaly, alert, show a trend, point
me at where to look. A sentinel may be sampled and slightly wrong; that's fine,
because a false alarm only costs one extra DB check, and it never has the final
say.

The relationship between them is **calibration**. I reconcile against the DB once
to confirm the dashboard agrees with the truth (clean run, then `COUNT(*)` =
metric = k6, all three equal). Once calibrated, I can reasonably trust the
dashboard for continuous perception — and skip re-running ad-hoc SQL by hand
every time. Calibration is a one-time investment to build trust; the dashboard is
the ongoing payoff that reuses it.

With one discipline: **calibration is not permanent.** Change the code, change a
metric's definition, or introduce a failure mode I never instrumented, and the
old calibration is void — reconcile against the DB again. Especially the last
one: a dashboard can only find the problems I designed it to find. "All zero"
only ever means "the laws I'm watching hold," never "nothing is wrong." The
coverage of the sentinel is exactly the set of invariants I built into it; the
blind spot is everything I didn't.

## The position, in one line

Reliable correctness verification cannot live in the observability layer
(sampled, cross-process, coupled to code). It must be built on the one thing that
never detaches — **state persisted in the same transaction as the business
operation, in the single source of truth** — and read by an independent,
state-based reconciler. Outbox and CDC both honor that; for *verification* CDC
goes furthest, because the binlog is a fact the business code cannot forge or
forget. The verdict channel and the sentinel channel run in parallel — Grafana is
the sentinel, never the judge. The dividing line between what can be trusted and
what cannot is a single boundary: inside the DB's durability and transaction, or
outside it.