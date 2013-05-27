package com.opt.mobipag.data;

public class Validation {
    private int idStop;
    private int idLine;
    private int seqId;
    private String date;

    public Validation(int idStop, int idLine, String date, int seqId) {
        setIdStop(idStop);
        setIdLine(idLine);
        setDate(date);
        setSeqId(seqId);
    }

    public int getIdStop() {
        return idStop;
    }

    void setIdStop(int idStop) {
        this.idStop = idStop;
    }

    public int getIdLine() {
        return idLine;
    }

    void setIdLine(int idLine) {
        this.idLine = idLine;
    }

    public String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    public int getSeqId() {
        return seqId;
    }

    void setSeqId(int seqId) {
        this.seqId = seqId;
    }

}
