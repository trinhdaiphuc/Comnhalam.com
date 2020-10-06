package org.bitmap.comnhalam.service.Impl;

import org.bitmap.comnhalam.form.EditProfileForm;
import org.bitmap.comnhalam.form.ProductCartForm;
import org.bitmap.comnhalam.form.ShippingForm;
import org.bitmap.comnhalam.form.TopSellerForm;
import org.bitmap.comnhalam.model.Order;
import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.OrderRepository;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void addAmount(Long id, Integer quantity, Order order) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(() -> new Exception("Không tìm thấy sản phẩm"));
        Integer newQuantity = product.getQuantity() - quantity;
        Integer newSold = product.getSold() + quantity;
        if(newQuantity < 0) {
            throw new Exception(product.getName() + " đã hết hàng");
        } else {
            product.setQuantity(newQuantity);
            product.setSold(newSold);
            order.addProduct(product, quantity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(User user, List<ProductCartForm> carts, Order order) throws Exception {
        for(ProductCartForm c : carts) {
            addAmount(c.getId(), c.getQuantity(), order);
        }
        user.addOrder(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }

    @Override
    public Order createOrder(ShippingForm shippingForm) {
        return orderRepository.save(new Order(shippingForm));
    }

    @Override
    public List<TopSellerForm> getTopSellers(int limit) {
        String ql = "select new " + TopSellerForm.class.getName() + "(p.user.id, sum(p.sold * p.price)) " +
                "from Product p " +
                "group by p.user.id";
        List<TopSellerForm> topSellers = (List<TopSellerForm>)entityManager.createQuery(ql).setMaxResults(limit).getResultList();
        topSellers.sort((a, b) -> {
            if(a.getAvenue() == b.getAvenue())
                return 0;
            return a.getAvenue() < b.getAvenue() ? 1 : -1;
        });

        for(TopSellerForm seller : topSellers) {
            User user = userRepository.getOne(seller.getId());
            seller.setName(user.getFirstName() + " " + user.getLastName());
            seller.setImg(user.getImg());
        }

        return topSellers;
    }


}
