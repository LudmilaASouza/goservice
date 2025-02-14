package com.soulcode.goserviceapp.service;

import com.soulcode.goserviceapp.domain.Agendamento;
import com.soulcode.goserviceapp.domain.Cliente;
import com.soulcode.goserviceapp.domain.Prestador;
import com.soulcode.goserviceapp.domain.Servico;
import com.soulcode.goserviceapp.domain.enums.StatusAgendamento;
import com.soulcode.goserviceapp.repository.AgendamentoRepository;
import com.soulcode.goserviceapp.service.exceptions.AgendamentoNaoEncontradoException;
import com.soulcode.goserviceapp.service.exceptions.StatusAgendamentoImutavelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PrestadorService prestadorService;

    public Agendamento findById(Long id){
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        if (agendamento.isPresent()) {
            return agendamento.get();
        } else {
            throw new AgendamentoNaoEncontradoException();
        }
    }

//    #Task
    public List<Agendamento> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Authentication authentication) {
        Prestador prestador = prestadorService.findAuthenticated(authentication);
        if (dataInicio == null || dataFim == null) {
            throw new RuntimeException();
        }
        return agendamentoRepository.findByDataAgendamentoBetween(dataInicio, dataFim, prestador.getId());
    }

    public Agendamento create(Authentication authentication, Long servicoId, Long prestadorId, LocalDate data, LocalTime hora){
        Cliente cliente = clienteService.findAuthenticated(authentication);
        Prestador prestador = prestadorService.findById(prestadorId);
        Servico servico = servicoService.findById(servicoId);
        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setPrestador(prestador);
        agendamento.setData(data);
        agendamento.setHora(hora);
        return agendamentoRepository.save(agendamento);
    }

    @Cacheable(cacheNames = "redisCache")
    public List<Agendamento> findByCliente(Authentication authentication){
        Cliente cliente = clienteService.findAuthenticated(authentication);
        System.err.println("BUSCANDO NO BANCO DE DADOS...");
        return agendamentoRepository.findByClienteEmail(cliente.getEmail());
    }

    @Cacheable(cacheNames = "redisCache")
    public List<Agendamento> findByPrestador(Authentication authentication){
        Prestador prestador = prestadorService.findAuthenticated(authentication);
        System.err.println("BUSCANDO NO BANCO DE DADOS...");
        return agendamentoRepository.findByPrestadorEmail(prestador.getEmail());
    }

    public void cancelAgendaPrestador(Authentication authentication, Long id){
        Prestador prestador = prestadorService.findAuthenticated(authentication);
        Agendamento agendamento = findById(id);
        if(agendamento.getStatusAgendamento().equals(StatusAgendamento.AGUARDANDO_CONFIRMACAO)){
            agendamento.setStatusAgendamento(StatusAgendamento.CANCELADO_PELO_PRESTADOR);
            agendamentoRepository.save(agendamento);
            return;
        }
        throw new StatusAgendamentoImutavelException();
    }

    public void confirmAgendaPrestador(Authentication authentication, Long id){
        Prestador prestador = prestadorService.findAuthenticated(authentication);
        Agendamento agendamento = findById(id);
        if(agendamento.getStatusAgendamento().equals(StatusAgendamento.AGUARDANDO_CONFIRMACAO)){
            agendamento.setStatusAgendamento(StatusAgendamento.CONFIRMADO);
            agendamentoRepository.save(agendamento);
            return;
        }
        throw new StatusAgendamentoImutavelException();
    }

    public void cancelAgendaCliente(Authentication authentication, Long id){
        Cliente cliente = clienteService.findAuthenticated(authentication);
        Agendamento agendamento = findById(id);
        if(agendamento.getStatusAgendamento().equals(StatusAgendamento.AGUARDANDO_CONFIRMACAO)){
            agendamento.setStatusAgendamento(StatusAgendamento.CANCELADO_PELO_CLIENTE);
            agendamentoRepository.save(agendamento);
            return;
        }
        throw new StatusAgendamentoImutavelException();
    }


    public void completeAgenda(Authentication authentication, Long id){
        Cliente cliente = clienteService.findAuthenticated(authentication);
        Agendamento agendamento = findById(id);
        if (agendamento.getStatusAgendamento().equals(StatusAgendamento.CONFIRMADO)){
            agendamento.setStatusAgendamento(StatusAgendamento.CONCLUIDO);
            agendamentoRepository.save(agendamento);
            return;
        }
        throw new StatusAgendamentoImutavelException();
    }

}
