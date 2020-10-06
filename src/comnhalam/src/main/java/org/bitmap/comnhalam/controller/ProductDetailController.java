package org.bitmap.comnhalam.controller;

import javafx.scene.control.Pagination;
import org.bitmap.comnhalam.form.CommentForm;
import org.bitmap.comnhalam.model.Comment;
import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.model.Tag;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.CommentRepository;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProductDetailController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductDetailController.class);

    @RequestMapping("/productDetail")
    public String productDetail(HttpServletRequest request, Model model,
                                @Param("id") String id, @Param("page") String page) {
        request.getSession().setAttribute("ahihi", "he nho");

        if (id == null) {
            logger.error("productDetail >> id null");
            return "redirect:/";
        }

        Long n = 1L;
        try {
            n = Long.parseLong(id);
        } catch (Exception e) {
            logger.error("productDetail >> id wrong format");
            return "redirect:/";
        }

        try {
            Product product = productRepository.findById(n).orElseThrow(() -> new Exception("Product not found"));
            model.addAttribute("product", product);

            //Lấy phần tử đầu tiên của Set<Tag>
            Tag tag = product.getTags().isEmpty() ? null : (Tag) product.getTags().toArray()[0];
            List<Product> productListByTag = null;
            if (tag != null)
                productListByTag = productRepository.findByTags(tag.getId());

            model.addAttribute("TopBy4Tag", productListByTag);
            model.addAttribute("commentForm", new CommentForm(product.getId()));

            List<Comment> comments = commentRepository.findByProductId(n);
            model.addAttribute("comments", comments);

            //Phân trang các comment
            PagedListHolder<Comment> pages;
            int pageInt = 1;
            int tmp = comments.size() / 4;
            int total = (comments.size() % 4 == 0) ? tmp : tmp + 1;

            if (total < 1) total = 1;

            try {
                pageInt = Integer.parseUnsignedInt(page);
            } catch (Exception e) {

            }

            pages = new PagedListHolder<Comment>();
            pages.setSource(comments);
            pages.setPageSize(4);

            if (page != null) {
                pages.setPage(pageInt - 1);
            }

            request.getSession().setAttribute("Comments", pages);
            model.addAttribute("totalPage", total);
            model.addAttribute("commentPage", pages);
            model.addAttribute("currentPage", pageInt);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("productDetail >> " + e.getMessage());
            return "redirect:/";
        }

        return "index/productDetail";
    }

    @RequestMapping(value = "/user/postcomment", method = RequestMethod.POST)
    @Transactional
    public String postComment(Model model, CommentForm commentForm,
                              Authentication authentication) {
        if (authentication.getAuthorities() == null) {
            return "index/productDetail";
        }
        User user = userRepository.findByEmail(authentication.getName());
        try {
            Product product = productRepository.findById(commentForm.getProductId()).orElseThrow(() -> new Exception("Product not found"));
            if (commentForm.getStar() == 0d || commentForm.getReview().equals("")) {
                model.addAttribute("commentForm", new CommentForm(product.getId()));

                return "redirect:/productDetail/?id=" + commentForm.getProductId() + "&error=" + commentForm.getStar();
            }
            user.addComment(product, commentForm.getReview(), commentForm.getStar());

            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/productDetail/?id=" + commentForm.getProductId() + "#tab2";
    }

}
