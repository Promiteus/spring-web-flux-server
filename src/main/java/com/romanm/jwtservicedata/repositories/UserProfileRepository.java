package com.romanm.jwtservicedata.repositories;

import com.romanm.jwtservicedata.models.UserProfile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserProfileRepository extends ReactiveMongoRepository<UserProfile, String> {
     Mono<UserProfile> findUserProfileByUserId(String id);
}
