package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.form.PrintReportForm;
import org.bitmap.comnhalam.model.Order;
import org.bitmap.comnhalam.model.OrderDetail;
import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.repository.OrderDetailRepository;
import org.bitmap.comnhalam.repository.OrderRepository;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.bitmap.comnhalam.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping("/admin")
    public String adminView(Model model) {
        model.addAttribute("countUsers", userRepository.count());

        Long countProducts = 0L;
        Long countSoldProducts = 0L;

        List<Product> products = productRepository.findAll();
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();

        for (Product product : products) {
            countProducts += product.getQuantity();
        }

        for (OrderDetail order : orderDetails) {
            countSoldProducts += order.getQuantity();
        }

        model.addAttribute("countProducts", countProducts);
        model.addAttribute("countSoldProducts", countSoldProducts);
        Date date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        model.addAttribute("countNewUsers", userRepository.findUsersByCreateOnGreaterThan(date).size());
        model.addAttribute("topSellers", productService.getTopSellers(5));

        return "admin/index2";
    }

    @RequestMapping("/admin/number-of-transactions")
    @ResponseBody
    public List<Integer> noTransaction() {
        Date from = new Date();
        from.setHours(0);
        from.setMinutes(0);
        from.setSeconds(0);

        Date to = new Date();
        to.setHours(23);
        to.setMinutes(59);
        to.setSeconds(59);

        from.setDate(from.getDate() - 6);
        to.setDate(to.getDate() - 6);

        List<Integer> list = new ArrayList<>();

        list.add(orderRepository.findOrdersByCreateOnBetween(from, to).size());

        for (int i = 0; i < 6; i++) {
            from.setDate(from.getDate() + 1);
            to.setDate(to.getDate() + 1);
            int size = orderRepository.findOrdersByCreateOnBetween(from, to).size();
            list.add(size);
        }

        return list;
    }

    @RequestMapping("/admin/number-of-transaction/{startDate}/{endDate}")
    @ResponseBody
    public List<Integer> noTransactions(
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate
    ) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date start = null;
        Date end = null;
        List<Integer> list = new ArrayList<>();
        try {
            start = dateFormat.parse(startDate);
            end = dateFormat.parse(endDate);
            end.setDate(end.getDate() + 1);

            while (!start.equals(end)) {
                Date tmp = (Date) start.clone();
                tmp.setHours(23);
                tmp.setMinutes(59);
                tmp.setSeconds(59);
                int size = orderRepository.findOrdersByCreateOnBetween(start, tmp).size();
                list.add(size);
                start.setDate(start.getDate() + 1);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    @RequestMapping("/admin/frame")
    public String frame() {
        return "admin/layout/frame";
    }

    @RequestMapping(value = "/admin/print/{from}/{to}")
    public String printViewDay(Model model,
                               @PathVariable("from") String from,
                               @PathVariable("to") String to) {
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

        List<PrintReportForm> printReportForm = new ArrayList<>();
        List<Order> orders = orderRepository.findOrdersByCreateOnBetween(fromdate, todate);
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
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "admin_report";
    }

}
