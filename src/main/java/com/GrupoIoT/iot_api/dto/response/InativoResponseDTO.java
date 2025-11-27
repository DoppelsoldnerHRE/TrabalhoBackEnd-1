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
  private String tipo; 
  private LocalDateTime ultimaAtualizacao;
  private Long diasInativo;


  private String localizacao; 
  private String tipoSensor; 
  private Long dispositivoId; 
  private String dispositivoNome; 
}
