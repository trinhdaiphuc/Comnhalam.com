package org.bitmap.comnhalam.form;

public class TopSellerForm {
    private Long id;
    private String name;
    private Double avenue;
    private String img;

    public TopSellerForm() {
    }

    public TopSellerForm(Long id, Double avenue) {
        this.id = id;
        this.avenue = avenue;
    }

    public TopSellerForm(Long id, String name, Double avenue) {
        this.id = id;
        this.name = name;
        this.avenue = avenue;
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

    public Double getAvenue() {
        return avenue;
    }

    public void setAvenue(Double avenue) {
        this.avenue = avenue;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
