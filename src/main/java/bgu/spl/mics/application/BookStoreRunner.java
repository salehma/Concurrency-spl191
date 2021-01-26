package bgu.spl.mics.application;


import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;



/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner implements Serializable {
    private static final Logger log = Logger.getLogger(TimeService.class.getName());
    private static Inventory inv=Inventory.getInstance();
    private static MoneyRegister mr=MoneyRegister.getInstance();
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        int counter = 0;
        log.log(Level.INFO, "starting reading from xml file..");
        try {
            Object obj = (Object) parser.parse(new FileReader(args[0]));
            JSONObject main = (JSONObject) obj;
            JSONArray inventory = (JSONArray) main.get("initialInventory");
            JSONArray resources = (JSONArray) main.get("initialResources");
            JSONObject services = (JSONObject) main.get("services");
            int numOfSellings = Integer.parseInt(services.get("selling").toString());
            int numOfInventoryServices = Integer.parseInt(services.get("inventoryService").toString());
            int numOfLogistics = Integer.parseInt(services.get("logistics").toString());
            int numOfresourcesServices = Integer.parseInt(services.get("resourcesService").toString());
            JSONArray clients = (JSONArray) services.get("customers");
            counter+=numOfInventoryServices+numOfLogistics+numOfresourcesServices+numOfSellings+clients.size();
            CountDownLatch latch= new CountDownLatch(counter);
            SellingService[] sellersArray= new SellingService[numOfSellings];
            InventoryService[] inventoryArray= new InventoryService[numOfInventoryServices];
            LogisticsService[] logisticsArray= new LogisticsService[numOfLogistics];
            ResourceService[] resourcesArray=new ResourceService[numOfresourcesServices];
            APIService[] clientsArray =new APIService[clients.size()];
            HashMap<Integer,Customer> customers= new HashMap<Integer,Customer>();
            initializingResources(resources);
            initializingInventory(inventory);
            TimeService timeService_ = initializingTime(services, latch);
            for (int i = 1; i <= numOfInventoryServices; i++)
                inventoryArray[i - 1] = new InventoryService("inventory " + String.valueOf(i), latch);
            for (int i = 1; i <= numOfSellings; i++)
                sellersArray[i - 1] = new SellingService("seller " + String.valueOf(i), latch);
            for (int  i = 1;i <= numOfLogistics ; i++)
                logisticsArray[i-1] = new LogisticsService("logistic " + String.valueOf(i), latch);
            for (int  i = 1;i <= numOfresourcesServices ; i++)
                resourcesArray[i-1] = new ResourceService("resource " + String.valueOf(i), latch);
            initializingClient(clients, clientsArray,customers, latch,inventory);
            for (LogisticsService logisticsService : logisticsArray) {
                Thread l = new Thread(logisticsService);
                l.start();
            }
            for (ResourceService resourceService : resourcesArray) {
                Thread r = new Thread(resourceService);
                r.start();
            }
            for (InventoryService inventoryService : inventoryArray) {
                Thread in = new Thread(inventoryService);
                in.start();
            }
            for (APIService apiService : clientsArray) {
                Thread c = new Thread(apiService);
                c.start();
            }
            for (SellingService sellingService : sellersArray) {
                Thread s = new Thread(sellingService);
                s.start();
            }
            Thread t = new Thread(timeService_);
            t.start();

            while(Thread.activeCount()>2) {

            }
            FileOutputStream fout1 = new FileOutputStream(args[1]);
            ObjectOutputStream oos1 = new ObjectOutputStream(fout1);
            oos1.writeObject(customers);
            inv.printInventoryToFile(args[2]);
            mr.printOrderReceipts(args[3]);
            FileOutputStream fout = new FileOutputStream(args[4]);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(mr);
            
            

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
       

    }

    /**
     * @param file -
     *             file that should deSerialize it
     * @return - the deSerialized object
     *
     */
    public static Object deSerialization(String file) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }


    /**
     * initialize the inventory
     * @param inventory -
     *                  a {@link JSONArray} which includes all the books we should
     *      *      * 	 *            read from the file
     *
     */
    private static void initializingInventory(JSONArray inventory) {
        log.log(Level.INFO, "initializing inventory...");
        Inventory store = Inventory.getInstance();
        BookInventoryInfo[] inventoryList = new BookInventoryInfo[inventory.size()];
        for (int i = 0; i < inventory.size(); i++) {
            JSONObject index = (JSONObject) inventory.get(i);
            String bookTitle = index.get("bookTitle").toString();
            int amount = Integer.parseInt(index.get("amount").toString());
            int price = Integer.parseInt(index.get("price").toString());
            BookInventoryInfo bookInventory = new BookInventoryInfo(bookTitle, amount, price);
            inventoryList[i] = bookInventory;
            log.log(Level.INFO, inventoryList[i].toString());

        }
        store.load(inventoryList);
    }

    /**
     * initialize the resources
     * @param resources -
     *                  a {@link JSONArray} which includes all the resources we should
     *      * 	 *            read from the file
     */
    private static void initializingResources(JSONArray resources) {
        log.log(Level.INFO, "initializing Resources...");
        ResourcesHolder rh = ResourcesHolder.getInstance();
        JSONObject a= (JSONObject)resources.get(0);
        JSONArray vehicles = (JSONArray) a.get("vehicles");
        DeliveryVehicle[] vehiclesList = new DeliveryVehicle[vehicles.size()];
        for (int i = 0; i < vehicles.size(); i++) {
            JSONObject index = (JSONObject) vehicles.get(i);
            int license = Integer.parseInt(index.get("license").toString());
            int speed = Integer.parseInt(index.get("speed").toString());
            DeliveryVehicle deliveryVehicle = new DeliveryVehicle(license,speed);
            vehiclesList[i] = deliveryVehicle;
            log.log(Level.INFO, vehiclesList[i].toString());

        }
        rh.load(vehiclesList);
    }

    /**
     * initialize the time service
     * @param services -
     *                 a {@link JSONObject} which includes the services we should
     *      * 	 *            read from the file
     * @param latch -
     *              an excution of {@link CountDownLatch}
     * @return time service {@Link TimeService}
     */
   private static TimeService initializingTime(JSONObject services, CountDownLatch latch) {
       log.log(Level.INFO, "initializing Time...");
       JSONObject time = (JSONObject) services.get("time");
       int speed = Integer.parseInt(time.get("speed").toString());
       int duration = Integer.parseInt(time.get("duration").toString());
       return new TimeService(speed, duration, latch);
   }


    /**
     * @param clients -
     *                a {@link JSONArray} which includes all the clients we should
     * 	 *            read from the file
     * @param clientsArray -
     *                     arry of clients
     * @param customers -
     *                  hash map that we would print as output
     * @param latch-
     *             an excution of {@link CountDownLatch}
     * @param inventory-
     *                 a {@link JSONArray} which includes all the books we should
     *      * 	 *            read from the file
     */
    private static void initializingClient(JSONArray clients, APIService[] clientsArray,HashMap<Integer,Customer> customers,
                                           CountDownLatch latch,JSONArray inventory) {
        log.log(Level.INFO, "initializing Clients...");
        for (int i = 0; i < clients.size(); i++) {
            JSONObject customer = (JSONObject) clients.get(i);
            String name = customer.get("name").toString();
            int id = Integer.parseInt(customer.get("id").toString());
            int distance = Integer.parseInt(customer.get("distance").toString());
            String address = customer.get("address").toString();
            JSONObject creditCArd = (JSONObject)customer.get("creditCard");
            int creditNumber = Integer.parseInt(creditCArd.get("number").toString());
            int creditAmount = Integer.parseInt(creditCArd.get("amount").toString());
            JSONArray purchaseSchedule = (JSONArray) customer.get("orderSchedule");
            ArrayList<orderSchedule> ListOfPurchases = new ArrayList<>();
            Customer c= new Customer(name,id,address,distance,creditAmount,creditNumber);
            customers.put(id,c);
            JSONObject purchase=null;
            for (int j = 0; j < purchaseSchedule.size(); j++) {
                 purchase = (JSONObject) purchaseSchedule.get(j);
                String bookTitle = purchase.get("bookTitle").toString();
                int tick = Integer.parseInt(purchase.get("tick").toString());
                ListOfPurchases.add(new orderSchedule(bookTitle, tick,0));
            }
            for (int k = 0; k < inventory.size(); k++) {
                JSONObject book=(JSONObject) inventory.get(k);
                String bookTitle = book.get("bookTitle").toString();
                int price = Integer.parseInt(book.get("price").toString());
                for (int h=0;h<ListOfPurchases.size();h++){
                    if(ListOfPurchases.get(h).getbookName().equals(bookTitle)){
                        ListOfPurchases.get(h).setPrice(price);
                    }
                }
            }
            for (int t=0;t<ListOfPurchases.size();t++){
                orderSchedule o=ListOfPurchases.get(t);
            }
            APIService client = new APIService(c,id,name, ListOfPurchases, latch);
            clientsArray[i] = client;
        }
    }


}
