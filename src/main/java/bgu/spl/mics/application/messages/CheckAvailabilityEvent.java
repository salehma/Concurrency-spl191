package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.orderSchedule;
/**
 * an {@link Event} sent when we should to check if a specific book is available in the store
 * Its
 *  * response type expected to be a {@link Boolean}.
 * */
public class CheckAvailabilityEvent  implements Event<Boolean> {
    private String senderName;
    private orderSchedule orderSch;

    /**
     * @param senderName - the name of the service that send the event
     * @param orderSchedule- the order that should be proccess
     */
    public CheckAvailabilityEvent(String senderName,orderSchedule orderSchedule) {
        this.senderName = senderName;
        this.orderSch = orderSchedule;
    }

    public String getSenderName() {
        return senderName;
    }

    public orderSchedule getPurchase() {
        return orderSch;
    }

}
