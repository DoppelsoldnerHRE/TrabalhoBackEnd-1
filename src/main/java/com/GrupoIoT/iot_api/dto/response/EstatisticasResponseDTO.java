package com.GrupoIoT.iot_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasResponseDTO {

  private Long sensorId;
  private String sensorNome;
  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;

  // Estatísticas
  private Long totalLeituras;
  private Long totalAlertas;
  private Double valorMedio;
  private Double valorMinimo;
  private Double valorMaximo;
  private String unidadeMedida;
}
