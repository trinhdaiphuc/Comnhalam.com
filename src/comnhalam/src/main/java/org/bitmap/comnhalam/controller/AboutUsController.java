package org.bitmap.comnhalam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AboutUsController {

    @RequestMapping("/aboutus")
    private String aboutUs() {
        return "aboutus";
    }

}
