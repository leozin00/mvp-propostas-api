package com.mvppropostas.config;

import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "app.keep-demo-seed", havingValue = "false")
@RequiredArgsConstructor
@Slf4j
public class DemoSeedCleanup implements ApplicationRunner {

  private static final UUID DEMO_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void run(ApplicationArguments args) {
    jdbcTemplate.update(
        "DELETE FROM proposal_items WHERE proposal_id IN (SELECT id FROM proposals WHERE user_id = ?)",
        DEMO_USER_ID);
    jdbcTemplate.update("DELETE FROM proposals WHERE user_id = ?", DEMO_USER_ID);
    jdbcTemplate.update("DELETE FROM clients WHERE user_id = ?", DEMO_USER_ID);
    jdbcTemplate.update("DELETE FROM subscriptions WHERE user_id = ?", DEMO_USER_ID);
    int users = jdbcTemplate.update("DELETE FROM users WHERE id = ?", DEMO_USER_ID);
    log.info("Seed de demonstração removido (keep-demo-seed=false). users={}", users);
  }
}
