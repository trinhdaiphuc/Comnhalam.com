package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.model.User;
import org.bitmap.comnhalam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExportReportController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/seller/exportReport")
    public String viewExportReport() {
        return "index/exportReport";
    }

}
