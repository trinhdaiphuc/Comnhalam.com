package org.bitmap.comnhalam.listener;

import org.bitmap.comnhalam.model.Notification;
import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.repository.NotificationRepository;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.bitmap.comnhalam.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ProductListener {
    private static final Logger LOG = LoggerFactory.getLogger(ProductListener.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int EXPIRATION = 60 * 24;

    @Scheduled(cron = "0 0 12 * * ?")
//    @Scheduled(fixedRate = 5000)
    public void create() {
        List<Product> products = productRepository.findAllByEnabled(true);
        for(Product p : products) {
            if(!isValid(p.getDate())) {
                p.setEnabled(false);
            }
        }
        productRepository.saveAll(products);
    }

    private boolean isValid(Date createOn) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createOn);
        cal.add(Calendar.MINUTE, EXPIRATION);
        Date date = new Date(cal.getTime().getTime());
        Date now = new Date();

        return date.compareTo(now) >= 0;
    }
}
