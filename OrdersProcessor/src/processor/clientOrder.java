package processor;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.TreeMap;
import java.util.Map;

public class clientOrder implements Runnable{
	private File orderFile;
	
	private TreeMap<String, Integer> orders;
	private TreeMap<String, Double> prices;
	
	
	public clientOrder(String fileName, TreeMap<String, Double> prices) {
		orderFile = new File(fileName);
		this.prices = prices;
		orders = new TreeMap<String, Integer>();
		
	}
	
	
	@Override
	public void run() { 
		try {
			Scanner scanner = new Scanner(orderFile);
			scanner.next();
			System.out.println("Reading order for client with id: " + scanner.next());
			while(scanner.hasNext()) {
				String curr = scanner.next();
				if(!orders.containsKey(curr)) { orders.put(curr, 1); }
				else { orders.put(curr, orders.get(curr) + 1); }
				scanner.next();
			}
			scanner.close();
		} catch (FileNotFoundException e) { e.printStackTrace(); }
	}
	
	
	public double getTotal() {
		double total = 0;
		for(Map.Entry<String, Integer> entry: orders.entrySet()) {
			total += orders.get(entry.getKey()).doubleValue() * prices.get(entry.getKey());
		}
		return total;
	}


	public String toString() {
		NumberFormat df = NumberFormat.getInstance();
		df.setGroupingUsed(true);
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		
		String str = "";
		for(Map.Entry<String, Integer> entry: orders.entrySet()) {
			if(entry.getValue() > 0) {
				str += "Item's name: " + entry.getKey() + ", " + "Cost per item: $" + df.format(prices.get(entry.getKey())) + ", Quantity: " + entry.getValue()
					+ ", Cost: $" + df.format(entry.getValue() * prices.get(entry.getKey())) + "\n";
			}
		}
		
		str += "Order Total: $" + df.format(this.getTotal()) + "\n";
		return str;
	}
	
	public Integer getProduct(String product) { 
		if(orders.containsKey(product)) {
			return orders.get(product);
		} else {
			return 0;
		}
	}
	
	
	
}
