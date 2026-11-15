package school.sptech.Cache_com_HashMap_no_SpringBoot.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.Cache_com_HashMap_no_SpringBoot.entidade.ConteudoCache;
import school.sptech.Cache_com_HashMap_no_SpringBoot.service.ConteudoService;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final ConteudoService service;

    public CacheController(ConteudoService service) {
        this.service = service;
    }

    @GetMapping("/estatisticas")
    public ConteudoCache.Estatisticas estatisticas() {
        return service.estatisticas();
    }

    @DeleteMapping
    public ResponseEntity<Void> limpar() {
        service.limparCache();
        return ResponseEntity.noContent().build();
    }
}