package POJO;

import java.io.Serializable;

/**
 * Created by USER on 26-07-2018.
 */

public class POrders implements Serializable {

    public int OrderId;
    public String OrderName;
    public String OrderDate;
    public int CustomerId;

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getOrderName() {
        return OrderName;
    }

    public void setOrderName(String orderName) {
        OrderName = orderName;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    @Override
    public String toString() {
        if(this.getOrderDate()!=null){
        StringBuilder result = new StringBuilder(this.getOrderDate());
        result.insert(4,"/");
        result.insert(7,"/");
        result.insert(10," ");
        result.insert(13,":");
        return this.getOrderName()+"-"+result.toString();
        }else{
            return this.getOrderName();
        }
    }
}
