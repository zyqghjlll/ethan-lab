# Why deliberately use a bare implementation to surface problems?

A note on the method behind ethan-lab's correctness work — written because I
doubted it myself, and the doubt was worth resolving in writing.

## The doubt

I know about the Inbox/Outbox pattern. So when I deliberately *don't* use it and
then "discover" that messages can be lost, am I not just staging a problem whose
answer I already know? Two sharper forms of the doubt:

1. **Self-fulfilling.** If I already know the fix, verifying a failure I
   engineered on purpose seems circular. What does it prove?

2. **Unknowns can't be simulated.** Real incidents come from situations I
   *didn't* foresee. But if a failure mode is unknown to me, I wouldn't know how
   to trigger it or observe it either. So does this method even reach the part
   that matters?

## Resolving doubt 1 — the object under test isn't the fix

The goal was never to "discover the Outbox." I already have that. The real
objects under test are different:

**Calibrating the detector.** What I'm actually testing is whether my
correctness checks *work* — does the Law 2 residual actually go non-zero when
messages are genuinely lost, and do I actually see it? This is testing my
*detection capability*, not testing whether Outbox is good. If a detector can't
catch a failure I *know* is coming, it has no chance against an unknown one.
Using a known fault to validate that a detector fires is standard practice (the
idea behind fault injection / mutation testing): prove the detector catches the
seeded defect before trusting it on real ones.

**Turning textbook knowledge into observed phenomenon.** I "know" Outbox the way
you know a sentence: *"without it, messages can be lost."* But have I *seen* it
lost — in my own system, under load, watched through my own metrics? Knowing a
concept and having made it happen and observed it are different things by an
order of magnitude. The value here is upgrading "I read about Outbox" into "I
made an Outbox-less system lose messages under load and caught it with a
conservation residual." The second is engineering capability; the first is
information.

So deliberately omitting the Outbox isn't theater — it's a controlled experiment
to calibrate the detector and to convert book knowledge into a lived,
observable event.

## Resolving doubt 2 — conservation laws don't depend on knowing the cause

The hidden wrong assumption: that the value of "surfacing problems" lies in
*discovering failure modes I don't know about*. It doesn't.

Most production incidents aren't novel failure types. They're *known* failure
types — loss, duplication, reordering, oversell, connection exhaustion —
happening quietly somewhere nobody verified. Knowing a failure type exists does
not mean my specific system won't hit it, nor that I'd see it if it did.

So the real value of "surfacing problems" is not finding new failure types. It
is: **for each known failure type, verify that my system either prevents it, or
that if it happens I see it immediately.** That's the valuable engineering
capability — not reciting failure modes, but having a mechanism that verifies
them away one by one in *my* system.

This answers "how do I trigger an unknown?" — I don't. I don't simulate
unknowns. I take the list of *known* failure types and make each one
triggerable, observable, and checkable in my system. Defense against the unknown
doesn't come from predicting it (impossible); it comes from building every known
failure into an observable conservation law — so that when *anything*, known or
not, breaks correctness, it must violate some law and show itself.

This is the key shift: **a conservation law doesn't detect a specific failure;
it detects anything that breaks conservation.** `distinct sent = distinct
consumed` doesn't care *why* the two differ — Outbox gap, a concurrency bug I
never imagined, some strange Kafka behavior — if it causes loss or duplication,
the residual goes non-zero. So conservation laws are precisely the weapon
against the unknown: I don't need to foresee the cause, only to define what must
hold when correct; any deviation is caught, cause known or not.

## The combined position

Not "I stage a known problem to discover it" (that *would* be pointless), but:

> I use a known failure to calibrate my conservation-law detector and confirm it
> actually fires under load. Once calibrated, the detector catches *any* failure
> that breaks conservation — known, unknown, unforeseen — because it judges the
> *result*, not the cause. The missing Outbox is one concrete known failure used
> to prove the detector works, not the destination.

## The honest condition

This position holds only if the failures are actually made to happen, actually
observed, and actually caught. If Law 2 only *theoretically* goes non-zero but I
never run it, never see the residual jump, never watch a message get lost — then
the method is still on paper and the original doubt still stands. The decisive
act is: omit the Outbox, run the load test, and *actually see* the residual go
non-zero and the messages disappear. Until that moment, the doubt is healthy and
worth keeping.