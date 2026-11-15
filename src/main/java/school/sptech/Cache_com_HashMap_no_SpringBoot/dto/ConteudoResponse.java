package school.sptech.Cache_com_HashMap_no_SpringBoot.dto;

import java.time.LocalDateTime;

public record ConteudoResponse(Long id, String titulo, String corpo,
                               String autor, LocalDateTime atualizadoEm) {
}