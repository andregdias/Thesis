package com.opt.mobipag.data;

import android.util.Pair;

public class Stop {
    private int id;
    private String nome;
    private String codsms;
    private String operador;
    private Pair<Double, Double> cordenadas;


    public Stop(int id, String nome, String codsms, String operador, Double coordx, Double coordy) {
        setId(id);
        setNome(nome);
        setCodsms(codsms);
        setOperador(operador);
        setCordenadas(coordx, coordy);
    }

    public String getNome() {
        return nome;
    }

    void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodsms() {
        return codsms;
    }

    void setCodsms(String codsms) {
        this.codsms = codsms;
    }

    public Pair<Double, Double> getCordenadas() {
        return cordenadas;
    }

    void setCordenadas(Double x, Double y) {
        this.cordenadas = new Pair<Double, Double>(x, y);
    }

    public String getOperador() {
        return operador;
    }

    void setOperador(String operador) {
        this.operador = operador;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}