package org.bitmap.comnhalam.form;

import org.bitmap.comnhalam.model.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

public class AddProductForm {
    private Long id;
    private String name;
    private String quantity;
    private String price;
    private String img;
    private Set<String> tags = new HashSet<>();
    private String description;
    private MultipartFile multipartFile;
    private String img500;


    public AddProductForm() {
    }

    public AddProductForm(Long id, String name, String quantity, String price, String img, Set<String> tags, String description) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.img = img;
        this.tags = tags;
        this.description = description;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getImg500() {
        return img500;
    }

    public void setImg500(String img500) {
        this.img500 = img500;
    }
}
