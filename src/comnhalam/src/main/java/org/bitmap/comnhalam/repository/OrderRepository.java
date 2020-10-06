package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.Order;
import org.bitmap.comnhalam.model.OrderDetail;
import org.bitmap.comnhalam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findOrdersByCreateOnBetween(Date from, Date to);
    List<Order> findOrdersByCreateOnBetweenAndUser(Date from, Date to, User user);

}
