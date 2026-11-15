package school.sptech.Cache_com_HashMap_no_SpringBoot.entidade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ConteudoCache {

    private static final Logger log = LoggerFactory.getLogger(ConteudoCache.class);

    public static class EntradaCache {
        final Conteudo conteudo;
        final Instant armazenadoEm = Instant.now();
        volatile Instant ultimoAcesso = armazenadoEm;

        EntradaCache(Conteudo conteudo) {
            this.conteudo = conteudo;
        }
    }

    public record Estatisticas(int tamanhoAtual, int limite, long acertos,
                               long falhas, long remocoes, double taxaDeAcerto) {
    }

    private final Map<Long, EntradaCache> mapa = new ConcurrentHashMap<>();
    private final int maxItens;
    private final Duration ttl;

    private final AtomicLong acertos = new AtomicLong();
    private final AtomicLong falhas = new AtomicLong();
    private final AtomicLong remocoes = new AtomicLong();

    public ConteudoCache(@Value("${app.cache.max-itens}") int maxItens,
                         @Value("${app.cache.ttl-segundos}") long ttlSegundos) {
        this.maxItens = maxItens;
        this.ttl = Duration.ofSeconds(ttlSegundos);
    }


    public Conteudo obter(Long id) {
        EntradaCache entrada = mapa.get(id);

        if (entrada != null && expirou(entrada)) {
            mapa.remove(id, entrada);
            log.info("Cache EXPIRADO - id={}", id);
            entrada = null;
        }

        if (entrada == null) {
            falhas.incrementAndGet();
            return null;
        }

        entrada.ultimoAcesso = Instant.now();
        acertos.incrementAndGet();
        return entrada.conteudo;
    }

    public synchronized void guardar(Long id, Conteudo conteudo) {
        if (!mapa.containsKey(id) && mapa.size() >= maxItens) {
            mapa.entrySet().stream()
                    .min(Comparator.comparing(e -> e.getValue().ultimoAcesso))
                    .ifPresent(e -> {
                        mapa.remove(e.getKey());
                        remocoes.incrementAndGet();
                        log.info("Cache REMOCAO (limite) - id={}", e.getKey());
                    });
        }
        mapa.put(id, new EntradaCache(conteudo));
    }

    public void invalidar(Long id) {
        if (mapa.remove(id) != null) {
            log.info("Cache INVALIDADO - id={}", id);
        }
    }

    public void limpar() {
        mapa.clear();
        log.info("Cache LIMPO");
    }

    public Estatisticas estatisticas() {
        long total = acertos.get() + falhas.get();
        double taxa = (double) acertos.get() / total;
        return new Estatisticas(mapa.size(), maxItens, acertos.get(),
                falhas.get(), remocoes.get(), taxa);
    }

    private boolean expirou(EntradaCache entrada) {
        return Instant.now().isAfter(entrada.armazenadoEm.plus(ttl));
    }
}