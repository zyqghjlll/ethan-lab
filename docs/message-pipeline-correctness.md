# When my dashboard couldn't tell me if the pipeline was correct

K6 was hammering my pipeline — 100 VUs, ramping. I opened my Grafana dashboard expecting it to tell me whether the run was healthy. It told me a lot: HikariCP saturating, consumer lag climbing. But when I asked the one thing I actually cared about — *in this run, did anything get lost or wrongly duplicated?* — it had no answer.

## The setup

A small asynchronous pipeline I run locally on Docker Compose, part of **ethan-lab**, my open-source playground for event-driven backend work:

```
HTTP → PostgreSQL → Kafka → Consumer
```

I drive it with k6 — ramp to 50 VUs, hold 100 for a minute, ramp down — with 10% of requests deliberately reusing an idempotency key to exercise dedup. I'd built a first-pass Grafana dashboard to watch it.

## The problem

The numbers were all there. "Duplicates: 4,563." Relative to what? They were cumulative totals across every run I'd done. A raw count has no notion of what it *should* be, so on its own it can't say whether the system was correct.

The real issue wasn't the dashboard. It was that I'd never defined what "correct" meant for this pipeline. You can't measure a residual if you haven't written the equation it's supposed to satisfy. So I stopped tuning and went to analyze the pipeline itself — the correctness of how it handles messages, and how a message moves through it.

## Two views

**Data-flow view** — every stage of the pipeline, and the full set of states a request can land in at each one (accepted / rejected / sent / unsent / consumed / duplicated), with the conservation rules that must hold between them. For example: distinct keys sent must equal distinct keys consumed plus distinct keys that ended in failure. *Distinct, not total* — with at-least-once delivery, totals won't match even when dedup works perfectly.

**Per-event view** — the legal lifecycle of a single message, from reaching the controller to a final state, including retries and manual recovery. The two views check each other: "a key enters Consumed at most once" is the same claim as the distinct-key law, seen from one message rather than the aggregate.

## The point

Correctness is a gate, and you can't stand at it until two things are done: every possible outcome at every junction of the pipeline is enumerated, and the state flow is designed so that *every message has a defined place to land* — no matter what happens to it. Until then, there is nothing to verify; "correct" isn't even a well-formed question yet. Defining it was the harder and more important half of the work — the dashboard was never going to do it for me.