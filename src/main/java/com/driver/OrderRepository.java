package com.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class OrderRepository {

    private Map<String,Order> orderMap;
    private Map<String,DeliveryPartner> partnerMap;
    private Map<String,List<String>> pairMap;
    private Set<String> orderNotAssigned;

    public OrderRepository() {
        orderMap = new HashMap<>();
        partnerMap = new HashMap<>();
        pairMap = new HashMap<>();
        orderNotAssigned =  new HashSet<>();
    }

    public String addOrder(Order order) {
        orderMap.put(order.getId(), order);
        orderNotAssigned.add(order.getId());
        return "New order added successfully";
    }

    public String addPartner(String partnerId) {

        partnerMap.put(partnerId,new DeliveryPartner(partnerId));
        return "New delivery partner added successfully";
    }

    public String addOrderPartnerPair(String orderId, String partnerId) {

        partnerMap.get(partnerId).setNumberOfOrders(partnerMap.get(partnerId).getNumberOfOrders()+1);

        if(pairMap.containsKey(partnerId)){
            List<String> currentOrder = pairMap.get(partnerId);
            currentOrder.add(orderId);
            orderNotAssigned.remove(orderId);
        }
        else{
            pairMap.put(partnerId,new ArrayList<>(Arrays.asList(orderId)));
            orderNotAssigned.remove(orderId);
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
        return pairMap.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        List<String> orderList = new ArrayList<>();

        List<String> orderIdList = pairMap.get(partnerId);
        for(String order : orderIdList){
            orderList.add(orderMap.get(order).getId());
        }
        return orderList;
    }

    public List<String> getAllOrders() {
        Collection<Order> values = orderMap.values();

        List<String> totalOrder = new ArrayList<>();
        for (Order order: values){
            totalOrder.add(order.getId());
        }
        return totalOrder;
    }

    public Integer getCountOfUnassignedOrders() {
        return orderNotAssigned.size();
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

        if(!pairMap.isEmpty()){
            orderNotAssigned.addAll(pairMap.get(partnerId));
        }

        partnerMap.remove(partnerId);
        pairMap.remove(partnerId);

        return partnerId;
    }

    public String deleteOrderById(String orderId) {

        orderMap.remove(orderId);
        if(orderNotAssigned.contains(orderId)){
            orderNotAssigned.remove(orderId);
        }
        else {
            for(List<String> orders: pairMap.values()){
                orders.remove(orderId);
            }

        }
        return orderId;
    }
}
