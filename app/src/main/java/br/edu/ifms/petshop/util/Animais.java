package br.edu.ifms.petshop.util;

import java.util.ArrayList;

public class Animais extends ArrayList<Animal> {

    public Animal getById(int id){
        for(Animal animal : this){
            if(animal.getId() == id){
                return animal;
            }
        }
        return null;
    }

}
