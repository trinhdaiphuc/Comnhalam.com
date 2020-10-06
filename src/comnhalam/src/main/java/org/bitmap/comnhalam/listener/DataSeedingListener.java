package org.bitmap.comnhalam.listener;

import org.bitmap.comnhalam.model.*;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.bitmap.comnhalam.repository.RoleRepository;
import org.bitmap.comnhalam.repository.TagRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeedingListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }

        if (roleRepository.findByName("ROLE_MEMBER") == null) {
            roleRepository.save(new Role("ROLE_MEMBER"));
        }


        if (roleRepository.findByName("ROLE_SELLER") == null) {
            roleRepository.save(new Role("ROLE_SELLER"));
        }


        if(userRepository.findByEmail("admin@gmail.com") == null) {
            User user = new User("Tran", "Phuc", "admin@gmail.com",
                    passwordEncoder.encode("123456"), "0123456789", "TP.HCM");
            user.setEnabled(true);
            user.addRole(roleRepository.findByName("ROLE_ADMIN"));
            user.addRole(roleRepository.findByName("ROLE_MEMBER"));
            user.addRole(roleRepository.findByName("ROLE_SELLER"));
            userRepository.save(user);
        }

        if(userRepository.findByEmail("seller1@gmail.com") == null) {
            for(int i=0; i<5; i++) {
                if (i == 1) {
                    User user = new User("Seller", "" + i, "seller" + i + "@gmail.com",
                            passwordEncoder.encode("123456"), "123456788", "123,abv,Q.Tan Binh,TP.HCM");
                    user.setEnabled(true);
                    user.addRole(roleRepository.findByName("ROLE_MEMBER"));
                    user.addRole(roleRepository.findByName("ROLE_SELLER"));
                    userRepository.save(user);
                } else
                if (i == 2) {
                    User user = new User("Seller", "" + i, "seller" + i + "@gmail.com",
                            passwordEncoder.encode("123456"), "123456788", "123,abv,Q.1,TP.HCM");
                    user.setEnabled(true);
                    user.addRole(roleRepository.findByName("ROLE_MEMBER"));
                    user.addRole(roleRepository.findByName("ROLE_SELLER"));
                    userRepository.save(user);
                } else
                if (i == 3) {
                    User user = new User("Seller", "" + i, "seller" + i + "@gmail.com",
                            passwordEncoder.encode("123456"), "123456788", "123,abv,Q.2,TP.HCM");
                    user.setEnabled(true);
                    user.addRole(roleRepository.findByName("ROLE_MEMBER"));
                    user.addRole(roleRepository.findByName("ROLE_SELLER"));
                    userRepository.save(user);
                } else
                if (i == 4) {
                    User user = new User("Seller", "" + i, "seller" + i + "@gmail.com",
                            passwordEncoder.encode("123456"), "123456788", "123,abv,Q.3,TP.HCM");
                    user.setEnabled(true);
                    user.addRole(roleRepository.findByName("ROLE_MEMBER"));
                    user.addRole(roleRepository.findByName("ROLE_SELLER"));
                    userRepository.save(user);
                } else
                {
                    User user = new User("Seller", "" + i, "seller" + i + "@gmail.com",
                            passwordEncoder.encode("123456"), "123456788", "123,abv,Q.4,TP.HCM");
                    user.setEnabled(true);
                    user.addRole(roleRepository.findByName("ROLE_MEMBER"));
                    user.addRole(roleRepository.findByName("ROLE_SELLER"));
                    userRepository.save(user);
                }
            }
        }

        if(tagRepository.findByName("Phở").isEmpty()) {
            tagRepository.save(new Tag("Phở"));
            tagRepository.save(new Tag("Cơm"));
            tagRepository.save(new Tag("Canh"));
        }


//        User[] sellers = new User[5];
//        for(int i=0; i<5; i++) {
//            sellers[i] = userRepository.findByEmail("seller" + i + "@gmail.com");
//        }
//
//        Random random = new Random();
//        for(int i=0; i < 100; i++) {
//            Product product = new Product("Sản phẩm " + i, 100, 1000d, "/index/img/product0" + (random.nextInt(8) + 1) + ".jpg");
//            product.setDescription(
//                    "It is a long established fact that a reader will be distracted by the " +
//                            "readable content of a page when looking at its layout. " +
//                            "The point of using Lorem Ipsum is that it has a more-or-less normal " +
//                            "distribution of letters, as opposed to using 'Content here, " +
//                            "content here', making it look like readable English." +
//                            " Many desktop publishing packages and web page editors " +
//                            "now use Lorem Ipsum as their default model text, and a search for " +
//                            "'lorem ipsum' will uncover many web sites still in their infancy. " +
//                            "Various versions have evolved over the years, sometimes by accident, " +
//                            "sometimes on purpose (injected humour and the like)."
//            );
//            List<Comment> comments = new ArrayList<>();
//            for (int j=0; j<3; j++){
//                comments.add(new Comment(sellers[i/20],product,"aa", random.nextInt(4) + 0.0));
//            }
//            product.setComments(comments);
//            product.setUser(sellers[i/20]);
//            productRepository.save(product);
//        }

    }
}
