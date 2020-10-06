package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.bitmap.comnhalam.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
public class HomeController {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagRepository tagRepository;


    @RequestMapping("/")
    public String home(Model model) {
        Date date = new Date();

        List<Product> resultTop6=productRepository.findTop6OrderByDate();
        model.addAttribute("top6Product", resultTop6);

        List<Product> resultRandom=productRepository.findAllByEnabled(true);
        List<Product> result = new ArrayList<>();
        if(resultRandom.size()>12 && resultRandom.size()<=20) {
            Random random = new Random();
            List<Integer> index=new ArrayList<>();
            int i=0;
            while (i<resultRandom.size()-12) {
                int tmp=random.nextInt(resultRandom.size());
                if (!index.contains(tmp)) {
                    index.add(tmp);
                    i++;
                }
            }
            for(int j=0;j<resultRandom.size();j++)
                if (!index.contains(j))
                    result.add(resultRandom.get(j));
        }
        else if (resultRandom.size()>20)
        {
            Random random = new Random();
            List<Integer> index=new ArrayList<>();
            int i=0;
            while (i<12) {
                int tmp=random.nextInt(resultRandom.size());
                if (!index.contains(tmp)) {
                    index.add(tmp);
                    i++;
                }
            }
            for(int j=0;j<12;j++)
                result.add(resultRandom.get(index.get(j)));
        }
        else
        {
            result=resultRandom;
        }

        model.addAttribute("randomProduct",result);
        model.addAttribute("tags", tagRepository.findAll());

        return "index/index";
    }


}
