package com.fernando.ikigai;
import android.graphics.Path;


public class Ikigai {

    private String ama;
    private String missao;
    private String paixao;
    private String bom;
    private String pago;
    private String precisa;
    private String profissao;
    private String vocacao;

    private Path amaShape;
    private Path pagoShape;
    private Path bomShape;
    private Path precisaShape;


    public Ikigai(String ama, String missao, String paixao, String bom, String pago, String precisa, String profissao, String vocacao,
                  Path amaShape, Path pagoShape, Path bomShape, Path precisaShape) {
        this.ama = ama;
        this.missao = missao;
        this.paixao = paixao;
        this.bom = bom;
        this.pago = pago;
        this.precisa = precisa;
        this.profissao = profissao;
        this.vocacao = vocacao;
        this.amaShape = amaShape;
        this.pagoShape = pagoShape;
        this.bomShape = bomShape;
        this.precisaShape = precisaShape;
    }

    public Path getAmaShape() {
        return amaShape;
    }

    public void setAmaShape(Path amaShape) {
        this.amaShape = amaShape;
    }

    public Path getPagoShape() {
        return pagoShape;
    }

    public void setPagoShape(Path pagoShape) {
        this.pagoShape = pagoShape;
    }

    public Path getBomShape() {
        return bomShape;
    }

    public void setBomShape(Path bomShape) {
        this.bomShape = bomShape;
    }

    public Path getPrecisaShape() {
        return precisaShape;
    }

    public void setPrecisaShape(Path precisaShape) {
        this.precisaShape = precisaShape;
    }

    public String getAma() {
        return ama;
    }

    public void setAma(String ama) {
        this.ama = ama;
    }

    public String getMissao() {
        return missao;
    }

    public void setMissao(String missao) {
        this.missao = missao;
    }

    public String getPaixao() {
        return paixao;
    }

    public void setPaixao(String paixao) {
        this.paixao = paixao;
    }

    public String getBom() {
        return bom;
    }

    public void setBom(String bom) {
        this.bom = bom;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }

    public String getPrecisa() {
        return precisa;
    }

    public void setPrecisa(String precisa) {
        this.precisa = precisa;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getVocacao() {
        return vocacao;
    }

    public void setVocacao(String vocacao) {
        this.vocacao = vocacao;
    }
}
