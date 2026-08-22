package com.mvppropostas.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mvppropostas.domain.entity.Client;

public interface ClientRepository extends JpaRepository<Client, UUID> {

  long countByUserId(UUID userId);
}
