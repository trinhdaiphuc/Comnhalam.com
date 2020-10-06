package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.model.Order;
import org.bitmap.comnhalam.model.OrderDetail;
import org.bitmap.comnhalam.model.OrderDetailId;
import org.bitmap.comnhalam.repository.OrderDetailRepository;
import org.bitmap.comnhalam.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CartAdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @RequestMapping("/admin/orders")
    public String listOrder(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @RequestMapping("/admin/orders/{id}")
    public String orderDetail(Model model,
                              @PathVariable("id") String id) {

        Long nId = null;

        try {
            nId = Long.parseUnsignedLong(id);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "ID wrong format");
        }

        try {
            Order order = orderRepository.findById(nId).orElseThrow(() -> new Exception("Order not found"));
            model.addAttribute("order", order);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "admin/order";
    }

    @RequestMapping("/admin/state-order/{id}/{state}")
    @ResponseBody
    public String orderState(@PathVariable("id") String id,
                             @PathVariable("state") String state) {
        Long nId = null;
        Byte nState = null;

        try {
            nId = Long.parseUnsignedLong(id);
            nState = Byte.parseByte(state);
        } catch (NumberFormatException e) {
            return e.getMessage();
        }

        try {
            Order order = orderRepository.findById(nId).orElseThrow(() -> new Exception("Order not found"));
            if(nState < 0 && nState > 4) {
                throw new Exception("State not found");
            }

            //Nếu đơn hàng đã được giao hoặc đã hủy thì không được phép sửa đổi
            if(order.getState() == Order.STATE_CANCELLED || order.getState() == Order.STATE_SUCCESSFUL_DELIVERY)
                throw new Exception("State mustn't modify");

            order.setState(nState);
            orderRepository.save(order);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "success";
    }

    @RequestMapping("/admin/active-order-detail/{orderId}/{productId}/{checked}")
    @ResponseBody
    public String activeOrderDetail(@PathVariable("orderId") String orderId,
                                    @PathVariable("productId") String productId,
                                    @PathVariable("checked") String checked) {

        Long nOrderId = null;
        Long nProductId = null;

        try {
            nOrderId = Long.parseUnsignedLong(orderId);
            nProductId = Long.parseUnsignedLong(productId);
        } catch (NumberFormatException e) {
            return e.getMessage();
        }

        try {
            OrderDetail orderDetail = orderDetailRepository.findById(new OrderDetailId(nOrderId, nProductId)).orElseThrow(() -> new Exception("Order Detail not found"));
            Order order = orderDetail.getOrder();
            if(order.getState() == Order.STATE_CANCELLED || order.getState() == Order.STATE_SUCCESSFUL_DELIVERY)
                throw new Exception("State mustn't modify");
            if(checked.equals("yes")) {
                orderDetail.setEnabled(true);
                order.setTotal(order.getTotal() + orderDetail.getPrice());
            } else {
                orderDetail.setEnabled(false);
                order.setTotal(order.getTotal() - orderDetail.getPrice());
            }
            orderDetailRepository.save(orderDetail);
            orderRepository.save(order);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "success";
    }

}
