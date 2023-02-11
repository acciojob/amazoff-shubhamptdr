package com.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//@Repository
public class OrderRepository {

    Logger logger = LoggerFactory.getLogger(OrderRepository.class);
    private Map<String,Order> orderMap;
    private Map<String,DeliveryPartner> partnerMap;
    private Map<String,String> orderPartnerMap;
    private Map<String,List<String>> pairMap;

    public OrderRepository() {
        orderMap = new HashMap<>();
        partnerMap = new HashMap<>();
        orderPartnerMap = new HashMap<>();
        pairMap = new HashMap<>();
    }

    public String addOrder(Order order) {
        logger.info("Called AddOrder:"+order);
        orderMap.put(order.getId(), order);
        return "New order added successfully";
    }

    public String addPartner(String partnerId) {
        logger.info("Called addPartner:"+partnerId);

        partnerMap.put(partnerId,new DeliveryPartner(partnerId));
        return "New delivery partner added successfully";
    }

    public String addOrderPartnerPair(String orderId, String partnerId) {

        partnerMap.get(partnerId).setNumberOfOrders(partnerMap.get(partnerId).getNumberOfOrders()+1);
        orderPartnerMap.put(orderId,partnerId);
        if(pairMap.containsKey(partnerId)){
            List<String> currentOrder = pairMap.get(partnerId);
            currentOrder.add(orderId);
        }
        else{
            pairMap.put(partnerId,new ArrayList<>(Arrays.asList(orderId)));
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

        return new ArrayList<>(pairMap.get(partnerId));

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
        int count = 0;

        int HH = Integer.parseInt(time.substring(0,2));
        int MM = Integer.parseInt(time.substring(3));
        int deliveryTime= HH*60 + MM;

        for (String order:pairMap.get(partnerId)){
            if(deliveryTime < orderMap.get(order).getDeliveryTime()){
                count++;
            }
        }

        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {

        int maxTime = 0;
        if(pairMap.containsKey(partnerId)){
            for (String orderId:pairMap.get(partnerId)){
                if(maxTime < orderMap.get(orderId).getDeliveryTime()){
                    maxTime = orderMap.get(orderId).getDeliveryTime();
                }
            }
        }


        int hours   = maxTime / 60;
        int minutes    = maxTime % 60;
        StringBuilder strTemp = new StringBuilder();
        if(hours < 10)
            strTemp.append("0"+hours+":");
        else
            strTemp.append(hours + ":");

        if(minutes < 10)
            strTemp.append("0" + minutes);
        else
            strTemp.append(minutes);


        return strTemp.toString();
    }

    public String deletePartnerById(String partnerId) {

        if(pairMap.containsKey(partnerId)){
            for (String order:pairMap.get(partnerId)){
                orderPartnerMap.remove(order);
            }
        }

        partnerMap.remove(partnerId);
        pairMap.remove(partnerId);

        return partnerId;
    }

    public String deleteOrderById(String orderId) {

            orderMap.remove(orderId);
            if(orderPartnerMap.containsKey(orderId)){
                String pId = orderPartnerMap.get(orderId);
                orderPartnerMap.remove(orderId);
                List<String> orders = pairMap.get(pId);
                orders.remove(orderId);
                pairMap.put(pId,orders);
            }

        return orderId;
    }
}
