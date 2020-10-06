package org.bitmap.comnhalam.form;

public class CommentForm {
    private String review;
    private Double star;
    private Long productId;

    public CommentForm(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Double getStar() {
        return star;
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public CommentForm(String review, Double star) {
        this.review = review;
        this.star = star;
    }

    public CommentForm() {
        this.star=0d;
    }
}
