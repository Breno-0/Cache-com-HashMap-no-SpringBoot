package school.sptech.Cache_com_HashMap_no_SpringBoot.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.Cache_com_HashMap_no_SpringBoot.dto.ConteudoRequest;
import school.sptech.Cache_com_HashMap_no_SpringBoot.dto.ConteudoResponse;
import school.sptech.Cache_com_HashMap_no_SpringBoot.entidade.Conteudo;
import school.sptech.Cache_com_HashMap_no_SpringBoot.mapper.ConteudoMapper;
import school.sptech.Cache_com_HashMap_no_SpringBoot.service.ConteudoService;

import java.net.URI;

@RestController
@RequestMapping("/conteudos")
public class ConteudoController {

    private final ConteudoService service;
    private final ConteudoMapper mapper;

    public ConteudoController(ConteudoService service, ConteudoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponse> buscar(@PathVariable Long id) {
        ConteudoService.ResultadoBusca resultado = service.buscar(id);
        return ResponseEntity.ok()
                .header("X-Cache", resultado.acerto() ? "ACERTO" : "FALHA")
                .body(mapper.paraResponse(resultado.conteudo()));
    }

    @PostMapping
    public ResponseEntity<ConteudoResponse> cadastrar(@RequestBody ConteudoRequest request) {
        Conteudo salvo = service.cadastrar(mapper.paraEntidade(request));
        return ResponseEntity.created(URI.create("/conteudos/" + salvo.getId()))
                .body(mapper.paraResponse(salvo));
    }

    @PutMapping("/{id}")
    public ConteudoResponse atualizar(@PathVariable Long id, @RequestBody ConteudoRequest request) {
        return mapper.paraResponse(service.atualizar(id, mapper.paraEntidade(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}