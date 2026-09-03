package com.mvppropostas.dto.response;

import java.util.List;

public record AdminAccountsPageResponse(
    List<AdminAccountResponse> items, int page, int size, int totalPages, long totalElements) {}
