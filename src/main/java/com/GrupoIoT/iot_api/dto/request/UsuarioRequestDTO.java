package com.GrupoIoT.iot_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

  @NotBlank(message = "Nome é obrigatório")
  @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
  private String nome;

  @NotBlank(message = "Email é obrigatório")
  @Email(message = "Email deve ser válido")
  @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
  private String email;

  @NotBlank(message = "Senha é obrigatória")
  @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
  private String senha;
}
