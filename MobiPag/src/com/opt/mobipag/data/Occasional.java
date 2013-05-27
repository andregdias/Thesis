package com.opt.mobipag.data;

import java.util.ArrayList;

public class Occasional extends Ticket {
    private int numzonas;

    public Occasional(int id, String details, double price, int numzonas, int tempoviagem, ArrayList<Validation> validacoes, String firstval) {
        super(id, details, price, tempoviagem, validacoes, firstval);
        setNumzonas(numzonas);
        setTempoviagem(tempoviagem);
        setValidacoes(validacoes);
    }

    public int getNumzonas() {
        return numzonas;
    }

    void setNumzonas(int numzonas) {
        this.numzonas = numzonas;
    }
}