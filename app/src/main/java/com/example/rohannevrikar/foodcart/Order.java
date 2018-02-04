package com.example.rohannevrikar.foodcart;

import java.io.Serializable;

/**
 * Created by Rohan Nevrikar on 31-01-2018.
 */

public class Order implements Serializable {
    private String customerName;
    private String deliveryAddress;
    private String contactNumber;

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
