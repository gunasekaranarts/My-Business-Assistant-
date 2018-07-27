package POJO;

import java.io.Serializable;

/**
 * Created by USER on 26-07-2018.
 */

public class OrderItem implements Serializable {

    public int ItemId;
    public String ItemName;
    public String ItemQty;
    public String ItemSuggest;
    public int OrderId;

    public int getItemId() {
        return ItemId;
    }

    public void setItemId(int itemId) {
        ItemId = itemId;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemQty() {
        return ItemQty;
    }

    public void setItemQty(String itemQty) {
        ItemQty = itemQty;
    }

    public String getItemSuggest() {
        return ItemSuggest;
    }

    public void setItemSuggest(String itemSuggest) {
        ItemSuggest = itemSuggest;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }
}
