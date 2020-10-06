package org.bitmap.comnhalam.form;

import org.bitmap.comnhalam.model.User;

import java.io.Serializable;

public class EditProfileForm {
    private Long id;
    private String firstName;
    private String lastName;
    private String numberPhone;
    private String address;
    private String email;

    public EditProfileForm() {
    }

    public EditProfileForm(Long id, String email, String firstName, String lastName, String numberPhone, String address) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.numberPhone = numberPhone;
        this.address = address;
    }

    public EditProfileForm(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.numberPhone = user.getNumberPhone();
        this.address = user.getAddress();
        this.email = user.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
