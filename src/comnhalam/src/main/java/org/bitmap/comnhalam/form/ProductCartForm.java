package org.bitmap.comnhalam.form;

import org.bitmap.comnhalam.model.Product;

public class ProductCartForm {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private String img;

    public ProductCartForm() {
    }

    public ProductCartForm(Product product, Integer quantity) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = quantity;
        this.img = product.getImg();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
