/*
 * 设计餐馆 II
 * 不能订外卖
 * 能预订座位
 * MAX_DINETIME 为 2， 意为占用一桌吃饭的最大时长为2小时
 * 如果餐桌被预定，则无法入座
 * 餐馆的桌子有不同大小
 * 餐馆会优先选择适合当前Party最小的空桌
 * 相对设计餐馆 I，Table新增functions 需要实现。相关函数之后会调用restaurantDescription, 来验证你的程序是否正确。
 */

/*
  样例
  
    meal(10.0)
    meal(13.0)
    meal(15.0)
    
    table(1, 4)
    table(2, 10)
    
    party(3)
    reservedDate(6) // Date(2013年1月4日 + 6 * Restaurant.HOUR);
    
    party(7)
    reservedDate(7) // Date(2013年1月4日 + 7 * Restaurant.HOUR);
    
    order(1)
    order(2, 3)
    
    findTableForReservation(1, 1) // 第一个的参数是party的id，第二个参数是reservedDate的id
    findTableForReservation(2, 2) 
    
    Table: 1, table size: 4, isAvailable: true. No current order for this table. Current reservation dates for this table are: 4 Jan 2013 06:00:00 GMT ; .
    Table: 2, table size: 10, isAvailable: true. No current order for this table. Current reservation dates for this table are: .
    *****************************************

    Table: 1, table size: 4, isAvailable: true. No current order for this table. Current reservation dates for this table are: 4 Jan 2013 06:00:00 GMT ; .
    Table: 2, table size: 10, isAvailable: true. No current order for this table. Current reservation dates for this table are: 4 Jan 2013 07:00:00 GMT ; .
    *****************************************

 */

class NoTableException extends Exception {
public NoTableException(Party p)
    {
        super("No table available for party size: " + p.getSize());
    }
}

class Meal {
    private float price;
    
    public Meal(float price)
    {
        this.price = price;
    }
    
    public float getPrice()
    {
        return this.price;
    }
}

class Order {
    private List<Meal> meals;
    
    public Order()
    {
        meals = new ArrayList<Meal>();
    }
    
    public List<Meal> getMeals()
    {
        return meals;
    }
    
    public void mergeOrder(Order order)
    {
        if(order != null)
        {
            for(Meal meal : order.getMeals())
            {
                meals.add(meal);
            }
        }
    }
    
    public float getBill()
    {
        int bill = 0;
        for(Meal meal : meals)
        {
            bill += meal.getPrice();
        }
        return bill;
    }
}

class Party {
    private int size;
    
    public Party(int size)
    {
        this.size = size;
    }
    
    public int getSize()
    {
        return this.size;
    }
}

class Table implements Comparable<Table> {
    private int id;
    private int capacity;
    private boolean available;
    private Order order;
    List<Date> reservations;
    
    public Table(int id, int capacity)
    {
        this.id = id;
        this.capacity = capacity;
        available = true;
        order = null;
        reservations = new ArrayList<>();
    }
    
    public int getId()
    {
        return this.id;
    }
    
    public int getCapacity()
    {
        return this.capacity;
    }
    
    public List<Date> getReservation()
    {
        return reservations;
    }
    
    public boolean isAvailable()
    {
        return this.available;
    }
    
    public void markAvailable()
    {
        this.available = true;
    }
    
    public void markUnavailable()
    {
        this.available = false;
    }
    
    public Order getCurrentOrder()
    {
        return this.order;
    }
    
    public void setOrder(Order o)
    {
        if(order == null)
        {
            this.order = o;
        }
        else 
        {
            if(o != null)
            {
                this.order.mergeOrder(o);
            }
        }
    }
    
    @Override
    public int compareTo(Table compareTable) {
        // TODO Auto-generated method stub
        return this.capacity - compareTable.getCapacity();
    }
    
    private int findDatePosition(Date d)
    {
        int len = reservations.size();
        if(len == 0)
            return 0;
        if(d.getTime() > reservations.get(len - 1).getTime())
        {
            return len;
        }
        
        int i=0;
        int j=len;
        
        while (i < j) {
            int m = (i + j) / 2;
            if(d.getTime() > reservations.get(m).getTime()) {
                i = m + 1; 
            } else {
                j = m;
            }
        }
        return j;
    }
    
