BuyComparator.java                                                                                  000644  000765  000024  00000000452 12133700010 015310  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         import java.util.Comparator;


public class BuyComparator implements Comparator<Request> {

	@Override
	public int compare(Request r1, Request r2) {
		
		if (r1.getQuote() > r2.getQuote()) {
			return -1;
		} else if (r1.getQuote() < r2.getQuote()) {
			return 1;
		} else {
			return 0;
		}
	}

}
                                                                                                                                                                                                                      ClientSocketHandler.java                                                                            000644  000765  000024  00000002351 12134042046 016417  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;


public class ClientSocketHandler implements Runnable {

	Socket clientSocket;
	LinkedBlockingQueue<String> clientRequestQueue = new LinkedBlockingQueue<String>();
	
	public ClientSocketHandler(Socket clientSocket, LinkedBlockingQueue<String> requestQueue) {
		
		this.clientSocket = clientSocket;
		this.clientRequestQueue = requestQueue;
	}
	
	/*
	 * This method receives the object from the socket and
	 * adds it to the request queue which has all the requests
	 * sent from clients to the server.
	 */
	
	@Override
	public void run() {
		
		System.out.println("[Client Socket Handler]: Listening for objects..");
		
		try {
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
					
				while(true) {
			
				String request = (String)ois.readObject();
					
					if (request != null) {
						
						request = request.toUpperCase();
					
						System.out.println("[Client Socket Handler]: Adding to client request queue..");
					
						clientRequestQueue.add(request);
					
						ois.close();
						clientSocket.close();
						return;
					}

			}
				
		} catch (Exception e) { e.printStackTrace(); }
				
	}

}
                                                                                                                                                                                                                                                                                       ./._README.TXT                                                                                      000644  000765  000024  00000000253 12134303762 013603  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                             Mac OS X            	   2   y      �                                      ATTR       �   �                     �     com.apple.TextEncoding   utf-8;134217984                                                                                                                                                                                                                                                                                                                                                     README.TXT                                                                                          000644  000765  000024  00000006057 12134303762 013241  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         README.TXT
==========

The following are the main classes in this program:
---------------------------------------------------

1. Account.java
---------------

Account class. Contains account info of clients like

* Client ID
* Balance
* List of stocks owned by the clients, with shares

This class also contains getters and setters and a 
method to print the account info of a particular client
for logging purposes.

2. ClientSocketHandler.java
---------------------------

This class is a thread that will receive the client request 
from the socket and adds it to the client request queue 
which has all the requests sent from clients to the server.
This thread essentially receives the client requests and passes
it to the main server thread.

3. Request.java
---------------

Request class. Fields of this class will be populated by the incoming client 
requests. The fields are:
 
1. Client ID
2. Stock symbol
3. Order Type
4. Quantity
5. Quote

This class also contains getter and setter methods and a method to print
the request for logging.

4. ServerSocketHandler.java
---------------------------

This class is a thread that will receive a server socket from the main
server thread, listen for socket connections from clients and pass the
received client socket to the Client Socket Handler thread. This thread
will continuously listen for client connections.

5. Stock.java
-------------

Main Stock class. Contains info about the stock.

1. Company Name
2. Symbol
3. Total No. of Shares, initially 10,000
4. Price, initially $100
5. Order Lists

This class contains getter and setter methods and contains a method to
print the stock info for logging purposes.

6. VSynchrony.java
------------------

The main server thread and the main class of the application. It extends the
ReceiverAdapter class of JGroups. It contains methods to receive messages from
the JGroups channel, the view change methods, the getState and setState methods
and the main eventLoop and methods to do the trade and process the client
requests.

The receive method will receive the messages sent from the JGroups channel and
process the requests. The getState method will send the request history through
the channel and setState will receive this request history and recreate the system
state from this request history and will also call the trading method. This method 
ensures fault tolerance and provides immunity from process crashes.

The start method creates a new JChannel, get the system state, create or join the
cluster and call the eventLoop. The eventLoop will create a server socket and pass
it to the server socket handler thread which starts the listening for client requests.
The eventLoop will also poll the client request queue for requests received from
the client socket handler thread.

The initializeStocks method parses the index.properties file and initializes the
stock state. The processRequest method parses the client request, creates the
request object from this request string and prints the order info.

The trade method will do the actual trades and update the stock info and client
account info.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 ./._Report.txt                                                                                      000644  000765  000024  00000000253 12134305654 014263  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                             Mac OS X            	   2   y      �                                      ATTR       �   �                     �     com.apple.TextEncoding   utf-8;134217984                                                                                                                                                                                                                                                                                                                                                     Report.txt                                                                                          000644  000765  000024  00000002701 12134305654 013711  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         Report.txt
==========

A1. The distributed shared objects in this program are:
-------------------------------------------------------

1. The stock state, which is a HashMap with the stock symbol
   string as keys and objects of the stock class as values.

2. The client account state, which is an array of objects of
   the Account class

3. The request history queue which a Linked List of strings
   containing the client requests.

A2. Byzantine failures
---------------------- 

Yes, my program is Byzantine failure safe, because the system
state is reconstructed every time a process joins a cluster,
which happens even when processes restart after they crash.

A3. Virtual Synchrony & JGroups
-------------------------------

No, virtual synchrony in JGroups is not scalable because in
the JGroups implementation, all the messages are sent to the
co-ordinator and all members maintains a FIFO buffer. The total
order is maintained because the co-ordinator sends all the messages
to all the nodes in the cluster. This puts a lot of load on all
nodes because they have to maintain buffers and there is a lot of
communication overhead because every node sends a message to the
co-ordinator first and then the co-ordinator sends the ordered 
messages to all the nodes.

A4. Need for distributed locking protocols
------------------------------------------

No, my program does not need distributed locking protocols,
because the consistency is maintained by JChannel.

                                                               Request.java                                                                                        000644  000765  000024  00000001624 12134007273 014167  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         /* Request class. Fields of this class will
 * be populated by the incoming client 
 * requests. The fields are:
 * 1. Client ID
 * 2. Stock symbol
 * 3. Order Type
 * 4. Quantity
 * 5. Quote
 */
public class Request {

	private int clientID;
	private String stockSymbol;
	private String orderType;
	private int quantity;
	private int quote;
	
	public Request(int clientID, String stockSymbol, String orderType, int quantity, int quote) {
		
		this.clientID = clientID;
		this.stockSymbol = stockSymbol;
		this.orderType = orderType;
		this.quantity = quantity;
		this.quote = quote;
		
	}
	
	public int getQuote() {
		return this.quote;
	}
	
	public int getClientID() {
		return this.clientID;
	}
	
	public int getQuantity() {
		
		return this.quantity;
	}
	
	public void printRequest() {
		
		System.out.print("[C" + clientID + " " + orderType + " " + stockSymbol + " " + quote + " " + quantity + "]" + " ");
	}
}
                                                                                                            SellComparator.java                                                                                 000644  000765  000024  00000000453 12133677613 015477  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         import java.util.Comparator;


public class SellComparator implements Comparator<Request> {

	@Override
	public int compare(Request r1, Request r2) {
		
		if (r1.getQuote() < r2.getQuote()) {
			return -1;
		} else if (r1.getQuote() > r2.getQuote()) {
			return 1;
		} else {
			return 0;
		}
	}

}
                                                                                                                                                                                                                     ServerSocketHandler.java                                                                            000644  000765  000024  00000002255 12134033426 016454  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;


public class ServerSocketHandler implements Runnable {

	ServerSocket serverSocket;
	LinkedBlockingQueue<String> clientRequestQueue = new LinkedBlockingQueue<String>();
	
	public ServerSocketHandler(ServerSocket serverSocket, LinkedBlockingQueue<String> requestQueue) {
		this.serverSocket = serverSocket;
		this.clientRequestQueue = requestQueue;
	}
	
	/*
	 * This method receives connections from a client, creates a client socket
	 * and passes this socket to the client socket handler thread.
	 */
	
	@Override
	public void run() {
		
		while(true) {
			try {
					System.out.println("[Server Socket Handler]: Listening for connection on server port");
				
					Socket clientSocket = serverSocket.accept();
		
					ClientSocketHandler clientSocketHandler = new ClientSocketHandler(clientSocket, clientRequestQueue);
				
					Thread clientSocketThread = new Thread(clientSocketHandler);
				
					clientSocketThread.start();
					
					System.out.println("[Server Socket Handler]: Client Socket Handler thread created");
				
			} catch (Exception e) { e.printStackTrace(); }

		}

	}
}
                                                                                                                                                                                                                                                                                                                                                   Stock.java                                                                                          000644  000765  000024  00000003456 12134042430 013621  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;


/* Main Stock class. Contains info about the stock.
 * 1. Company Name
 * 2. Symbol
 * 3. Total No. of Shares, initially 10,000
 * 4. Price, initially $100
 * 5. Order Lists
 */
public class Stock {

	private String name;
	String symbol;
//	private int totalShares;
	private int price;
	
	private SellComparator sc = new SellComparator();
	private BuyComparator bc = new BuyComparator();
	
	PriorityQueue<Request> sellList = new PriorityQueue<Request>(10, sc);
	PriorityQueue<Request> buyList = new PriorityQueue<Request>(10, bc);
	
	private LinkedBlockingQueue<Request> printQueue = new LinkedBlockingQueue<Request>();
	
	public Stock(String stockName, String stockSymbol, int initialPrice) {
		
		this.name = stockName;
		this.symbol = stockSymbol;
		this.price = initialPrice;
	}
	
	public void setPrice(int newPrice) {
		
		this.price = newPrice;
	}
	
