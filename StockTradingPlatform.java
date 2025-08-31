
import java.io.*;
import java.util.*;

class Stock {
    String symbol;
    double price;

    Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }
}

class Transaction {
    String stockSymbol;
    int quantity;
    double price;
    String type; 
    Date date;

    Transaction(String stockSymbol, int quantity, double price, String type) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.date = new Date();
    }

    public String toString() {
        return type + " " + quantity + " shares of " + stockSymbol +
               " @ " + price + " on " + date;
    }
}

class Portfolio {
    double cashBalance;
    Map<String, Integer> holdings; 
    List<Transaction> transactions;

    Portfolio(double initialBalance) {
        this.cashBalance = initialBalance;
        this.holdings = new HashMap<>();
        this.transactions = new ArrayList<>();
    }

    void buyStock(Stock stock, int quantity) {
        double cost = stock.price * quantity;
        if (cost > cashBalance) {
            System.out.println("❌ Not enough balance to buy " + stock.symbol);
            return;
        }
        cashBalance -= cost;
        holdings.put(stock.symbol, holdings.getOrDefault(stock.symbol, 0) + quantity);
        transactions.add(new Transaction(stock.symbol, quantity, stock.price, "BUY"));
        System.out.println("✅ Bought " + quantity + " shares of " + stock.symbol);
    }

    void sellStock(Stock stock, int quantity) {
        if (!holdings.containsKey(stock.symbol) || holdings.get(stock.symbol) < quantity) {
            System.out.println("❌ Not enough shares to sell.");
            return;
        }
        double revenue = stock.price * quantity;
        cashBalance += revenue;
        holdings.put(stock.symbol, holdings.get(stock.symbol) - quantity);
        if (holdings.get(stock.symbol) == 0) holdings.remove(stock.symbol);
        transactions.add(new Transaction(stock.symbol, quantity, stock.price, "SELL"));
        System.out.println("✅ Sold " + quantity + " shares of " + stock.symbol);
    }

    void viewPortfolio(Map<String, Stock> market) {
        System.out.println("\n===== Portfolio =====");
        System.out.println("Cash Balance: $" + cashBalance);
        for (String symbol : holdings.keySet()) {
            int qty = holdings.get(symbol);
            double currentValue = qty * market.get(symbol).price;
            System.out.println(symbol + " : " + qty + " shares (Value: $" + currentValue + ")");
        }
        System.out.println("======================");
    }

    void viewTransactions() {
        System.out.println("\n===== Transactions =====");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        System.out.println("=========================");
    }

    void savePortfolio(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(cashBalance);
            oos.writeObject(holdings);
            oos.writeObject(transactions);
            System.out.println("💾 Portfolio saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    void loadPortfolio(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            cashBalance = (double) ois.readObject();
            holdings = (Map<String, Integer>) ois.readObject();
            transactions = (List<Transaction>) ois.readObject();
            System.out.println("📂 Portfolio loaded!");
        } catch (Exception e) {
            System.out.println("⚠ No saved portfolio found.");
        }
    }

public class StockTradingPlatform {
    static Map<String, Stock> market = new HashMap<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        market.put("AAPL", new Stock("AAPL", 150.0));
        market.put("GOOG", new Stock("GOOG", 2800.0));
        market.put("TSLA", new Stock("TSLA", 700.0));
        market.put("AMZN", new Stock("AMZN", 3300.0));

        Portfolio portfolio = new Portfolio(10000.0); // Start with $10,000
        portfolio.loadPortfolio("portfolio.dat");

        while (true) {
            System.out.println("\n===== Stock Trading Menu =====");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. View Transactions");
            System.out.println("6. Save & Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1: viewMarketData(); break;
                case 2: tradeStock(portfolio, "BUY"); break;
                case 3: tradeStock(portfolio, "SELL"); break;
                case 4: portfolio.viewPortfolio(market); break;
                case 5: portfolio.viewTransactions(); break;
                case 6: portfolio.savePortfolio("portfolio.dat"); return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void viewMarketData() {
        System.out.println("\n===== Market Data =====");
        for (Stock s : market.values()) {
            System.out.println(s.symbol + " : $" + s.price);
        }
        System.out.println("========================");
    }

    static void tradeStock(Portfolio portfolio, String type) {
        System.out.print("Enter stock symbol: ");
        String symbol = sc.next().toUpperCase();
        if (!market.containsKey(symbol)) {
            System.out.println("❌ Stock not found!");
            return;
        }
        System.out.print("Enter quantity: ");
        int qty = sc.nextInt();

        Stock stock = market.get(symbol);
        if (type.equals("BUY")) portfolio.buyStock(stock, qty);
        else portfolio.sellStock(stock, qty);
    }
}
}