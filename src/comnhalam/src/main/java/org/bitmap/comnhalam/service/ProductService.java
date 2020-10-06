package org.bitmap.comnhalam.service;

import org.bitmap.comnhalam.form.EditProfileForm;
import org.bitmap.comnhalam.form.ProductCartForm;
import org.bitmap.comnhalam.form.ShippingForm;
import org.bitmap.comnhalam.form.TopSellerForm;
import org.bitmap.comnhalam.model.Order;
import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    void addAmount(Long id, Integer quantity, Order order) throws Exception;
    void createOrder(User user, List<ProductCartForm> carts, Order order) throws Exception;
    void deleteOrder(Order order);
    Order createOrder(ShippingForm shippingForm);
    List<TopSellerForm> getTopSellers(int limit);
}
