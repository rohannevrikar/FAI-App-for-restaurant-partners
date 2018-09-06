package tastifai.restaurant.Models;

/**
 * Created by Rohan Nevrikar on 04-02-2018.
 */

public class TimePOJO {
    private String receivedAt;
    private String dispatchedAt;
    private String acceptedAt;
    private String deliveredAt;

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getDispatchedAt() {
        return dispatchedAt;
    }

    public void setDispatchedAt(String dispatchedAt) {
        this.dispatchedAt = dispatchedAt;
    }

    public String getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(String acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public String getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(String deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}
