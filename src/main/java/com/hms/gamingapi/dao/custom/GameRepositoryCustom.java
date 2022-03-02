package com.hms.gamingapi.dao.custom;

import com.hms.gamingapi.model.PageApiResponse;
import com.hms.gamingapi.model.SearchRequest;
import reactor.core.publisher.Mono;

public interface GameRepositoryCustom {
    Mono<PageApiResponse> search(int pageNo, int pageSize, String sortBy, SearchRequest request);
    Mono<PageApiResponse> adminSearch(int pageNo, int pageSize, String sortBy);
}
