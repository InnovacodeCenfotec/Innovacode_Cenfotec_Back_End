package com.project.demo.rest.auth;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.demo.logic.entity.auth.AuthenticationService;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.auth.OAuth2AuthenticationService;
import com.project.demo.logic.entity.cloudinary.Image;
import com.project.demo.logic.entity.cloudinary.ImageRepository;
import com.project.demo.logic.entity.cloudinary.ImageService;
import com.project.demo.logic.entity.emailSender.EmailServiceJava;
import com.project.demo.logic.entity.resetPassword.ResetPasswordRequest;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Map;
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

    private final Cloudinary cloudinary;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;


    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthRestController(JwtService jwtService, AuthenticationService authenticationService, Cloudinary cloudinary ) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.cloudinary = cloudinary;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody User user) {
        User authenticatedUser = authenticationService.authenticate(user);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        Optional<User> foundedUser = userRepository.findByEmail(user.getEmail());

        foundedUser.ifPresent(loginResponse::setAuthUser);

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



    @PostMapping("/saveImage/{userId}")
    public ResponseEntity<String> addImagen(@RequestParam("file") MultipartFile file, @PathVariable Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) uploadResult.get("url");
        String imageName = (String) uploadResult.get("public_id");
        Image imagen = new Image();
        imagen.setUrl(imageUrl);
        imagen.setName(imageName);
        imagen.setUser(user);
        imagen.setSaveUrl("https://05b2-2800-860-7193-2e2-ad48-a1fa-d7a-a142.ngrok-free.app/"+"auth/saveImage/"+userId);
        imageRepository.save(imagen);


        String redirectScript = "<html><head><script type=\"text/javascript\">window.top.location.href = 'http://localhost:4200/app/galery';</script></head><body></body></html>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return new ResponseEntity<>(redirectScript, headers, HttpStatus.OK);
    }


    @GetMapping("/imagetoken/{id}")
    public String getImageToken(@PathVariable Long id){
        Optional<Image> image = imageRepository.findById(id);
        String jwtImageToken = jwtService.generateImageToken(image.orElse(null));
        return jwtImageToken;
    }


//    @PostMapping("/googleLogin/{idToken}")
//    public ResponseEntity<LoginResponse> login(@PathVariable String idToken) {
//        try {
//            OAuth2User oAuth2User = oauth2AuthenticationService.verifyToken(idToken);
//
//            if (oAuth2User == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            }
//
//            // Extraer el email del usuario autenticado
//            String email = oAuth2User.getAttribute("email");
//            Optional<User> userOptional = userRepository.findByEmail(email);
//
//            User user;
//            if (userOptional.isPresent()) {
//                user = userOptional.get();
//            } else {
//                // Registrar el usuario si no existe
//                user = new User();
//                user.setEmail(email);
//                user.setRole(roleRepository.findByName(RoleEnum.USER).orElseThrow());
//                user = userRepository.save(user);
//            }
//
//            // Generar el JWT para el usuario
//            String jwtToken = jwtService.generateToken(user);
//
//            // Preparar la respuesta
//            LoginResponse loginResponse = new LoginResponse();
//            loginResponse.setToken(jwtToken);
//            loginResponse.setExpiresIn(jwtService.getExpirationTime());
//            loginResponse.setAuthUser(user);
//
//            return ResponseEntity.ok(loginResponse);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

}