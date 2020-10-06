package org.bitmap.comnhalam.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bitmap.comnhalam.form.ProductCartForm;
import org.bitmap.comnhalam.form.ShippingForm;
import org.bitmap.comnhalam.model.*;
import org.bitmap.comnhalam.repository.*;
import org.bitmap.comnhalam.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Controller
public class InitController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ResourceLoader loader;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderRepository orderRepository;

    public class Info {
        public String title;
        public String info;
        public String img;
    }

    public void aTransaction(List<Product> productList, ShippingForm shippingForm,
                             List<Order> orderList, User user) throws Exception {
        if (productList.size() == 0)
            return;

        //Create some products
        List<Product> productRandom = new ArrayList<>();
        Random random = new Random();

        if (productList.size() < 5)
            productRandom = productList;
        else {
            int size = random.nextInt(6);
            while (size == 0)
                size = random.nextInt(6);

            if (size == productList.size())
                productRandom = productList;
            else {
                while (size > 0) {
                    int tmp = random.nextInt(productList.size());
                    if (!productRandom.contains(productList.get(tmp))) {
                        productRandom.add(productList.get(tmp));
                        size--;
                    }
                }
            }
        }

        List<ProductCartForm> carts = new ArrayList<>();
        //Random quantity of each products
        for (int i = 0; i < productRandom.size(); i++) {
            int quantity;
            if (productRandom.get(i).getQuantity() < 3)
                quantity = productRandom.get(i).getQuantity();
            else {
                quantity = random.nextInt(4);
                while (quantity == 0)
                    quantity = random.nextInt(4);
            }
            ProductCartForm productCartForm = new ProductCartForm(productRandom.get(i), quantity);
            carts.add(productCartForm);
        }
        if (carts.size() == 0)
            return;

        Order order = productService.createOrder(shippingForm);
        orderList.add(order);

        productService.createOrder(user, carts, order);
    }

    public void createAUser(List<User> userList, Integer number) {
        //Create User
        String email = "user" + number.toString() + "@gmail.com";
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User("User", number.toString(), email,
                    passwordEncoder.encode("123456"), "01234567891", "Hà Lội");
            user.setEnabled(true);
            HashSet<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_MEMBER"));
            user.setRoles(roles);
            userRepository.save(user);
        }
        userList.add(user);
    }

    @RequestMapping("/add_transaction")
    @ResponseBody
    public String addTransaction() throws Exception {

        //Create List User
        Random random = new Random();
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < 6; i++)
            createAUser(userList, i);
        User user = userList.get(random.nextInt(6));

        //Create all transaction
        List<Product> productList = productRepository.findAllAvailable();

        if (productList.size() == 0)
            return "Sold out! :) Sorry friend!";
        List<Order> orderList = new ArrayList<>();
        int count=0;

        while (productList.size() != 0 && count<30) {
            ShippingForm shippingForm = new ShippingForm(user);
            aTransaction(productList, shippingForm, orderList, user);
            productList = productRepository.findAllAvailable();
            user = userList.get(random.nextInt(6));
            count++;
        }
        List<Date> dateRandomList=new ArrayList<>();

        for(int i=0;i<orderList.size();i++)
        {
            //Random date order
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, random.nextInt(30 * 24 * 3600) * -1);
            Date dateRandom = calendar.getTime();
            dateRandomList.add(dateRandom);
        }

        Collections.sort(dateRandomList);

        for(int i=0;i<orderList.size();i++) {
            orderList.get(i).setCreateOn(dateRandomList.get(i));
        }
        orderRepository.saveAll(orderList);
        return "Thanks friend!";
    }

    @RequestMapping("/init")
    @ResponseBody
    public String init() {
        Gson gson = new Gson();
        Resource resource = loader.getResource("classpath:data.json");
        BufferedReader br = null;
        Random random = new Random();

        try {
            br = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String json = br.readLine();

            productRepository.deleteAll();

            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_SELLER"));
            List<User> sellers = userRepository.findUserByRoles(roles);

            List<Tag> tags = tagRepository.findAll();

            Type collectionType = new TypeToken<List<Info>>() {
            }.getType();
            Collection<Info> infos = gson.fromJson(json, collectionType);
            int count = 0;
            List<Product> products = new ArrayList<>();
            for (Info i : infos) {
                String img = i.img.replace("300", "500");
                if(isValid(img)) {
                    Product product = new Product(i.title, 100, 10000d, img);
                    product.setDescription(i.info);
                    products.add(product);
                    for (int j = 0; j < random.nextInt(4); j++)
                        product.addTag(tags.get(j));
                    product.setUser(sellers.get(count / 20));
                }
                count++;
            }

            productRepository.saveAll(products);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "init";
    }

    private boolean isValid(String u) {
        try {
            URL url = new URL(u);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            if(code == 404)
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
