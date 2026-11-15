package school.sptech.Cache_com_HashMap_no_SpringBoot.entidade;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Conteudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @Column(length = 4000)
    private String corpo;
    private String autor;
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    void atualizarData() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getCorpo() { return corpo; }
    public void setCorpo(String corpo) { this.corpo = corpo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
