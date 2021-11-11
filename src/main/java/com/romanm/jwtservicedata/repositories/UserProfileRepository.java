package com.romanm.jwtservicedata.repositories;

import com.romanm.jwtservicedata.models.UserProfile;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, String> {
     Mono<UserProfile> findUserProfileByUserId(String userId);
     Flux<UserProfile> findUserProfilesByUserIdIn(List<String> userIds);

     @Query(value="{userId:'?0'}")
     Mono<Void> deleteByUserId(String userId);

}
