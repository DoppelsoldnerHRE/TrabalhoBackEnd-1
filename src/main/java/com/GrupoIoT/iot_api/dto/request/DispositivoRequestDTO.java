package com.GrupoIoT.iot_api.dto.request;

import com.GrupoIoT.iot_api.model.entity.Dispositivo.StatusDispositivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoRequestDTO {

  @NotBlank(message = "Nome é obrigatório")
  @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
  private String nome;

  @NotBlank(message = "Tipo é obrigatório")
  @Size(max = 100, message = "Tipo deve ter no máximo 100 caracteres")
  private String tipo;

  @NotBlank(message = "Localização é obrigatória")
  @Size(max = 200, message = "Localização deve ter no máximo 200 caracteres")
  private String localizacao;

  private StatusDispositivo status;

  @NotBlank(message = "Endereço MAC é obrigatório")
  @Pattern(
    regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$",
    message = "Endereço MAC inválido. Formato esperado: XX:XX:XX:XX:XX:XX"
  )
  private String enderecoMac;

  @NotNull(message = "ID do usuário é obrigatório")
  private Long usuarioId;
}
