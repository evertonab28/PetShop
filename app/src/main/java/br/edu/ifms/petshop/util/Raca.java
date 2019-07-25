package br.edu.ifms.petshop.util;

import java.io.Serializable;

public class Raca implements Serializable {

    private Integer id;
    private String raca;

    public Raca(Integer id, String raca) {
        this.id = id;
        this.raca = raca;
    }

    public Raca() {

    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getRaca() { return raca; }

    public void setRaca(String raca) { this.raca = raca; }

    @Override
    public String toString() {
        return getRaca();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Raca) obj).getId();
    }

}
