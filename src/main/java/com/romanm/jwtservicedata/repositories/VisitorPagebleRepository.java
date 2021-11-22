package com.romanm.jwtservicedata.repositories;

import com.romanm.jwtservicedata.models.Visitor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface VisitorPagebleRepository extends ReactiveSortingRepository<Visitor, String> {
    Flux<Visitor> findVisitorByUserIdOrderByTimestampDesc(String userId, Pageable page);
}
