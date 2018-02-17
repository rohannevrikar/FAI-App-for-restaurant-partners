package tastifai.restaurant;

import java.io.Serializable;

/**
 * Created by Rohan Nevrikar on 01-02-2018.
 */

public class Item implements Serializable{
    private String item;
    private String qty;
    private String price;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
