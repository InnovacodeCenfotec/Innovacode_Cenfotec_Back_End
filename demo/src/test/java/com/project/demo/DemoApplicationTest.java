package com.project.demo.rest.auth;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.demo.logic.entity.auth.AuthenticationService;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.auth.OAuth2AuthenticationService;
import com.project.demo.logic.entity.cloudinary.Image;
import com.project.demo.logic.entity.cloudinary.ImageRepository;
import com.project.demo.logic.entity.emailSender.EmailServiceJava;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthRestController.class)
public class DemoApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@MockBean
	private RoleRepository roleRepository;

	@MockBean
	private UserService userService;

	@MockBean
	private EmailServiceJava emailService;

	@MockBean
	private OAuth2AuthenticationService oauth2AuthenticationService;

	@MockBean
	private Cloudinary cloudinary;

	@MockBean
	private ImageRepository imageRepository;

	@MockBean
	private AuthenticationService authenticationService;

	@MockBean
	private JwtService jwtService;

	private User testUser;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		testUser = new User();
		testUser.setId(1L);
		testUser.setEmail("test@example.com");
		testUser.setPassword("password123");
	}

	@Test
	public void testAuthenticate() throws Exception {
		when(authenticationService.authenticate(any(User.class))).thenReturn(testUser);
		when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtToken");
		when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"test@example.com\", \"password\": \"password123\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("mockJwtToken"));

		verify(authenticationService, times(1)).authenticate(any(User.class));
		verify(jwtService, times(1)).generateToken(any(User.class));
	}

	@Test
	public void testRegisterUser() throws Exception {
		when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
		when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Optional.of(new Role()));
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"test@example.com\", \"password\": \"password123\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(testUser.getEmail()));

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	public void testForgotPassword() throws Exception {
		when(userService.createPasswordResetToken(any(User.class))).thenReturn("mockToken");

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/forgot-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"test@example.com\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("Password reset link sent to your email"));

		verify(emailService, times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
	}

	@Test
	public void testResetPassword() throws Exception {
		when(userService.resetPassword(eq("mockToken"), eq("newPassword"))).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.put("/auth/reset-password/mockToken")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"newPassword\": \"newPassword\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("Password reset successfully"));

		verify(userService, times(1)).resetPassword("mockToken", "newPassword");
	}
}
