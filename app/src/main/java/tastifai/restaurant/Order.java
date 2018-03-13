package tastifai.restaurant;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rohan Nevrikar on 31-01-2018.
 */

public class Order implements Serializable {
    private String customerName;
    private String deliveryAddress;
    private String contactNumber;
    private String dateTime;
    private ArrayList itemList;
    private String guid;
    private String totalPrice;

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public ArrayList getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList itemList) {
        this.itemList = itemList;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    private String preferences;



    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber= contactNumber;
    }
}
