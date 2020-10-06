package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.OrderDetail;
import org.bitmap.comnhalam.model.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {

}