	public int getPrice() {
		
		return this.price;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public void printStockInfo() {
		
		
		System.out.println("Price: " + price);
    	System.out.print("BUYS: ");
    	
    	// Print the buy lists of the stock

    	
    	while (!buyList.isEmpty()) {
    		
    		Request tempReq = buyList.poll();
    		tempReq.printRequest();
    		printQueue.add(tempReq);
    	}
    	
    	while (!printQueue.isEmpty()) {
    		
    		buyList.add(printQueue.poll());
    	}
    	
    	System.out.printf("\n");
    	System.out.print("SELLS: ");
    	
    	// Print sell lists
    	
    	while (!sellList.isEmpty()) {
    		
    		Request tempReq = sellList.poll();
    		tempReq.printRequest();
    		printQueue.add(tempReq);
    	
    	}
    	
    	while (!printQueue.isEmpty()) {
    		
    		sellList.add(printQueue.poll());
    		
    	}
    	
	}
	
}
                                                                                                                                                                                                                  VSynchrony.java                                                                                     000644  000765  000024  00000021233 12134303554 014660  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class VSynchrony extends ReceiverAdapter {

	static int processID;
	private static int numClients;
	private static int port;
	private static HashMap<String, Stock> stockState = new HashMap<String, Stock>();
	private static Account[] accountList; 
	private LinkedBlockingQueue<String> clientRequestQueue = new LinkedBlockingQueue<String>();
	private LinkedList<String> requestHistory = new LinkedList<String>();
	int orderNumber = 1;
	int tradeNumber = 1;
	JChannel channel;

	
	public VSynchrony(int pid, int clients, int serverPort) {
		processID = pid;
		numClients = clients;
		port = serverPort;
		accountList = new Account[numClients];
	}

	public void viewAccepted(View new_view) {
      
		System.out.println("** view: " + new_view);
		
    }
	
	public void receive(Message msg) {
		
		String clientRequest = (String)msg.getObject();
		
		requestHistory.add(clientRequest);
		
		processRequest(clientRequest);						// Check for thread-safety here
		
		trade();							// Do the trading
		
	}
	
	public void getState(OutputStream output) throws Exception {
        
		synchronized(requestHistory) {
            
			Util.objectToStream(requestHistory, new DataOutputStream(output));
			
        }
    }
	
	@SuppressWarnings("unchecked")
	public void setState(InputStream input) throws Exception {
        
		LinkedList<String> list = (LinkedList<String>)Util.objectFromStream(new DataInputStream(input));
        
		synchronized(requestHistory) {
            
			requestHistory.clear();
			requestHistory.addAll(list);
            
        }
		
		while (!list.isEmpty()) {
			
			processRequest(list.poll());
			trade();
			
		}
		
    }
	
	public static void main(String[] args) {
		
		/* Parse command line args */
		int pid = Integer.valueOf(args[0]);
		int clients = Integer.valueOf(args[1]);
		int serverPort = Integer.valueOf(args[2]);
		
		try {
				new VSynchrony(pid, clients, serverPort).start();
				
		} catch (Exception e) { e.printStackTrace(); }
	}
		
    private void start() throws Exception {

    	initializeStocks();					// Read the index.properties file and initialize the stockList[] array
		createAccounts();					// Create accounts for clients based on numClients
		
        channel = new JChannel("protocol.xml"); 			// use the default config, udp.xml
        channel.setReceiver(this);
        channel.connect("Vivitsu_Maharaja");
        channel.getState(null,10000);					// system state, the entire request history is received and the system state is rebuilt
		eventLoop();
		channel.close();
    }
    
    /*
     *  The main event loop. This method creates a server socket and passes it to
     *  a server socket handler thread which will continuously listen for client
     *  connections and process them.
     */
    
    private void eventLoop() {
    
		
		try {
				System.out.println("Now listening on socket at port " + port);
			
				ServerSocket serverSock = new ServerSocket(port); 	// Create a new server socket where clients will send requests
				
				ServerSocketHandler serverSocketHandler = new ServerSocketHandler(serverSock, clientRequestQueue);
				
				Thread serverSocketThread = new Thread(serverSocketHandler);	// Start a server socket handler thread which will receive client connections
				serverSocketThread.start();
				
				System.out.println("Server socket thread started..");
		
		} catch(Exception e) { e.printStackTrace(); }
		
		while (true) {
			try {				
					
					if (!clientRequestQueue.isEmpty()) {
						
						System.out.println("Polling client request queue..");
						
						String clientRequest = clientRequestQueue.poll();
					
						Message msg = new Message(null, null, clientRequest);
					
						channel.send(msg);
					
// 						processRequest(clientRequest);		// This is redundant
					
//						trade();							// Do the trading
					}
				
				    
			} catch (Exception e) { e.printStackTrace(); }
			
		}
		
    }
    
	/* 
	 * This method will read the index.properties file & initialize
	 * the stockList[] array of this class. Each member of the stockList[]
	 * array is an object of class Stock initialized with the stockName,
	 * stockSymbol & initial price of $100.  
	 */
	
	private void initializeStocks() {
		
		try {
			
			FileReader fr = new FileReader("index.properties");
			BufferedReader br = new BufferedReader(fr);						// open a buffered reader on file index.properties
			
			String str = br.readLine();										// read the first line
//			int i = 0;

			
			while (str != null) {
				
				if (str.charAt(0) == '#') {
					
					str = br.readLine();
					continue;
					
				}
				
//				System.out.println(str);
				
				String[] stockInfo = str.split("\\t"); 					// split the strings on the space
			
//				System.out.println(stockInfo[0]);
//				System.out.println(stockInfo[1]);
				
				Stock stock = new Stock(stockInfo[1], stockInfo[0], 100);	// initialize an object of class Stock 
				
				stockState.put(stockInfo[0], stock);
				
				str = br.readLine(); 										// read next line
//				i++;
			}
			
			br.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/*
	 * This method will create account for numClients no of Clients
	 * with client ID starting from 1 and increasing by 1 for each 
	 * client. An object of class Account is created initialized
	 * with the client ID & initial balance. The stock info of each
	 * account is also initialized with 0 stocks for each type of
	 * stock.
	 */
	
	private void createAccounts() {
		
		for (int i = 0; i < numClients; i++) {
			
			accountList[i] = new Account(i+1, 10000);
			
/*			for (int j = 0; j < stockList.length; j++) {
				
				accountList[i].setStockInfo(stockList[j].getName(), 0);
				
			} */
			}
		
		}
  
    
    private void processRequest(String clientRequest) {
    	
    	String[] parsedRequest = clientRequest.split("\\s+");
    	
    	int clientID = Integer.valueOf(parsedRequest[0].substring(1));
    	String orderType = parsedRequest[1];
    	String stockSymbol = parsedRequest[2];
    	int quote = Integer.valueOf(parsedRequest[3]);
    	int quantity = Integer.valueOf(parsedRequest[4]);
    	
    	Request request = new Request(clientID, stockSymbol, orderType, quantity, quote);
    	
    	// Get the stock object 
    	Stock stockObj = stockState.get(stockSymbol);
    	
    	System.out.println("Order #" + orderNumber + ": " + clientRequest);
    	
    	// Print stock info
    	stockObj.printStockInfo();
    	
    	System.out.printf("\n");
    	System.out.print("[");
    	
    	// Print account info
    	for (int i = 0; i < accountList.length; i++) {
    		
    		accountList[i].printAccountInfo();
    		
    	}
    	
    	System.out.print("]");
    	System.out.printf("\n");
    	System.out.println("------------------------------------------------------------");
    	
    	orderNumber++;
    	
    	// Insert this request in the sell/buy list
    	if (orderType.equalsIgnoreCase("BUY")) {
    		
    		stockObj.buyList.add(request);
    		
    	} else if (orderType.equalsIgnoreCase("SELL")) {
    		
    		stockObj.sellList.add(request);
    		
    	}
    	
    	
    }
    
    private void trade() {
    	
    	
    	for (Stock stock : stockState.values()) {
    		
    		if (stock.sellList.peek() == null || stock.buyList.peek() == null) {
    			
    			continue;
    			
    		}
    		
    		int sellQuote = stock.sellList.peek().getQuote();
    		int sellerID = stock.sellList.peek().getClientID();
    		
    		Iterator<Request> it = stock.buyList.iterator();
    		
    		if (sellQuote > stock.buyList.peek().getQuote()) {
				
				continue;
			} 
    		
    		while (it.hasNext()) {
    			
    			Request r = it.next();
    		
    			if (r.getQuote() > sellQuote) {
    				
    				stock.setPrice(sellQuote);
    			
    				int buyerID = r.getClientID();
    			
    				/* Should be updated to call method in Account class which updates balance & set portfolio value */
	    			accountList[buyerID - 1].setBalance((accountList[buyerID - 1].getBalance() - (sellQuote * r.getQuantity())));
	    			accountList[sellerID - 1].setBalance((accountList[sellerID - 1].getBalance() + (sellQuote * r.getQuantity())));	
	    			
	    			it.remove();
	    			stock.sellList.remove();
	    			tradeNumber++;
	    			break;
	    			
	    		}
    			
    		}
    		
    	}
    	
    }
    
}                                                                                                                                                                                                                                                                                                                                                                     makefile                                                                                            000644  000765  000024  00000000377 12134021442 013372  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         JFLAGS = -g -cp .:jgroups-3.2.7.Final.jar
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	VSynchrony.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	find . -name \*.class | xargs $(RM) 
	$(RM) log*
                                                                                                                                                                                                                                                                 ./._protocol.xml                                                                                    000755  000765  000024  00000000337 12134053625 014636  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                             Mac OS X            	   2   �      �                                      ATTR       �   �   G                  �   G  com.apple.quarantine q/0003;516f102a;The\x20Unarchiver;3551EBD6-8DE0-4C1E-A587-BC3BC5599404                                                                                                                                                                                                                                                                                                  protocol.xml                                                                                        000755  000765  000024  00000005421 12134053625 014263  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         
<!--
  Default stack using IP multicasting. It is similar to the "udp"
  stack in stacks.xml, but doesn't use streaming state transfer and flushing
  author: Bela Ban
-->

<config xmlns="urn:org:jgroups"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="urn:org:jgroups http://www.jgroups.org/schema/JGroups-3.3.xsd">
    <UDP
         mcast_port="${jgroups.udp.mcast_port:45588}"
         tos="8"
         ucast_recv_buf_size="5M"
         ucast_send_buf_size="640K"
         mcast_recv_buf_size="5M"
         mcast_send_buf_size="640K"
         loopback="true"
         max_bundle_size="64K"
         max_bundle_timeout="30"
         ip_ttl="${jgroups.udp.ip_ttl:8}"
         enable_diagnostics="true"
         thread_naming_pattern="cl"

         timer_type="new"
         timer.min_threads="4"
         timer.max_threads="10"
         timer.keep_alive_time="3000"
         timer.queue_max_size="500"

         thread_pool.enabled="true"
         thread_pool.min_threads="2"
         thread_pool.max_threads="8"
         thread_pool.keep_alive_time="5000"
         thread_pool.queue_enabled="true"
         thread_pool.queue_max_size="10000"
         thread_pool.rejection_policy="discard"

         oob_thread_pool.enabled="true"
         oob_thread_pool.min_threads="1"
         oob_thread_pool.max_threads="8"
         oob_thread_pool.keep_alive_time="5000"
         oob_thread_pool.queue_enabled="false"
         oob_thread_pool.queue_max_size="100"
         oob_thread_pool.rejection_policy="discard"/>

    <PING timeout="2000"
            num_initial_members="20"/>
    <MERGE2 max_interval="30000"
            min_interval="10000"/>
    <FD_SOCK/>
    <FD_ALL/>
    <VERIFY_SUSPECT timeout="1500"  />
    <BARRIER />
    <pbcast.NAKACK2 xmit_interval="500"
                    xmit_table_num_rows="100"
                    xmit_table_msgs_per_row="2000"
                    xmit_table_max_compaction_time="30000"
                    max_msg_batch_size="500"
                    use_mcast_xmit="false"
                    discard_delivered_msgs="true"/>
    <UNICAST  xmit_interval="500"
              xmit_table_num_rows="100"
              xmit_table_msgs_per_row="2000"
              xmit_table_max_compaction_time="60000"
              conn_expiry_timeout="0"
              max_msg_batch_size="500"/>
    <pbcast.STABLE stability_delay="1000" desired_avg_gossip="50000"
                   max_bytes="4M"/>
    <pbcast.GMS print_local_addr="true" join_timeout="3000"
	           view_bundling="true"/>
    <!--SEQUENCER /-->
    <UFC max_credits="2M"
         min_threshold="0.4"/>
    <MFC max_credits="2M"
	    min_threshold="0.4"/>
    <FRAG2 frag_size="60K"  />
    <RSVP resend_interval="2000" timeout="10000"/>
    <pbcast.STATE_TRANSFER />
    <pbcast.FLUSH  />
</config>
                                                                                                                                                                                                                                               ./._stock_prices.jpg                                                                                000666  000765  000024  00000000473 12134312441 015441  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                             Mac OS X            	   2  	     ;                                      ATTR      ;   �   �                  �   �  com.dropbox.attributes   x��I�0 @ѫ�� A0aQ\�
D+�!����@A�pw���u.�Lb6��Ճ����d��K� !d%Eaھ�A���=�!��H���v������|#�h]�cxp�!*/�^=�N���X��&�O�I�L?dŸ��(*��s]�ж��,�o7,                                                                                                                                                                                                     stock_prices.jpg                                                                                    000666  000765  000024  00000271443 12134312441 015076  0                                                                                                    ustar 00Vivitsu                         staff                           000000  000000                                                                                                                                                                         ���� JFIF      �� C 		
 $.' ",#(7),01444'9=82<.342�� C			2!!22222222222222222222222222222222222222222222222222�� ��" ��           	
�� �   } !1AQa"q2���#B��R��$3br�	
%&'()*456789:CDEFGHIJSTUVWXYZcdefghijstuvwxyz���������������������������������������������������������������������������        	
�� �  w !1AQaq"2�B����	#3R�br�
$4�%�&'()*56789:CDEFGHIJSTUVWXYZcdefghijstuvwxyz��������������������������������������������������������������������������   ? ��(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��3��[��{{�"���)�Cɽ~dܠ����v����[xn�Q���5?��*��8�E$��v��6;n-���j�/�~�g%���ݛf���0 �;8 ��A��4mb�XS\�,nl�.vn^H�:nf��P�6 �\����0�W^��DWw֗����ycf���ad�噟2�d؃n0��z�I�K�)ol��ԭ����n�f�L�����7/��$�R��אA4m��ZBmCM�mky%yY����T��0����-�Ᏽ�w&�y��7^N�h����s$>Z����_,Nr�@�����j���� ��L1�iß��r�6g�wn�qFܶƏ�� oJ��{�&����N�s"��d �F�Yrr\�*�%;�_kzL�:�iv�\[Hb��d"�e*Q��/��%��0�ږ�g�5���y��{�. e�邪,���~D
W�v�N���x�Pֵ�,���v�����9�)J�\�F�?���/���-K�ͺ�b��K}2�Rg���r����
����'i�}�,v�^�� �,����-nu�&�Qin�H�d2儋�/cJ�������m��i�����- 2���$�J?��s�>���^x{��l66Wږ����	���-ʱ�y�Wp d��
�k^&�DM;O�uM�.�g�m�j&PHFu%�z� ���`�b���i�6���q�ZE��Eu-�x�jpp�4a���Z��-�gw��ղH��[m�2H���2��Dۆ�6.Y�}��}�E����j`����Xň|�7>V�q�.6��w>ѿ��/f�4����M����4�|��q���i�H�w9ڱ� fj�iط�q�4�t����6�n#U\�,�K�EҦ�E��w1�^_\}�����F!f*6ƙ�rx �EPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEP\���T�����u�ؖ>�y.#?�#%U8���|��`��V_�tٵ�
��]�F���MoHHP΅A8�'���J;��	��y,�2�&Y������6ݍ�����^
�5��s��"����H|�_��3�C1��{��a�W/i��O
xn�d��=
(L`��e� hYI��]���;[k`� ���������goimu	�����2���K2����0�ݪ�/�Y�2�K;� iV�Q�|3���@#*NFA�I��Q���!w�QEn`���Q�d+�f&W$�Q��)f����x��±� �� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �WAE s� ��?���� 0� �T�w�� ��?��� �Qg� %Y� �U�������9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��9� �N�� C^�� ���?�;��z�a� ⫠��?����d� �+� ?�;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���_��� ���� ���A@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �U�Q@� �'~� ��C� ��?�U��?���� 0� �Tx�I熿�k� ���
 �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ���� �� �;��z�a� �� ���� �5��1�� ����� �ס� ���*��N�� C^�� ��� ?�;��z�a� �� ���� �5��1�� ����� �ס� ���*��N�� C^�� ��� ?�;��z�a� �� ���� �5��1�� ����� �ס� ���*��N�� C^�� ��� ?�;��z�a� �� ���� �5��1�� ����� �ס� ���*��N�� C^�� ��� ?�;��z�a� �� ���� �5��1�� ����� �ס� ���*��N�� C^�� ��� ?�;��z�a� �� ���� �5��1�� ����� �ס� ���*��N�� C^�� ��� ?�;��z�a� �� ���� �5��1�� ����� �ס� ���*��N�� C^�� ��� ?�;��z�a� �SMմ�fݮ4�B��r�%��*��8%I���5�[��u[� 'g�.]��3��g���� �sş��� H�h���( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( �����g��c� �+Z�+�����<Y� aX� �ր:
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��1��n#�~���m>��p��$�,1ׁ"~~ƶ(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��ln.�_M{��i��6!��0y��:�=}3[PEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEP?�ln5*mc�$]B�b7���)����s[Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@����<��^G��Z���̛����zV�PEPEPEPEPEPEPEPEPEPEP?�O�'�� �U���Z�+��'����*�� E-O=ωVyN���"7�S��<�܀q�'�����ͥ���f���W������_�F�:���Ҧ{�2�1�A���+�v�lw1l �;�۵���p���n����9��Q^]�MGŒ��Ӵ�����-����c3*�r"RW%�P��N��^*� �6�� �i���`gF��̝������nQXj�W��o�K� �գ�ɨIJ���m�T��i��;�����Ӛtev�ޟ��q/5[>�����8��}�����ȹ8��(4�����"3<���F�3��2�0I��sҸ� >��k��zv�}}6�i��q����-�bU,�G�a���;�<K�C��#��Ku�`�Ц��d 0W�������f�������E�2O/����-��Ǔ�E���l�gB����{Hg�d�r�`I��B8��}=qY�)������vQ���캴��I�u�%bq�E��<��?H���'�X��Ͱ���yUJ�{���7<F��8��� �,/��=:���O2��$��F�`
�FAj�s�� �y��Z� 襮��9�?�(z����?�m�� ��?�+��Z�g� %Y� �U���������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(�K�z����N�P]�H��h���� t
_*�m��&������,_�����2Z"�6!F$��%�rO�<Uak�ZZi�+���p<b0�ip����}�:���G��cT��f�c*?��a�$a��&O*��{�z�ԥ�T�}�]����~�6h����(��(����O<5� `�_���W?�O�'�� �U���Z�( ��d�$em��y����z��i6��i+�!�+t)Ig*� ,ĳ;�I'�$ԕ^���(���R:mr�>� �����
j~�we+��QL������Ck�K$�n'/#��y�fc�8���iϣ�i-o��?ϋ{|�s3I�r2]���*���XtMKM���IM�YH� ��2�.8�w��cX�5+]f�K���K���f��h&�p6��f�C@�^���O����ˍ��b7���G<��1��V}��x/#�֬����&��rg�lJdB$ �W��Km}��"�f���mf��ZT��Z%�0l��� e^m��9��{� �,,m��:���?.��$��N�P��N j�r��������{O���m>��\]��m�'2\�<��4ɵ�|��k\c��%��n��1ۦs���g�� �C��a� �n�����<Y� aX� �֋?�(z����?�m�� ��?�+��Z�AEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEP\� ���9���
�� �V��W?���x��±� �� tQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE d�~��T/�i�ʏI�o��Y:|ޠb���z��֒�*ȸ���e=�{Q����5{��"��̃�z������}A�:�W���1�VN	o�{�(�w�]�4w
�um3A8A��`������n�$�W�'W� �s�m:[ǲ�z�p,���ï��F�8����ZT���}W�y>�EU�Q@� �?�xk��V��)kU��$vs%�,I;n���++���O<5� `�_���T��ĮD���K�*��z^�d��U2m)<���s�{ɰ=�nq��zdu�
*
M[�}�oE�r��O���7��q;�ˡ6�*��8]/���y� ��� �Uv��xZ4⢢�����J
���E/���y� ��� �Ubt�B��Ny+9�ؓR�ZƜ"���hӃ�b�����X�e�&��,m4�����K��8��#��.K,����jFA�"�E�k^����:gү��҈�K���e�Kp� '!r	נQVhr�g�}kE�E����M�{���$
�	b�\�!2��:(�cr��4|?�t�{����4�RKH��fo0��Z�\�+�`~��8�\��<�[�1�Te���I�i�$Q�Y/����Q�U��9-�����Wz�����1�I���M�Y�;�i��8>��E s��P���X�ۺ<=� !��V?�"����J�� `��wG���9���
�� �V��QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@��m��*EJ^I$`��I$� ��j�S�t+?6#� 1�c��ޛ�a�a�Hi
R��n�ٱ<�[A$�ʑC��I*���I< z�x���,�����a���Vi$M��PA��o ��E��ʆ,D�����x��i�S����������*�s��� 8.E3�_&�g2�,z���yw\ƍ��(��1	��Z4jۗ��MvW�m���L�h��H��֎�O1���FO�a+ _+��9�S�ޢ�����aET�QE QE QE QE QE�:�����tqk��sH/��F�*�c�\���0��~��dT��ި۔�G�ҹY`,�UU����4�D�$�.��@�M瑌+v5�ӕ�� ��aV�K�)Yk������+s�
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(�����f�{���e�I�n���N3�8�ݕ�Q���c�*x{��W�����_3���L;`đ������UM*��3H���<ϲ�;��v�8�U�QV��u�VN;_ON��VM��J�?�ߋW��9�rY��l*8�8 �lkZ�j6?n�UI<��e�]���x�8#*��f�K�
RZ�[?ó��\�ET�o��f�<~M��. -���=�B#�[������q{�QL���'����*�� E-\�U��gE�5UX�"Io���̠���j��?�xk��V��)k�����	(��o�4d� k�й���o�=U���AlfuѵB���w�*�y;����� �8 �7�E�X��2�22 �A �qR����k�I7M~?�p��6��Gp�F��9�� GW�p�+X���\� ��'�����\�?��� �����`U`�0c�#�|���N��0��l�k��Q�U��O��̟�{��5O��m� Ǫ��̷P���{6�.v���#0��*�i5��H�YA/��l��U���Pj�2yk���.�ZH�MČ�-�*���(�B��ܑY�xfkS�h��n���w�At�ۿ�$]ʞd����G|f��{������'�~�P�6͸>`B0w��Ǘ4����٥�V��+G�[�\ۺ�w��e#�c6y�:��#��.��]C��N=R��J�{��/ ����Y��@�hD8B�z�I���Ju�gKם��5���f��2�.�	�'C��a\��&���QK[�&��|�����ԩB�s!������s��I�i�:.�m�Ecw�\^��Hdh�fHLxVa�#'?>�0;MV������W2K������7٥{{_(���GV� y�[�M�x�%�GHu4H�����Kv�����'�Ic�yw> �jA=��v%0�?�C+�1ɍ��`��W�y������~�v�C;dh����'���;�pǜ.v���J�� `��wG���9���
�� �V�Y� �C��a� �n�����<Y� aX� �ր:
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��=� !��V?�"����� �sş��� H�h���( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��d��/4�,qF����TI$�W���( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ����� ��s�8#��_�H����~��O����.��l�~�_0�z�ӧ*��n�>��tnV�$����m������/���y�����.�)���߷�� ���� �_/˴��'E?�g��ޕ�Z~�����o���՘�xz)'��V��;��I�,A�!�;\���V�Vs�)�L�V�~O�WC�e�_o��H�� d�``�8#�nUkN���u3��~�m%���v��W8��3�5T$�V2�ɯ���*���� lh:v��y?l����ݻf���g늽Q(�I�[��QREPEPEPEPEP1�ZZ�K✉;�,�`p�R%���#،�V΍y&����Ϊ�B�dQ��c�<� ��#����C����מ_������|z��������_���n'��v�}��8���� ��:[��g��uW�������QEt��EPEPEPEPEPEPEPEPEPEPEPEPEPE5]X�V�� zg� �4��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
���l���/ww^_i8yT���$�=FG9�֬�C��!������n띈"ۏ?9� g�"g����TR�w�+���QT`QE d�įQ�R_��\��8U�l���۰�}��#֦Ks�����U��U���QY�l�[\ɥ\��I�`��L�� �K'ʬy�(���'g�n� y���� oKvf�QT`rzN��F����������6�G��TP��\��AW>�����&�� �����9� �������	�� �>�����&�� �����9� �������	�� �>�����&�� �����9� �������	�� �>�����&�� �����9� �������	�� �>�����&�� �����9� �������	�� �>�����&�� �����9� �������	�� �>�����&�� �����0�}R��o�MSQ�����u[[6�Qbi['t�I&c�t���9���
�� �V��W?���x��±� �� tQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE ��{�C�,� ���Ek]s�� ��?�+��Z�AEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPE5�cFw`���f8 z����nx��r��o.V?���،aH�*�N1��D�F:=�u-��h���� S^y�P�m(����xd���%��W.)Ll`B����I`��-ӛ�T�]��C������j��2�%�<��f=c���c�a ���U#:��k�� ��s�/�R�u^Kݎ����+��}/�[�iz�k����#yb����d�]���nҡ@ .W~�|-ǅ4�<<V�E"�GU
�}`A�"��Zk�A��u��p��*�p��( ��( ��( ��( ��( ��( ��( ��( ���I%�,x'#�ry�e���c<u�Ɨϧ�;S�)%�y!�<2Dp�,�F����I�8ɣ٪��n��β��.m�s-��ik��v�v�ζ�B^4˱q$J@,\��+�h#n�kС�+���ȒF�QX@ֺu���^(��B@�l�I)�6x'n��@-��!��n�\�cR�(��K����z������������_�j�����n.��S�{[)?�rð�w��Z�h�"�"Itt`A�Em�;u���q�;l�u��փ袊�@��(��'�t�S���/�HGC$���Z2Fpq��ҷ+¿&�yq,z���?yw\���(���0=����y���Q�(���aEPEPEPEPTu��4�P��U����d2���3��U���T���3bsl�q�љ��wIw���X�kAEՊ��W$�՜z�,��fhU3s��I+�ggg4�����a���I���G�6w�4�^�J��U�$p"0�pB�j������޿f�������:�ǝ��n8�FT�-(�$N	�IF[�[�m?�k~��QEn@QE QE QE QE QE QE QE QE QE QE QE QE T�Z�P6r�!�2;���x�=e��Q�K��'�T����O*��L�j(-�I#�L��FuZT���9��M���{nRe�~�SH�|6�،�r�`��kX�&����F���O��lO,�F0��9 �� [�����-���re�]Nwww��,QEt�EPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPY6��(�f<v�Cl�� �9	i$Q�h	#���kVN��د��e�����<�����r{p&[�oKJs�����3Z�(�0
(��
��Y�p��jʗ�0h���$g�p0x88l��QI��	�II�/#�,a��YVE�Gd=
����؂*�d��J��9� �MFDG� �S�����Q:�2� �$kR��]X$��������QEQ�QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@s�� ��?�+��Z�A\� ���9���
�� �V��QE QE QE QU$�죹�ٮ�,� fT_��<�.ӏ�v��z���\��J��*��H�2���AGz�%v���(� ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��[�vl	�����9!pz�qP� hy�XE���k�lC������0qY��]L�jk���j����ń_i� ����?�x9�>P�#g�������e�lC�����-�21Wi{��_��~"y?� ��~ED�Vu���̪C.��!� ezs�r��5n�*��b���EZ�K+m�^�f�#������lrX���,���a����5�w9g=K1���ܒj��2�%�<��f=c���c�a ��ֵWs���O�� O�~-�>�l.bn$K���z����#�U���`z֬�'����n��P��P���G�R;֢��W.�z��QEQ�QE QE QE QE QE QE QE QE W-���cD��ʻ��g�J�m�����<�f0s�S\���g�/o����v���G/&}�f�U=���4��9y[� �[��1:�q�ߡ��E&�Uf��ILѳ�;u�n�09�p8튵EL���ɔ#5i"��%����?�Yh��:�rs����:Ȋ����*�r���
��ڹ���f�>G?�/C�2F���ӎگ����gj���_��?�ԷER�d��_C�G���-ՇT�Nr�?�J��
-�n�K�&û/�0q����QU�zS��ڎ�S7A� �ǉ� �&��Im[�����5��E�)4�GC�`d� �:�߮�Gƽ#� ���mQE�(��(��(��(�����ğ4�Y� ���Ҧq�D_���Etu�j� ����>�^f{��1m���|�W�8��)juh��_�::��Yɨ�Z��,�-ʹ��s����v�5�6)6=�+���.[�T��e%����9�8�]�K�[�p��wE}>�=GM���Yb��&@� Fqߚ�Y>�ͽ݁�ew$@���͍W�,r"�m p�j���K�
�Gn��? ��*���(��(��(��(��(��(��(��(��(��(��(���Nf�<m�i$!���]B]�H�U9+)�$�9�q�5��2[�$Ҷ��R�q� 2k��/q�k:�ˇ�qj�=V"ہ�Ye�h��+`c�PW�od���|��/�
�����m��L<Xb������Io���߃	'��o�8�+�����~���d���1k+�<���GT�A��v�M/P�V�l�(��x�@u�#8>�%xy��5����8���-�袊�p��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(��y���_L��[B�8A�!A'��C��ɧhZ}�̭-��p�C�%T�������Z���{{iG���d���*�29�֩�F�J
�[���(�0
(��
(��!�����{K���r.Hܬ0FG=T�n�t�����ZmId�_8J`��WPNܝ����� ��� [�������iW�P��*��3-=�z>� �]����O��ET��,n�]E'�`����$O�|�7�>���M=��E�J�ES$(�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� �xkS�����^�ho������'���O��[��Q'����>g�i�����s,�w;]Ġ�v��dg/u=Nw��O\]�v2� LpJ�8�����$u����be[N2Kݺ۵��Rl�QE�XQE QE QE QE QE QE QE QE QE QE QE QEU��(�0Ư<��(@%{���/��g�je%vL�+ɖ����O20�L2����ۧ�� q��� d��9ny��r��aV?����H������(TP�� =G�?%�� �3~�{{��� ����:�����IX#��!U,\w]�W98�$s��
(���A5�̰�:xd�.�(���*��y%��6�y3m��CH���Q�69
�{U�ɏ�;ē9��ӣX��N�s���;J�'$	���ڌS���Z� _;�,���a����5�w9g=K1���ܒj�U%m�'&��fL?'��w|�m����7�m����Ϧ���Y7_'���n�.�x�B�~�Q�=�Ƶ�c�Wז]��4��(�0
(��
(��
(��
(��
(��
(��
(��
(�� ���N�����˶�����N�PI8��X����;��˼�4�7���k�D������������Qi��mN�+CO2"ۧ\�����A・~������6ypc��?���������� ����� q�]e��Z���ET��Q@Q@yO�����o��؅�\"�K,A�S	2FY��[*AOP�����{�����6	ڪ2N=r��Wv>	��d��&&Dԥ��tteC��cD$c!	��k��7}5=������)�WW�v���f�%��n�I�[�*cr�?1�R�c�~�(�x9�8�Z�տ��K���I�����#x�bG�[��z`��7+Ы�C��k�<����QE�Q@Q@Q@Q@s��#�W.a�h�����p��qؠ9玎���#S{���]�J�:���H���v'�+엚��=(M�����g�x�E֭<}�jP�'�qZ�J��q�#��I���	t�|�b�6��y�gE
�� g }��׀�����x��6���si��s�H��/#�l�3�OqW�?y=Ń�^��j2�[��c�d��oFI6��߷�5��ʽ(&�����5u�^�wS8�1#� E�\��/�UU�&����e��B`�:Փ�� �j:U��U'6�?\G(�\{� �r=��֯&;�uV�0�u�i�X(��� ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(�:��~�+,^|���3m#�K�H��'CҮ��w�N�i`e� �NWi�N�!?36X�I9&�/� �i�]>����<+ElW��7�^#�<p�MTt���;�����~�|���JU;�迫�ʺ�/q�^C�Hg%H��ɑ�:����'��(��G
��B�y� S�O$y�OA\���umgO���.<Ο������n��4�����|�������Һ�_��9��E&�EPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPN���{E�^)%�$�ر�����z�Y0� �x���Ζ�����34��*�����Tǫ7���{/���aEUQ@Q@W�I^�a��.ܫ[���G8_0�����V(��8�V���𞣢kw�5��n�?v�"��#crr����ƿ/���
(��MSVGN/<T��K^��(����(�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��+��y�xz��N��U�6�\/��5�7v�
>f'��u �(�}[P�]F�H�L�]E-���3$qG�7Ȭ���P�F��[V��f�<s�i�z��:k\]-K2�� ˮ���Y�̥?�X������g��c� �+Z�����3y6��j��� Ǽ�m��8$7�e�7#c={W����<Y� aX� �ր:
(��
(��
(��3|C�˫xkU�`dY���6����
�p	�O�O��j�M����wO� `���8$g֭��� w�[+N�`ߧ��� �ݡߎۼ����N3[�h;�k�N� �Sr�(�QE QE QE QE QE QE QE QE QE T3�Ck�yϰK �X��Ǡ'���g� u �?������$�Is��S.�Eܡ��8�rp2� LJq���(���k�]Mj�=���%�I�#"8�'���z��d�^]�����,'�Y�s!�7���/ ���[Cj�!� 'sc�Rz��y�放�����i��V]�� %�s+���盩��� �6�G��1�x��sV��+x�PƑƽ ?O��0Qw���� �*4�}�~�ג�(���@��( ��(��}���Kr#�dH�ݷ̑�TL���FO9<Q�X� gi�[<�^Yv��$bY��1c���S�B>!���� [�O�卽��R��V���� ���� O�_��QT`d�?-��+q_��z.�%E��eQ�X���'Ŀ.���v�����s$���Uc���ֵJ��7��(K�}���EUQ@Q@Q@Q@Q@Q@Q@Q@r� �x��>���-3���&}�����u'���ؿ���s9���ɸv��1� �sY�u������{4Ys��+v0���trI� 2��:zR4zM��J�bS a����>�$�z�N��O�W�� ��c����r�QRnQE QP�]Ceg=����I�U'����۲3�� �a��i��m��\�Ꮢ���|�Ǖ�0�gW�Ϣ��#���(LgvT�c�G�Z���v�/.�7�v1 ���UT��=I��ǚ->��u^�/E��~���d�9nOxOVy����Z	w6Y�M��ṋ1c���g#��?���SD�ť��sK� L���H�� e�I����zmN\�Zs{�'��MIsMϤ��=W�QEI!EPEPEPEP\�{��n�}t�e-��o�FoRY�rX�����
j�9ym%�5���UTz�� ;�Y���}/M�\�����D�=��
�Ԋ����ut���� �b��� ����^#�y�MLlq�[m��=���}
��5/����z����}�X���#�`|���(�:�9�k�٣a��{�Y��C8 �:֭���ō�~e��M��7#��pOJ��MB�R�{��i��u�B�u�Y��;��}���[�����<�ê�x�*ݭ�7�p]۾�'�d��F�a�py�j��o'�ѡK�7�6��קs2ṕ�+p��ʺ��xs�zQ���c�ke���4��J{� ���'�<�5�R�W�X�O����ֿ�5����(��(��(��(��(��(��(��(��(��(���N��R�\m�[�98P~�@���jX��gW6�U��J#s|� \��q��z��I����̫I��n�� ��ܭ����;��0��M�o��o�G]�g�I��@�'������^���(\H��$�̏�%��y$�y5�[�q�j;-��~�EF) �X� �x�O����w`���d��O� C(��8<���-�o�Y_Q���_o챫�\�?��v`x'��;j(� ��ޚ��e[IA����H�h���p��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��d��/4�,qF����TI$�W�����z}��_���V ��>N�m��:՗�Ȥ��Zb�%����0�aP\�y�X�s�I�jT��F����%�v^�E�QT`QE QE QMw�9���I� ���uVB�y�Q��ݵq�a�_p���>��6�c(��d�q��](�((�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(���i�^���RM<�e�GjY��L  rI<b�(�Yc�7\x'������amM"^V��e��h$S���i�$��Q�e�d��L^�F~�"�8<Տ���}�F7����P?����5M^'����#�xZ�T����V�2�1�mN^��Y�$�T�jH�ƀZ�W돱�V7PO=q����J��=� !��V?�"����/�~����� ��܂���2�)�Fv r�Ic�I'�IE QE QE ����ͭ�/1Zjrl'�5��y�`:���Xv��oImiv��+cf�R�:|��I'zZӚ}���/Չ��(��QE QE QE QE QE QE QE QE QE�����t�'d�V��h�XF*n*>l�	8lmj^�W�VVp],�n"@��dI��t�P�+���<����(��� ���ܰ<~�B����
��:�=4�9�xf�d�E*��*��A��}������x���������k���Ee�,�\�ku#Ii#��s�Bx�OS�\����/�Z�s�p��
(��EPEPU5;���:[�� �E��d�B�g��*2x���O��|C�o������{��z�L��ъr��Z����<�rޙc���Eld�dye۷̑�g|v�Ŏ8U�(���������
(��&v�k5�5KKt�<������@<u5n���8.��|ƲF�#r��8<�55d�_��	� Yo�J?�$c�u��+�8��_�S}�?'���֢�*��(��(��(��(��(��(��(���:��N�w~"�"��i�N�8?36`I�V��n�m0r./�iB��� f=�HT�1��pXքT�%-�~�W�	�*i��>��ľkCg�˷hf*"FNH'�=�n�j??� ��ܧ��2~�1���;��iM�}����œ]���ꂊ(�6
(��
ɿ� ���m��a��ys�>J�.��V�V���/4�,qF����TI$���E �{��d����]\me�՗�,b5 wSש�j�M�{�u;h�__����J(�����`�M#��6�<�qa<�jY�gk��I+cӦI�q�\��J��*��H�2���AGz�_�x�&~#s=��Y+y� �1Hs���#3�/�DO�2�� E-mp���+}� ��/������m���(��
(��
(��
(��
(��2u� ��af��./��OA州�� ���{�ۑ-��@���ҤV�fs,�UQX�z Q�>��Qj���m�|�>�w\�A�{����8�9�� ?�D����T���Az�P������V�oe���w����-���('��->�$�E*��5yճE�I����wVF��J<I��l�=���'�= �3c�H�EMg���MJߢ\���Ϗ)���X�'п'�*ާ�Ū��LΉ*�I�n��H8e 2��ڰ��	n'�ﮕ#��it�A@"8��$�N@g������Xb����N������#|>�P��探�(��B�(��(��(��(��(��(��(��(��(��(��k���>�d9mB�jSc�X!+"L��G$�lwa��~�$���a� L�/����՗�sT4��� j���+}�}������7l�����'#T?��"�^���0~�_%���_��ET��c�|Z�W,�o}mqb�21A���Do^q�lU-G��$�|���}w�?�~TT���]sL��_�O�l��ڄ�����JuE����X�
�1$�d�֕a�c�0�̶����>��_���N����`����EF��v���?U�QE�(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��|M��z�ۧ�6Y����-����|c#�kVN���WF�7���X�Cq��{H�:�}܉��c|6�T�k�k��QEQ�QE QE QE U]E�=:w`��r��gn��$�q�V��� M�$�mn�pzI ���y�g�TΫ�yV�ʳ�yV�O�����	�n渞B�$��dΠ��[Xa��x�z*(��B#���a���MhQEltQ@Q@Q@Q@e�Z��p��M�#��R��\d�V7PO=q����(���/�~����� ���(��C�6���j��<�3���]�c׹�J�+�����<Y� aX� �ր:
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��=� !��V?�"����� �sş��� H�h���( ��( ��( �;����N����eכ�7��f�]�d��M펦�+[�5�H�,�}$,Âc6�1B�Z4lt�)�+|?��%�13r�(�QE QE QE QE QE QE QE QE �e�^i�X�K;�¨�I�)���� �%��9���$@��u� #����8I�\Ҕ9�������E �{��d����]\me�՗�,b5 wSשԢ����O�n_קȆ���9�.|��ȹ#r���5R��kk���}�6~�s����  ����M����w�t�c#$A� �A�憺��jܲ���_���55�ku5��X_��?g��ppq�� I�e�r��	�S���� �
(��EPMR��7K����d��_�Pu�
8<���L���Ӣ�2y������Hĳ�;nb���*�� �2�"���by�c��c�e�8 �XV�J���~�5�W�~��QEQ�QEcx��1xkI���x�Y�2�Xٹ*\�1�:�t�ʤ� �؛��Y>�ݭ�q47�>b�o��^}�D?�:�*ޟ�E�@���ܶ"��e��Ӝ�' ��#*T��_r�G�;���)o��):���g9��i=Ί^�)�G���f�QL�(�� (�� (�� (�� (�� (�� (�� +��7�W�~->�;Hۧ�,�̙}������X~� Hѿ���5iZ� q�c|y ����q�<��w��Ӝ��� 5�ܿ?ϫ��ܪ�,��6(?���~�v�A����/*��?��G���]�:Z�]���eG^iwo���
(��6
(�K,pB�M"�jY��@�OA@%}��� ��[��o���A
�� ��#�(�+Z��X�xe�ncd��a!I(�G�!ye����Z�1�^����Mt��� ��
(����5��j�^/��y�����Ig�+�n��Bx��}{j:$is*��U@P  J���I%�����p�m�F#c"1��w+�pO��M�Em"��z�kr�r�Xb��=�y]��X ��Xvi���.Ғ��%��~�(��(��(��(��(���� ��������:Fݟ�����^1��ȗ��֗� �0T�����n]X��BX�/�3�ǫ������<1� a7� �K�0z�T������ ��N��{E����M�(��
�<Y� �����ݦ �ּ����?����U�*@�gTu�;�WJ��e�em��wyR����n�ꭃ������e�V>�-�Eӟ$�>�?��Eax2Y��~��V�f����ż>׈��#����Zݬ¬9&���(��
(��
(��
(��
(��
(��
(��
(��
(��
(�/.>�g,�w�)*��v�ܜ��)(�إ%��Fu��:��p���t`F,���+����)�d���.���Zܲ=�S%��IY'r^V�vc� 3������>����[��c�"C&O3y �I7����\($�����ݪ�����-�����(����*��Ƒw ��FeC���)���S8�Eǹ!��÷� E��g���)�Q�I"wY\��Ad�\�H�v�\�� C���C�.�܌:s�ټ�"@s��%�������!S�W�䐩O�
]��(���(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��W��/�wc�O/��v~�Bc��xƵd���V�_�w���T��� ���#�e�F�����o��sZ�(�0
(��
(��
(�M2[�$Ҷ��R�q� 2i6��i+�ɝ|� lM3`gb�Y��8#q\���-���lq�E� �.K��l��W?u;W��O^I��UM�w�/���Κm�ˮ�K�_��QE��QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@s�� ��?�+��Z�A\� ���9���
�� �V��QE QE QE V�xВC�p�YM#�\����UPX�� �ҷ++��s�>�lmc�.nlg�$���`O�:��f�h7�~b{�TW�j6�ֲy��1,�>܌��jzɦ��(�EPEPEPEPEPEPEPY6_�����s�l����Ұ=��P�v�O9$U�N��;N��G��0�E�o�#�������rx�K������̞k�K�W�'=yf�O$�j^����)��z/��^��袊� ��(�Xo-�	�tm���A �9#�@#��ku5��X_��?g��ppq�� I�e�r��Cuk��:n���� �G �rsI���V�ߗ��W_�����K���m����m$3���	`8W����UthN���8U{������Ve�rY�@�;�8 w$
�Y3� ��\��ska���i���=�ї ��aaJN�B��JW��W�y�X�l䲱��&c5˩�i��y*8U�!UGj�EҲ�����P��)��� <.�(��N���5��n�*$jd�,[��n>i��?�V����MWQ<�o�O�?y~_�WF�q��d�az�UgB����:��_��"d��f�#F����Z�l&�h�;��)n-X)�+�0a2g�vn n�؏�^/����l"����t}z��88]n�y#�Q��~�a�H"�_��4,�V��2�v��k�.u�Q�O2�x�,ׂd;d�)�\}�E � ���9Vj�ն�����m��?�_��QE`bQE QE QE QE QE QE QE cx�ya���[��]\������i�Edr wRH� H�Z�A��I1(H�B�( � v�}S�'�Z���~Ѩo띈!ُ�����9����P�{����D�)i� ;��>�-�w� ���C�b��-#�ҭ�?ze���3���c��\t��}~�L�� �ߨQE��VN���yo�/1���=*xC��``�2,���9e�^i�X�K;�¨�I�+;E�G�]F�6K���a���q�yR�\����2��7��'W�޿�7���EUQ@�~[�9�gd$u*crG�*��ҹ��]_i�Ȋ���c�t�-_o��jFF~bOPM����(�㹋i����:�?�t/�?����� Sq��G;<�K.�������8 a��H�k���v�8��n_�� ���E QE QE QE QECuu���w�#i$l�Td�z
�n�°��6OX�Ԥ`{I��A�VVϡ���_�=�?��� Ѷ��Y�|?��6�6H�s.A*�ǿ �>�`}3��Z� ����2�� F�Q��S�h���� n�5ڞ.�������4nQE(�� ���'�>"���&�R(g��*�#��9m��͓�<�unz���[��{qf�2-İ�oh�N#�'�ۏ��m���[�f����{
�$�����28ᔂ�;Gj���R]����Sjڸ˺_���_�[��+3��( ��( ��( ��( ��( ��( ��( ��( �W�������}P����ؠ���\W�Ziw��� ��&	�T�2q�n�\�q�+9'9F������_y�w�����r�������ztIa<���)ꬦ�s�(�9m���ݜ�Z�]���K���0A	4�e�A�C���OZծ��s�l����������(�FQE r���|%�Hym.�����[���� \�I��=Mg,K}���Ko<I$�ȡ�n��
�1��5_�s�?�4�s+�wot�1f���ܠ���:�� � �sUG\*_�&����aCH۵���6h���p��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��|���?~�?�H;��X`�@��'�'��7}n>��c��1)>���J֩�_����j��_���QEQ�QE QE U'� L�X�0ڶ�3�R`�����o'hR5�4�#�7L�$�1�Fq���m�ۤ(I1����O�'�}Me/~\�� ��~�3����o�/��z��E��QE QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@s�� ��?�+��Z�A\� ���9���
�� �V��QE QE QE QE����:m�� Yc�$#��ar?�-#88�@�[����êX72�jw;�����.>�:�Q�w+|O������Q�(���aEPEPEPEPEPEPEPM��v�gb��k�ˀy�,JG|�����H5�Y:�U�ګ���#o�`	�0�OF�� kT�U~���vJ�������
(���(�� (�� �{g�!�|R�p�?@�}y#��	H0��H�+�T�E��$��zg�#+�T�AVk�SR��{6iL�a̱�
��6 �� $�dc �A�-uF��k�[~C��#��f��Y�5�Dg=����ܐ*&�K+.^�f3\�����8'���\�Tv����R�:d�/�l�I�Aa9-"��/����GU �ѿ�j!5Q��X�5L$#	�e�˧��QE��QE Q�5�*k��Εv�0�����4݃�s��O9<
4m;�'F��2��CY'+��'W�����,rI$��To��c�<���Q}���m��\���CB�#��V��)�uz���~��W!�A.��]*�(��Sއ�#RD7.,���p���Lyypz���d+/���B�(w2�nQ���Y+)6�8 �q��X��ŧ����� ���+֊����� mQYZ%��G.�&�F�dsˀ�hAY�F0���eu��ժ�%��QE QE QE QE QE QE QP^�A�X\_]I��[D�J�'j($�O ���n���/_�u��a/�x�]ă݄�L��� �GUv�G�tb���e8 �<���9�|7a�{/�!5��?���x��s����^K���]�� ��𧎒|�/ee�ee��VҌ���춈���(TP�� =:�*MҰQE��� ��X�C����p��F2�x ��yʻqԍj��?�<�e���o��n��� ��' �m?tV�Lu�����mK�w���m�QEFEP-_�"�A���ʇѓ�S�s7�tۉ�H�tym-ۮ�VA#/0������M
\A$2���R�3��0k���{�g�����'�Yv}�%��s�:c�j�rP��.��M��C�ҏ�G� I������*�(�� (�� (�� +'��?����}�e���y�"ݎ�ߜw�2:ֵd럽�I�o�w鼎��W�q� �A�'�"g�|7��k�Z����5;�l�'Ϯf?�&s��T4O�]�,��H��¬y"1m���v�L�毟�ki��al۳��a�߶��U
��m��̲jw�����s".O|""�@�t���
�������Z�O��I~f�QRnQE d���Z3ގ��xX}���i��a׶wN	�m��Q�I���g��8� [����D�kfX���h�H�R��2�z��a���|ms�X��\L��(In[c���fGa�M]%Q�[IO��l���W����:Z*���E��^®�*�� �n��8e ��ڭ��.-��B�(�EPEPEPEPEPEPEPME���lVK�)S��=�P�{��+3�(����Ġ%�QFB������m�	���v���5Y�趫�R���������������ztIa<���)ꬦ�s�(�9j�|r���|�_�3���=�9|��� ��QRnQE QE R�������iwc����>�����Z����:�܎�:�Q����<�i����`>��� �k	O	��}7# � ǙG�T?�������>�_���w}��;��k�a�_+;��ߊ0���� E���QEI�QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE��~��H���-ߛ,c��ќ7�Q>���u�'��� ��+�� >O�o���9֩����0�����[�AEUQ@5�cFw`���f8 z�uR�� L�[Q�@�k�C����q� ����V��R|�M�_���Die{�T�8�0��	�An	��A\պ(��!ʬQEQaEPEPEPEPEPEPEP\� ���9���
�� �V��W?���x��±� �� tQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE ��{�C�,� ���Ek]s�� ��?�+��Z�AEPEPEPEP����~��y�g�7���0�Ƿٷg��c�����oó�������M�~�i�9�E�0s�[���]���4$QE`0��( ��( ��( ��( ��( ��( ����K���� ��}��4����{���:Փo���K������5�����m�ӝ�L��s|>���˯�~65���� ��( ��( ��( ��(./��܉�6 �9ʔiv�Y����ws���QE$�عԝGy��QL�����W�N��8:Ħ���cdv��yI&�ݷ#�)C�j?պ����xg�2�}m�i59L�?�;Dsʩ�m=G8 nQEg�7/����Uj�MGB�,aeYnm���(,��5z�ɫ�	�IIn�z�cuc���O�iv�ۅ̳�H��%#~V6�х�Mn�<W0G<��ʡ�6��dG޳|1�xn����H��!�^bb=�!#����I��ӺX���6�� ������b�'��*�S~֒]R�7v��슯N���M�r�(��(��(��(��(��(���S��am��V�m?tņ�e'��r�#���u��/�g�����b�:�O2g����XgRx I��͍��O��u���l'��T�y����g�>}p�1� �3�oz�T�����ϕl�=��n��/���:��wk���2���{��k�h����+'[� L�4e� �������� ��,|G��}�ZՓ��y�����#��s�� �[�N@#��~詖��sz>��� .޽>��V�kQEFEPEP\���H�-h�~J�c�[*K6 �Gt����v�OMuu�x�!� �/�0���W�uϗ�����k7��.�Zo}?�T����9m��� $Φ����G�鶷Ы,W0��a�`�;�V+D�R��q{���(QE QE VM��|Q�B�Ɛ\ܨ��Lq�� �e�c���5�&���^���-��E� 8i���+�ǣ�Tˢ����Ӛ]��}���>}^�E�V8�'������O�������&��i�y;�ݙ�bO$�OZ��	���W�h��x䍊�0�R#�A�]EmpAEJ8�P��  p �J:a�����Hᡳ�����(���p��( ��+���h~_ߥ�Q��9#Q�v�&$u�I��:Փq���9��w�=����C�D��_��=y�S.�ގ�P�-$�l�]��<|�ױ}���m���\�_�KL�'�nVW�,縰K�(��B�Quj��u4y<��㝻�u�Y^A��[�Z��[\ĳD�#r0Gu����UO��_��s�v'��+�Q@Q@Q@Q@Q@Q@Es:���p����/R �-R�� H������Q�j�ߡ/��]��$�t�ά�c��/V,4�1���(��<��D$�s��A'�n�KK�,^�'���M��N�LsL�Y��   	'�g��l-���
ܭ���L����Us���r�TU<:��� ����8EE(���EV%�Q@Q@�o�L�_��|{�`����gިx��3h���V��{����G�\}t'�� ��
\A$2���R�3��0k�օ�����k��v�4��Q$d(�|�S�=0GXglROii�����U���� �GGEG�\��J��*��H�2���AGz���h�(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��#����p~p�kru�$Q���,�Q��_�kZ�|3��z���7�m���]����|g�kT��L��W�}�~�EUQ@]\}� B��$i�nc�$��������f�%?4�cF�,ð�
���.���0�(�9����\u�A�"*�e}�������a�iӧ��� /VQEjlQE QE QE QE QE QE QE QE ��{�C�,� ���Ek]s�� ��?�+��Z�AEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEP\� ���9���
�� �V��W?���x��±� �� tQE QE QE QE a�?���.#�Y.f�v����F_����ˌ��w+�_&�g2�,z���yw\ƍ��(��1	��ަ��/U�;��[�QX(�� (�� (�� (�� (�� (�� �y�c5ܪ̱�B �9�Gv' �P��rXi6�������y����N�=^��W�� �uK9~dI���?ՂG �v0���Z�k+��ܤ��_�~��QEFEPEPEPEPEPEPXv��1�u��ݏJ��:�Q��Dz���5���iZl��+�D��y��FY�
�� w�4>]3C���d{��K����N伬2�� g��?r���E��|��4����aEPN�������e�����|����Tp{rm�Z|Z��Z�΀�H��t`�� ��*�A� ������^֭ۗ�H�64b >��s�#��kR�'5��7��Q��?�&� 37E�%��h��Q�a�Fśb�)�r�2��0U��� Į�/��D�������6�����[Պ�q�����4s��EV#
(��
(��
(��
(��
����� �����No�����ۯ|���c�d���.���B��Q��n	M�F�Yʨ�H�S�z|ZN�g�����@�F�X�(PN ������o���j� �n�Y|�w���g��*��������KI����vfy��+�q�|0�q�Y�z���2��"�_���h��������'�IK��^?+��o1�l��4n��M89�Eu"�� �<���w��n��?���ntֵy� ��-OR�u+��[�ift	&�AweW���W2�� ��I�zgJ\럹׎���Xv������/���QZ�!EPEPX�z|Z�ִ������	<�h��tA���~��T���~�}�����7�ǿ)8�]������-��5���Ρ.���D��Q_��^0���Oe�u�|;� G����� Yi8�O���Om�m!ۜ�q�{:ڬTjJ+d�����F������ �QE!EPEPY:/�.�����ϖ޻"�&���q�g���'�??��nz}�}���y�e۞�ߌ��p:T��#xiJr�_������s�9�x��OG���]��	�u5�\����v��Ww��㻲A,�I�64b{㜂A�kHi��+�K�g�Q�_�QE&�EPY>"�Ν��'���#�;����⵪��g���X�̱\��C����L��F�&�R2���ӯ�X�=7�%z��x��~�g�zw<H��'�?hJ��^I�h�ws��Đ��`$�î �� ������g���b^�=��"���ڬ���~q u��9U`�� <}�
X��'e>�G�tlʬ]6���Er��Q�E��E4QK��i�@ۜ���+(rd
X�@
�Ua^��Tt�wWAEVC
(��
(��
(��
(��
(��
�a��n/:��#��=�X��0�#Ǿ1������i��M�X��6�U��m�u�t!���b��w[���-�t�O,��Ge �'�z���nE����Ί�}jt��'nW��_�n��������+���Ze��V~�R�J���D� �>v'�ܬo�*hq]\��]߳^Β�F��6�$ƥc���  ���?�m>���vs-��(�QE QE KK�m.�)�M��]�h�a���;Uڥk��F�#���q��J��co�VS�q~���g�H?U�_�(xC��ս�� Wc,�����
��X�8��pJܬ='�ľ!�~d�X/Ԏ�7�a � ���Ϧ
��r�1:�r�߯�k��(�QE QE QE Vv�u=��$��)t����b0>b��2��`����G1�_��ᤚ�� 2J�K���fVb����]�+rz&n�SP�u��T���!5h���;�(��9�(��(��(��(��(��(��(��(��(��(���G,�xv� ȑ����!��s���{�=��V�d럽�I�o�w鼎��W�q� �A�'�"g�|2��[�k�Z��ӊ(��!�5�(�*"*�� ��EF�VQE J��._��˺���@݈#��qT<K�ϧ�H�2�@����:��~�}��I�#�f����v���h79��q�*�o���~����p�������?�����QEu��EPEPEPEPEPEPEPEPEP\� ���9���
�� �V��W?���x��±� �� tQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE ��{�C�,� ���Ek]s�� ��?�+��Z�AEPEPEPEP���F� �Hlf�6RDB���e`� ҷ(�?q��3Đ���z���]�����A�o�G�����Sr�(�QE QE QE QE QE QY~ �E�d��F������й�]q�(���GP���t��5�t_���՛�u!�O"6eL���7��d�kS"�8!Ha�c�5
��
�p ���ea՟<ܖ�=:~ES3
(��
(��
(��
(��
(��
(��0���k:f��4q�/���<�@�ʱ�c/@�98 �V��Ӥ���{)�:}�&e��ѕ�yCc��d�nV��֩� /����HK�QE�(���/���l>�<�?]�h�����Z�u�x�G���o�g��7��v}��c���j��ѽmT%կɵ�$��Ė��|�rfM:F�rY��vX �����3V�Q�t��+TD�ȹ�U��}���S��A*FU� �f\��������g�t����U'Q�ӰYd���I���-����,��ga�2&�D���^�
(���(��(��(�={�2� H��:�]͎b�"@��?�y
G$�87+��7�W�~->�;Hۧ�,�̙}������[��Ta�_����	w+��ֺu�� ^(��7B@'�}�kk�R$TR�H�}[����N=U�U��	v�ֵ_����kY�%���H*���w{�(A%f�Si�灂�lf�������~�?��{����`�	���i'd��F<�#��ł��wK3@�x-�J�d�$�.Kr	~H�ճEEr�Z����V�ߨQE�(��(��\��S���� 2zew�� !���v�j?'�'��\���1����b���_����k�����l�|1���R��iZK��}��� h\z�/��(z�vu�C���+����mYQٸ� G��%S���*���T��:��V����7��vU�0������
(��1
(��
(��(�W�i��}
�Kmm$�eIU$g����xe���eg�'|� �0O�G�_�EhO������ގI�7_l�0��g�jέ�i�D~�̐7�W`��c�ƫj2k�� 2���������2�_h6!ѭ��bL��ɉ[=NiF6z�GAXq~� �w~g��M2#��:Y|Ϯ|�����2s�]����;%���L�)%dQEbPQE QE��� ��j���Ko��(��cJ�� ��H&��i�Z�Cyn���Il���\�PÐp{AU�/�o?l�3=��n>�{��9ε*rq�]�7�k%?�I���Lͽ�4�A����O������V!T��u%QT�FT x�� ��I� ��� r�� �b��� L��� ��+xג\��v�U�h�GN�,5_5m'�,8�9b�vd�	���qW�����j�[]��Xs���rŜn�"�ɜ v������t�i?�����	w�;Qv�ע������p��ݟg�=��vW���ET��;-R���&Dm��F�Q��22���u����%f0��) QE QTouk[)��-5�.䵁w����\�ol(=H��F��su/�}�� ���f�'D�8!9ed�r荣EۚZ/?�n� "�����8'��e� �L�=Y�
�����@��� `�uu+���� >�2���s�m	�A�*�����	���Y��yۻ1<���=�X����~�0����]��g>��Idl��j���tIU!z�P����S\����#�� ���[)�{v3���
J���v1�ó�OP��h5����m����fX�흻��yq����L[�7kErOڥ���_���I�N����� ����eQPsQ@Q@Q@R?�����ٷg���� ������J����^�x�� d�1�)�>��]��_��k�)���ߓ(^� ���J�?$Wv��3�k()$J��K�	�e�Al���G�=���:����K}ō��WEX�����O ��]�u�	z����h�n(�QE QE QE r�>���cd��Vߩ)��.1�`v�� -^!ޝ�m�÷��U�_꼻��o��r"��K۳8��AN?��k�����<���z}��4��-�8tXN0�[�/�o?l�3=��n>�{��9ˮ�d��������g})�8wM?�7�� %/�֢�)EPEPEPEPEPEPEPEPEPEPY2~����9�5��o�>l����ɓ�M��#:Փ���^֮��H��6,bP~��q���̺#z)˲��&kQEFE��;�1*�IVi���X%���צ$f��%�`�*�g����.��IE93=-��o�����>���a^��p�9�*U�1�u������X���?��e�i�|V�Q��a� uG�@¨��r�I>k[����lʍ4�9/y�� E�_�QE�(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(���-��=B����S��S��d�2s��9cn:n��7+I� G�/�m_�&��#���X@?�n�s邼�@ޞ��G���b{��QE`0��( ��( ��( ��( ��( ��� O�#8�63� � V�جx��Ș��t.��������Am$��v�����AU4KY�����6�\~��dJܲ�u¯'
�2qR�i��!)����O�4h��� ��( ��( ��( ��( ��( ��( �oO(�M�����b��6*ѳ)- >���c#;0$V�a��L�\�m���DA��+dђ,|�9`wëO����6 �+h#��(bP���TP0 � �RQE`��c
(��
(��2u��f^��5�_'����׶<���q�r5�'��'���z��e����.%۞�ٌ��pzV�J����������m�QT`a��%��-���Q�x�U�=��x�9$�J��Y� E��ڄrEm,/$�urm�1���"�w���<��z�4O�o�E7�������~aX�Ŗe����$g�/����r�є���t�ז�?+$C�+=���	⹂9��%�U��eu# �8 ��%y�[FXQE QE QEcx�yb���pJ��t�YG4lCB�Ȱ�9ʗ݌���GZ�p��P]]��3�_��#�'R�K����r�h���,F4�� 
ܨ��+h#��(bP���TP0 � �RS�>z���ĕ�J����^YdyH� d#)?���~�v��ki��al۳��a�߶��U�槼��葕/�o��K�AY:��e�l��]��o#����\��P}���֬�����J��� ��Q�"��� |� �O��\�;p�Ŀd��&ѭEUQ@Q@Q@Rվ]:IO݅�v�*���
qWj��u�][���Kt�9����.�ʺr�$�3���Mw\�?r��M��w)�Ĭ�T {�ֻ:�u����62%���|��
�j=�p}�sE��Qд��V[�h�p�
('��tUjN3_j+�����)�ӏF� ��2�QY�Q@Q@:�ϫ�H��ݼ�G$ �U-��3��wgR�������i��0��C��I���l��������G��&N�6�ټ����זYR?��O��?�aS�k�K�7�0�t�������~��>#���c����%�9�S��63� ܬ?~�R����S���S��q�H�rz�]؟�[���$��J�(���aEPEPN�����޿f�������:�ǝ��n8�F�T�l���3��TC�nv�R3���U����e��j�9�n���3��ZK����ޢ�go��~<�ՋtQEQ�QE��hvZ��tU���vG{l�\�$.������	ꦪ��Y�8���V�����"��] Z3�HnQ[F��,�^��~B�SO��Hk+���H�	GS�0�ʰw���l�ŏ�Ff[]EWʶ��S�ܲD
�\��*����yލw���A���V���ILr��o����|��4k�j�p
�)Bܗ�������=_S	N�)u{?/Sկ�;=;�3bI3�Ċ^I1��E���8�*�5MO�?��6�~���;b��K�q�cm�i�a��4a$��%}bg�Lg���63��p8U��x����7��� �j����Q� 3��Ӈ���w�-���V5,�-t�LV�,Jͽ����ى��e�I�j�a���_�ѿ�m/� #Q���F� �����T��[5� �G��e7'yj��+�^*� �6�� �i���x����7��� �j>�>�� �����`xf(��	5��Q���:���� D��2!!�����|O>'Դ�4��,���DS�o���!�DLU�EF2aA���t~��y�.48��(�Y�qt����GF��chG]\#��UO���Z$��V� �%K�ӡ.�,��ǔ�6,#/!�KX�$�į� ��+R�u?�-R�Q_�Agr{�d��"]�:�J�rH֯.=�U�ڨ������
(���(�� (�� *��Ł��ؤ�W>����� ��U��k�:��,N�[�$�gU7NIvfUӕ)%ِ�Zw�ƃ��o���i-�ͻ�oR��Fq���E��4;S򼟶[Gq��ݳz��p3���Y��n�a�@BJ���g����t��d�����"��d�m�"���Pjt�4ף��_�-I;5�7(��Ġ��( ��( �o�,�C��J���,aF�<0��er�/
� H������?�a��z����)��n�8��*�m��c���f�?���� ?D���`��v��Gmc6��6�8U*��1�L(�5��K��7��{��S�+��� �J��߂xk������ 7�O$y�2�=��T� p|�Ԧ�-�k���d�y���eS���<W#w����N�����G� m~��QZ!EPEPEPEPEPEPEPEPEPEPY>��nܻ�[��p�HҨ>�\�OZ�^�������϶�`��L�a �,Tܐ*ݭ�6VpZ[�� �c�rN�Q�2y�*~���t7ڃ��� ��������,-^��M�&2@,I'@�@ I  I�M�yf�x�|\^ClbI��#*V%c�l>2�I2j���37�Jt�`���=��m�V,2��n�4JN���f�e�ğ�����3�:� *���Q��~��w��3	��P������TQEI�QE QE QE QE QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@s�� ��?�+��Z�A\� ���9���
�� �V��QE QE QE QE V�x�m���LO+��+����Lx�z6q�����?ѼU�]��;�~Θނm���6����\��?�_�Bf�QX(�� (�� (�� (�� (�� ����,t��\�d�_X#n�\Č��]��F�d�?�w���xI�8���ԓ�di{�ݜ�֩�����mO����o�QEFEPEPEPEPEPEP��i��Ryv��4Ҿ	ڊ	'��=*��l�ѡ{��j7_�7�!�3�ˑ�����¢��T��e���/>uȻ�2�nD��<���Ip8$nV�ܤ��k�Z/���B�QE`0��( ��(��c���^�y�_ڠ��s�r��w�hү���+� /��Tͳvv�Pq���[��������=�{��之v;gfq�8��S��ִ�5����QEQ�Tq�O+�#���eP����*�� ;T�Qp1���Q�%ޓ;�r1�C ���,G�bN78�!��m]�.�g�D?�!j��z����,w�1�	��+uZ�TW��� ���<W0G<��ʡ�6��dGޤ�i�=s�w���e܌d���f'$��X���6�@�p)��f~����ooۋI�ߓ�g*6�G��T�2Ob��'.��~_r�7(���aXz��g��kD� �?7P��;~F�����d`O_%��7+G� K���@��XD��8�q �a,�!�f09ކ�S�=?[����(��J�������#$6�?�ߐ��KK��^�Y�}� �]�i�q�.�;U�ʏ��}~�Lp��O��{�VM��|Q�̜Ɛ[[1��L�� �e����5�'C�캵�� ���}��Z���LG��-��KJso�_;��3Z�(�0
(��
(��
(��
(��9f� G����i��Pш$g��o���$?~�������sE�����Gl�<�`q��S�����Ŏ�����b9����a���zZz�<C�[}�7ȼ���ŷ�Fs��1�H��FK�q��_����I�՗��f�Q@Q@Q@:��C�\��+ȳ��;˻>�~1��s��>mn�춗v?�-3靭������b��o��������\�bR}�����j�6����Mi�	�Y*Y����`�8A}�� x� �(>�?�u����:m�� Y}����rfp?�!98�I�[�SK���t�=6v���6���QB�p ��[���S�)-�d�QE�(��(��t/�i�u�5��?���OӶ<���s�p5�&?�_̝��eU^��m���VX@<�&032ѦoKބ���� �sZ�(�0
*��{+w�������*��d�:�+?�wڧ�c�V� �us�LGT�~g\+�������%Ͳ���������]>-���l@~�eP9f88Q�{
��ꚗ�gڟ�op��a��L��_�F:�g���Ln i�v����V�2~��gb�A�^�f�/��>w�� ���Уg���Ln i�v����V�2~��gb�A�[F&i�j%e
��@� �A��g֟E4���U%'y0��)�QE QE G4\ I�I2�WP@e!���=����/�n�a�����7�<m��7+��CU4k���x.�}夆��� ����7c�5�Y7� �/���!�7l�}���o�ئ ���8Z�h���{�t�k׷�}�ƵQT`QE QE QE R�~]:8�݅��*�Pg�
3T,��|e�ۏ�+�h.�O����V\��%� p2� �M�/���/���:aY�=o���5CT� F�V�w���i�:cz	�����q��s�`����_����)�v���ܢ�*M(��(��^Oo`��Ryz��������	i0x;^M��m�EW����Z%�{ �E�G%�B�#m�I+���6�r���i�^jǘ-���~�a�c̈��8�7�����<Icn9K��B8*�(Ǹ*g<wA��u�{�T�� W�K�w6���u���ކ�QY�>�<7cn~��c��/	11Ő��J֬��7����H��أ=vH��H2������S�#|O�e.�����EUQ@Q@Q@Q@Q@Q@Q@Q@Q@:���,�}��/���Vg��>N�m��:Փu���:��o�,�����6>�V� x8n�ƵLwlޮ��&�m�� ��j�x�˧�ɲ��]��0ļ�.q��Iw�`�5^���N�����˶�����N�PI8��Y�l2[%浪/�w:��s�&�����n
�X��fl�@�h'V{/�� ����Ni4����� ���#��좍�d���D�<�W9���?z�*��/$˶y�͔g;I ダ�u�{ժ��y�w���܊I۝�� .���p��*�B�(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +/�:���ti�=A��FaK�IJ�b����b{ �� $jW7��!o��ᢌΚ=�$�F�V�� � �RG}�Ҁ55=Y4�*$����l�m-�������UUr2�@�Q�̠�� ����;�\� i�~����h�|����|��͟>�5W��ߏ�I_������/l���X�`:���� ��~�� .��� �� ���?�����_���{� �4�Y5/6'�����.v������Y���p�H�a���g�{�C�,� ���EkE��<}�ʟ4i����9 {�(O����������K�O6��i��i�.����}�ې�2 1�1��� �������� �M7� %V�e��n$�I�#ƅ����{d����QE QE QE ���å߯2�jv������~�;��Q�w+�_'�u{��[Kg����,C̍�����G���]ݾOF)lnQE�(��(��(��(����K�>ɼ���K�-��]�cR@9�D�G@}qS_jvzw�.fĒgˉ��cڊ63��p9<W������K��h����3-��l�g0��pfq��
͵���Q�Z*��=<���.X�u�k����:}�zv�kc3Em
B��X� �b��B�gW�(�ȻT��N�O֟Z��I�I��QE2B�(��(��(��(��(��++ė��hOi'�y.�kY$Ұ�6 ���O�x=*���j��� �� ӵ=WY<	e�K� L��E%����7C��85�PYY��X[�����[D�ę'j( �O u��֚��[t�Z/�K`��+1�Q@Q@d���V�_�v�ﰞ��T��� ����'Z�ls�M^�yIc��$����#�?Ry��toKZsO�;���Z�(�0
(��
(��
(��0� ���������u���C� lr6uc��L��n�G�$_`�|Ak��� O^g�h��Ϊ?x��8]�kr���s_ϯ���+
݊���e���:��[�@�����' �$�U�՜�>���=����d���<q̌ǎ9�W��E�M�<�x~Xlln��m=�����y�c�ɸJY�k��X�� J��Q܄W� �xN~mᤐ��K�!��ǲ��<gNwrz�Y�-?7����r�F�2i��	&����c���O�Z�ͦM�����0L��vq�^MI8��tB�'
r���6�Ҭ�v��3���E����B
Q]�|1��n������� �be`=�9�����$>��	.��	C��
��q����rF+F(����8�P��0�  :
>���t:v��� %� ���*��(��(��(��(�[Ŀ��uۆ��RC��� ��� \��`e�8��t�� G�]���.�$�F=�eh���8FO;x��K9�����G���Hh`BB���F<uuϦ឵-�����m'�g���V ��24H@8�2�q����)K�v�5p�|N=�������7h�� (�� (��|Sυ5d�����we*�=Ib ����f���'wi��o���-̧��H<�ol�1��g�+3�_>��Z��w׶��G?����Y	��2J�k��?�?����k��q��q�K;m�S�#=�j����/��䖧>*~ֲ}���u4QE#@��( ��( ��( �m~X�N�e�b���,���r	�{F�'L�@���#Y^�h��o �v�H��KE�2n�p�X�
�w&u[Pm�}8���vOG��g_uuoen�wAct��E\���Rg��Q�8�m>���_�˟�a�� ���8#p��
h3�4�z�-ީ�i䅃,�a��]�J QS�Pdaz�"�%w��ƕ	���ק�u�oG�εѭ�K��[���ۛ��#` *dpv�ry�(�I-�y�Sw�
(��EPEPEPEPEPP�Z�{g=��o�x�9$nV##������Ӻ3�k���x.�}夆��� ����7c�5�Y7�> ����]G�9���wBNx&U�i`�j���kY+�������o+QTbQE QE R_�[�wm�l��ͻ�޿�֨x������� Wc,7����3���X���2GZ�s��N�^���1�ߟ���{�T׶pj67Q���142�H܌##��=(�MS�w�e�����{?�_Ԟ���������n��x��������"�>���`b�j�A�n����(���
�����	me'��_J-m_ �n�������q�fޤV�a�� ���ڀ�������Bۧ*G��<���9ڊWs{G_�_~�W5l��Ӭ-�mc����XbL�� O'�:��*�Rԏ>l��,�V8IM�t� Y�0=p�>���y���_L��[B�8A�!A'��C�Yɧ�v����p����<�˶O$��$�Iɮy7)��� _��v��{/���/QEFO��� {��M�D���{cg|�Y:��5]�|��{id=rF�i�3"B���u�c�F���%�~M�� ��*��(��(��(��(��+�񉻋H�E��B�n�d{g���I�+�T6rp;v�v��\��){Z���s~)c��H�P��r� �:�}y��u*�&֡�h��4w��3���1��7�w�6_�[/����xRhdY"�C#�ʰ<��*i��ͱ�G�����~�袊��
(��
(��2t� ���X���W�g��v!�v}��c���j����4�w���<�+!�$o#��� W�  8�*M_P��"��T�Q�`�F����V ���y;TδQ����o�ң]���oЫ?�Nu��כ*]� ��j�I���U����$�~��&�+Q�#���������K��[��TV�#���f��i��1�3w$���r7��y��e`�;�A��ш���(�/�/�ӡ�S�j�~����W-�E�Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@V^��-¶��i��l��OyضO!�d cc�� 5+�����<Y� aX� �֏�������	�� 䪯���P�J���CUM�A	���R�G�#��J �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� *9����[{���	P��ȡ�ԌA��8�IE W�����䳿����Lo�xĈ� �����
>�g�����H>��yf�Ǘ��nͽ6�t�X��+��Y�q��ZAikvCb4\�Np2I?�c�{�C�,� ���Ek]s�� ��?�+��Z�AEPEPEPEPQ�W0I�$�ʥ$�E��`�ڤ�����3�s�
y�yf�O�y$��3��I$�I'�l���О1�p�^���\ʨ�vUP� ҷ+|J�i���b��(���aEPEW������α+6��;vU�c��'��>~��qk�}�� ���f�D�9�a��rKCXQ��3�wֿ"�������u�Y� ?y۲��(�=�QΩ���I��fܫ��r���l]:0rA�#,Y�6�S�k�]�u;o��䌟��ظPzW��{��|
�� /���R�L�Ӽ�m$�d�����bY�������E4��JR�擻
(��!EPEPEPEPEPEPXr� ������m����{�� ��X����h�A��O<�1)y$���($�� w���(�E��O��m��E*ѳ(����;2@$����J��|� �_�Пcf�(�QE QE QE �߸�|{y�e�����Ȼq���?E��u�'R�ν��//,�ِzlh̤�w@��O��o��MwO�M�h֢�*��(��(��(��(��(��_��XDyI.F����<�
�T����|s��]���xϦv�>�Ҳ����~f5�wk�W��QEjld�����v���_�yk�U���H���@�j��?����^}��؂-�����q�r5�V��Ҝ#�� ~�(��� ��( ��( ��( ��(��.�hǅ0L�������±Ǳ��f��	,d�6���1C�3�*�GF=�8#pk����Os­��݆cu��Ԋ���QϠ��4k$Rj:��8ʰ7s=E:j�j��G� Idag��o�O���Ee�rY%�퇟#Is"�s����$�!Ն{�ɭJ��mR�p}��(�@VO�?ykgl��5�����%Y[�d��Ꭴ
֬�C��!������n띈"ۏ?9� g�"g����TR�w�+���r����xb�ۍ]��-��(a��A�1�r:+ˏ�X�\�����m�3��f��������w��t��v?���c8��d����y҇�(�� =�p�Z�����3���*M(��(��(��(��(��(��(��(��(��(��(��(��(�
��q�3ZJ̫"�:2���`pA�@5�y%�7
�ynQFȼ�S�.y*�{��ɟ�%��WC�[�A?��?չ�7�'$�
�����?~��~�z�R5���� ��( ��(��������-��s�?��0�j�T�Q�������UFI;O�������� �QY-*?E�� �1��d���� �1|9�����+MNM����\6~�;��S�w+?Ѽw6�~ߦ'���g������<c=8���vb5��t����75AEV3u�B]?M&�Q��a�n	3p��9*��c��ǵO�i�iZlP��D�2HAy�;�Y�,ǹ$���o����k��Y雭������:��b0�:�ܭ�{�T�o����ؗs'[� H};O_�=�r�9(����="&{�:Փ�W��~�ch�+/M��V�$0'9֮h��:+{��<���,QEQ���o���W=~ǲ�o�����l��{g8=+Z�,Q��4k$R)WGV�=EgxrY&�֘���p-�Y�����w�sS���멾�=��SR�(�0
(��
(��
(��
(��
�����-<v�?�9�s��K'<8��Y7�^(���Ggܸ�G>\M��@�#ם�3�����T��]~��#Z���cI��N��.]���f)X�L����8�9�jQM��3�IC�fO��i�&�"��v���2�F"9���0���VO�'��m����}�� �Yz���9mԽ��i���������j+'�o�.�o��x߾�� �\�@��d�.�֦���N���/���
��^I�hZ��*�-��� q�%T��v�VO�?ykgl��5�����%Y[�d��Ꭴ
Sv�+*�R���u�V��h�404�-,���0Q��$�*���S�k�:M;2Z�*��[�.W�`@a�U��2��'z��W�]�j�gܭ�ߏ��$|��Y6��4gv
�	fc����jT��������e'&�&T�� I�+ʷ�'� p� �x��z�U,��U+-�ߵ�
'�$x��[�Zz����ƒ����]?��QEhlQE QE QE QE QE QE QE QE QE QE QE ��{�C�,� ���Ek]s�� ��?�+��Z�AEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEP\� ���9���
�� �V��W?���x��±� �� tQE QE QE QE QE a�&��X�呯��T�Lf��KF랙F�nV_���ߙ���L���ɖ_3�>.�wq�nV���?(�I~bAL�X�P�Ȩ��Ac��@�I {�}d��#��ry��q��#�Ƀ�6�q�'
N �<��f���*F�a��5 i徂��R�8�������=EW�~��q��}������?��ÞAqLpF�\���)c<Z��n������wR�`\n,���C���S��ً�CUӤ��ק���gأg��i1��yv��-�EP��s�Fv�rFj�V�%��9�n�aES$(�� (�� (�� (�� (�� (�� (�� (���uԹ�E���^��4��|�g��(��L;�A��V��ʣ��/Q7b��u�7Q�&��,5O+�el�����Ь�<�A�����U���H�4msR��K��GnZ�M$��]��hi6*�9��=~�s
4��S�5%mm���[�z���+�,(�� (�� (�� +'_��V�������>cy���G��:Փ���w���G���b�*��Y >��u�����Ɗ���z3Z�(�0
(��
(��
(��
(��
(��
�o��V�Q�Qc�����/�j�R�~t����nd�;|���~9������S�����Qv�(�M��� {�������/+��6I7�s��צ�1��j�Ѿk�rU�7�tm�D�����ЩEkT�c|F�K�_����QEFEPEPEPEP-W�=#� ��?�jU�ԼG
�z�؃��h�;e����'�5X� �%� �{I� ���a��kV���֗n����6o� �c�˜d�j��¿���g���m�_�v]~�/���7��;o���jɳ�׊5XS��k��1�2���Q�t�}Iεg��O�/�'�i7��QT`����|����yx��H۳��V��5�'K�L�ȓ�l��5�$� }K!�_��L�F�t��e���&Y��m*�!��_!}9�3��_�O��������O��;Ϳ>� iۏ�3�p/��D��k�v�s��>���xk�� �����^�7����qm׾|��۱�2t������w��¿���nQE&�EPEPEPEPEPEPEPEPEPEPEPEPEPU����,f���VE�t8d=C)����؀j�5}8�%�(�7�^؃p������`,���<�<2璬��^��� @�"��J1� Of�ܴy������5�b��5�������;���*�B�(��i.�o�Я�ޅ��8�ʜUڥ���{ ��\����C�����ZT��_��c=*��������� ��:� ܎�&yGQ��j��i�t�R~�F�a������t?�X�����d����KF�3�:��vOZP������V�Y��.���j����7���R��^]��Ecڴ��g��sm�Db �̡[ ��c�ȸ`���9sKe��}����i�iZlP��D�2HAy�;�Y�,ǹ$��tV_���-�`����E�2)�I%a6G pI�8�c9�yKsJP皂��7ÿ�Ӥ�<���\���d�&�o�,|px�ֵ2(����8�P��0�  :
}(�+�����:/�QE�²t/�i�u�5��?���OӶ<���s�p5�&��!���9cQ��6e���^��`s���������i� ���EUQ@Q@Q@Q@d��:��~~eyŴ/�1�0Wҙ�<�q��.��������Am$��v�����AU4+Y�4;8�Se�G�\������<q˳8�*^�H��)K�����/�Ѣ�*��(����,>m"�ˌ˕�^=�� 1�Q�	�j)4��*Jm�qkQ��m���.�Js�x9�Ĝ�N�����A�[}wF��X����6���D��D|� ���h$݂Uun�vs�9�MF�j�c�Ђ=A�.���.ƚ�Ż��4����p�F�(u,�c [̏yI^<�j� �S��S�Ԍ�)r4��G};7��������ZT6�/�*�y�۷͕ؼ��'n�flp8���3�b9V��� ���<Wj�������`�稌/�ylv�{�i:���Z����g�W[S��/�/�v�(�6
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��=� !��V?�"����� �sş��� H�h���( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( �����g��c� �+Z�+�����<Y� aX� �ր:
(��
(��
(��
(��
(��0�/�hWO�sEs`�u8I�?���q뒼rHܬ?��m����S�xx���돣΄����ޮ�����ݿ�	n²o� �a��i��m��\�Ꮢ���|�Ǖ�0աuu���w�#i$l�Td�z
��Z���v�/.�7�v1 ���UT��=I�ij�N�^�]O����_s��EUQ@Q@Q@Q@Q@Q@Q@Vn���c:��ms{z˽m�P3�噈D�컶�\���BSv�U�y�K{����mCPLo��P�FF�$$y�컰v��Pf꺧:��� ?��J˟��»rx�S�V�����j��6��[G���D\�����ּ���>g�m�}~_)Veb��6�y���jLv�z9��u*w�1���� ����$��$qơU  � ;T�TN���۷@�QE�(��(��(��(���q�:mՌ���/�8`q��Պ(j�2qjKtQ�o$�t->�eU���9� �	�{sW�'�_.����v��[D?�s<h��UQ���<ֵL♮"*5��o�
(���(�� (�� (�� (�� *��΍d��<�{�#$�rI$�ѫ�]�>����z���?@����}�� c�oE���EGZ��Nе�UZ[ki&@�*J�#8��hݕΈA�J+vW��Ϡ�p>�ܒ�F;��F�A�
�|�zֵW���Ӵ�[Y�+hR.r�( g������5:��ٶQEQ�QE QE QE QE W-�� �x�J����1����6m���L�=��S\����F�~c�MF�@�$y<�O�;m�z��*����2��0���� 8�w߹�&�pܤ��Y�:�`������u#Z�u� �Eax��-���C�7��� ���{�ۃ�Y�vwT֜���� �
(���+'ÿ>�-�����ĢN�bXF��<��O�B�V���6Vs��>� ����N�Q�p9�*��k5��4�K��<�G"���u/�F�҄����� O����>����?�*J��� �xV�����?���i�g��3nx�3��T�ơ�+jS��6�}��,`*�[2@��M��>]'�ZV�;#Mig4d�,���2=+JZR�~�_�u���K�����ET��Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@5;�:[a'�!��.��\�C#��
px8��2��GN����Hr�Żw�"���﵃�28�u��  ��[꟥�'��F��y>�R�w7���U�����֢�*��(��C��7j8S.@�X�?\*��J�T��5{9�h�����F?�Ҳ�����z~�5��f�?R�a�?�~�����~� ��������ݎq�d�5�Xz'�6��ط����p�>�H��n�9�s�#��S֔�����M^��cQ��Ҧ�X��WjC��l��#M�;w;*��3���Gӿ���m_:U��M�o�+�ynN�����p*����U�k���R'�F�_�[��Jܢ~�:�_��k�@Z��d�?�:�b��Ǜx���ڪ��*�=�c��5�'L� H�u{��Gg/�d�w�D�ʧ��c �Z�4�e�����kQEFEPY:��<C����7ϳ���K�>�F1��s��d���zP���c���u�4�L�;� ���	�3�no�֪�{��[�5���� ��( ��( ��( ��('ğ�ў�ro�K2�����W�c.��0kVM��O�4�~�m��W��<��d��R��j��lަ��W���~!EUQ@Q@a�S��T����S���I�̈���p��x�n�Z��.��WR�Q����x�@J�D,��>��Ť�6zl��	m!�����J�{�[�� -� 5�!u�~� ˰��3�!�� �B���*�R�� �_��xǗ����%�F��v���ן˧��̩{ש�oN���@��+S`��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( �����g��c� �+Z�+�����<Y� aX� �ր:
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
*����5�Z\���=��k,�*��;�x���y����gW�D�u+B���Y�-���_�\�[�
9Q"�}��e]�X�(�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(�?~�·�}~��P��� ��ͳ=�y{s�3�b�*���ūi7�l�������$d�J���"���:�R!&����N��a�������Ay7��oɂM��v-� �L5{m4s��˟|1�W�u/�x�F��;F��7��6^]�n. �b 	��ت���nz�Z5�冀f�P���}_�yX(�����( ��( ��( ��( ��( ��(��,���I剥XQ�;w���\�q�@���4k��[G���khnV��Ϋ*�M�eS�K0Uܻ����M:;�� *����"HQ��F$��°!A��VJ?�|%����4�F�Hؙ�y���1�Y@�%x=?�N�8w:���e�����؆�{-���k:�7�� eY���Z�k�_G��d
�2e�?L���hl��v�!��@ݏ.�,ēܚ�Eqδ��V������`��+!�Q@Q@Q@Q@Q@Q@Q@:/�e�oϖ���V���s����&��^(�aN#x-�Xz��H��1F1���':�0�����~H(��� ��( ��( ��( ��(������m�|ޛ3'��j�R���e���N1�p��v�����~�Li�9��/�?Յd����Z���{{iG���d���*�29�֬�S��և
s"O%�H�����,c~o@qs�lvᴪ��_�_�֢�*��(��(��(��(��(��mO�:���|�ֺ��=�͵h���fQ�w$�]Mr�&�L�8Sy�9�cz���F}��U��]ݾM4�+|P~�5|G�xkSX#g��4![�
J�;� F9����xRhdY"�C#�ʰ<��)��៓�ֶ�~Ǿ�w���1n�l��;g=k?��]N��z?����QT`d���������q��3�<�D��pO�x=+Z�u��ˤٷ�����GQ�θ� �D��ߑ�R�&o=(�z����g-����կY.좰���%̏'�w2�8^� WS\����r�����W��&?t,!n�?�r�� p9Mi0�O���WK�q�Cf���(�7
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��[-ޓqv��|��H̍�C�����s��U���ՌЀř~P��$����Q�2Gnǥ)+�iJ\���ї�x��]�Gl�CF����k/�]�q���ܯ<�|-?��S%���%��ڣI��c+&���C�H^=�d�-����:2�^��^aF�:������ �|���
(��8�j?'�'��\���1����b��-_�Ү%z��в�>�Q�ʷ�����c���e�T]��>-���(��rZ��q��Z���Em�
|�ݟ�goy^y⏇�k�"�����4��i�٤�����!�ljġ"*B���y~�[�j���?��ռ˝����ke��D$y]��,�Dbid��w(yB����8��+J�i���M�W,�K��G�'��pN8��[�:�期vR؆��+9��dF�H�'j��8�SB���F�K��u&�� ��F2:�v�'�9=j/~��7��3��g���Rz�ƒ(#��Ө֬w�����)3��h�7�QT`QE _P��Q�n�ffX�ax\�� �����QCW�q��R[���y&��i��*��6���PN3ۚ�Y>�Օř��.���/��<x����0w���"*5d���ӧ�QEQ�QE QE QE2Yc��i8�R��p�$�z
+��3�#Y��O I�l�u�5�O�K*��1���j���RE�Z4�w���6)$�du�� �@�9�J�|&���֗M>�?@��*��(��(�?�� �#O��k����]�Nnzwϑ��vy��u;k�b��7!K����`�s��#�������_x������if]�yo+"D����r@�8�3R�Fw�/n�R�8�R|�P�gw �^#nY����SZJ����_�����d�
j�N�����c���+S�
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��=� !��V?�"����� �sş��� H�h���( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(����5�Z\���=��k,�*��;�x���swڻ^�rh�E��m��B+f���Qˍ�%����$ ��n�5�g�ѵ�{���e�IJ_nU�Ȥ!N	�F�Xq�x�A���t�� I:�'ӑ�#<�@
�b��(�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(����*�Mѣ�����zvK{k�#�Bz�rDF~���N=���/4�,qF����TI$���6I.&ק��;iP#X�2F��*�ѳ+3���rN��(/&���FԽ�����忭���(��B�(��(��(��(��(��(���/'��}3L�d���]�[2�JA���`AUr��{���4�6J��U���#!@<4�@9
fW�gg��[[G�$�%�$�'�bI$�I$�I5�J����� E�w��p�����-���g �ĒrX��1$�I$�I$���+&�wc
(��Q@Q@Q@Q@Q@Q@Q@Q@2~����8�M��o�^T���cΓ�]��Փ�~�Z��N$y�c�B�� ������Nu�c�7����~�~AEUQ@Q@Q@Q@��������=B�,X{6?����KO���q�e�m���g?��v���߻��������VO�� � w�V]�|��߳��ls�Y:o��j�x����b�%�w@8�Mˢ;h�.�~m/ɳZ�(�0
(��
(��
(��
(��
(��
�q��Y��;���,"�>�����Z�k;R�)�,�$�9�KyRE�FŔ����^�?��g�Q�0�|�~hѬ��wZͲ�7��_M�G+s��9�q�L�����/B�y^Y����I$b��cRI'�I�O��^/����l"����t}z��88���u9_F��E�^W�W���j(����y��iP�1�7*=$Sa��d��z��jɷ�������D��F��3��%|�G��{V�Lz�z�rǲ_����K���]%���W�A1R�ťF�n灞��m;�#�V��ŧ]O/b�s<o}I˜gy�3�֟��~[��o��7��߫�B�(�7
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��!�������7F���r#�A �99��M�ΒY^>��M�,����@��`��	ۓ�Yz�RD��Z��]Y�~�F�,$���b��*��3-=�zO��O��O�����S"�9�I��d�E��*��#���5mS]Ddu�ea�G��Q@5r���&�hdb��HX���Aޭ�-7�K�ފ�MǷ�|����㚻Y������7�ߊ�(����ɓ�+�p�T��iY[��[j2���`O���kVN����	}v��:����=�,A��qש֩���o��Jʭ����(��� ��( ��(&��>$��ה�;{�O]�">�`C�'��kVM��Q�L�F�\�)���r� �b����Fu�c�]����.�~��p��*��(��(��|M��z�ۧ�6Y����-����|c#�kVN��� ���w���7u��mǿ��� ��s�3�lo�Ҫ�m~�CZ�(�0
(��
(��
(��0�O��o_�o��%-!�}�q)*1��4��z�*O� ����d2� �}����cǾ7c>Շ����PM��[u�?���/���y����<u5X����V�?����:��_�� �QEI�QE QE QE QE QE QE QE QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@z����͎�iu%�����K��EX�%؁�PX�	bH�X2ɠ�\\6�cu'�6�v-M�P�pb�UfQ�lJ�T��մ��6��j��uլR¯h!;�C`D�����zϷ��ݤ�sA��q$��M9������Ϳ*(���4�QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@S%�8!y��c�5,��
�rI'��������-��7��砅O�l�E�u���}b4�ck9�G �^�K��Ɫ�GaV4X�xe�ncd��a!I(�G�!ye����U�~O��?�X�B�xB�����p�{��2������|���������EVFAEPEPEPEPEPYW����>��I�T�ڮ�-�
� ��PC0 ���BU�t�=RMBE��c�BH�$�*��@ e�O��Ӡh�g�I̚yH2L� Y� g       ��<��W�k�-�,�వKkh�D��$�$��$��I$�I$�I&���ɶ��(�EPEPEPEPEPEPEPEPEPO�?wkgr�M���ޛ�X��t��㞠֬���MY�������K+Bb�j��3ykF/����(�0
(��
(��
(��
(����o�^Mm�8��8!I2��\�Bg%�>�4��L�_��|w�b����gڮ�!�-���lq�E� �T���J�� ����l��7w�:��eag���?��1W����6��ef�����
	8�~*�M;B��feim�����*�g���u�(?6�����QT`QE QE QE QE QE KS�a�Q��s����u�~5v�j�ir�fT>��2���k*�×��#�R~L�����n�Eisui
� v(��8���2y8�$�Sj_�״[���[2M�����}	��k�����&�7�:o�sӶ<���s�p&�ɦ�p>Sowo)����U6{,�c����5Ռ�$�����;0��Q�ޭ���QEdbd迼��nW�f�>[z�8��g��ងӚd��I�m�ƥ��8 d�g��}��.��1�$�4��W ��֬��ת>��裹b0 �$��d��I�v��^:n�_gE� n�/����.�����O�����2	de�������q�V��{y�ݧ0\jm�?���b���1$R/=v�pA;��Z*2Q]W��i�B
+�QE�aEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPL�)�>�x����߹Y��$S�qם� lZ֪��q�3ZJ̫"�:2���`pA�@5�y%ݳ%ʪ�۰��|�M��o���þg J�������� ?���/QEF+o��w�u��ϟL���<��ڮ�&�5�����>� }��-���*�eKK���:��4tR�f� P�:��~�yw�\GQ�CɌ"�rIl $�
�Y:��N��X�^ss2t�q�ϴ�'�n���;(E:��e��Z�����ǧi��0�4VФ(\�P �;�V(��+he)97'�
(��Q@Q@:� �_����l���l� �eb=���֪:՜����X�ʲ��I
8PYH�njm>�=GM���Yb��&@� Fqߚ������1}�_-�KQEQ�QE QE VN��� �?w��,����2�Ͽ�����Y>��=ks���6� s�s.����g�3�ҥ�I_֦���9w����?Z�(�0
(��
(��
����J�������I9�?손��g ������ �5
���5��<C��(�Es�B��zd�?{|6�c.����������j6�������s"�F��(?{����aT�g�lol{����*�q�ך]���eG^iw���QE��QE QE QE QE QE QE QE QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@s�� ��?�+��Z�A\� ���9���
�� �V��QE QE QE QE VN���yo�/1���=*xC��``�2,���j��ҭ��_����� H��)��z4���e��oG�N�m�^�v��Շ��^ho�������2aN:��,k�M�<F�a�������� Wc,7����3���X���2GZ��kV1�ߧ�s�cr�(�QE QE QE QE ��j����j.� ��G�&8�!T\� 3)�j����j.� ��G�&8�!T\� 3,�~��G<�H�d��A�g �@8 `      m�.i|����1��ŧ@���$�7�4�d�� � �       袲����(�EPEPEPEPEPEPEPEPEPEP%�9�xf�d�E*��*��A����$��&�F�Y,�gw9f%$��֥d�o�!�p�]�E�DY�UQ�� � /�_�cx� ^��EUQ@Q@Q@R��h`�r�\Ŵz�p�� V?�]�W_��l"y�9�)C��E��e[�}>�c� �k����]��+Sc'��?����}�e���y�"ݎ�ߜw�2:ֵd�_��ѭ��f�b��IW�g�ᎄ�֩_f�Ҕ#�� %�0��*��(��(��(��(��(����n�f�rBJ��W�c���M&��$���o�3�j����v-u/��y��y=
󜁭�Yɨ�Z��,�-ʹ��s����v�4�x��&�W�: ���a����v:o^��֕=�ž������T��*uѕ����6��e����0�~jj�M;B�/�Uim�����*���_�_.����v��[D?�s<h��UQ���<��_�EhO������ގI�7_l�0��g�k�g#�S��{>���\���ǧi��0�4VФ(\�P �;�Qj����|�� �R��^�y���ܛ-!�杰XH�|�98���T�p��O�C��I�������+�G����/1]��]��ފY�6��:�Fp@<V�exb�};�z5��~]͵�ʙk�j���Jծ�KN���� 3E�QE�(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��u��8�ӥ�{.��h7�q�1��:�]@��kQI��Js��]|�QY6�*���~-_/d��fhGa�����屭j�
��vZ������+� �k	O	��}7# � ǙG�Wj�����J~�,���T`��S��Y�J�^���XiVK����VM��O�5+��mVj��<�+�d�R���j����t8/��e�o�H�>eR{�R�� �[�#����/E���[�kQEFEPEPEPY>�tU������!����E�ª��N9浫'M�ν�[�/,�^:lh�@}w@��G=@��'�Z�֔�������j(���(�� (�� ��7���n�l�g���s��c�7�񌎵�Y:��F��Y@�K���$k�r$�&�g9:�+�lަ��W���QT`QE QE ���>;�`����J��}>�1����cr�{g��%�,S�������â%�>��A�B��5���*�E�� ���I�E˶�q��|�q��3O�ԫ�}����AWi��*"�E *���S���yb�:P䂋�QEYaEPEPEPEPEPEPEPEPEPEPEPEPEP\� ���9���
�� �V��W?���x��±� �� tQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE ��{�C�,� ���Ek]s�� ��?�+��Z�AEPEPEPEPN����h�� /ۼ� kuǙ� }nX� �3p����'D� L���� ���G����� ��4��G����Z�1���o[ݵ.������_�Vo�t�uoj�l�5ݜ�F�3�PN8���*+HM�JKt`T���m&�R�]a��'�d 0WP�3��V��� �𭕧_�o��� �����m�^�s��'�ʺ�P�(��bZ���+!�Q@Q@y���y��}��7;�f���p����_4�T��+�r��~�\V��}/W���L�9}M\C�Ól]'���f1"0�?���0/y�#um=]� ��z+쎟I����^3��M,�VY]���l 6�m (
z�+�Rrm�(� ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ����"^#K�E�N����}K�ֵd���+��xF�����-2���D�P;
�n�����
���r5���� ��( ��( ��( �C�����X[.�w��?��q�5v�Y|׺�XN�	�F��Y�=ϭeSx�?ѳ��y���h�EV��L�?����U�Ǚ�vo�����Ǯ��5�Y6?��&�p�$Q�ِz�Pғ��:�<t'Z�=Y�}9c�/�_�(��� ��( ��( ��( ��( ��( ��(��������k�����C�����k��?w��.��1>}3er�� �yϿ�u5_����T�0� ]�_s2t��kZ�/ď<w*=chR0���Ru�}ɫ��R�m�����>�D�B�E��sيw��<_���}6y~���퍝��'�|_m���XK�� ���Ϯ|�:t��23�Ky���z���O�/�~zz���x����ny2i�b^fQj=X�J�ܰ�8���o����̩ua�����<�wڐ�V'�����Mz?����b5��v�$u4QEI�QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QESR��}�F�y7���.L2`��w�y$	i��o�Y?&�p�ɆLP�������M� �J�?��W�^��rUVc�lx�9$�j*^��ޟ��{������Y�s�Z�n䄕�P��	��N��p�;�	 �V*��őQ�Ry�G`�F `  {T=*�G��������� 6V�'�tg���̨��$`��X˷|m$�ZՓu���Kq�X�ג�W`�F=�S9�GC�V�m��=�p���� �$�aEUQ@Q@Q@d����W"]�I�z;��ѯ�+�p:��v�Y:���o�]��o���\��R}�����s|>����4�m�5���� ��( ��(&��'Ě��T��+5�_k���B�NG ֵd�o��2^�M�x��1h�{�e�6� �j�ms|N�{i�i� (��� ��( ��(��4���<�kh�i_�E�������Y�k��M��m؉&�BA-"������$�$�rjo��@�O�JX�
��1��e(?���	 �j�_��瓪�ħ���3�~<����V҄c������L¶��v����ET��Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@s�� ��?�+��Z�A\� ���9���
�� �V��QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QU����9//�വ��A.H,x$ƣ�V�m���O�ZE��Wo2�E[N�q����r(�s�� ��?�+��Zֆ����ߛ������8�>�p���q���8=}g�{�C�,� ���Ek@Q@Q@Q@d��F��������\y���参�7�kZ�tO��?Yo�~��{[�|���sI�y�O�2���oGݽ_��קݿ��Mj(���(�� ����f����596����p��<��NIܬ;���Z�������u�����~)C>\�$��[��~�?�I�-��(�QE QE a���?��_��n�O��c�� ��$\���>����?Ӯ�-l���J�Z?��C
x�Hf�Xgrȧ= ܭ�{���W�ݷ��](�QE QE QE QE QE QE QE QE QE QE QE QE �7���-�/�aq�c��$�n}q��鹽MkVN��j���oa�(`���"z�=�L���3|?��%�3Z�(�0
(��
(��
(��
���`$vY$����)�A����%�����Lm&��8�h���%�����Lk�c8�+'�U�;�1z�V��տ&OEV��N��د��e�����<�����r{p5�'���SIs��i�7wvP��Ԗ$�ܒkZ�
7�� K���h��(�0
(��
(��
(��
(��
(��
(��9o~���e� Z�V[p���������9k��[�?�ѥ��֐Iq�e���� d�|s��:���?��iK���0����'��� 6d�� �Դ[���h����'Ѥw`��E��I��/	v�d��4��6΃�	<K�h3\�i$W��!�e`=ʡ���h�����\_ϼ��o!q� �I�'��o��z��۝� ��R� 3Z�mK��0�
s$W�2H�-'&C軮s����WS\��� ���趷{��fZ�w�Z@�_-��V����%� ��~m}�[�����E&�EPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPN�� �:#���}�t�Fy&1�O^
19|6%��p�"J�%A�ч����l件W�e[�v3Z���I��n�R���`����m��IL"�W�<�w`����pq��H9�y�W������EՔ*Ǭ��Zi}����?�U楩|�ʹY�p��H����`z�}�QѬ���[;I�Z�8TL�rLe�'�Kd�y$����
�SLD��>]�^�E�QTbQE QE QE ��8��÷�Dm%�P�����猁܇U8���(��Ջ�?g55��dR�<)42,�H����XAu�������[n�c�g���K��c�vg�����D]�c�J����
(���V_���-�`����E�2)�I%a6G pI�8�jVN�����V�W��̝3C!��)�����g���S{-~KW�qE�0Ʊ��DA�P8 �S袨���(��(��(�=S�'�Z���~Ѩo띈!ُ�����9�Ϟ�Y�-4�>�̡���p{��Y;�G�_D���Hw����G_�^�8��(�+��m���n����R �5X��B��+U�6�>*ދ��~L��(�7
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��=� !��V?�"����� �sş��� H�h���( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(���k�^�4�i��N�G-3�+�b9Dj*ʙPXy�6�#���ԼDuX�ƏO���qܘuv F-��H�YYKNRm�����{MOIMKʕ.g���"�m�da��~eeel�2�r�� �����?�?�|ߴ��p�G�1�;v1�.1�gɷg�@��^6�w�jF	/�b���m�9c���fb�N��N~b���귖>$�TV�Ե:�le���T��|�̕x�Lr9뎓L�SM�e{��/&����o� \�_�UUW'
��8��N���9���
�� �V� �C�Й���/�H��$i���t�HT�>��H���#К�� (�� (�� �ok:�^æEmt������Y_'c���A��P�7�#c�~��lW����˷�?0��[�%rz��=�P��<s�K�xe��6�u;	^{c�T�E�0Ʊ��DA�P8 �VP��'&�;�8�uhS�$��� �z�o��+S�(�� ø�ߎ��/��]y��~�m�n���I�����kr����]�ԉ����B�8&3m3'���F�L��·+z�=?V%�(���aEPX�!�Y �I����X�$��xa2��H^�@���[5��� ����]nb���=
y��G�nS�E���k�}?>����A��I1(H�B�( � v�(��n���Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@d���A�� ������ZՓ��5���� E�L�o��<=W�kQEFEPEPEP7�}Bkd[o��$w��a�|��H]�#�<w?�kO�I��IU�7;�fr6�6��9�S^���:�����W܇=y8lq��΂"�0�N �$����ӛ��h�_��=*����{�Z//��l:�|Q�x_R������ޒA��e�FO<�ZՓ����2˧�o��� ��f~�����ݞq��?�����}��KW��QEQ�QE QE QE QE QE QE QE��;�WD����,���M����� ����G ��Q���t�O��~�mǗ�v����3���˧I)���NޥQ��{�N*��~O��Ķ��i2� vX��"��цG�j��Mvi��� ���ZIuI�����<?<Q[]L���� 1��͇y-��1��[?����އ���4�ZGFR��f$����̟0i\y���96x՚6܅�J���pH�QZXZ�CV�,k)��yi��I䁓��������=h��o����?/[�xw��)֦O�/�ו���I���6ǣ��Eu5�x'�k��������)�� �P<�� >��:�(���(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�<i6�i�f���8e���Q �����Y��a�6� �$�ǫ�w��y���X���/ɰǸ��J�����K�F�v5G9�I[g�u�م���0��9v����8�yd�O^8�J�L�(��!�5�(�*"*�� ��Z-�94��
(��!EPEPEPEPN����^��W~lQ��$ErޤL�����֬��/���l>�<�?]�h�����Z�X������Y�)�QT`�o���ɾ�vp%��$s�J��_���=9�ZՓ���i�_�Z�y.C� ~2qc��>8<s�je�Hޗ�Nr�_���֢�*��(��(��*���� c�:���y�c����ݷ~�-���8늨��J1݁��� �ى��}KU���_�in���V�0��V��kDӿ���L2��ٚl6� ]���FN?�.x��[t�%:ӜvoOE�0������i��(��F�EPEPEPEPEPEPEPEPEPEPEPEPEPYz��ycp�[�R��c-������̕x�Lr9�J(�� ��T� �3\� ��_��U�!q-ޡ�沞�F�S0NP�b��rQ�y��=}x���� �sş��� H�h���( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( �����g��c� �+Z�+�����<Y� aX� �ր:
(��
(��
(��
(��
(��
(��0�[Ƅ�#���i�H��&w'���Ğ ���Y^'��Q�ck�ssc<1&@�� 2x�֯Y^A��[�Z��[\ĳD�#r0Gu�޴#����?�]I袊�aEPW�/'��Kk)<�B�Qkj��v��<��&Ӎ�6�"�YY��X[�����[D�ę'j( �O u��7�&���y��~�g�{�<Ȋ�#�#p8zܭ���T�o׷�}��](�QE QE QE QE QE QE QE QE QE QE QE QE QE QE d�[�E�� � �kZՓ���M� _���S-kT��F������aEUQ@R�� L��k�+��a�G� z���S]\}�ʾd��<����G��'�E��٠
��J~id���X� �a�8������ ���1����N�����Q=QZ�d�~��T/�i�ʏI�o��Y:|ޠcZ�m� }�����+u�4��=	_$�鰞�2��i�.��:~��QT`QE QE QE QE QE QE QE Uԡ{�*��t�@�8�*@��L������*FC�I"dFQцz���W-����_G�FK;ea�EU?�$��o����J��~_��SET�����_� ״��	�� �=�����+����?u�v*dPA� �G�P�_���� �e���Z�|3� ����w�6nQE&�EPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPN���KE� [��I:a) R}A�v	��Y>&�<=us��{/6� �q.����g�s�ҵ�W�ѽMiB^����
(�����n����$�}�m�lp��s�.�9�x�v���Y�in� �5�5�;UF �砬�c�׺E��In��cvF��@�y� ���R��f��iF=�� E�Y��EUQ@Q@a�����vķz���~�����@�{��d���5k��%�;|贫��;v�߻��P]rR3���%	{G�Sr����[{�w����y�/��l����8�����,m��o�cX�c��qS�-(���}z��\i�-����B�(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(����xGM�?�,b��t2@L.G�%�$g�+r��1��uK�[MN�yt������'@}��0N�֌���U��u7(���aY^"���F�,d٨��Y��¶r�˷
�pqZ��m� Mx~k=3u��uy���Pv�F`�gS[PK��[G_�_7�	��Vpi��6��v��,1&Iڊ '��jz(��m݌(��@QE QE QE QE QE QE QE QE QE QE QE QE QE QE d���� ����
֬�~O���|�/<o�$۱��M��+Z�}����zG�AEUU+���V�r�)k�v��=�>��A3�*�'����d� lna\����O��������EB<�!Un�B�(�,+'C�캵�� ���}��Z���LG��ՓᏟ�v7�����`�+������F�Ҍ��_-_�j(���(�� (�� (�� (�� (�� (�� (�� +��� �|c�a��(&�O1�;�b�1�\*��p:��� G��2��זr���u���#��q��'9�1T�&��5�{��i�]*B^��W���E&�-���>����ϸ��!�A����k���"�b�S��>���1� �3�oz�j����� ����)�Ro�~�XQE&�EPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEP%�9�xf�d�E*��*��A���K$����i.b�Cp�w7��$���:��~�5�Y:����_���n'��v�}��8�$oh�vi��O�h֢�*��� ҼW3�KE�Yzo��:��X� q��9�Ƶd����w��ܒ�>�"�*6_P�ƍ�s�����a����4�'��|��7
(���(�� (�� +���/�zč�����1�|��[�����Q��p9'��[�?�:~�s�3�&�c=��iUG�P]wq�� T��?�Z?{��"�1�C����\�h���p��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( �����g��c� �+Z�+�����<Y� aX� �ր:
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��=� !��V?�"����� �sş��� H�h���( ��( ��( ��( ��( ��( �=/��Z����Ϩo��aُo�n��x�;��� ��;�g?o���o�ʛ6�~�&s���`�z:�Ǻ���7(���fn��K��ڪ=�� ���!�n�%W�lrX����>-+M��wH�I/#ugr �1%��$���� !oz�����ԉ���V��R�+z��;�� O�_���
(��QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE�u�x�Lv��� ǀ\�,�U��I�kZ�u��� C���/��=t����2��,SZ�1�� ]���?��QEQ���m	��<�
�Y� �$x�Y���ZRħ|�z�{  Þrj(��/��X �y��!�����A���}��ӧ�� ����=�s������QEjlQE gk�SYxsT��}��i,���ʄ���QV�ma�����6Akk�v����AY� �孝��4���Z��en}�7?�:�+Z�|L�ZQ����o��0��*��(��(��(��(��(��(��(��mO���t?�\��n�2������r9u5�x����_�E��#��L�B؝҆#���g5O�O�|�q�L+�.�~:~�SET�������y�.��F���fx����B�G#���i�}�:������̯���NF}$a�H����]������䑅y�ݿ�O�QRnQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE ��?����wi����2��P� ��09�Ƶd�_�״[���[2M�����}	�.�ކ�Q���j���I�跗p*��p��d<��.$�� rI��Փ�~�]&Ϳ��_��:�-^u��
%؞��n�Aa�Qsm��Z�����ǧi��0�4VФ(\�P �;�V(��+he)97'�
(��Q@Q@�,�X<)��i^+��k{V����'����%�Fx9$����Ip`�"�I���Qq��@�@s�j���F�J�����sq�e�+pf��|ąI������m6�L��U��5�9�/��'�'$��U~q����#	{Ւ�w��~o�E�(���(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� +Y�ψ|9q�s5���M�2�/G=~\g��X~*�4�9��c��8�˺�4l�Gu>���Mo�֪�{��[��ܪ:ƣ���Mv�yҮԆ�|�]�F��v�vU��g'�W���g���m�D%r̡����g�9ȸR@4b��-����oO+�{Gӿ���m_:U��M�o�+�ynN�����p*�TJNrr��aET�QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE��N4�s�Ewk,��g����
	'�ֵe��)'�1���e2� �1(@ ��b�9�I��d�E��*��#��_�������G�K�iH��������$ ��đ���;H�L�Dd����$��ܓ��Ac�Fi�3|�d��Ϣ��:�z�S?y�/����8�>g��]�?�� �b�"Ɗ��Q@
�0 ��(���QE QE��~�^�mׇ�Io	=6,f">��C����VJ�� ��n��v	�㿝#n���L}[�֩�Vo[E�_�o�aEUQ@Q@Q@Q@Q@Q@Q@r�<�4�>Ḋ�R�����M�H��Dc��� '��k���� $�^� �V�b���"�4�晆'�R}������(�79m'���G���mm<��x�~�M�\y�]��������>)��iWxU�$F#�����;c�]�s]MT�������ZJ]���p��*M(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��|K��1� Wo=�̧���2H��V8�q�kU}B�=GM����b���r�3ߚ�+ţZP�=�E��� _�� �����ϓ���|��lh��j:�}2��sm�aAe�=������z}��_���V ��>N�m��)�kF��ou����_�ƵQVsQ@Q@Q@������,V"���neˌ� Դ��0@�S\�����-J� ��n�	���"�T� ��.� H��H�����V�$��w� �7�R��)����-���*M(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(���gǂ5�Cc4Ѱ�"G�+ ��A ���Q�<V�iR4,�� Y�U�$�r@�(ϒ�d��О������{2��K�`��* $e���;�z�C�����.��v3�H���7,����.y�;W#��5��BLG����pH
3]D^QV��6]� �v��
�M��E�}~Z/���	;�QErQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE ��n<)���ⴊ)�:�VS�C�A�,���4�Q�gw8U�I=s�U��g>���7�jw���v�c#�>a岰�A��g#)�E�� ����r�U�� ��͑��xI��݊���A����?�� �5v�)opĻc�B(�p ���B-+���N*���QEY�QE QE��~�R֮��b�N�D�0TE��1ً��kVO��mf��'���� z9&y������y�j�|)�����O�O�(��� ��( ��( ��( ��( ��( ��( ��( �Z���/� ��O���*gh��"�9���~� ������ �K[���yn<l�J�8��@�Đ�q"��  ; njS=��y4M�H�wS���$V�����5ݑ
�ت��� ����1p� 4��]��ZF��ǩ;UFOe�u5�����#"��g̜��%c����x�����\jJ/���:p���QY��Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@����>������i��L��f�@?�Ƞ���Q��Ҭ�4�+3��,ÿn7mP3���rZ��c�$�>����j1l����= d�H$]�8��+*m7�M?���1��"�Vs��t_�7�`��+S�
(��
(��
�yⶂI�"�%/$�0UE$�x �%`���xZ�	X$7�Ce3���O*D�@B�O ��G�()Ԍ_V�ݕ���U�c��'K��}�T�YD��v>t�dv$�  t�SKFM6"��@e�H��rY�;`�*�d��y������ S,:�%�ߨQE͂�(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(���Ees�Moj�	�1���Uo����볜����V��sp���S�>xCS����w׳}���I�h�_=�F�|��e;�%R����Y=�+l^*x����b�TU�QE�0��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(��J�3$�9v�Z�G�9����ONx�Z���wY�ԯ�n�L���{c�"62�� ���,��`���E���%6���|d��T욖�+��Ed� ���?�G� ڿ��?��U�s�p��j�����mfQE30��( �����f�{���e�I�n���N3�8�u��_�EhO������ގI�7_l�0��g�jd�ѭ�Ռe�k�-�V?ٚE����}��߷���l��E4�������-�QE�
(��
(��
(��
(��
(��
(��
(��
(��9�LżAh-���,PF�"��B8�w<���7�%���^���Ej�C�G<s�=h�.�_��L��"���p<ƕ
��uW\�nOIdtH�^[mB����2$�T��1�G4h�c(�J��K��u8��ǲk��lYY��X[�����[D�ę'j( �O u�袛m���(��@QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE y���5�<W����G+�+�
4`ByAۆP��ܦYO������"+3M��dp1� �99�T�Vh^)MͲ��س� ��Cߡ�Wfܖ�����ϣ��Z0RJ�V�ko���TS!�'�IeO�=#��J}j�j��M5tQE1�Q@r�,� K��5?�<��6{�#08_���U�±=0z���&?��M���a�I�*��ۜ�8#�����k� �SN��{�}��SET��Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@s�� ��?�+��Z�A\� ���9���
�� �V��QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@d� ���?�G� ڿ��?��QI��B|�=S�]������ ����� ����֡;��˪�=������(��faY:�ϫ�H��ݼ�G$ �U-��3��w�Y-�� Ƿ���?���t��O!��^��g��I7�?�5��Z�(�0
(��
(��
(��
(��
(��
(��
(��
(��
(��0!���ǗV����='iʕ���������{T���./�;ߙ��0�ď 9�A�1�>�(�[��HQ.崞�"ς�tnT��)>���j֗�,Ѽ��se�QĮ2r,��n$=1�>�)�-��{�y%oK3���_�����EU�EPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPY�x�76˖?�b���z0~��Y�'�IeO�=#��J}U��Ssl�c��,�H=G����{ͧu����W�M8>h��~��_5����C2O�6ʟlzGb����WF�����(�0�[�� �W�"���S�A�pLJG�QS#�A=oZ���GS���Iq��ۿb��pq�u�R�v�����"���|�d���Fzd��= ���_�f֬#����M�(���(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +&��$~F�'���X�&� �]�'��~��/�L�(��5�)�������]Q�'k�[1�V\Rɥ̖�R4��0K{�9d'���=����O͂��'qN��Y:o��j�x����b�%�w@8�N�d�����-��_����V�x�H�~�I��HҞ��/E���f�QT`QE QE QE QE QE QE QE QE QE e�vpO�h�2G�k{��$m���ߏZ�i�I�&ԋ?�� Um+$��<g9�q�s�#���,�Ĭ��j���Yf\�J4�>(�E��A�Ν�����,q�q��V�=�㚕��k��ߏ��3��Z�޿�J��lQE��QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE T��i.bR��CMl���� W�s�YGY]20YNA��U��hT�,KK��{�����r?�U��^��m�覣������ �QN������Yt�4� �=����[�褖;ò�
#���w�R��|_�ۉ��H�����g�R�GYFr3�\q�C�O�� �[�f֬��_�(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�\/w�izD��o���;�$4/$���)2�+�Hc�u�8ui�n��$�c]m$�$ŧ���d�A����[��3��� �sş��� H�hы���R��y泷�������H�W�Y|�%�r�I�gn�\�?ĺ��O[�ޛc;jq��ut�1_����d�c@��� �w�� ��?��� �V��uoż���9#`��FApA�$��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(���3F�E"�tq�`x ��VtRɥ̖�R4��0K{�9d'���=����O͂���b�x^�Y"�J�8ʰ<A�)5�Bv�e�˫�l�绸}�AI#`���$�s�UM�k/iv�	�x-"�E�;YP28�+��^�k��7�%Ʃ�B��H���rH��b��2�| �6��N��b.�����u�!��x݂	 �	���s�ԧn�뫆�K�ovOE��� ��QE��QE QE QE QE QE QE QE QE QE e����~�a�y�,�єnOb���%��3�����` @��
0@y��<�.�uk��w*$S+#�@������28�g ����墬rF\�+��n�ĳ��B�l��GV+��M�� ῭NR�޶����4QEu��EPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPEPGF�v�-�ģ$���~������"�0d`
���=E:�n�`��y�	g���^Tu�9�ld�%����RTW3�!�qy� ԯ�h�+T��(a�2c=3��X���ŕ�M#�q"��wr�J�,OS��'~�#V��v���hN�j�U#�����
(��:B�(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��+/R�.��\-����3�X&+�2q�F}� jW?���x��±� ����?���� 0� �U_��z�����.�d�Sd�H$Fŝ�8a��~�QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE��:�Q��oc}I��AG�p2%�1VR��������pxRk[H��Ť:�W�P��<��0��0�`n!�w��|�1���(�}'O�]F�WԄ�]E���C$qG���ʥ��\��F
�|������9���
�� �V��W?���x��±� �� tQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE d�Z��#M�#�'X�P�o��e2s�N:�mX>�n���� 4����=N9'�rL�T���i*Ӕ6�]�(�3
(��
(��
(��
(��
(��
(��
(��
(��
(��"��A��y� ����{w�x�0���Z�9*��F0���`;g��y�I6��qr綦n��KjQEY�QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE TS��(#��/9dzon�Z)4��JJ̩�X.�k䪢����힤g�$ۢ�P�a쉄#N*VH(���(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��� �sş��� H�k������g��c� �+Z �(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� +�����<Y� aX� �ֺ
��=� !��V?�"����(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��˃R�_j[,~E����0qiu`y�1��mJ (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (���]JkGC��c)z��!E���y�5�~�R�(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(�����)��:+�6w���!�#l��ǂ9��|u�Z� QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QY~Ԧ�|+�j��{eċ!C: d���SZ� QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE W?���x��±� ��t��{�C�,� ���Ek@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@z��5�Ž���wZ��<�E,�(�hT;��b .� �Iq�72��x�h��0�G&��Z{�5��>�;dK ��܅��K*��c�a��f���?��D�d��L`1N�Jf4J��e�\�������g�I�4���;k���73��<����l��<�&_1���+3- u�d�ľo�����1�����7u�wD���9��s�=� !��V?�"�����������7�C�_���� h̞w��6���ߓ~� ��*ľ��N����Z��ۥY���d�#H�<�Y�������� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z ,� ��?�
�� ѷu�W.���Q��|]����˳����>82?��*��#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� �����?����d� �+�?��?�s�?�͗� #�{�]��i4�.�KIL�.�lr�<[��}}q@E�� �=���ߛ/�G���S��=s����= tW?� ��� C��� ~l����G�O�����e� ���Q\� �#ڧ�z����� �z?��?�s�?�͗� #�AEs� ��j��9����� ��� �{T� ��\� �6_��@�� �=���ߛ/�G���S��=s����= tW?� ��� C��� ~l����G�O�����e� ���Q\� �#ڧ�z����� �z?��?�s�?�͗� #�AEs� ��j��9����� ��� �{T� ��\� �6_��@�� �=���ߛ/�G���S��=s����= tW?� ��� C��� ~l����G�O�����e� ���Q\� �#ڧ�z����� �z?��?�s�?�͗� #�AEs� ��j��9����� ��� �{T� ��\� �6_��@�� �=���ߛ/�G���S��=s����= tW?� ��� C��� ~l����G�O�����e� ���Q\� �#ڧ�z����� �z?��?�s�?�͗� #�AEs� ��j��9����� ��� �{T� ��\� �6_��@�� �=���ߛ/�G���S��=s����= tW?� ��� C��� ~l����G�O�����e� �� x��@v������+����ޡ�u��rH�X�˳x�dCſfU>�犱� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� �� �'����*�� E-t��xB�Lӭ�,�]�GkkC
yvgj(FM�N j��#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ������ �G�O�����e� ���=���ߛ/�G����� ��?�s�?�͗� #�� ��� C��� ~l���:
+�� �{T� ��\� �6_��G�#ڧ�z����� �z �(���S��=s����=��j��9����� ��������g��c� �+Z?��?�s�?�͗� #��E0�f�������Ot#[�H�4U ,k�ր5(�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� ��.�6��]_T�X�{+)�#Y*Y�dz��{g�����/��������-(r �Q"�;cm�c�L;�&��[M�Yѯ����`����F���]J�2������cP��'�E�q钴�5����CŹ��yK�G;s�_�
w�tQE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE QE y߇��� ��>�sypn[�RE�I$��p�r �04T)�!�J��ҵ�@��j2�I���Cq��r..R,��F"ʻ�1���l�( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(���>� ����F�;$�['nF�cr�p���F������v�ܖl�kH�&�x۹Q�Q�h �F��+���S7���f��9Χ6������-������#?08j ,�:'��4�k��l�,�.Yn�d�d�'�AW�� �c�$��H
Kn�+��R��c��x�-�����{I�e�#F���I�H��[p�@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@G9�m�kx�p�ƒ9Ef� ����8�5%��Wڦ�e��$�Y���g�]�apI�D�;T���<�Y�&��l���Ӡ�#���"�jz���?xDkq,a����8�\�b���qos�	ϕ��v&�Hp^!�hb�J��æ{����j���ȱ�=2V������cx�2��)v��`.r���N���(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(��(���KN�L��/��}s�\j�c72�������7�j�����坉��W'��x�5��Դ�Y$s��)�5�`vF���İ.�d���ysr��<[�˵�4׿6�%í�Ki%EF��G�3�v�˄��W'u��I�5�m��=�Mt򰸅gvyQc��]��e�r����( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(��&��Z����:������m���lmQS/!��e|�~`@@\�/�~��K��z��RKw�~-�7R��Ky���� 3�.N0lk�$�������*�d��ϨI	y�>d� pv�l\���lк� ��?"��;�x�'��壎7�^9�"ǩR9H�AcD6GG����5���&�Y�w+�Ip���[�#i����`�3y�4��K ]���F���Ns�$��I<�
 (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� (�� �y���Y�HR4.�8�؀3¨%����Ϗ�p���gDWxƉ{�U�
H�*��O�t���� �C��a� �n����;��@���umI�$���̆Q��8����y8Uy�Xd�k�c���=���E�i��xsQ��n�y.������8��1���ۃ�*  �J+���Դ�g�7G�n�档�J&��i��H�,Rml�`\��Uy��U;;�}��*�4���tX�7�E�t�,�0��.T2n �qEy������t)>ɪ��{w%��jz��$���K��jZ5;��J�
y��ޞu�M׮��!w��$�)�ǝ���0���M�|�.Ib�$7��?g6�y��Df�h��l�/;����G<���,W��#co�xn.�^��X幒E2b�1
 fF�8&�Y�ܧ�4?����I��Op�nR�H��a'b|��6Qw3e��w�N� U���/�c���[}���0L��8�S�9 ��J:��5)/���0�nbHS0��v0��`�20@T��}N�Ö����Z��̛�܍ctdr2	�@�������&�.�0�6���4�q��F<�z�W5����4�FO3Q��]�@�	�{K��`n!a�ãaB�$�o�$��Éd����~��!&l��9������@�W��������%ܦ�w�F��:|�ǟO\W��w/���>���δ�E3_92Fl䘪��P�4b� ��������5�M�_-���L��Y�X��Ҳ��K��8ݝ��+���Q\���xpj�^�%��q&���d�u�����V�ry���rJ �?S����^���Y�-΅}t�Sԣ�u�5P�	�9ݓ�6�m��Eq���yk�E��sU�>k��m��nd�o/d������1�*�Bq������h���Ȉ�DE��6Gdhنwe)�-�nw6wJ(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
(��
��6��3_�x��(����Q����b� �:&�tɴ�o��,�;(vH�K�g*��`AN�3��Z�A��y临��Y�o&�F�M����[o��Ib0Y��Q@녞��9�[�k�>e1JPȅO-:���4�|/�����{�>&���w-���� 󙰭���3�lQ@v���o���ws�ڹ{y�笠h�FF�dv�
��)9*�X���e��u��O��T3�b��;�"-��l��'{��͝J(I𾓢}��0οe���λ�o*7�򋽎�Q�t�9�i����c�A>��P�!�7l�t���s��ʠ���6u(�� �M;�?b�?�?��۳{������|����\X���i4���S4q�FO~Waϯ�*�N�J��������K�w���苡F��*y8�g�xCC������O>��Z︕��(�V%f+b6(���7(��tM:��K���l:V߱.�>V"h�9��Ga�z�5^���6��5՟��v/K	]H�D!Y�6�@ # 0����k�i�zt�	o���n�@�v���6�1�,��1?(����}&=:�š�xo�0\��ܳI$d��v.l p1$�آ�3�=�W��βE��[\�o"�����[i��*����66�u�v���p�H�I%���I,I$�I$Պ( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��( ��(��                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             