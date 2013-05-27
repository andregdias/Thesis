package com.opt.mobipag.data;

public class User {
    private int id;
    private String email;
    private String password;
    private String pin;
    private String name;
    private String address;
    private String mobile;
    private int nif;
    private String dob;
    private double balance;
    private int maxamount;
    private String privatekey;
    private String publickey;

    public User(int id, String email, String password, String pin, String name, String address, String mobile, int nif, String dob, double balance, int maxamount, String privatekey, String publickey) {
        setId(id);
        setEmail(email);
        setPassword(password);
        setPin(pin);
        setName(name);
        setAddress(address);
        setMobile(mobile);
        setNif(nif);
        setDob(dob);
        setBalance(balance);
        setMaxamount(maxamount);
        setPrivatekey(privatekey);
        setPublickey(publickey);
    }

    public String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    public String getPin() {
        return pin;
    }

    void setPin(String pin) {
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getNif() {
        return nif;
    }

    void setNif(int nif) {
        this.nif = nif;
    }

    public String getDob() {
        return dob;
    }

    void setDob(String dob) {
        this.dob = dob;
    }

    public double getBalance() {
        return balance;
    }

    void setBalance(double balance) {
        this.balance = balance;
    }

    public int getMaxamount() {
        return maxamount;
    }

    void setMaxamount(int maxamount) {
        this.maxamount = maxamount;
    }

    public String getPrivatekey() {
        return privatekey;
    }

    void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    public String getPublickey() {
        return publickey;
    }

    void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}