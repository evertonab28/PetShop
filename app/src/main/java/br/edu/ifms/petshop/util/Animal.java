package br.edu.ifms.petshop.util;

import java.io.Serializable;

public class Animal implements Serializable {
    private Integer id;
    private String nome;
    private Integer idade;
    private Double peso;
    private String sexo;
    private Raca raca;
    private String porte;
    private Cliente proprietario;
    private String observacao;
    private String url;
    //private int foto;

    public Animal(Integer id, String nome, Integer idade, Double peso, String sexo, Raca raca, String porte, Cliente proprietario, String observacao, String url) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.peso = peso;
        this.sexo = sexo;
        this.raca = raca;
        this.porte = porte;
        this.proprietario = proprietario;
        this.observacao = observacao;
        this.url = url;
        //this.foto = foto;

    }

    public Animal() {

    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdade() { return idade; }

    public void setIdade(Integer idade) {  this.idade = idade; }

    public Double getPeso() { return peso; }

    public void setPeso(Double peso) { this.peso = peso; }

    public String getSexo() { return sexo; }

    public void setSexo(String sexo) { this.sexo = sexo; }

    public Raca getRaca() { return raca; }

    public void setRaca(Raca raca) {
        this.raca = raca;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public Cliente getProprietario() {
        return proprietario;
    }

    public void setProprietario(Cliente proprietario) {
        this.proprietario = proprietario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
//
//    public int getFoto() { return foto; }
//
//    public void setFoto(int foto) { this.foto = foto; }
}
