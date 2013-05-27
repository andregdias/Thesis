package com.opt.mobipag.data;

import java.util.ArrayList;
import java.util.List;

public class Signature extends Ticket {
    private int numvalidacoes;
    private List<Zone> zonas;

    public Signature(int id, String details, double price, int numvalidacoes, int tempoviagem, List<Zone> zonas, ArrayList<Validation> validacoes, String firstval) {
        super(id, details, price, tempoviagem, validacoes, firstval);
        setNumvalidacoes(numvalidacoes);
        setTempoviagem(tempoviagem);
        setZonas(zonas);
    }

    public int getNumvalidacoes() {
        return numvalidacoes;
    }

    public void setNumvalidacoes(int numvalidacoes) {
        this.numvalidacoes = numvalidacoes;
    }

    public List<Zone> getZonas() {
        return zonas;
    }

    void setZonas(List<Zone> zonas) {
        this.zonas = zonas;
    }

    public String getListZonas() {
        String s = "";
        for (Zone z : zonas)
            s += z.getDescritor() + ", ";
        return s.substring(0, s.length() - 2);
    }
}