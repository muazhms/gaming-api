package com.hms.gamingapi.dao.custom;

import com.hms.gamingapi.model.PageApiResponse;
import reactor.core.publisher.Mono;

public interface GameRepositoryCustom {
    Mono<PageApiResponse> searchPublished(int pageNo, int pageSize, String sortBy);
}
