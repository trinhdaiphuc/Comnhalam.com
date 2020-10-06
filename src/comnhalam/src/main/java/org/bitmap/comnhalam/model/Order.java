package org.bitmap.comnhalam.model;

import org.bitmap.comnhalam.form.EditProfileForm;
import org.bitmap.comnhalam.form.ShippingForm;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    public static final byte STATE_CANCELLED = 0;
    public static final byte STATE_SUCCESSFUL_DELIVERY = 1;
    public static final byte STATE_RECEIVING= 2;
    public static final byte STATE_DELIVERING= 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double total = 0d;

    private Date createOn = new Date();

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderDetail> orderDetails = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String numberPhone;

    private String address;

    private String email;

    private Byte state = STATE_RECEIVING;

    public Order() {

    }

    public Order(ShippingForm shippingForm) {
        this.name = shippingForm.getFirstName().trim() + " " + shippingForm.getLastName().trim();
        this.numberPhone = shippingForm.getNumberPhone();
        this.address = shippingForm.getAddress();
        this.email = shippingForm.getEmail();
    }

    public void addProduct(Product product, Integer quantity) {
        OrderDetail orderDetail = new OrderDetail(this, product, quantity);
        orderDetails.add(orderDetail);
        product.getOrderDetails().add(orderDetail);
        this.total += orderDetail.getPrice();
    }

    public void removeProduct(Product product) {
        for (OrderDetail d : orderDetails) {
            if (d.getOrder().equals(this) && d.getProduct().equals(product)) {
                orderDetails.remove(d);
                d.getProduct().getOrderDetails().remove(d);
                d.setOrder(null);
                d.setProduct(null);
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStateStr() {
        return getStateStr(state);
    }

    public String getStateStr(Byte state) {
        String stateStr = null;
        switch (state) {
            case STATE_CANCELLED:
                stateStr = "Đã hủy";
                break;
            case STATE_SUCCESSFUL_DELIVERY:
                stateStr = "Giao hàng thành công";
                break;
            case STATE_RECEIVING:
                stateStr = "Đã nhận đơn hàng";
                break;
            case STATE_DELIVERING:
                stateStr = "Đang giao";
                break;
        }
        return stateStr;
    }


    public byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }


}
