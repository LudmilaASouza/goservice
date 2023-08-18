package com.soulcode.goserviceapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller //Controle das rota
@RequestMapping(value = {"/", "/home"}) //Endereçamento das rotas
public class HomeController {

    @GetMapping
    public String home(){
        return "home";
    }
}


