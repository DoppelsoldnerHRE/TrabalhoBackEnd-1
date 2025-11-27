package com.GrupoIoT.iot_api.controller;

import com.GrupoIoT.iot_api.dto.request.UsuarioRequestDTO;
import com.GrupoIoT.iot_api.dto.response.UsuarioResponseDTO;
import com.GrupoIoT.iot_api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  private final UsuarioService usuarioService;

  @Autowired
  public UsuarioController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  // CREATE
  @PostMapping
  public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO dto) {
    UsuarioResponseDTO created = usuarioService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  // READ ONE
  @GetMapping("/{id}")
  public ResponseEntity<UsuarioResponseDTO> getOne(@PathVariable Long id) {
    UsuarioResponseDTO usuario = usuarioService.getOne(id);
    return ResponseEntity.ok(usuario);
  }

  // READ ALL com paginação e filtro opcional
  @GetMapping
  public ResponseEntity<Page<UsuarioResponseDTO>> getAll(
    @RequestParam(required = false) String nome,
    @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

    Page<UsuarioResponseDTO> usuarios;
    if (nome != null && !nome.isEmpty()) {
      usuarios = usuarioService.getAllByNome(nome, pageable);
    } else {
      usuarios = usuarioService.getAll(pageable);
    }
    return ResponseEntity.ok(usuarios);
  }

  // UPDATE
  @PutMapping("/{id}")
  public ResponseEntity<UsuarioResponseDTO> update(
    @PathVariable Long id,
    @Valid @RequestBody UsuarioRequestDTO dto) {
    UsuarioResponseDTO updated = usuarioService.update(id, dto);
    return ResponseEntity.ok(updated);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    usuarioService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
