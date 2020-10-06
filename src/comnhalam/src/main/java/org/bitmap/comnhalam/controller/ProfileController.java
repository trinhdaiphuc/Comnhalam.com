package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.error.FileStorageException;
import org.bitmap.comnhalam.form.EditProfileForm;
import org.bitmap.comnhalam.form.UploadImageForm;
import org.bitmap.comnhalam.model.Order;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.OrderRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.service.CustomUserDetails;
import org.bitmap.comnhalam.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private final static Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @RequestMapping("/user/edit_profile")
    public String editProfileView(Model model,
                                  @Param("message") String message,
                                  Authentication authentication) {
        User user =  userRepository.findByEmail(authentication.getName());

        EditProfileForm editProfileForm = new EditProfileForm(user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getNumberPhone(), user.getAddress());
        model.addAttribute("editProfileForm", editProfileForm);
        model.addAttribute("image", user.getImg());
        model.addAttribute("uploadImageForm", new UploadImageForm(user.getId()));
        model.addAttribute("message", message);
        return "editProfile";
    }

    @RequestMapping(value = "/edit_profile/uploadImage", method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public String uploadUserImage(RedirectAttributes redirectAttributes, UploadImageForm uploadImageForm) {
        redirectAttributes.addAttribute("message", "Upload success");

        User user =  null;
        try {
            user = userRepository.findById(uploadImageForm.getId()).orElseThrow(() -> new Exception("User Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/edit_profile/"; //Redirect đi đâu đây
        }

        try {
            MultipartFile multipartFile = uploadImageForm.getMultipartFile();

            String fileName = fileStorageService.StoreFile(user.getId(), multipartFile);
            String path = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/product-img/")
                    .path(fileName).toUriString();
            user.setImg(path);
            userRepository.save(user);

            return user.getImg();
        } catch (FileStorageException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    @RequestMapping(value = "/edit_profile/updateInfo", method = RequestMethod.POST)
    @Transactional
    public String updateUserinfo(RedirectAttributes redirectAttributes,
                                 EditProfileForm editProfileForm,
                                 Authentication authentication){
        User user_update = null;
        try{
            user_update = userRepository.findById(editProfileForm.getId()).orElseThrow(() -> new Exception("User not found"));
        } catch (Exception e){}
        Pattern pattern = Pattern.compile("\\d*");
        Matcher matcher = pattern.matcher(editProfileForm.getNumberPhone());
        if (!matcher.matches()) {
            redirectAttributes.addAttribute("message", "Wrong type of number phone");
            return "redirect:/user/edit_profile/";
        }

        user_update.setFirstName(editProfileForm.getFirstName());
        user_update.setLastName(editProfileForm.getLastName());
        user_update.setAddress(editProfileForm.getAddress());
        user_update.setNumberPhone(editProfileForm.getNumberPhone());
        userRepository.save(user_update);

        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        userDetails.setName(user_update.getFirstName() + " " + user_update.getLastName());

        redirectAttributes.addAttribute("message", "Changed information success");
        return "redirect:/user/edit_profile/";
    }

    @RequestMapping("/user/order_list")
    public String orderList(Model model,
                            Authentication authentication,
                            @Param("page") String page) {

        Integer nPage = 1;

        try {
            nPage = Integer.parseUnsignedInt(page);
        } catch (Exception e) {
            nPage = 1;
        }

        User user = userRepository.findByEmail(authentication.getName());
        List<Order> orders = orderRepository.findByUser(user);
        orders.sort((o1, o2) -> {
            if(o1.getId() == o2.getId())
                return 0;
            return o1.getId() > o2.getId() ? -1 : 1;
        });

        PagedListHolder<Order> pages = new PagedListHolder<>();
        pages.setSource(orders);

        pages.setPageSize(10);

        Integer goToPage = nPage - 1;

        if (goToPage <= pages.getPageCount() && goToPage >= 0) {
            pages.setPage(goToPage);
        } else {
            return "redirect:/user/order_list?page=1";
        }

        int cur = pages.getPage() + 1;
        int begin = Math.max(1, cur - orders.size());
        int end = Math.min(begin + 4, pages.getPageCount());
        int total = pages.getPageCount();

        int f_index = begin;
        int l_index = end;

        if (cur > end){
            f_index = cur - 2;
            l_index = cur + 2;
            while (l_index > total)
                l_index--;
        }
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", cur);
        model.addAttribute("findex", f_index);
        model.addAttribute("lindex", l_index);
        model.addAttribute("total", total);
        model.addAttribute("baseUrl", "/user/order_list");
        model.addAttribute("orders", pages);
        return "index/orderList";
    }

    @RequestMapping("/user/order_list/{id}")
    public String orderDetails(Model model,
                               @PathVariable("id") String id,
                               Authentication authentication) {

        Long nId = null;

        try {
            nId = Long.parseUnsignedLong(id);
        } catch (Exception e) {
            model.addAttribute("error", "Bạn Không có hóa đơn này");
            return "index/orderDetails";
        }

        User user = userRepository.findByEmail(authentication.getName());
        List<Order> orders = orderRepository.findByUser(user);

        Order order = null;

        for(Order o : orders) {
            if(o.getId() == nId) {
                order = o;
                break;
            }
        }

        if(order == null) {
            model.addAttribute("error", "Bạn Không có hóa đơn này");
        } else {
            model.addAttribute("order", order);
            model.addAttribute("orderDetails", order.getOrderDetails());
        }

        return "index/orderDetails";
    }


    @RequestMapping("/user/cancel_order/{id}")
    public String cancelOrder(Model model,
                              Authentication authentication,
                              @PathVariable("id") String id) {

        Long nId = null;

        try {
            nId = Long.parseUnsignedLong(id);
        } catch (Exception e) {
            model.addAttribute("error", "Bạn Không có hóa đơn này");
            return "index/orderDetails";
        }

        User user = userRepository.findByEmail(authentication.getName());
        List<Order> orders = orderRepository.findByUser(user);

        Order order = null;

        for(Order o : orders) {
            if(o.getId() == nId) {
                order = o;
                break;
            }
        }

        if(order == null) {
            model.addAttribute("error", "Bạn Không có hóa đơn này");
        } else {
            if(order.getState() == Order.STATE_RECEIVING) {
                order.setState(Order.STATE_CANCELLED);
                orderRepository.save(order);
                return "redirect:/user/order_list";
            } else {
                model.addAttribute("errorOrder", "Bạn không thể hủy đơn đặt hàng này");
                model.addAttribute("order", order);
                model.addAttribute("orderDetails", order.getOrderDetails());
            }
        }

        return "index/orderDetails";
    }
}
