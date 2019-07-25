package br.edu.ifms.petshop.util;

import java.util.ArrayList;

import br.edu.ifms.petshop.util.Cliente;

public class Clientes extends ArrayList<Cliente> {

    public Cliente getById(int id){
        for(Cliente cliente : this){
            if(cliente.getId() == id){
                return cliente;
            }
        }
        return null;
    }

}
