package com.github.adaviding.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/*
 * A class that haves similarly to the C# CancellationTokenSource.  It is used to trigger a stop event.
 */
public class StopTrigger {

    /*
     * Construct a stop trigger.
     */
    public StopTrigger() {
        this.latch = new CountDownLatch(1);
        this.stopToken = new StopToken(this);
    }

    /*
     * Waits indefinitely for the stop event to be triggered.
     */
    void await() throws InterruptedException {
        this.latch.await();
    }

    /*
     * Waits up to a time limit for the stop event to be triggered.
     * @param timeLimit  The amount of time to wait.
     * @param unit       The unit of time for the timeLimit parameter.
     * @return True if the stop event was triggered, false otherwise.
     */
    boolean await(long timeLimit, TimeUnit unit) throws InterruptedException {
        return this.latch.await(timeLimit, unit);
    }

    /*
     * Gets the StopToken which is tied to this trigger.
     */
    public StopToken getStopToken() {
        return this.stopToken;
    }

    /*
     * True if the stop event has been triggered, false otherwise.
     */
    public boolean isStopped() {
        return this.latch.getCount() == 0L;
    }

    /*
     * Trigger the stop event.
     */
    public void stop() {
        this.latch.countDown();
    }

    /*
     * The countdown latch that implements the actual concurrency logic.
     */
    private final CountDownLatch latch;

    /*
     * The stop token tied to this trigger.  (We only ever need to create one.)
     */
    private final StopToken stopToken;
}
