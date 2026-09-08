package com.mvppropostas.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FirebaseAuthTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;

  @Test
  void healthDoesNotRequireAuthentication() throws Exception {
    mockMvc.perform(get("/api/v1/health")).andExpect(status().isOk());
  }

  @Test
  void publicProposalPdfDoesNotRequireAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/public/proposals/demo-aurora/pdf"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_PDF));
  }

  @Test
  void profileRequiresAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/profile"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Autenticação obrigatória."));
  }

  @Test
  void profileProvisionsNewUserFromFirebaseToken() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/profile")
                .with(
                    jwt()
                        .jwt(
                            token ->
                                token
                                    .subject("firebase-new-user")
                                    .claim("email", "nova@empresa.com")
                                    .claim("name", "Nova Conta"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("nova@empresa.com"))
        .andExpect(jsonPath("$.name").value("Nova Conta"));

    org.assertj.core.api.Assertions.assertThat(
            userRepository.findByFirebaseUid("firebase-new-user"))
        .isPresent();
  }

  @Test
  void profileLinksExistingDemoUserByEmail() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/profile")
                .with(
                    jwt()
                        .jwt(
                            token ->
                                token
                                    .subject("firebase-demo-uid")
                                    .claim("email", "leonardo@empresa.com"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
        .andExpect(jsonPath("$.email").value("leonardo@empresa.com"));
  }

  @Test
  void deleteAccountRemovesProvisionedUser() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/profile")
                .with(
                    jwt()
                        .jwt(
                            token ->
                                token
                                    .subject("firebase-delete-user")
                                    .claim("email", "delete-me@empresa.com")
                                    .claim("name", "Conta a excluir"))))
        .andExpect(status().isOk());

    org.assertj.core.api.Assertions.assertThat(userRepository.findByFirebaseUid("firebase-delete-user"))
        .isPresent();

    mockMvc
        .perform(
            delete("/api/v1/profile")
                .with(
                    jwt()
                        .jwt(
                            token ->
                                token
                                    .subject("firebase-delete-user")
                                    .claim("email", "delete-me@empresa.com"))))
        .andExpect(status().isNoContent());

    org.assertj.core.api.Assertions.assertThat(userRepository.findByFirebaseUid("firebase-delete-user"))
        .isEmpty();
  }
}
