package com.GrupoIoT.iot_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeituraResponseDTO {

  private Long id;
  private Double valor;
  private LocalDateTime dataHora;
  private Boolean alerta;


  private Long sensorId;
  private String sensorNome;
  private String unidadeMedida;


  private Long dispositivoId;
  private String dispositivoNome;
}
