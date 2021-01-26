package bgu.spl.mics;

import static org.junit.Assert.*;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.Before;
import org.junit.Test;
public class MessageBusImplTest {
    MessageBusImpl messB;
    ExampleBroadcast exB;
    ExampleEvent exE;
    ExampleMessageSenderService eventSender;
    ExampleMessageSenderService broadcaster;
    ExampleBroadcastListenerService brodcastto;
    ExampleEventHandlerService eventer;

    @org.junit.Before
    public void setUp() throws Exception {
        messB= MessageBusImpl.getInstance();
    }


    @Test
    public synchronized void testSubscribeBroadcast() {
        broadcaster = new ExampleMessageSenderService("sender1", new String[] { "broadcast" });
        brodcastto = new ExampleBroadcastListenerService("bListener", new String[] { "1" });
        messB.register(broadcaster);
        messB.register(brodcastto);
        exB = new ExampleBroadcast("broadcast");
        messB.subscribeBroadcast(exB.getClass(), brodcastto);
        messB.sendBroadcast(exB);
        try {
            assertTrue(messB.awaitMessage(brodcastto).getClass().equals(ExampleBroadcast.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public synchronized void testSendBroadcast() {
        broadcaster = new ExampleMessageSenderService("sender1", new String[] { "broadcast" });
        brodcastto = new ExampleBroadcastListenerService("bListener", new String[] { "1" });
        messB.register(broadcaster);
        messB.register(brodcastto);
        exB = new ExampleBroadcast("broadcast");
        messB.subscribeBroadcast(exB.getClass(), brodcastto);
        messB.sendBroadcast(exB);
        try {
            assertTrue(messB.awaitMessage(brodcastto).getClass().equals(ExampleBroadcast.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messB.unregister(broadcaster);
        messB.unregister(brodcastto);
    }
    @Test
    public synchronized void testSubscribeEvent() {
        eventSender = new ExampleMessageSenderService("sender1", new String[] { "event" });
       // requester = new ExampleEventHandlerService("rHandler", new String[] { "1" });
        exE = new ExampleEvent("sender1");
        messB.register(eventSender);
        messB.subscribeEvent(exE.getClass(), eventSender);
        messB.sendEvent(exE);
        try {
            assertTrue(messB.awaitMessage(eventSender).getClass().equals(ExampleEvent.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public synchronized void testComplete() {
        eventSender = new ExampleMessageSenderService("sender1", new String[] { "request" });
        eventer = new ExampleEventHandlerService("rHandler", new String[] { "1" });
        exE = new ExampleEvent("sender1");
        messB.register(eventSender);
        messB.register(eventer);
        messB.subscribeEvent(exE.getClass(), eventSender);
        Future<String> futureObject = (Future<String>)messB.sendEvent(exE);
        messB.complete(exE, "sender1");
        try {
            assertTrue(futureObject.isDone());
            assertTrue(messB.awaitMessage(eventSender).getClass().equals(exE.getClass()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public synchronized void testSendRequest() {
        eventSender = new ExampleMessageSenderService("sender1", new String[] { "request" });
        eventer = new ExampleEventHandlerService("rHandler", new String[] { "1" });
        exE = new ExampleEvent("sender1");
        messB.register(eventSender);
        messB.register(eventer);
        messB.subscribeEvent(exE.getClass(), eventSender);
        Future<String> futureObject = (Future<String>) messB.sendEvent(exE);
        assertTrue(futureObject.getClass().equals(exE.getClass()));
    }

    @Test
    public synchronized void testRegister() {
        eventSender = new ExampleMessageSenderService("sender1", new String[] { "request" });
        eventer = new ExampleEventHandlerService("rHandler", new String[] { "1" });
        exE = new ExampleEvent("sender1");
        messB.register(eventSender);
        messB.register(eventer);
        messB.subscribeEvent(exE.getClass(), eventSender);
        Future<String> futureObject = (Future<String>) messB.sendEvent(exE);
        assertTrue(futureObject.getClass().equals(exE.getClass()));
    }

    @Test
    public synchronized void testAwaitMessage() {
        eventSender = new ExampleMessageSenderService("sender1", new String[] { "request" });
        eventer = new ExampleEventHandlerService("rHandler", new String[] { "1" });
        exE = new ExampleEvent("sender1");
        messB.register(eventSender);
        messB.register(eventer);
        messB.subscribeEvent(exE.getClass(), eventSender);
        messB.sendEvent(exE);
        broadcaster = new ExampleMessageSenderService("sender1", new String[] { "broadcast" });
        brodcastto = new ExampleBroadcastListenerService("bListener", new String[] { "1" });
        messB.register(broadcaster);
        messB.register(brodcastto);
        exB = new ExampleBroadcast("broadcast");
        messB.subscribeBroadcast(exB.getClass(), brodcastto);
        messB.sendBroadcast(exB);
        try {
            assertTrue(messB.awaitMessage(brodcastto).getClass().equals(ExampleBroadcast.class)
                    && messB.awaitMessage(eventSender).getClass().equals(ExampleEvent.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    @Test
    public synchronized void testUnregister() {

        MessageBusImpl messB2 = new MessageBusImpl();
        ExampleEventHandlerService erh = new ExampleEventHandlerService("ddd", new String[] { "1" });
        messB2.register(erh);
        try{
            messB2.unregister(erh);
        }catch(Exception e){
            assertTrue(false);
        }


    }





}