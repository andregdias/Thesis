package com.opt.mobipag.data;

public class Zone {
    private int id;
    private String descritor;

    public Zone(int id, String descritor) {
        setId(id);
        setDescritor(descritor);
    }

    public String getDescritor() {
        return descritor;
    }

    void setDescritor(String descritor) {
        this.descritor = descritor;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}