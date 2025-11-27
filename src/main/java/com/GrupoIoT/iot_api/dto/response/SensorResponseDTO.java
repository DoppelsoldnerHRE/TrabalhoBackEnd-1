package com.GrupoIoT.iot_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorResponseDTO {

  private Long id;
  private String nome;
  private String tipoSensor;
  private String unidadeMedida;
  private Double limiteMinimo;
  private Double limiteMaximo;
  private Boolean ativo;
  private LocalDateTime ultimaAtualizacao;

  // Informações do dispositivo
  private Long dispositivoId;
  private String dispositivoNome;

  // Quantidade de leituras (não retorna a lista completa)
  private Integer quantidadeLeituras;
}
