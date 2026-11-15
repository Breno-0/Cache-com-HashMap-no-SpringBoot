package school.sptech.Cache_com_HashMap_no_SpringBoot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.Cache_com_HashMap_no_SpringBoot.entidade.Conteudo;

public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {
}