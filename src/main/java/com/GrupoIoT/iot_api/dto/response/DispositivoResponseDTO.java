package com.GrupoIoT.iot_api.dto.response;

import com.GrupoIoT.iot_api.model.entity.Dispositivo.StatusDispositivo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoResponseDTO {

  private Long id;
  private String nome;
  private String tipo;
  private String localizacao;
  private StatusDispositivo status;
  private String enderecoMac;
  private LocalDateTime dataCadastro;
  private LocalDateTime ultimaAtualizacao;

  // Informações do usuário proprietário
  private Long usuarioId;
  private String usuarioNome;

  // Quantidade de sensores (não retorna a lista completa)
  private Integer quantidadeSensores;
}
