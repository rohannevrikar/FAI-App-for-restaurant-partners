package tastifai.restaurant.Models;

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
    private double totalPrice;
    private double userLat;
    private double userLng;
    private boolean isNavigationAvailable;
    private String instruction;
    private double discount;
    private double totalUser;
    private double deliveryCharge;
    private double restaurantEarnings;

    public double getRestaurantEarnings() {
        return restaurantEarnings;
    }

    public void setRestaurantEarnings(double restaurantEarnings) {
        this.restaurantEarnings = restaurantEarnings;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(double totalUser) {
        this.totalUser = totalUser;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getinstruction() {
        return instruction;
    }

    public void setinstruction(String instruction) {
        this.instruction = instruction;
    }

    public boolean isNavigationAvailable() {
        return isNavigationAvailable;
    }

    public void setNavigationAvailable(boolean navigationAvailable) {
        isNavigationAvailable = navigationAvailable;
    }

    public double getUserLat() {
        return userLat;
    }

    public void setUserLat(double userLat) {
        this.userLat = userLat;
    }

    public double getUserLng() {
        return userLng;
    }

    public void setUserLng(double userLng) {
        this.userLng = userLng;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
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
        this.contactNumber = contactNumber;
    }
}
