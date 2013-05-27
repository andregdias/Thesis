package com.opt.mobipag.data;

import java.util.ArrayList;

public class Ticket {
    private int id;
    private String details;
    private double price;
    private int tempoviagem;
    private ArrayList<Validation> validacoes;
    private String firstval;

    public Ticket(int id, String details, double price, int tempoviagem, ArrayList<Validation> validacoes, String firstval) {
        setId(id);
        setDetails(details);
        setPrice(price);
        setTempoviagem(tempoviagem);
        setValidacoes(validacoes);
        setFirstval(firstval);
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    void setDetails(String details) {
        this.details = details;
    }

    public double getPrice() {
        return price;
    }

    void setPrice(double price) {
        this.price = price;
    }

    public int getTempoviagem() {
        return tempoviagem;
    }

    void setTempoviagem(int tempoviagem) {
        this.tempoviagem = tempoviagem;
    }

    public ArrayList<Validation> getValidacoes() {
        return validacoes;
    }

    void setValidacoes(ArrayList<Validation> validacoes) {
        this.validacoes = validacoes;
    }

    public String getFirstval() {
        return firstval;
    }

    public void setFirstval(String firstval) {
        this.firstval = firstval;
    }
}