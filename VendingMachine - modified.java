/*
 * 描述
 * Vending Machine一共有三种状态：NoSelection, HasSelection, InsertedMoney
 * Vending Machine一共卖三种饮料：Coke, Sprite和MountainDew
 * 要求Vending Machine在正确的状态要有正确的输出
 *
 * 输入：
 * select("Coke")
 * select("Sprite")
 * insert(500)
 * execTrans()
 *
 * 输出:
 * Current selection is: Coke, current inserted money: 0, current state is : HasSelection
 * Current selection is: Sprite, current inserted money: 0, current state is : HasSelection
 * Current selection is: Sprite, current inserted money: 500, current state is : InsertedMoney
 * Current selection is: null, current inserted money: 0, current state is : NoSelection
 */

/*
 * Add Enum Coin, Item;
 * Add Inventory/Stock
 */

// https://javarevisited.blogspot.com/2016/06/design-vending-machine-in-java.html#axzz6nLrXdLvD

public class VendingMachine {
    private Item currentSelectedItem;
    private int currentInsertedMoney;
    private List<Coin> currentCoins;
    
    private NoSelectionState noSelectionState;
    private HasSelectionState hasSelectionState;
    private InsertedMoneyState insertedMoneyState;
    
    private Inventory<Coin> cashInventory = new Inventory<Coin>();
    private Inventory<Item> itemInventory = new Inventory<Item>();
    
    public VendingMachine() {
        currentInsertedMoney = 0;
        currentSelectedItem = null;
        
        noSelectionState = new NoSelectionState(this);
        hasSelectionState = new HasSelectionState(this);
        insertedMoneyState = new InsertedMoneyState(this);
        
        state = noSelectionState;
        
        initialize();
    }
    
    private void initialize() { 
        //initialize machine with 5 coins of each denomination and 5 cans of each Item
        for(Coin c : Coin.values()){ 
            cashInventory.put(c, 5); 
        } 
        for(Item i : Item.values()){ 
            itemInventory.put(i, 5); 
        }
    }

    public void setSelectedItem(Item item) {
        this.currentSelectedItem = item;
    }
    
    public Item getSelectedItem() {
        return currentSelectedItem;
    }
    
    public void addMoney(Coin coin) {
        this.currentInsertedMoney += coin.getDenomination();
        currentCoins.add(coin);
        // cashInventory.add(coin);
    }
    
    public void emptyInsertedMoney() {
        this.currentInsertedMoney = 0;
    }
    
    public int getInsertedMoney() {
        return currentInsertedMoney;
    }
    
    public int getSalePrice() {
        if (currentSelectedItem == null) {
            System.out.println("Please make a selection before asking price");
            return 0;
        } else if (itemInventory.hasItem(currentSelectedItem)){
            return currentSelectedItem.getPrice();
        }
        throw new SoldOutException("Sold Out, Please buy another item");
    }
    
    public void changeToNoSelectionState() {
        state = noSelectionState;
    }
    
    public void changeToHasSelectionState() {
        state = hasSelectionState;
    }
    
    public void changeToInsertedMoneyState() {
        state = insertedMoneyState;
    }
    
    public void selectItem(Item item) {
        state.selectItem(item);
    }
    
    public void insertMoney(Coin coin) {
        state.insertMoney(coin);
    }
    
    public void executeTransaction() {
        state.executeTransaction();
    }
    
    public int cancelTransaction() {
        return state.cancelTransaction();
    }
    
    public void collectItemAndChange(int amount) {
        List<Coin> changes = getChange(amount);
        if (changes.size() > 0) {
            // deduct item
            itemInventory.deduct(currentSelectedItem);
            // add coins
            for (Coint c : currentCoins) {
                cashInventory.add(c);
            }
            currentCoins.clear();
            // deduct coin
            for(Coin c : changes){
                cashInventory.deduct(c);
            }
            setSelectedItem(null);
            emptyInsertedMoney();
            changeToNoSelectionState();
        }
        return;
    }
    
    public List<Coin> getChange(int amount) throws NotSufficientChangeException {
        List<Coin> changes = new ArrayList<>();       
        if (amount > 0) {
            int balance = amount;
            while (balance > 0) {
                if (balance >= Coin.QUARTER.getDenomination() 
                            && cashInventory.hasItem(Coin.QUARTER)){
                    changes.add(Coin.QUARTER);
                    balance = balance - Coin.QUARTER.getDenomination();
                } else if (balance >= Coin.DIME.getDenomination() 
                                 && cashInventory.hasItem(Coin.DIME)) {
                    changes.add(Coin.DIME);
                    balance = balance - Coin.DIME.getDenomination();
                } else if (balance >= Coin.NICKLE.getDenomination() 
                                 && cashInventory.hasItem(Coin.NICKLE)) {
                    changes.add(Coin.NICKLE);
                    balance = balance - Coin.NICKLE.getDenomination();
                } else if (balance >= Coin.PENNY.getDenomination() 
                                 && cashInventory.hasItem(Coin.PENNY)) {
                    changes.add(Coin.PENNY);
                    balance = balance - Coin.PENNY.getDenomination();
                } else {
                    throw new NotSufficientChangeException("Not Sufficient Change in Inventory.");
                }
            }
        }       
        return changes;
    }
    
    public List<Coin> refund(){
        setSelectedItem(null);
        emptyInsertedMoney();
        changeToNoSelectionState();
        List<Coin> refund = new ArrayList<>(currentCoins);
        currentCoins.clear();
        return refund;
    }
    
