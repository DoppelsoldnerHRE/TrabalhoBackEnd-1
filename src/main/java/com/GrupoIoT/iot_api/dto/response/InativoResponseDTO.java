package com.GrupoIoT.iot_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InativoResponseDTO {

  private Long id;
  private String nome;
  private String tipo; // "DISPOSITIVO" ou "SENSOR"
  private LocalDateTime ultimaAtualizacao;
  private Long diasInativo;

  // Informações adicionais
  private String localizacao; // Para dispositivos
  private String tipoSensor; // Para sensores
  private Long dispositivoId; // Para sensores
  private String dispositivoNome; // Para sensores
}
