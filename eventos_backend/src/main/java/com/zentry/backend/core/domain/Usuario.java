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
@Table(name = "usuario")
public class Usuario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "bloqueado", nullable = false)
    private Boolean bloqueado = false;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "habilitado", nullable = false)
    private Boolean habilitado = true;

    @Column(name = "clave_de_usuario", nullable = false, length = 200)
    private String claveDeUsuario;

    @Column(name = "nombre_de_usuario", nullable = false, length = 50)
    private String nombreDeUsuario;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles;
}
