package com.opt.mobipag.data;

public class Line {
    private int id;
    private String linecode;
    private String name;
    private String pathcode;

    public Line(int id, String descritor, String name, String pathcode) {
        setId(id);
        setDescritor(descritor);
        setName(name);
        setPathcode(pathcode);
    }

    public String getDescritor() {
        return linecode;
    }

    void setDescritor(String descritor) {
        this.linecode = descritor;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getPathcode() {
        return pathcode;
    }

    void setPathcode(String pathcode) {
        this.pathcode = pathcode;
    }
}