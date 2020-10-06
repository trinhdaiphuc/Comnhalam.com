package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.form.ShippingForm;
import org.bitmap.comnhalam.form.ProductCartForm;
import org.bitmap.comnhalam.form.ShippingForm;
import org.bitmap.comnhalam.form.UserRegisterForm;
import org.bitmap.comnhalam.model.Order;
import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.service.ProductService;
import org.bitmap.comnhalam.validator.ShippingValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShippingValidator shippingValidator;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null)
            return;
        else {
            if (target.getClass() == ShippingForm.class)
                dataBinder.setValidator(shippingValidator);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(CartController.class);

    @RequestMapping("/add_product_to_cart/{id:\\d+}")
    public String addAProductToCartWidget(@PathVariable("id") Long id) {
        return "redirect:/add_product_to_cart/" + id + "/1";
    }

    @RequestMapping("/add_product_to_cart/{id:\\d+}/{quantity:\\d+}")
    public String addProductToCartWidget(Model model,
                                         HttpServletRequest request,
                                         @PathVariable("id") Long id,
                                         @PathVariable("quantity") Integer quantity) {

        Product product = null;

        try {
            product = productRepository.findById(id).orElseThrow(() -> new Exception("Product not found"));
            HttpSession session = request.getSession();
            List<ProductCartForm> carts = (List<ProductCartForm>) session.getAttribute("carts");
            if (carts == null) {
                carts = new ArrayList<>();
            }
            boolean had = false;
            for (ProductCartForm p : carts) {
                System.out.println("p.getId(): " + p.getId());
                System.out.println("product.getId(): " + product.getId());
                if (p.getId().equals(product.getId())) {
                    p.setQuantity(p.getQuantity() + quantity);
                    had = true;
                    break;
                }
            }
            if (!had) {
                carts.add(new ProductCartForm(product, quantity));
            }

            Integer totalQty = 0;
            Double totalPrice = 0d;
            for (ProductCartForm p : carts) {
                totalQty += p.getQuantity();
                totalPrice += p.getPrice() * p.getQuantity();
            }
            ProductCartForm totalCarts = new ProductCartForm();
            totalCarts.setQuantity(totalQty);
            totalCarts.setPrice(totalPrice);
            session.setAttribute("carts", carts);
            session.setAttribute("totalCarts", totalCarts);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
        }

        return "index/cart_widget";
    }

    @RequestMapping("/remove_product_to_cart/{id:\\d+}")
    public String removeProductToCart(Model model,
                                      HttpServletRequest request,
                                      @PathVariable("id") Long id) {
        Product product = null;

        try {
            product = productRepository.findById(id).orElseThrow(() -> new Exception("Product not found"));
            HttpSession session = request.getSession();
            List<ProductCartForm> carts = (List<ProductCartForm>) session.getAttribute("carts");

            if (carts != null) {
                ProductCartForm had = null;
                for (ProductCartForm p : carts) {
                    if (p.getId().equals(product.getId())) {
                        had = p;
                        break;
                    }
                }
                if (had != null)
                    carts.remove(had);
                Integer totalQty = 0;
                Double totalPrice = 0d;
                for (ProductCartForm p : carts) {
                    totalQty += p.getQuantity();
                    totalPrice += p.getPrice() * p.getQuantity();
                }
                ProductCartForm totalCarts = new ProductCartForm();
                totalCarts.setQuantity(totalQty);
                totalCarts.setPrice(totalPrice);
                session.setAttribute("carts", carts);
                session.setAttribute("totalCarts", totalCarts);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
        }
        return "index/cart_widget";
    }

    @RequestMapping("/view_cart")
    public String viewCart(Model model) {

        return "index/viewCart";
    }

    @RequestMapping(value = "/modify_cart", method = RequestMethod.POST)
    @ResponseBody
    public String modifyCart(Model model,
                             HttpServletRequest request,
                             @RequestBody List<ProductCartForm> carts) {
        HttpSession session = request.getSession();
        List<ProductCartForm> cartsSession = (List<ProductCartForm>) session.getAttribute("carts");
        List<ProductCartForm> tmp = new ArrayList<>();
        if (cartsSession == null || cartsSession.isEmpty()) {
            return "Bạn chưa chọm mua bất cứ sản phẩm nào";
        } else {
            for (ProductCartForm c : cartsSession) {
                boolean have = false;
                for (ProductCartForm cartTmp : carts) {
                    if (c.getId() == cartTmp.getId()) {
                        c.setQuantity(cartTmp.getQuantity());
                        have = true;
                        break;
                    }
                }
                if (!have) {
                    tmp.add(c);
                }
            }
            cartsSession.removeAll(tmp);
            Integer totalQty = 0;
            Double totalPrice = 0d;
            for (ProductCartForm p : cartsSession) {
                totalQty += p.getQuantity();
                totalPrice += p.getPrice() * p.getQuantity();
            }
            ProductCartForm totalCarts = new ProductCartForm();
            totalCarts.setQuantity(totalQty);
            totalCarts.setPrice(totalPrice);
            session.setAttribute("carts", cartsSession);
            session.setAttribute("totalCarts", totalCarts);
        }
        return "success";
    }

    @RequestMapping("/shipment_details")
    public String shipmentDetails(Model model,
                           Authentication authentication,
                           HttpServletRequest request) {
        List<ProductCartForm> carts = (List<ProductCartForm>) request.getSession().getAttribute("carts");
        if (carts == null || carts.isEmpty()) {
            model.addAttribute("error", "Bạn chưa chọm mua bất cứ sản phẩm nào");
            return "index/viewCart";
        } else {
            User user = userRepository.findByEmail(authentication.getName());
            model.addAttribute("shippingForm", new ShippingForm(user));
        }
        return "index/shipmentDetails";
    }

    @RequestMapping(value = "/shipment_details", method = RequestMethod.POST)
    public String shipmentPost(Model model,
                               HttpServletRequest request,
                               @Validated ShippingForm shippingForm,
                               BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            Map<String, Object> errors = bindingResult.getModel();
            model.addAttribute("shippingForm", shippingForm);
            return "index/shipmentDetails";
        }
        HttpSession session = request.getSession();
        session.setAttribute("shippingForm", shippingForm);
        return "redirect:/checkout";
    }

    @RequestMapping("/checkout")
    public String checkout(Model model,
                           HttpServletRequest request,
                           Authentication authentication) {
        List<ProductCartForm> carts = (List<ProductCartForm>) request.getSession().getAttribute("carts");
        ShippingForm shippingForm = (ShippingForm)request.getSession().getAttribute("shippingForm");
        if(carts == null || carts.isEmpty()) {
            model.addAttribute("error", "Bạn chưa chọm mua bất cứ sản phẩm nào");
            return "index/viewCart";
        }
        User user = userRepository.findByEmail(authentication.getName());
        if(shippingForm == null) {
            return "redirect:/shipment_details";
        }
        return "index/checkout";
    }

    @RequestMapping("/checkout/finish")
    public String checkoutFinish(HttpServletRequest request,
                                 Authentication authentication,
                                 Model model) {
        List<ProductCartForm> carts = (List<ProductCartForm>) request.getSession().getAttribute("carts");
        ShippingForm shippingForm = (ShippingForm)request.getSession().getAttribute("shippingForm");
        if(carts == null || carts.isEmpty()) {
            model.addAttribute("error", "Bạn chưa chọm mua bất cứ sản phẩm nào");
            return "index/viewCart";
        }

        if(shippingForm == null) {
            User user = userRepository.findByEmail(authentication.getName());
            shippingForm = new ShippingForm(user);
            request.getSession().setAttribute("shippingForm", shippingForm);
        }

        User user = userRepository.findByEmail(authentication.getName());
        Order order = productService.createOrder(shippingForm);

        try {
            productService.createOrder(user, carts, order);
            request.getSession().setAttribute("carts", null);
            request.getSession().setAttribute("totalCarts", null);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            productService.deleteOrder(order);
            return "index/viewCart";
        }
        return "index/orderSuccess";
    }
}