    public String printState() {
        String res = "";
        
        res = "Current selection is: " + currentSelectedItem + ", current inserted money: " + currentInsertedMoney
               + ", current state is : " + state;
        
        return res;
    }
    
    public void printStats(){ 
        System.out.println("Total Sales : " + totalSales);
        System.out.println("Current Item Inventory : " + itemInventory); 
        System.out.println("Current Cash Inventory : " + cashInventory); 
    }
}

enum Item {
    COKE("Coke", 25), PEPSI("Pepsi", 35), SODA("Soda", 45);
    
    private String name;
    private int price;
    
    private Item (String name, int price) {
        this.name = name;
        this.price = price;
    }
    
    public String getName() {
        return name;
    }
    
    public long getPrice() {
        return price;
    }
}

enum Coin {
    PENNY(1), NICKLE(5), DIME(10), QUARTER(25);
    private int denomination;
    private Coin(int denomination) {
        this.denomination = denomination;
    }
    
    public int getDenomination() {
        return denomination; 
    } 
}

class Inventory<T> { 
    private Map<T, Integer> inventory = new HashMap<T, Integer>();
    
    public int getQuantity(T item) {
        Integer value = inventory.get(item);
        return value == null? 0 : value ;
    }
    
    public void add(T item) {
        int count = inventory.get(item); 
        inventory.put(item, count+1); 
    } 
    
    public void deduct(T item) { 
        if (hasItem(item)) { 
            int count = inventory.get(item); 
            inventory.put(item, count - 1); 
        } 
    } 
    
    public boolean hasItem(T item) { 
        return getQuantity(item) > 0; 
    } 
    
    public void clear() { 
        inventory.clear(); 
    } 
    
    public void put(T item, int quantity) { 
        inventory.put(item, quantity); 
    }
}

interface State {
    public void selectItem(Item item);
    public void insertMoney(Coin coin);
    public void executeTransaction();
    public int cancelTransaction();
    public String toString();
}

class NoSelectionState extends implements State {
    private VendingMachine vendingMachine;
    
    public NoSelectionState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }
    
    @Override
    public void selectItem(Item item) {
        // TODO Auto-generated method stub
        vendingMachine.setSelectedItem(item);
        vendingMachine.changeToHasSelectionState();
    }
    
    @Override
    public void insertMoney(Coin coin) {
        // TODO Auto-generated method stub
        System.out.println("Please make a selection first");
    }
    
    @Override
    public void executeTransaction() {
        // TODO Auto-generated method stub
        System.out.println("Please make a selection first");
    }
    
    @Override
    public int cancelTransaction() {
        // TODO Auto-generated method stub
        System.out.println("Please make a selection first");
        return 0;
    }
    
    @Override
    public String toString(){
        return "NoSelection";
    }
}

class HasSelectionState extends implements State {
    private VendingMachine vendingMachine;
    
    public HasSelectionState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }
    
    @Override
    public void selectItem(Item item) {
        // TODO Auto-generated method stub
        vendingMachine.setSelectedItem(item);
    }
    
    @Override
    public void insertMoney(Coin coin) {
        // TODO Auto-generated method stub
        vendingMachine.addMoney(coin);
        vendingMachine.changeToInsertedMoneyState();
    }
    
    @Override
    public void executeTransaction() {
        // TODO Auto-generated method stub
        System.out.println("You need to insert money first");
    }
    
    @Override
    public int cancelTransaction() {
        // TODO Auto-generated method stub
        System.out.println("Transaction canceled");
        vendingMachine.refund();
        return 0;
    }
    
    @Override
    public String toString(){
        return "HasSelection";
    }
}

class InsertedMoneyState extends implements State {
    private VendingMachine vendingMachine;
    
    public InsertedMoneyState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }
    
    @Override
    public void selectItem(Item item) {
        // TODO Auto-generated method stub
        System.out.println("Already has a selection, please cancel transaction to make a new selection");
    }
    
    @Override
    public void insertMoney(Coin coin) {
        // TODO Auto-generated method stub
        vendingMachine.insertMoney(coin);
    }
    
    @Override
    public void executeTransaction() {
        // TODO Auto-generated method stub
        int diff = vendingMachine.getInsertedMoney() - vendingMachine.getSalePrice();
        if (diff >= 0) {
            // System.out.println("Executing transaction, will return you : " + diff + " money and item: " + vendingMachine.getSelectedItem().getName());            
            vendingMachine.collectItemAndChange(diff);
        } else{
            throw new NotFullPaidException("Price not full paid, remaining: ", (-diff));
        }
    }
    
    @Override
    public int cancelTransaction() {
        // TODO Auto-generated method stub
        int insertedMoney = vendingMachine.getInsertedMoney();
        vendingMachine.refund();
        return insertedMoney;
    }
    
    @Override
    public String toString(){
        return "InsertedMoney";
    }
}


class NotFullPaidException extends RuntimeException { 
    private String message; 
    private int remaining; 
    
    public NotFullPaidException(String message, int remaining) { 
        this.message = message; 
        this.remaining = remaining; 
    } 
    
    public int getRemaining(){ 
        return remaining; 
    } 
    
    @Override 
    public String getMessage(){ 
        return message + remaining; 
    } 
}

class NotSufficientChangeException extends RuntimeException { 
    private String message; 
    public NotSufficientChangeException(String string) { 
        this.message = string; 
    } 
    
    @Override public String getMessage(){ 
        return message; 
    } 
}

class SoldOutException extends RuntimeException {
    private String message; 
    
    public SoldOutException(String string) { 
        this.message = string; 
    } 
    
    @Override public String getMessage(){ 
        return message; 
    } 
}

