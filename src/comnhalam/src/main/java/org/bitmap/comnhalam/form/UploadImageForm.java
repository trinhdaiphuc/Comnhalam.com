package org.bitmap.comnhalam.form;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class UploadImageForm implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private MultipartFile multipartFile;

    public UploadImageForm() {
    }

    public UploadImageForm(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
