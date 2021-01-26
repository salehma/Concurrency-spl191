package bgu.spl.mics;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> list;
	private ConcurrentHashMap<Class<? extends Event>, LinkedList<MicroService>> EventsTypesList;
	private ConcurrentHashMap<MicroService, LinkedList<Class<? extends Event>>> SubscribedEvents;
	private ConcurrentHashMap<Class<? extends Event>, Integer> CountersList;
	private ConcurrentHashMap<Event, Future> eventFutureObject;
	private ConcurrentHashMap<MicroService, LinkedList<Class<? extends Broadcast>>> SubscribedBroadcasts;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedList<MicroService>> BroadcastTypesList;
	private Future futureObject;
	//private ConcurrentHashMap<Event> EventSender;
	protected MessageBusImpl() {
		list = new ConcurrentHashMap<>();
		EventsTypesList = new ConcurrentHashMap<>();
		BroadcastTypesList = new ConcurrentHashMap<>();
	//	eventSender = new ConcurrentHashMap<>();
		SubscribedEvents = new ConcurrentHashMap<>();
		SubscribedBroadcasts = new ConcurrentHashMap<>();
		CountersList = new ConcurrentHashMap<>();
		eventFutureObject = new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!EventsTypesList.containsKey(type))
			EventsTypesList.put(type, new LinkedList<>());
		if (!SubscribedEvents.containsKey(m))
			SubscribedEvents.put(m, new LinkedList<>());
		synchronized (SubscribedEvents.get(m)) {
			SubscribedEvents.get(m).addFirst(type);
		}
		synchronized (EventsTypesList.get(type)) {
			EventsTypesList.get(type).add(m);
		}
		if (!CountersList.containsKey(type))
			CountersList.put(type, 0);
	}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized(type) {
			if (!BroadcastTypesList.containsKey(type)) {

				BroadcastTypesList.put(type, new LinkedList<>());
			}
			if (!SubscribedBroadcasts.containsKey(m)) {

				SubscribedBroadcasts.put(m, new LinkedList<>());
			}
			SubscribedBroadcasts.get(m).addFirst(type);
			BroadcastTypesList.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized (e.getClass()) {
			eventFutureObject.
					get(e).
					resolve(result);
		}

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (BroadcastTypesList.containsKey(b.getClass())) {
			synchronized (BroadcastTypesList.get(b.getClass())) {
				for (int i = 0; i < BroadcastTypesList.get(b.getClass()).size(); i++)
					try {
						list.get
								(BroadcastTypesList.
								get(b.getClass()).
								get(i)).
								put(b);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized(e.getClass()) {
			if (EventsTypesList.containsKey(e.getClass())) {
				if (EventsTypesList.get(e.getClass()).size() != 0) {
					try {
						//	synchronized (CountersList.get(e.getClass())) {
						list.get(EventsTypesList.get(e.getClass()).get(CountersList.get(e.getClass()) % EventsTypesList.get(e.getClass()).size())).put(e);
						CountersList.replace(e.getClass(), CountersList.get(e.getClass()) + 1);
						Future<T> futureObject = new Future<>();
						eventFutureObject.put(e, futureObject);
						//	}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					return eventFutureObject.get(e);
				}
				//return null;
			}

			return null;

		}
	}

	@Override
	public void register(MicroService m) {
		if (!list.containsKey(m)) {
			list.put(m, new LinkedBlockingQueue<>());
		}
	}

	@Override
	public void unregister(MicroService m) {
		if (list.containsKey(m))
			list.remove(m);

		if (SubscribedBroadcasts.containsKey(m)) {

			synchronized (SubscribedBroadcasts.get(m)) {
				for (int j = 0; j < SubscribedBroadcasts.get(m).size(); j++) {
					BroadcastTypesList.get(SubscribedBroadcasts.get(m).get(j)).remove(m);

				}
			}
		}
		if (SubscribedEvents.containsKey(m)) {
			synchronized (SubscribedEvents.get(m)) {
				for (int j = 0; j < SubscribedEvents.get(m).size(); j++) {
					EventsTypesList.get(SubscribedEvents.get(m).get(j)).remove(m);
					if (j < CountersList.get(SubscribedEvents.get(m).get(j)))
						synchronized (CountersList.get(SubscribedEvents.get(m).get(j))) {
							CountersList.replace(SubscribedEvents.get(m).get(j),
									CountersList.get(SubscribedEvents.get(m).get(j)) - 1);

						}
				}
			}
		}
		SubscribedEvents.remove(m);
		SubscribedBroadcasts.remove(m);


	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message f = null;
		if (list.containsKey(m))
			f = list.get(m).take();
		else
			throw new IllegalStateException("The MicroService " + m.getName() + " is unregistered");
		return f;

	}

	public static synchronized MessageBusImpl getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;

	}
	public int numOfMicroServices() {
		return list.size();
	}

}
