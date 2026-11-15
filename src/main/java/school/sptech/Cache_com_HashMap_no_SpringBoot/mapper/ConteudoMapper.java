package school.sptech.Cache_com_HashMap_no_SpringBoot.mapper;

import org.springframework.stereotype.Component;
import school.sptech.Cache_com_HashMap_no_SpringBoot.dto.ConteudoRequest;
import school.sptech.Cache_com_HashMap_no_SpringBoot.dto.ConteudoResponse;
import school.sptech.Cache_com_HashMap_no_SpringBoot.entidade.Conteudo;

@Component
public class ConteudoMapper {

    public Conteudo paraEntidade(ConteudoRequest request) {
        Conteudo c = new Conteudo();
        c.setTitulo(request.titulo());
        c.setCorpo(request.corpo());
        c.setAutor(request.autor());
        return c;
    }

    public ConteudoResponse paraResponse(Conteudo c) {
        return new ConteudoResponse(c.getId(), c.getTitulo(), c.getCorpo(),
                c.getAutor(), c.getAtualizadoEm());
    }
}