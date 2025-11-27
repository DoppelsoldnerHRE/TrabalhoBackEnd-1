package com.GrupoIoT.iot_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorRequestDTO {

  @NotBlank(message = "Nome é obrigatório")
  @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
  private String nome;

  @NotBlank(message = "Tipo do sensor é obrigatório")
  @Size(max = 50, message = "Tipo do sensor deve ter no máximo 50 caracteres")
  private String tipoSensor;

  @NotBlank(message = "Unidade de medida é obrigatória")
  @Size(max = 20, message = "Unidade de medida deve ter no máximo 20 caracteres")
  private String unidadeMedida;

  private Double limiteMinimo;

  private Double limiteMaximo;

  private Boolean ativo;

  @NotNull(message = "ID do dispositivo é obrigatório")
  private Long dispositivoId;
}
