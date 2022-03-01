package com.hms.gamingapi.dao.custom.impl;

import com.hms.gamingapi.dao.custom.GameRepositoryCustom;
import com.hms.gamingapi.model.Game;
import com.hms.gamingapi.model.PageApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

public class GameRepositoryCustomImpl implements GameRepositoryCustom {
    @Autowired
    private ReactiveMongoOperations operations;

    @Override
    public Mono<PageApiResponse> searchPublished(int pageNo, int pageSize, String sortBy) {
        Criteria criteria = Criteria.where("status").is("PUBLISHED");
        pageNo -= 1;
        Query query = new Query();
        query.addCriteria(criteria);
        query.skip(pageNo * pageSize);
        query.limit(pageSize);
        query.with(Sort.by(sortBy).descending());

        Query paginationQuery = new Query();
        paginationQuery.addCriteria(criteria);
        int finalPageNo = pageNo + 1;

        return this.operations.find(query, Game.class)
                .collectList()
                .flatMap(games -> this.operations.count(paginationQuery, Game.class)
                .map(count -> {
                    PageApiResponse pageApiResponse = new PageApiResponse(games);
                    pageApiResponse.setPageNo(finalPageNo);
                    pageApiResponse.setTotalElements(count);
                    pageApiResponse.setTotalPages((int) ((count + pageSize - 1) / pageSize));
                    return pageApiResponse;
                }));
    }
}
