package org.bitmap.comnhalam.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 12)
    private String numberPhone;

    private String address;

    private Date createOn;

    private boolean enabled;

    private String img;

//    private Character gender;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Product> products;

    int star;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Order> orders = new ArrayList<>();


    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Notification> notifications = new ArrayList<>();

    public User() {
    }


    public User(String firstName, String lastName, String email, String password, String numberPhone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.numberPhone = numberPhone;
        this.address = address;
        this.createOn = new Date();
        this.img = "/index/img/avatar.png";
        this.star = 0;
    }

    public void addComment(Product product, String review, Double star) {
        Comment comment = new Comment(this, product, review, star);
        comments.add(comment);
    }

    public void removeComment(Product product) {
        for (Comment comment : comments) {
            if (comment.getUser().equals(this) && comment.getProduct().equals(product)) {
                comments.remove(comment);
                product.getComments().remove(comment);
                comment.setProduct(null);
                comment.setUser(null);
            }
        }
    }

    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public int getStar() {
        return this.star;
    }

    public void calculaStar() {
        if (products.size() == 0) return;
        int count = 0;
        float sum = 0;
        for (int i = 0; i < products.size(); i++) {
            float tmp = products.get(i).getAverageStar();
            if (tmp > 0) {
                sum += tmp;
                count++;
            }
        }
        this.star = Math.round(sum / count);
    }

    public int getNonStar() {
        return 5 - this.getStar();
    }

    public List<Product> get3Product() {
        List<Product> productList = new ArrayList<>();
        if (products.size() <= 3) {
            productList = products;
        } else {
            int i = 0;
            while (i < 3) {
                Random random = new Random();
                int tmp = random.nextInt(products.size());
                if (!productList.contains(tmp)) {
                    productList.add(products.get(tmp));
                    i++;
                }
            }
        }

        return productList;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
