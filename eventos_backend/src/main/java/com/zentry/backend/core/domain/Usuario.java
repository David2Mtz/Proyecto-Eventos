package com.zentry.backend.core.domain;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Usuario")
public class Usuario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idUsuario;

    @Column(nullable = false)
    private Boolean bloqueado;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private Boolean habilitado;

    @Column(nullable = false, length = 200)
    private String claveDeUsuario;

    @Column(nullable = false, length = 50)
    private String nombreDeUsuario;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "UsuarioRol",
        joinColumns = @JoinColumn(name = "idUsuario"),
        inverseJoinColumns = @JoinColumn(name = "idRol")
    )
    private Set<Rol> roles;
}
