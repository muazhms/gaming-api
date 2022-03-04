package com.hms.gamingapi.dao.custom.impl;

import com.hms.gamingapi.dao.custom.GameRepositoryCustom;
import com.hms.gamingapi.model.Game;
import com.hms.gamingapi.model.PageApiResponse;
import com.hms.gamingapi.model.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

public class GameRepositoryCustomImpl implements GameRepositoryCustom {
    @Autowired
    private ReactiveMongoOperations operations;

    @Value("${game.file.path}")
    private String fileUploadPath;

    @Value("${game.poster.path}")
    private String posterUploadPath;

    @Value("${game.download.url}")
    private String gameDownloadUrl;

    @Override
    public Mono<PageApiResponse> search(int pageNo, int pageSize, String sortBy, SearchRequest request) {
        Criteria criteria = Criteria.where("status").is(request.getStatus())
                .and("online").is(request.isOnline())
                .and("downloadable").is(request.isDownloadable());
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
                .map(game -> {
                    game.setPoster(posterUploadPath.concat(game.getPoster()));
                    if (game.isOnline()) {
                        game.setGameFile(fileUploadPath.concat(game.getGameFile()));
                    }
                    if (game.isDownloadable()) {
                        game.setDownloadableFile(gameDownloadUrl.concat(game.getDownloadableFile()));
                    }
                    return game;
                })
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

    @Override
    public Mono<PageApiResponse> adminSearch(int pageNo, int pageSize, String sortBy) {
        pageNo -= 1;
        Query query = new Query();
        query.skip(pageNo * pageSize);
        query.limit(pageSize);
        query.with(Sort.by(sortBy).descending());

        Query paginationQuery = new Query();
        int finalPageNo = pageNo + 1;

        return this.operations.find(query, Game.class)
                .map(game -> {
                    game.setPoster(posterUploadPath.concat(game.getPoster()));
                    if (game.isOnline()) {
                        game.setGameFile(fileUploadPath.concat(game.getGameFile()));
                    }
                    if (game.isDownloadable()) {
                        game.setDownloadableFile(gameDownloadUrl.concat(game.getDownloadableFile()));
                    }
                    return game;
                })
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
