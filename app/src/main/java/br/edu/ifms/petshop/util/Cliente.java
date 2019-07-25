package br.edu.ifms.petshop.util;

import java.io.Serializable;

public class Cliente implements Serializable {
    private int id;
    private String nome;
    private String RG;
    private String CPF;
    private String telefone;
    private String email;
    private String endereco;

    public Cliente(int id, String nome, String RG, String CPF, String telefone, String email, String endereco) {
        this.id = id;
        this.nome = nome;
        this.RG = RG;
        this.CPF = CPF;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
    }

    public Cliente(int id) {
        this.id = id;
    }

    public Cliente() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRG() {
        return RG;
    }

    public void setRG(String RG) {
        this.RG = RG;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return getNome();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Cliente) obj).getId();
    }
}
