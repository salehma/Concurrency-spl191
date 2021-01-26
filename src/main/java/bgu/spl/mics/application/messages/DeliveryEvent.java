package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * an  sent when a delivery is required
 * * response type expected to be a {@link DeliveryVehicle}.
 */

public class DeliveryEvent implements Event<DeliveryVehicle> {
    private String address;
    private int distance;
/*
* @param address- the address of the customer
* @param distance- the distance of the address
* */
    public DeliveryEvent(String address, int distance){
        this.address=address;
        this.distance=distance;
    }


    public String getAddress() {
        return address;
    }

    public int getDistance() {
        return distance;
    }

}
