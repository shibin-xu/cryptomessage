package org.cloudguard.commons;

import java.util.Objects;

public class RegisterRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;

    public RegisterRequest(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisterRequest)) return false;
        RegisterRequest that = (RegisterRequest) o;
        return getEmail().equals(that.getEmail()) &&
                getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getPassword().equals(that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getFirstName(), getLastName(), getPassword());
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
