package org.bitmap.comnhalam.controller;

import org.apache.commons.io.FileUtils;
import org.bitmap.comnhalam.form.AddProductForm;
import org.bitmap.comnhalam.form.PrintReportForm;
import org.bitmap.comnhalam.form.TagForm;
import org.bitmap.comnhalam.model.Order;
import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.model.Tag;
import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.OrderRepository;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.bitmap.comnhalam.repository.TagRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.service.FileStorageService;
import org.bitmap.comnhalam.validator.AddProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AddProductValidator addProductValidator;

    @Autowired
    private FileStorageService fileStorageService;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null)
            return;
        else {
            if (target.getClass() == AddProductForm.class)
                dataBinder.setValidator(addProductValidator);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(ProductController.class);


    @RequestMapping(value = "/products")
    public String show(Model model,
                       HttpServletRequest httpServletRequest,
                       @Param("product") String product,
                       @Param("tag") String tag,
                       @Param("page") String page,
                       @Param("pageSize") String pageSize,
                       @Param("location") String location,
                       @Param("id") String id) {
        PagedListHolder<?> pages = (PagedListHolder<?>) httpServletRequest.getSession().getAttribute("Products");
        List<Product> products = null;
        String url_base = null;


        int pageSizeInt = 9;
        int pageInt = 1;

        try {
            pageSizeInt = Integer.parseUnsignedInt(pageSize);
            pageInt = Integer.parseUnsignedInt(page);
        } catch (Exception e) {

        }

//        if (page == null)
//            page = 1;
//        if (pageSize == null)
//            pageSize = 9;

        if (id != null && (product == null || product.isEmpty()) && (tag == null || tag.isEmpty()) && location == null) {
            int x = Integer.parseInt(id);
            User user = userRepository.findById(Long.parseLong(id)).get();
            products = productRepository.findByUserAndEnabled(user, true);
            url_base = String.format("/products?id=%s&", id);
        } else if (id != null && (product == null || product.isEmpty()) && tag != null) {
            User user = userRepository.findById(Long.parseLong(id)).get();
            if (!tag.equals("all")) {
                Set<Tag> tags = tagRepository.findByName(tag);
                products = productRepository.findByTagsAndEnabledAndUser(tags, true, user);
            } else {
                products = productRepository.findByUserAndEnabled(user, true);
            }
            url_base = String.format("/products?id=%s&tag=%s&", id, tag);
        } else if (id != null && product != null && (tag == null || tag.isEmpty())) {
            User user = userRepository.findById(Long.parseLong(id)).get();
            products = productRepository.findByNameContainingAndEnabledAndUser(product, true, user);
            url_base = String.format("/products?id=%s&product=%s&", id, product);
        } else
            //Find all product
            if ((product == null || product.isEmpty()) && (tag == null || tag.isEmpty()) && location == null) {
                products = productRepository.findAllByEnabled(true);
                url_base = "/products?";
            }
            //Find by Tag
            else if ((product == null || product.isEmpty()) && tag != null) {
                if (!tag.equals("all")) {
                    Set<Tag> tags = tagRepository.findByName(tag);
                    products = productRepository.findByTagsAndEnabled(tags, true);
                } else {
                    products = productRepository.findAllByEnabled(true);
                }
                url_base = String.format("/products?tag=%s&", tag);
            }
            //Find by products
            else if (product != null && (tag == null || tag.isEmpty())) {
                products = productRepository.findByNameContainingAndEnabled(product, true);
                url_base = String.format("/products?product=%s&", product);
            }
            else if ((product == null || product.isEmpty()) && location != null && tag == null){
                String tmp = location.toUpperCase();
                List<Product> temp = productRepository.findAllByEnabled(true);
                products = new ArrayList<>();
                for (int i=0; i<temp.size(); i++){
                    String address = temp.get(i).getUser().getAddress();
                    int count = 0;
                    int begin_index = 0;
                    int last_index = 0;
                    for (int j = address.length()-1; j>0; j--){
                        if (String.valueOf(address.charAt(j)).equals(",")){
                            count++;
                            if (count == 1){
                                last_index = j;
                            }
                            if (count == 2){
                                begin_index = j;
                                break;
                            }
                        }
                    }
                    String user_location = address.substring(begin_index+1, last_index).toUpperCase();
                    if (user_location.equals(tmp)){
                        products.add(temp.get(i));
                    }
                }
                url_base = String.format("/products?location=%s&", tmp);

            }
            //Find by products and tag
            else if (product != null && tag != null){
                if (!tag.equals("all")) {
                    Set<Tag> tags = tagRepository.findByName(tag);
                    products = productRepository.findByTagsAndNameContainingAndEnabled(tags, product, true);
                } else {
                    products = productRepository.findByNameContainingAndEnabled(product, true);
                }
                url_base = String.format("/products?product=%s&tag=%s&", product, tag);
            }

        if (location == null)
            location = "None";

        if (pages == null) {
            pages = new PagedListHolder<>(products);
            pages.setPageSize(pageSizeInt);
        } else {
            final int goToPage = pageInt - 1;
            pages = new PagedListHolder<>(products);
            pages.setPageSize(pageSizeInt);
            if (goToPage <= pages.getPageCount() && goToPage >= 0) {
                pages.setPage(goToPage);
            }
        }

        httpServletRequest.getSession().setAttribute("Products", pages);
        int cur = pages.getPage() + 1;
        int begin = Math.max(1, cur - products.size());
        int end = Math.min(begin + 4, pages.getPageCount());
        int total = pages.getPageCount();

        int f_index = begin;
        int l_index = end;

        if (cur > end) {
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
        model.addAttribute("baseUrl", url_base);
        model.addAttribute("products_page", pages);
        model.addAttribute("show", pageSizeInt);
        model.addAttribute("product", product);
        model.addAttribute("location", location);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", tagRepository.findAll());


        return "index/products";
    }

    @RequestMapping("/search_product")
    public String searchProduct(Model model,
                                @Param("product") String product,
                                @Param("tag") String tag) {

        List<Product> products = null;

        if (!product.isEmpty()) {
            if (tag.equals("all")) {
                products = productRepository.findTop10ByNameContainingAndEnabled(product, true);
            } else {
                Set<Tag> tags = tagRepository.findByName(tag);
                products = productRepository.findTop10ByTagsAndNameContainingAndEnabled(tags, product, true);
            }
        }

        model.addAttribute("resultSearchProducts", products);
        return "index/resultSearchProduct";
    }

    @RequestMapping("/get_tag")
    @ResponseBody
    public List<TagForm> getTag() {
        List<TagForm> tags = new ArrayList<>();
        List<Tag> tagList = tagRepository.findAll();
        for (Tag t : tagList) {
            tags.add(new TagForm(t.getName()));
        }
        return tags;
    }

    @RequestMapping("/seller/add_product")
    public String addProductShow(Model model,
                                 @Param("message") String message) {


        AddProductForm addProductForm = new AddProductForm();

        model.addAttribute("add_product", addProductForm);
        model.addAttribute("message", message);
        model.addAttribute("tags", tagRepository.findAll());


        return "seller_add_product";
    }

    @RequestMapping(value = "/seller/add_product", method = RequestMethod.POST)
    public String addProduct(Model model,
                             RedirectAttributes redirectAttributes,
                             @ModelAttribute("add_product") @Validated AddProductForm addProductForm,
                             Authentication authentication,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("add_product", addProductForm);
            return "seller_add_product";
        }

        redirectAttributes.addAttribute("message", "Add product success");

        User user = userRepository.findByEmail(authentication.getName());

        Product p = new Product(addProductForm.getName(), Integer.parseInt(addProductForm.getQuantity()), Double.parseDouble(addProductForm.getPrice()), "");
        p.setUser(user);

        productRepository.save(p);

        String path = saveImage(p.getId() + "-500", addProductForm.getImg500().split(",")[1]);
        p.setImg(path);

        for (String t : addProductForm.getTags()) {
            Tag tag = tagRepository.findWithName(t);
            if (tag != null) {
                p.addTag(tag);
            }
        }

        p.setDate(new Date());
        p.setDescription(addProductForm.getDescription());

        productRepository.save(p);
        return "redirect:/seller/add_product";
    }

    @RequestMapping("/seller/modify_product")
    public String modifyProductMainShow(@Param("message") String message) {
        return "redirect:/seller/modify_product/1";
    }

    @RequestMapping("/seller/modify_product/{id}")
    public String modifyProductshow(Model model,
                                    RedirectAttributes redirectAttributes,
                                    @Param("message") String message,
                                    @PathVariable("id") Long id) {
        Product product = null;
        try {
            product = productRepository.findById(id).orElseThrow(() -> new Exception("Product Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/seller/modify_product/";
        }

        Set<String> tags = new HashSet<>();
        for (Tag t : product.getTags()) {
            tags.add(t.getName());
        }

        AddProductForm addProductForm = new AddProductForm(product.getId(), product.getName(), String.valueOf(product.getQuantity()), String.valueOf(product.getPrice()),
                product.getImg(), tags, product.getDescription());

        model.addAttribute("modify_product", addProductForm);
        model.addAttribute("message", message);
        model.addAttribute("tags", tagRepository.findAll());
        return "seller_modify_product";
    }

    @RequestMapping(value = "/seller/modifyProduct", method = RequestMethod.POST)
    public String modifyProduct(Model model,
                                RedirectAttributes redirectAttributes,
                                @ModelAttribute("modify_product") @Validated AddProductForm addProductForm,
                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            try {
                Product product = productRepository.findById(addProductForm.getId()).orElseThrow(() -> new Exception("Không tìm thấy sản phẩm"));
                addProductForm.setImg(product.getImg());
                model.addAttribute("modify_product", addProductForm);
                model.addAttribute("tags", tagRepository.findAll());

            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addAttribute("message", e.getMessage());
                return "redirect:/seller/modify_product/";
            }
            return "seller_modify_product";
        }
        redirectAttributes.addAttribute("message", "Sửa sản phẩm thành công!");

        Product p = null;
        try {
            p = productRepository.findById(addProductForm.getId()).orElseThrow(() -> new Exception("Không tìm thấy sản phẩm"));
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/seller/modify_product/";
        }


        if (!addProductForm.getImg500().isEmpty()) {
            String path = saveImage(p.getId() + "-500", addProductForm.getImg500().split(",")[1]);
            p.setImg(path);
        }
        p.setDate(new Date());
        p.setName(addProductForm.getName());
        p.setPrice(Double.parseDouble(addProductForm.getPrice()));
        p.setQuantity(Integer.parseInt(addProductForm.getQuantity()));
        Set<Tag> tags = new HashSet<>();
        for (String t : addProductForm.getTags()) {
            Tag tag = tagRepository.findWithName(t);
            if (tag != null) {
                tags.add(tag);
            }
        }
        p.setTags(tags);
        p.setDescription(addProductForm.getDescription());

        productRepository.save(p);
        return "redirect:/seller/modify_product/" + p.getId();
    }

    private String saveImage(String name, String encodedString) {
        String fileName = fileStorageService.storeFile(name, encodedString);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/product-img/")
                .path(fileName)
                .toUriString();
        return fileDownloadUri;
    }

    @GetMapping("/product-img/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @RequestMapping(value = "seller/products")
    public String show(Model model,
                       Authentication authentication,
                       HttpServletRequest httpServletRequest,
                       @Param("product") String product,
                       @Param("page") String page,
                       @Param("pageSize") String pageSize) {
        PagedListHolder<?> pages = (PagedListHolder<?>) httpServletRequest.getSession().getAttribute("Products");
        List<Product> products = null;
        String url_base = null;


        int pageSizeInt = 9;
        int pageInt = 1;

        try {
            pageSizeInt = Integer.parseUnsignedInt(pageSize);
            pageInt = Integer.parseUnsignedInt(page);
        } catch (Exception e) {

        }

        User user = userRepository.findByEmail(authentication.getName());
        products = productRepository.findByUserAndEnabled(user, true);
        url_base = String.format("/seller/products?");

        if (pages == null) {
            pages = new PagedListHolder<>(products);
            pages.setPageSize(pageSizeInt);
        } else {
            final int goToPage = pageInt - 1;
            pages = new PagedListHolder<>(products);
            pages.setPageSize(pageSizeInt);
            if (goToPage <= pages.getPageCount() && goToPage >= 0) {
                pages.setPage(goToPage);
            }
        }

        httpServletRequest.getSession().setAttribute("Products", pages);
        int cur = pages.getPage() + 1;
        int begin = Math.max(1, cur - products.size());
        int end = Math.min(begin + 4, pages.getPageCount());
        int total = pages.getPageCount();

        int f_index = begin;
        int l_index = end;

        if (cur > end) {
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
        model.addAttribute("baseUrl", url_base);
        model.addAttribute("products_page", pages);
        model.addAttribute("show", pageSizeInt);
        model.addAttribute("product", product);


        return "seller_show_products";
    }

    @RequestMapping("/seller/dis-product/{id}")
    @ResponseBody
    public String disProduct(@PathVariable("id") String id, Authentication authentication) {
        Long nId = null;

        try {
            nId = Long.parseUnsignedLong(id);
        } catch (NumberFormatException e) {
            return "fail";
        }

        User user = userRepository.findByEmail(authentication.getName());

        if (user == null) {
            return "fail";
        }

        try {
            Product p = productRepository.findById(nId).orElseThrow(() -> new Exception("Không tìm thấy sản phẩm"));
            if (p.getUser().equals(user)) {
                p.setEnabled(false);
                productRepository.save(p);
                return "success";
            } else
                return "fail";
        } catch (Exception e) {
            return "fail";
        }
    }

    @RequestMapping(value = "/seller/report/{from}/{to}/{title}")
    public String printViewDay(Model model,
                               Authentication authentication,
                               @PathVariable("from") String from,
                               @PathVariable("to") String to,
                               @PathVariable("title") String title) {

        DateFormat dateFormat =
                new SimpleDateFormat("dd-MM-yyyy");
        Date fromdate = null;
        Date todate = null;

        try {
            fromdate = dateFormat.parse(from);
            todate = dateFormat.parse(to);
            todate.setHours(23);
            todate.setMinutes(59);
            todate.setSeconds(59);
        } catch (ParseException e) {
            return "";
        }

        User user = userRepository.findByEmail(authentication.getName());
        List<PrintReportForm> printReportForm = new ArrayList<>();
        List<Order> orders = orderRepository.findOrdersByCreateOnBetweenAndUser(fromdate, todate, user);
        List<String> dates = new ArrayList<>();

        int id = 1;
        for (int i = 0; i < orders.size(); i++) {
            String date = dateFormat.format(orders.get(i).getCreateOn());

            if (!dates.contains(date)) {
                dates.add(date);

                PrintReportForm reportForm = new PrintReportForm();
                reportForm.setId(id);
                reportForm.setDate(date);
                reportForm.setMoney(reportForm.getMoney() + orders.get(i).getTotal());
                reportForm.setNumber_order(reportForm.getNumber_order() + 1);
                printReportForm.add(reportForm);
                id++;
            } else {
                PrintReportForm reportForm = null;
                for (int j = 0; j < printReportForm.size(); j++) {
                    String report_date = printReportForm.get(j).getDate();
                    if (date.equals(report_date)) {
                        reportForm = printReportForm.get(j);
                        break;
                    }
                }
                if (reportForm != null) {
                    reportForm.setMoney(reportForm.getMoney() + orders.get(i).getTotal());
                    reportForm.setNumber_order(reportForm.getNumber_order() + 1);
                }
            }
        }
        for (int i = 0; i < printReportForm.size(); i++) {
            PrintReportForm reportForm = printReportForm.get(i);
            printReportForm.get(i).setTotal(reportForm.getMoney() * reportForm.getPercent());
        }

        model.addAttribute("reportform", printReportForm);
        model.addAttribute("title", title.toUpperCase());
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "seller_report";
    }
}