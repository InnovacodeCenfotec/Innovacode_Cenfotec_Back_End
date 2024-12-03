package com.project.demo.rest.auth;

import com.project.demo.logic.entity.auth.AuthenticationService;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.auth.OAuth2AuthenticationService;
import com.project.demo.logic.entity.emailSender.EmailServiceJava;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.resetPassword.ResetPasswordRequest;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthRestController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailServiceJava emailService;

    @Autowired
    private OAuth2AuthenticationService oauth2AuthenticationService;


    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthRestController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    /*@PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody User user) {
        User authenticatedUser = authenticationService.authenticate(user);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        Optional<User> foundedUser = userRepository.findByEmail(user.getEmail());

        foundedUser.ifPresent(loginResponse::setAuthUser);

        return ResponseEntity.ok(loginResponse);
    }*/
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody User user, HttpServletRequest request) {
        Optional<User> foundedUser = userRepository.findByEmail(user.getEmail());

        if (foundedUser.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("No se ha encontrado el usuario"  ,
                    HttpStatus.UNAUTHORIZED, request);
        }

        User authenticatedUser = foundedUser.get();

        // Check if the user is disabled
        if (!authenticatedUser.isEnabled()) {
            return new GlobalResponseHandler().handleResponse("Usuario deshabilitado"  ,
                    HttpStatus.FORBIDDEN, request);
        }

        // Proceed with authentication if the user is not disabled
        authenticatedUser = authenticationService.authenticate(user);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setAuthUser(authenticatedUser);

        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);

        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role not found");
        }
        user.setRole(optionalRole.get());
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody User user) {
        String token = userService.createPasswordResetToken(user);
        if (token == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        String resetLink = "Enter in this link to reset your password: " + "http://localhost:4200/reset-password" + " Your token is " + token;
        emailService.sendEmail(user.getEmail(), "Password Reset Request", "To reset your password, click the link below:\n" + resetLink);
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    @PutMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody ResetPasswordRequest request) {
        boolean result = userService.resetPassword(token, request.getNewPassword());
        if (!result) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/googleLogin/{idToken}")
    public ResponseEntity<LoginResponse> login(@PathVariable String idToken) {
        try {
            OAuth2User oAuth2User = oauth2AuthenticationService.verifyToken(idToken);

            if (oAuth2User == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Extraer el email del usuario autenticado
            String email = oAuth2User.getAttribute("email");
            Optional<User> userOptional = userRepository.findByEmail(email);

            User user;
            if (userOptional.isPresent()) {
                user = userOptional.get();
            } else {
                // Registrar el usuario si no existe
                user = new User();
                user.setEmail(email);
                user.setRole(roleRepository.findByName(RoleEnum.USER).orElseThrow());
                user = userRepository.save(user);
            }

            // Generar el JWT para el usuario
            String jwtToken = jwtService.generateToken(user);

            // Preparar la respuesta
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            loginResponse.setAuthUser(user);

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}