package com.GrupoIoT.iot_api.controller;

import com.GrupoIoT.iot_api.model.entity.Usuario;
import com.GrupoIoT.iot_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

  private final UsuarioRepository usuarioRepository;

  @PostMapping("/usuario")
  public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
    Usuario saved = usuarioRepository.save(usuario);
    return ResponseEntity.ok(saved);
  }

  @GetMapping("/usuarios")
  public ResponseEntity<List<Usuario>> listarUsuarios() {
    return ResponseEntity.ok(usuarioRepository.findAll());
  }
}
