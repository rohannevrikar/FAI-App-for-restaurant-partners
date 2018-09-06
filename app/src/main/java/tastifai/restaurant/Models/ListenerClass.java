package tastifai.restaurant.Models;

import android.util.Log;

import tastifai.restaurant.Interfaces.OrderListener;

public class ListenerClass {
    private static final String TAG = "ListenerClass";
    private OrderListener orderListener;

    final public void setListener(OrderListener orderListener) {
        Log.d(TAG, "setListener: ");
        this.orderListener = orderListener;
    }
}
