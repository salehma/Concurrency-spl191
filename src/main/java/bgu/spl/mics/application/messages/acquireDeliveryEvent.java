package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * an {@Link Event} that sent when a delivery is required
 *  and need to  acquire a vehicle
 *  response type expected to be a {@link DeliveryVehicle}.
 */
public class acquireDeliveryEvent implements Event<DeliveryVehicle> {

}
