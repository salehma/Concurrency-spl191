package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * a {@link Broadcast} messages that is sent at every passed clock tick.This
 * message must contain the current tick.
 */
public class TickBroadcast implements Broadcast {
    private int tick;

    /**
     *
     * @param tick
     *            - the current tick that this message contains.
     */
    public TickBroadcast(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }


}