    public boolean noFollowReservation(Date d)
    {
        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        int position = findDatePosition(d);
        
        if(position < reservations.size())
        {
            Date nextReservation = reservations.get(position);
            int diff = (int) ((nextReservation.getTime() - d.getTime()) / MILLI_TO_HOUR);
            if(diff < Restaurant.MAX_DINEHOUR)
            {
                return false;
            }
        }
        return true;
    }
    
    public boolean reserveForDate(Date d)
    {
        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        int position = findDatePosition(d);
        int before = position - 1;
        int after = position;
        
        if (before >= 0)
        {
            Date previousReservation = reservations.get(before);
            int diff = (int) ((d.getTime() - previousReservation.getTime()) / MILLI_TO_HOUR);
            if(diff < Restaurant.MAX_DINEHOUR)
            {
                return false;
            }
        }
        
        if (after < reservations.size())
        {
            Date nextReservation = reservations.get(after);
            int diff = (int) ((nextReservation.getTime() - d.getTime()) / MILLI_TO_HOUR);
            if(diff < Restaurant.MAX_DINEHOUR)
            {
                return false;
            }
        }
        
        reservations.add(position, d);
        return true;
    }
    
    public void removeReservation(Date d)
    {
        reservations.remove(d);
    }
}

class Reservation {
    private Table table;
    private Date date;
    
    public Reservation(Table table, Date date)
    {
        this.table = table;
        this.date = date;
    }
    
    public Date getDate()
    {
        return date;
    }
    
    public Table getTable()
    {
        return table;
    }
}

public class Restaurant {
    private List<Table> tables;
    private List<Meal> menu;
    public static final int MAX_DINEHOUR = 2;
    public static final long HOUR = 3600*1000;

    public Restaurant()
    {
        tables = new ArrayList<Table>();
        menu = new ArrayList<Meal>();
    }
    
    public void findTable(Party p) throws NoTableException
    {
        Date currentDate = new Date();
        for(Table t: tables)
        {
            if(t.isAvailable())
            {
                if(t.getCapacity() >= p.getSize())
                {
                    if(t.noFollowReservation(currentDate))
                    {
                        t.markUnavailable();
                        return;
                    }
                }
            }
        }
        throw new NoTableException(p);
    }
    
    public void takeOrder(Table t, Order o)
    {
        t.setOrder(o);
    }
    
    public float checkOut(Table t)
    {
        float bill = 0;
        if(t.getCurrentOrder() != null)
        {
            bill = t.getCurrentOrder().getBill();
        }
        
        t.markAvailable();
        t.setOrder(null);
        
        return bill;
    }
    
    public List<Meal> getMenu()
    {
        return menu;
    }
    
    public void addTable(Table t)
    {
        tables.add(t);
        Collections.sort(tables);
    }
    
    public Reservation findTableForReservation(Party p, Date date)
    {
        for(Table table : tables)
        {
            if(table.getCapacity() >= p.getSize())
            {
                if(table.reserveForDate(date))
                {
                    return new Reservation(table, date);
                }
            }
        }
        return null;
    }
    
    public void cancelReservation(Reservation r)
    {
        Date date = r.getDate();
        r.getTable().removeReservation(date);
    }
    
    public void redeemReservation(Reservation r)
    {
        Date date = r.getDate();
        Table table = r.getTable();

        table.markUnavailable();
        table.removeReservation(date);
    }
    
    public String restaurantDescription()
    {
        String description = "";
        for(int i = 0; i < tables.size(); i++)
        {
            Table table = tables.get(i);
            description += ("Table: " + table.getId() + ", table size: " + table.getCapacity() + ", isAvailable: " + table.isAvailable() + ".");
            if(table.getCurrentOrder() == null)
                description += " No current order for this table"; 
            else
                description +=  " Order price: " + table.getCurrentOrder().getBill();

            description += ". Current reservation dates for this table are: ";

            for(Date date : table.getReservation())
            {
                description += date.toGMTString() + " ; ";
            }

            description += ".\n";
        }
        description += "*****************************************\n";
        return description;
    }
}
