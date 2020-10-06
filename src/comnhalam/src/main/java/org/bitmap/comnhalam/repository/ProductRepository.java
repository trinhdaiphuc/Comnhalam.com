package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.form.TopSellerForm;
import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.model.Tag;
import org.bitmap.comnhalam.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTagsAndNameContaining(Set<Tag> tags, String name);
    List<Product> findByTags(Set<Tag> tags);
    List<Product> findByUser(User user);
    List<Product> findByNameContaining(String name);
    List<Product> findTop10ByNameContaining(String name);
    List<Product> findTop10ByTags(Set<Tag> tags);
    List<Product> findTop10ByTagsAndNameContaining(Set<Tag> tags, String name);
    List<Product> findByUserAndEnabled(User user, boolean isEnabled);
    List<Product> findByTagsAndEnabledAndUser(Set<Tag> tags, boolean isEnabled, User user);
    List<Product> findByNameContainingAndEnabledAndUser(String product, boolean isEnabled, User user);
    Product findByName(String name);
    @Query(value="select * from product p order by p.date  DESC  Limit 0, 6", nativeQuery=true)
    List<Product> findTop6OrderByDate();
    @Query(value="select * from product p order by RAND()  Limit 0, 12", nativeQuery=true)
    List<Product> findByRandomProduct();
    @Query(value = "select * from product p, product_tag pt " +
            "where pt.tag_id = :id AND pt.product_id = p.id order by p.date DESC limit 0,4 ",nativeQuery = true)
    List<Product> findByTags(@Param("id") Long id);
    List<Product> findAllByEnabled(boolean enabled);
    List<Product> findByTagsAndEnabled(Set<Tag> tags, boolean enabled);
    List<Product> findByTagsAndNameContainingAndEnabled(Set<Tag> tags, String product, boolean enabled);
    List<Product> findByNameContainingAndEnabled(String product, boolean enabled);
    List<Product> findTop10ByTagsAndNameContainingAndEnabled(Set<Tag> tags, String name, boolean enabled);
    List<Product> findTop10ByNameContainingAndEnabled(String name, boolean enabled);
    @Query("select p from Product p where p.enabled = true AND p.quantity > 0 ")
    List<Product> findAllAvailable();

}
