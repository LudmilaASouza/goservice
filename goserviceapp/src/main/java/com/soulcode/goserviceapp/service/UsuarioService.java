package com.soulcode.goserviceapp.service;

import com.soulcode.goserviceapp.domain.Administrador;
import com.soulcode.goserviceapp.domain.Cliente;
import com.soulcode.goserviceapp.domain.Prestador;
import com.soulcode.goserviceapp.domain.Usuario;
import com.soulcode.goserviceapp.repository.UsuarioRepository;
import com.soulcode.goserviceapp.service.exceptions.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository; //Injeção de dependências;

    @Autowired
    private PasswordEncoder encoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id){
        Optional<Usuario> result = usuarioRepository.findById(id);
        if(result.isPresent()) {
            return result.get();
        }
        throw new UsuarioNaoEncontradoException();
    }

    public Usuario createUser(Usuario usuario){
        String passwordEncoded = encoder.encode(usuario.getSenha());
        usuario.setSenha(passwordEncoded);
        usuario.setId(null);

        switch (usuario.getPerfil()){
            case ADMIN:
                return createAndSaveAdministrador(usuario);
            case PRESTADOR:
                return createAndSavePrestador(usuario);
            case CLIENTE:
            default:
                return createAndSaveCliente(usuario);
        }
    }

    private Administrador createAndSaveAdministrador(Usuario u) {
        Administrador admin = new Administrador(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getSenha(),
                u.getHabilitado(),
                u.getPerfil());
        return usuarioRepository.save(admin);
    }

    private Prestador createAndSavePrestador(Usuario u){
        Prestador prestador = new Prestador(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getSenha(),
                u.getHabilitado(),
                u.getPerfil());
        return usuarioRepository.save(prestador);
    }

    private Cliente createAndSaveCliente(Usuario u){
        Cliente cliente = new Cliente(u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getSenha(),
                u.getHabilitado(),
                u.getPerfil());
        return usuarioRepository.save(cliente);
    }
}