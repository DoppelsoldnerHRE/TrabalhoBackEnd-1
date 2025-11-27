package com.GrupoIoT.iot_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeituraRequestDTO {

  @NotNull(message = "Valor é obrigatório")
  private Double valor;

  @NotNull(message = "ID do sensor é obrigatório")
  private Long sensorId;
}
