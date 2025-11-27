package com.GrupoIoT.iot_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

  private Long id;
  private String nome;
  private String email;
  private LocalDateTime dataCriacao;

  // Não retornamos a senha por segurança
  // Não retornamos a lista de dispositivos para evitar recursão infinita
}
