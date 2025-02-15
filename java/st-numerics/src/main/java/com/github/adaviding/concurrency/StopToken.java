package com.github.adaviding.concurrency;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

/**
 * A class that haves similarly to the C# CancellationToken.
 *
 * To use an instance of this class, first construct a StopTrigger and then get the token from that.
 */
public class StopToken {

    /*
     * This constructs an instance which is tied to a StopTrigger.
     */
    StopToken(StopTrigger source) {
        this.stopTrigger = source;
    }

    /*
     * Waits indefinitely for the stop event to be triggered.
     */
    public void await() throws InterruptedException {
        this.stopTrigger.await();
    }

    /*
     * Waits up to a time limit for the stop event to be triggered.
     * @param timeLimit  The amount of time to wait.
     * @param unit       The unit of time for the timeLimit parameter.
     * @return True if the stop event was triggered, false otherwise.
     */
    public boolean await(long timeLimit, TimeUnit unit) throws InterruptedException {
        return this.stopTrigger.await(timeLimit, unit);
    }

    /*
     * True if the stop event has been triggered, false otherwise.
     */
    public boolean isStopped() {
        return this.stopTrigger.isStopped();
    }

    /*
     * Throw an exception if the stop event has been triggered.
     */
    public void throwIfStopped() throws CancellationException {
        if (this.isStopped()) {
            throw new CancellationException("Operation cancelled because a StopToken was triggered.");
        }
    }

    /*
     * The trigger for the stop event which is tied to this instance.
     */
    private final StopTrigger stopTrigger;
}
