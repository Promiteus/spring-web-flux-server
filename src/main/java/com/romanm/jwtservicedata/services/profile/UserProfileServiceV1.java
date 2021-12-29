package com.romanm.jwtservicedata.services.profile;

import com.romanm.jwtservicedata.constants.MessageConstants;
import com.romanm.jwtservicedata.models.UserProfile;
import com.romanm.jwtservicedata.models.Visitor;
import com.romanm.jwtservicedata.models.responses.profile.ResponseUserProfile;
import com.romanm.jwtservicedata.repositories.UserProfileRepository;
import com.romanm.jwtservicedata.repositories.VisitorRepository;
import com.romanm.jwtservicedata.services.interfaces.UserProfileService;
import com.romanm.jwtservicedata.services.mongodb.MongoVisitorOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.List;

import java.util.stream.Collectors;

@Slf4j
@Service("userProfileServiceV1")
public class UserProfileServiceV1 implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final VisitorRepository visitorRepository;
    private final MongoVisitorOperations mongoVisitorOperations;

    /**
     * @param userProfileRepository UserProfileRepository
     * @param visitorRepository VisitorRepository
     * @param mongoVisitorOperations MongoVisitorOperations
     */
    @Autowired
    public UserProfileServiceV1(UserProfileRepository userProfileRepository, VisitorRepository visitorRepository, MongoVisitorOperations mongoVisitorOperations) {
        this.userProfileRepository = userProfileRepository;
        this.visitorRepository = visitorRepository;
        this.mongoVisitorOperations = mongoVisitorOperations;
    }

    /**
     * Преобразователь рекактивных данных в формат ResponseUserProfile
     * @param sink MonoSink<ResponseUserProfile>
     * @param profile UserProfile
     * @param visitorFlux Flux<Visitor>
     */
    private void toResponseDataFormat(MonoSink<ResponseUserProfile> sink, UserProfile profile, Flux<Visitor> visitorFlux) {
        ResponseUserProfile responseUserProfile = new ResponseUserProfile(profile);

        visitorFlux.collectList().subscribe(visitors -> {
            if ((visitors != null) && (visitors.size() > 0)) {
                List<String> visitorsIds = visitors.stream().map(Visitor::getVisitorUserId).collect(Collectors.toList());
                this.userProfileRepository.findUserProfilesByIdIn(visitorsIds).collectList().subscribe(userProfiles -> {
                    responseUserProfile.getLastVisitors().addAll(userProfiles);
                    //Профиль пользователя с визитерами
                    sink.success(responseUserProfile);
                });
            } else {
                //Профиль пользователя без визитеров
                sink.success(responseUserProfile);
            }
        });
    }

    /**
     * Получить составную сущность профиля пользовтаеля по идентификатору пользователя - ResponseUserProfile
     * @param userId String
     * @return Mono<ResponseUserProfile>
     */
    @Override
    public Mono<ResponseUserProfile> getUserProfile(String userId) {

        if (userId == null) {
            return Mono.create(sink -> {
                sink.success(null);
            });
        }
        //Получить данные профиля текущего пользователя
        Mono<UserProfile> userProfile = this.userProfileRepository.findUserProfileById(userId);
        //Запросить первые 30 посетителей, начиная с текущей даты, для текущего пользователя
        Flux<Visitor> visitorFlux = this.mongoVisitorOperations.findVisitorByUserIdDistinctVisitorUserIdOrderByTimestampDesc(userId, 0, 30);//this.visitorRepository.findVisitorByUserId(userId);

        return Mono.create(sink -> {
             userProfile.doOnSuccess(profile -> {
                this.toResponseDataFormat(sink, profile, visitorFlux);
             }).subscribe();
         });
    }

    /**
     * Сохранить новый профиль или изменить текущий
     * @param userProfile UserProfile
     * @return  Mono<UserProfile>
     */
    @Override
    public Mono<UserProfile> saveOrUpdateUserProfile(UserProfile userProfile) {
        if (userProfile == null) {
            return Mono.create(sink -> {
                sink.success(new UserProfile());
            });
        }

        return this.userProfileRepository.save(userProfile);
    }

    /**
     * Удалить профиль пользователя
     * @param userId String
     * @param soft boolean
     * @return Mono<Boolean>
     */
    @Override
    public Mono<Boolean> removeUserProfile(String userId, boolean soft) {
        if (userId == null) {
            return Mono.create(sink -> {
                sink.success(false);
            });
        }

        return Mono.create(sink -> {
            this.userProfileRepository.removeUserProfileById(userId).doOnSuccess(cons -> {
                sink.success(cons != null);
            }).subscribe();
        });
    }

}
