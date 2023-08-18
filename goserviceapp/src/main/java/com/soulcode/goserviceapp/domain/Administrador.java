package com.soulcode.goserviceapp.domain;

import com.soulcode.goserviceapp.domain.enums.Perfil;
import jakarta.persistence.Entity;

@Entity
public class Administrador extends Usuario {

    public Administrador() {
        super();
        setPerfil(Perfil.ADMIN);
    } //CONSTRUTOR VAZIO, PORÉM SETA AO ENUM PERFIL ADMIN

    public Administrador(Long id, String nome, String email, String senha, Boolean habilitado, Perfil perfil) {
        super(id, nome, email, senha, habilitado, perfil);
    }



}
