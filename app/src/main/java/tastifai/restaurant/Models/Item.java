package tastifai.restaurant.Models;

import java.io.Serializable;

/**
 * Created by Rohan Nevrikar on 01-02-2018.
 */

public class Item implements Serializable{
    private String item;
    private String qty;
    private double price;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
