package com.zentry.backend.core.config;

import com.zentry.backend.core.domain.Rol;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.features.usuario.repository.RolRepository;
import com.zentry.backend.features.usuario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearRolesIniciales();
        crearUsuariosIniciales();
        imprimirHashesPasswords();
    }

    private void crearRolesIniciales() {
        if (rolRepository.count() == 0) {
            rolRepository.save(Rol.builder()
                    .nombreRol("ADMIN")
                    .descripcionRol("Administrador del sistema")
                    .fecha(LocalDateTime.now())
                    .build());
            rolRepository.save(Rol.builder()
                    .nombreRol("ANFITRION")
                    .descripcionRol("Anfitrión de eventos")
                    .fecha(LocalDateTime.now())
                    .build());
            rolRepository.save(Rol.builder()
                    .nombreRol("STAFF")
                    .descripcionRol("Personal de staff")
                    .fecha(LocalDateTime.now())
                    .build());
            rolRepository.save(Rol.builder()
                    .nombreRol("USER")
                    .descripcionRol("Usuario estándar")
                    .fecha(LocalDateTime.now())
                    .build());
        }
    }

    private void crearUsuariosIniciales() {
        if (!usuarioRepository.existsByNombreDeUsuario("admin")) {
            Rol adminRol = rolRepository.findByNombreRol("ADMIN").orElse(null);
            Set<Rol> roles = new HashSet<>();
            if (adminRol != null) roles.add(adminRol);

            Usuario admin = Usuario.builder()
                    .nombreDeUsuario("admin")
                    .email("admin@zentry.com")
                    .claveDeUsuario(passwordEncoder.encode("ipn2026"))
                    .bloqueado(false)
                    .habilitado(true)
                    .roles(roles)
                    .build();
            usuarioRepository.save(admin);
        }

        if (!usuarioRepository.existsByNombreDeUsuario("alumno")) {
            Rol userRol = rolRepository.findByNombreRol("USER").orElse(null);
            Set<Rol> roles = new HashSet<>();
            if (userRol != null) roles.add(userRol);

            Usuario alumno = Usuario.builder()
                    .nombreDeUsuario("alumno")
                    .email("alumno@zentry.com")
                    .claveDeUsuario(passwordEncoder.encode("escom2026"))
                    .bloqueado(false)
                    .habilitado(true)
                    .roles(roles)
                    .build();
            usuarioRepository.save(alumno);
        }
    }

    private void imprimirHashesPasswords() {
        System.out.println("Generando los Hash para las claves de usuario:");
        System.out.println("Clave = admin : " + passwordEncoder.encode("admin"));
        System.out.println("Clave = limitado : " + passwordEncoder.encode("limitado"));
        System.out.println("Clave = ezja : " + passwordEncoder.encode("ezja"));
        System.out.println("Clave = escom2026 : " + passwordEncoder.encode("escom2026"));
        System.out.println("Clave = ipn2026 : " + passwordEncoder.encode("ipn2026"));
    }
}
