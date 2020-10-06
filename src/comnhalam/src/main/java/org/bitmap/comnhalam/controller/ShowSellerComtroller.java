package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.model.Comparator.UserNameComparator;
import org.bitmap.comnhalam.model.Comparator.UserStarComparator;
import org.bitmap.comnhalam.model.Role;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.RoleRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ShowSellerComtroller {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @RequestMapping("/show_sellers")
    public String ShowSellers(Model model, HttpServletRequest request, @Param("sort") String sort,
                              @Param("arrow") String arrow, @Param("page") String page) {

        PagedListHolder<User> Pages;

        Set<Role> role_seller = new HashSet<>();
        role_seller.add(roleRepository.findByName("ROLE_SELLER"));

        List<User> users = userRepository.findUserByRoles(role_seller);
        List<User> Sellers=new ArrayList<>();

        //Update Star of each Sellers
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).isEnabled()) {
                users.get(i).calculaStar();
                Sellers.add(users.get(i));
            }
        }

        //Sorting
        String sortBy = "", orderBy = "";
        if (sort == null) {
            Collections.sort(Sellers, new UserNameComparator());
            sortBy = "Name";
            orderBy = "increase";
            if (arrow != null && arrow.equals("decrease")) {
                Collections.reverse(Sellers);
                orderBy = "decrease";
            }
        } else if (sort.equals("Name")) {
            sortBy = "Name";
            orderBy = "increase";
            Collections.sort(Sellers, new UserNameComparator());
            if (arrow != null && arrow.equals("decrease")) {
                Collections.reverse(Sellers);
                orderBy = "decrease";
            }
        } else {
            if (arrow != null) {
                sortBy = "Rating";
                orderBy = "increase";
                Collections.sort(Sellers, new UserStarComparator());
                if (arrow != null && arrow.equals("decrease")) {
                    Collections.reverse(Sellers);
                    orderBy = "decrease";
                }
            }
        }

        //pagination
        int pageInt = 1;
        int pageSize = 6;
        int tmp = Sellers.size() / pageSize;
        int total = (Sellers.size() % pageSize == 0) ? tmp : tmp + 1;

        if (total < 1) total = 1;

        try {
            pageInt = Integer.parseUnsignedInt(page);
        } catch (Exception e) {

        }

        Pages = new PagedListHolder<User>();
        Pages.setSource(Sellers);
        Pages.setPageSize(pageSize);

        if (page != null) {
            Pages.setPage(pageInt - 1);
        }

        model.addAttribute("totalPage", total);
        model.addAttribute("SellerPage", Pages);
        model.addAttribute("currentPage", pageInt);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderBy", orderBy);
        return "/index/show_sellers";
    }

}
