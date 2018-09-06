package tastifai.restaurant.Interfaces;

import java.util.ArrayList;

import tastifai.restaurant.Models.Order;

public interface OrderListener {
    public  void orderCallBack(ArrayList<Order> order);
}
