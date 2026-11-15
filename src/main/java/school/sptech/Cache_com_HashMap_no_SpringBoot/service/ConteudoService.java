package school.sptech.Cache_com_HashMap_no_SpringBoot.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.Cache_com_HashMap_no_SpringBoot.entidade.Conteudo;
import school.sptech.Cache_com_HashMap_no_SpringBoot.entidade.ConteudoCache;
import school.sptech.Cache_com_HashMap_no_SpringBoot.repository.ConteudoRepository;

@Service
public class ConteudoService {

    private static final Logger log = LoggerFactory.getLogger(ConteudoService.class);

    public record ResultadoBusca(Conteudo conteudo, boolean acerto) {
    }

    private final ConteudoRepository repository;
    private final ConteudoCache cache;
    private final long atrasoMs;

    public ConteudoService(ConteudoRepository repository, ConteudoCache cache,
                           @Value("${app.fonte.atraso-ms}") long atrasoMs) {
        this.repository = repository;
        this.cache = cache;
        this.atrasoMs = atrasoMs;
    }

    public ResultadoBusca buscar(Long id) {
        long inicio = System.currentTimeMillis();

        Conteudo emCache = cache.obter(id);
        if (emCache != null) {
            log.info("Cache ACERTO - id={} - {} ms", id, System.currentTimeMillis() - inicio);
            return new ResultadoBusca(emCache, true);
        }

        Conteudo carregado = buscarNaFonte(id);
        cache.guardar(id, carregado);
        log.info("Cache FALHA  - id={} - {} ms", id, System.currentTimeMillis() - inicio);
        return new ResultadoBusca(carregado, false);
    }

    public Conteudo cadastrar(Conteudo conteudo) {
        return repository.save(conteudo);
    }

    public Conteudo atualizar(Long id, Conteudo dados) {
        Conteudo existente = repository.findById(id).orElseThrow(() -> naoEncontrado(id));
        existente.setTitulo(dados.getTitulo());
        existente.setCorpo(dados.getCorpo());
        existente.setAutor(dados.getAutor());
        Conteudo salvo = repository.save(existente);
        cache.invalidar(id);
        return salvo;
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw naoEncontrado(id);
        }
        repository.deleteById(id);
        cache.invalidar(id);
    }

    public ConteudoCache.Estatisticas estatisticas() {
        return cache.estatisticas();
    }

    public void limparCache() {
        cache.limpar();
    }

    private Conteudo buscarNaFonte(Long id) {
        try {
            Thread.sleep(atrasoMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return repository.findById(id).orElseThrow(() -> naoEncontrado(id));
    }

    private ResponseStatusException naoEncontrado(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Conteúdo não encontrado: id=" + id);
    }
}