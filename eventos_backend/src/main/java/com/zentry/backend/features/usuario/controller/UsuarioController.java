package com.zentry.backend.features.usuario.controller;

import com.zentry.backend.features.usuario.dto.UsuarioResponse;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.features.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos()
                .stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return UsuarioResponse.fromEntity(usuario);
    }

    @GetMapping("/username/{username}")
    public UsuarioResponse buscarPorUsername(@PathVariable String username) {
        Usuario usuario = usuarioService.buscarPorNombreUsuario(username);
        return UsuarioResponse.fromEntity(usuario);
    }

    @PostMapping
    public UsuarioResponse crear(@Valid @RequestBody Usuario usuario) {
        Usuario usuarioCreado = usuarioService.crear(usuario);
        return UsuarioResponse.fromEntity(usuarioCreado);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.actualizar(id, usuario);
        return UsuarioResponse.fromEntity(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}
