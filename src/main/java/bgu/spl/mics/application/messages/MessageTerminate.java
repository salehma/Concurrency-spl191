package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.CyclicBarrier;

/**
 * A {@link Broadcast} message that will be sent when the framework ends. all
 * receiving this message should terminate .
 */
public class MessageTerminate implements Broadcast {
    private CyclicBarrier c;

    /**
     * constructor
     *
     * @param c
     *            - execution of CyclicBarrier , all the MicroServices should
     *            ends together.
     */
    public MessageTerminate(CyclicBarrier c) {
        this.c = c;
    }

    public CyclicBarrier getC() {
        return c;
    }

}
