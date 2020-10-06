package org.bitmap.comnhalam.service.Impl;

import org.bitmap.comnhalam.captcha.CaptchaSetting;
import org.bitmap.comnhalam.captcha.GoogleResponse;
import org.bitmap.comnhalam.error.ReCaptchaInvalidException;
import org.bitmap.comnhalam.error.ReCaptchaUnavailableException;
import org.bitmap.comnhalam.service.CaptchaService;
import org.bitmap.comnhalam.service.ReCaptchaAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    @Autowired
    private CaptchaSetting captchaSetting;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    private ReCaptchaAttemptService reCaptchaAttemptService;

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Override
    public void processResponse(String response) {
        if (reCaptchaAttemptService.isBlocked(getClientIP())) {
            throw new ReCaptchaInvalidException("Khách hàng đã vượt tối đa số lần không thành công");
        }

        if (!responseSanityCheck(response)) {
            throw new ReCaptchaInvalidException("Trả về chứ kí tự không hợp lệ");
        }

        final URI verifyUri = URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s", getReCaptchaSecret(), response, getClientIP()));
        try {
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

            if (!googleResponse.isSuccess()) {
                if (googleResponse.hasClientError()) {
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new ReCaptchaInvalidException("reCaptcha không được xác thực thành công");
            }
        } catch (RestClientException rce) {
            throw new ReCaptchaUnavailableException("Đăng kí không khả dụng tại thời điểm này.  Xin vui lòng thử lại sau.", rce);
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    @Override
    public String getReCaptchaSite() {
        return captchaSetting.getSite();
    }

    @Override
    public String getReCaptchaSecret() {
        return captchaSetting.getSecret();
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
