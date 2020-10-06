package org.bitmap.comnhalam.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;


@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Date createOn = new Date();

    private String review;

    private Double star;

    private Comment() {    }

    public Comment(User user, Product product, String review, Double star) {
        this.user = user;
        this.product = product;
        this.review = review;
        this.star = star;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public long getStar() {
        return Math.round( star);
    }

    public long getNonStar() {
        return 5 - Math.round( star);
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public String getUserName(){
        return this.user.getLastName() + " " +  this.user.getFirstName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(user, comment.user) &&
                Objects.equals(product, comment.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, product, review, star);
    }


}
