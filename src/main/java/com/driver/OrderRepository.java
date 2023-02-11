package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderMap;
    HashMap<String,DeliveryPartner> partnerMap;
    HashMap<String,String> orderPartnerMap;
    HashMap<String,List<Order>> pairMap;

    public OrderRepository() {
        orderMap = new HashMap<>();
        partnerMap = new HashMap<>();
        orderPartnerMap = new HashMap<>();
        pairMap = new HashMap<>();
    }

    public String addOrder(Order order) {

        try {
            orderMap.put(order.getId(), order);
        }catch (Exception e){
            System.out.println(e);
        }
        return "New order added successfully";
    }

    public String addPartner(String partnerId) {
        try {
            DeliveryPartner partner = new DeliveryPartner(partnerId);
            partnerMap.put(partnerId,partner);
        }catch (Exception e){
            System.out.println(e);
        }

        return "New delivery partner added successfully";
    }

    public String addOrderPartnerPair(String orderId, String partnerId) {
        try{
            DeliveryPartner partner = partnerMap.get(partnerId);
            int noOfOrder = partner.getNumberOfOrders()+1;
            partner.setNumberOfOrders(noOfOrder);

            orderPartnerMap.put(orderId,partnerId);
            List<Order> currentOrder = pairMap.getOrDefault(partnerId,new ArrayList<Order>());
            currentOrder.add(orderMap.get(orderId));
            pairMap.put(partnerId,currentOrder);
        }catch (Exception e){
            System.out.println(e);
        }


        return "New order-partner pair added successfully";
    }

    public Order getOrderById(String orderId) {

        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return partnerMap.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        List<String> currentOrder = new ArrayList<>();
        for (Order order:pairMap.get(partnerId)){
            currentOrder.add(order.getId());
        }
        return currentOrder;
    }

    public List<String> getAllOrders() {
        List<String> totalOrder = new ArrayList<>();
        for (Order order:orderMap.values()){
            totalOrder.add(order.getId());
        }
        return totalOrder;
    }

    public Integer getCountOfUnassignedOrders() {
        return orderMap.size()-orderPartnerMap.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int HH = Integer.parseInt(time.substring(0,2));
        int MM = Integer.parseInt(time.substring(3));
        int deliveryTime= HH*60 + MM;

        int count = 0;
        for (Order order:pairMap.get(partnerId)){
            if(deliveryTime < order.getDeliveryTime()){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int maxTime = 0;
        for (Order order:pairMap.get(partnerId)){
            if(maxTime < order.getDeliveryTime()){
                maxTime = order.getDeliveryTime();
            }
        }

        String strTemp;
        int hours   = maxTime / 60;
        int minutes    = maxTime % 60;

        if(hours < 10)
            strTemp = "0" + hours + ":";
        else
            strTemp = hours + ":";

        if(minutes < 10)
            strTemp = strTemp + "0" + minutes;
        else
            strTemp = strTemp + minutes;


        return strTemp;
    }

    public String deletePartnerById(String partnerId) {
        for (Order order:pairMap.get(partnerId)){
            orderPartnerMap.remove(order.getId());
        }
        partnerMap.remove(partnerId);
        pairMap.remove(partnerId);
        return partnerId;
    }

    public String deleteOrderById(String orderId) {

        orderMap.remove(orderId);
        String pId = orderPartnerMap.get(orderId);
        orderPartnerMap.remove(orderId);

        List<Order> orders = pairMap.get(pId);
        for (Order order:orders){
            if(order.getId().equals(orderId)){
                orders.remove(order);
            }
        }
        pairMap.put(pId,orders);

        return orderId;
    }
}
