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
        // 1. Usuario Administrador (Gestión total)
        if (!usuarioRepository.existsByNombreDeUsuario("admin")) {
            Rol adminRol = rolRepository.findByNombreRol("ADMIN").orElse(null);
            Set<Rol> roles = new HashSet<>();
            if (adminRol != null) roles.add(adminRol);

            Usuario admin = Usuario.builder()
                    .nombreDeUsuario("admin")
                    .email("admin@zentry.com")
                    .claveDeUsuario(passwordEncoder.encode("admin2026"))
                    .bloqueado(false)
                    .habilitado(true)
                    .roles(roles)
                    .build();
            usuarioRepository.save(admin);
        }

        // 2. Usuario Anfitrión (Organizador de eventos)
        if (!usuarioRepository.existsByNombreDeUsuario("organizador")) {
            Rol anfitrionRol = rolRepository.findByNombreRol("ANFITRION").orElse(null);
            Set<Rol> roles = new HashSet<>();
            if (anfitrionRol != null) roles.add(anfitrionRol);

            Usuario anfitrion = Usuario.builder()
                    .nombreDeUsuario("organizador")
                    .email("anfitrion@zentry.com")
                    .claveDeUsuario(passwordEncoder.encode("eventos2026"))
                    .bloqueado(false)
                    .habilitado(true)
                    .roles(roles)
                    .build();
            usuarioRepository.save(anfitrion);
        }

        // 3. Usuario Staff (Encargado de escanear QR / Control de acceso)
        if (!usuarioRepository.existsByNombreDeUsuario("staff")) {
            Rol staffRol = rolRepository.findByNombreRol("STAFF").orElse(null);
            Set<Rol> roles = new HashSet<>();
            if (staffRol != null) roles.add(staffRol);

            Usuario staff = Usuario.builder()
                    .nombreDeUsuario("staff")
                    .email("staff@zentry.com")
                    .claveDeUsuario(passwordEncoder.encode("staff2026"))
                    .bloqueado(false)
                    .habilitado(true)
                    .roles(roles)
                    .build();
            usuarioRepository.save(staff);
        }
    }

    private void imprimirHashesPasswords() {
        System.out.println("=================================================");
        System.out.println("USUARIOS INICIALES CREADOS:");
        System.out.println("Admin: admin@zentry.com / admin2026");
        System.out.println("Anfitrión: anfitrion@zentry.com / eventos2026");
        System.out.println("Staff: staff@zentry.com / staff2026");
        System.out.println("=================================================");
    }
}
