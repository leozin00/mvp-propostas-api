package com.mvppropostas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.dto.request.ProfileBusinessRequest;
import com.mvppropostas.dto.request.ProfilePersonalRequest;
import com.mvppropostas.dto.response.UserProfileResponse;
import com.mvppropostas.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @GetMapping
  UserProfileResponse getProfile() {
    return profileService.getProfile();
  }

  @PutMapping("/personal")
  UserProfileResponse updatePersonal(@Valid @RequestBody ProfilePersonalRequest request) {
    return profileService.updatePersonal(request);
  }

  @PutMapping("/business")
  UserProfileResponse updateBusiness(@Valid @RequestBody ProfileBusinessRequest request) {
    return profileService.updateBusiness(request);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteAccount() {
    profileService.deleteAccount();
  }
}
