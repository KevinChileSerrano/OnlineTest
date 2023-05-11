package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class OrdersProcessor {
	
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter item's data file name: ");
		String priceFilename = sc.next();

		TreeMap<String, Double> prices = new TreeMap<String, Double>();
		prices = setPrices(prices, priceFilename);

		System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
		String multiThreadOption = sc.next();
		boolean multiThread = false;

		if (multiThreadOption.equalsIgnoreCase("y")) {
			multiThread = true;
		}

		System.out.println("Enter number of orders to process: ");
		int numberOfOrders = sc.nextInt();

		System.out.println("Enter order's base filename: ");
		String baseFilename = sc.next();

		System.out.println("Enter result's filename: ");
		String resultsFilename = sc.next();

		long startTime = System.currentTimeMillis();
		sc.close();

		TreeMap<Integer, clientOrder> clients = new TreeMap<Integer, clientOrder>();

		if (multiThread) {
			multithreaded(baseFilename, priceFilename, numberOfOrders, prices, clients);
		} else {
			singlethread(baseFilename, numberOfOrders, prices, clients);
		}
		writeToFile(clients, prices, resultsFilename);

		long endTime = System.currentTimeMillis();

		
		System.out.println("Processing time (msec): " + (endTime - startTime));
		System.out.println("Results can be found in the file: " + resultsFilename);
	
	}
	
	private static void multithreaded(String baseFilename, String priceFilename, int customerNumber, TreeMap<String, Double> productPrices, TreeMap<Integer, clientOrder> clients) {
		Thread[] threads = new Thread[customerNumber];

		
			for (int i = 1; i <= customerNumber; i++) {
				try {
					Scanner tempScanner = new Scanner(new File(baseFilename + Integer.toString(i) + ".txt"));
					tempScanner.next();

					clientOrder tempCO = new clientOrder(baseFilename + Integer.toString(i) + ".txt",
							productPrices);

					Thread t = new Thread(tempCO);
					threads[i - 1] = t;

					t.start();

					clients.put(tempScanner.nextInt(), tempCO);
					tempScanner.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		
	}

	private static void singlethread(String baseFilename, int nOrders, TreeMap<String, Double> productPrices, TreeMap<Integer, clientOrder> clients) {
   		for(int i = 1; i <= nOrders; i++) {
			try {
				
				Scanner tempScanner = new Scanner(new File(baseFilename + Integer.toString(i) + ".txt"));
				tempScanner.next();
				
				clientOrder currOrder = new clientOrder(baseFilename + Integer.toString(i) + ".txt", productPrices);
				currOrder.run();
				
				clients.put(tempScanner.nextInt(), currOrder);
			} catch (FileNotFoundException e) { e.printStackTrace(); }
		}
	}
	
	
	private static void writeToFile(TreeMap<Integer, clientOrder> orders, TreeMap<String, Double> itemCosts, String targetFile) {
		NumberFormat df = NumberFormat.getInstance();
		df.setGroupingUsed(true);
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		
		FileWriter writer;
		try {
			writer = new FileWriter(targetFile);	
			
			for(Map.Entry<Integer, clientOrder> entry: orders.entrySet()) {
				writer.write("----- Order details for client with Id: " + entry.getKey() + " -----\n");
				writer.write(entry.getValue().toString());        
			} 
			
			writer.write("***** Summary of all orders *****\n");
			
			double total = 0;
			for(String product: itemCosts.keySet()) {
				int productCount = 0;
				for(Integer key: orders.keySet()) { productCount += orders.get(key).getProduct(product); }
				
				if(productCount > 0) {
					writer.write("Summary - Item's name: " + product + ", Cost per item: $" + df.format(itemCosts.get(product)));
				
					writer.write(", Number sold: " + productCount + ", Item's Total: $" + df.format(productCount * itemCosts.get(product)));
				}
				
				total += productCount * itemCosts.get(product);
			}
			
			writer.write("\nSummary Grand Total: $" + df.format(total));
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public static TreeMap<String, Double> setPrices(TreeMap<String, Double> prices, String priceFileName) {
		
		Scanner sc;
		try {
			sc = new Scanner(new File(priceFileName));
			while(sc.hasNextLine()) {
				String temp = sc.next();
				double tempPrice = sc.nextDouble();
				prices.put(temp, tempPrice);
			}
			sc.close();
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		return prices;
	}

}