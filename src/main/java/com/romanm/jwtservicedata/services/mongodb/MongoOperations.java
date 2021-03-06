package com.romanm.jwtservicedata.services.mongodb;

import com.romanm.jwtservicedata.models.ChatItem;
import com.romanm.jwtservicedata.models.UserProfile;
import com.romanm.jwtservicedata.models.Visitor;
import com.romanm.jwtservicedata.models.requests.SearchBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class MongoOperations {
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    /**
     * Постраничный запрос уникальных посетителей, отсортированный по последней дате посещения
     * @param userId String
     * @param page int
     * @param pageSize int
     * @return Flux<Visitor>
     */
    public Flux<Visitor> findVisitorByUserIdDistinctVisitorUserIdOrderByTimestampDesc(String userId, int page, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Visitor.getVisitorUserIdFieldName()).is(userId));
        query.with(Sort.by(Visitor.getVisitorTimestampFieldName()).descending());
        query.with(PageRequest.of(page, pageSize));
        return reactiveMongoTemplate.find(query, Visitor.class).distinct(Visitor::getVisitorUserId);//.limitRate(pageSize);
    }

    /**
     * Получить список профилей постарнично, кроме профиля с notUserId
     * @param page int
     * @param pageSize int
     * @return Flux<UserProfile>
     */
    public Flux<UserProfile> findAllUserProfilesByPage(int page, int pageSize, String notUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").ne(notUserId));
        query.with(PageRequest.of(page, pageSize));
        return reactiveMongoTemplate.find(query, UserProfile.class);
    }

    /**
     *
     * @param userId String
     * @return Mono<UserProfile>
     */
    public Mono<UserProfile> findUserProfile(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(userId));
        return reactiveMongoTemplate.findOne(query, UserProfile.class);
    }

    /**
     * Получить список профилей постарнично c учетом параметров SearchBody, кроме профиля с notUserId
     * @param page int
     * @param pageSize int
     * @return Flux<UserProfile>
     */
    public Flux<UserProfile> findAllUserProfilesByPage(int page, int pageSize, String notUserId, SearchBody searchBody) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").ne(notUserId));

        if (searchBody.getKids() >= 0) {
            query.addCriteria(Criteria.where("kids").is(searchBody.getKids()));
        }
        if (searchBody.getAgeFrom() < searchBody.getAgeTo()) {
            query.addCriteria(Criteria.where("age").gte(searchBody.getAgeFrom()).lte(searchBody.getAgeTo()));
        }

        Optional.ofNullable(searchBody.getFamilyStatus()).ifPresent(familyStatus -> {
            query.addCriteria(Criteria.where("familyStatus").is(familyStatus));
        });
        Optional.ofNullable(searchBody.getMeetPreferences()).ifPresent(meetPreferences -> {
            query.addCriteria(Criteria.where("meetPreferences").is(meetPreferences));
        });
        Optional.ofNullable(searchBody.getSexOrientation()).ifPresent(sexOrientation -> {
            query.addCriteria(Criteria.where("sexOrientation").is(sexOrientation));
        });
        Optional.ofNullable(searchBody.getSex()).ifPresent(sex -> {
            query.addCriteria(Criteria.where("sex").is(sex));
        });
        Optional.ofNullable(searchBody.getCountry()).ifPresent(country -> {
            query.addCriteria(Criteria.where("country").is(country));
        });
        Optional.ofNullable(searchBody.getRegion()).ifPresent(region -> {
            query.addCriteria(Criteria.where("region").is(region));
        });
        Optional.ofNullable(searchBody.getLocality()).ifPresent(locality -> {
            query.addCriteria(Criteria.where("locality").is(locality));
        });

        query.with(PageRequest.of(page, pageSize));
        return reactiveMongoTemplate.find(query, UserProfile.class);
    }

    /**
     * Получить уникальные чаты по полю fromUserId. Вернет [201,202,203]
     * @param userId String
     * @param page int
     * @param pageSize int
     * @return Flux<ChatItem>
     */
    public Flux<String> findDistinctProfileIdOfChat(String userId, long page, long pageSize) {
        Query query = new Query();
        //log.info("findDistinctProfileIdOfChat userId: "+userId);
        query.addCriteria(Criteria.where("fromUserId").is(userId));
        query.with(Sort.by("timestamp").descending());
        return reactiveMongoTemplate.findDistinct(query, "userId", ChatItem.class, String.class).skip(page*pageSize).take(pageSize);
    }

    /**
     * Получить уникальные чаты по полю fromUserId. Вернет [201,202,203]
     * @param userId String
     * @param page int
     * @param pageSize int
     * @return Flux<ChatItem>
     */
    public Flux<ChatItem> findDistinctProfileIdOfChats(String userId, long page, long pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("fromUserId").is(userId));
        query.with(Sort.by("timestamp").descending());
        return reactiveMongoTemplate.find(query, ChatItem.class).distinct(ChatItem::getUserId).skip(page*pageSize).take(pageSize);
    }

    /**
     *
     * @param userId String
     * @param fromUserId String
     * @param page long
     * @param size long
     * @param direction int
     * @return Flux<ChatItem>
     */
    public Flux<ChatItem> getCurrentProfileChatCorrespondence(String userId, String fromUserId, long page, long size, Sort.Direction direction) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("fromUserId").is(fromUserId));
        query.addCriteria(Criteria.where("message").ne(""));
        query.with(Sort.by("timestamp").descending());
        //query.with(PageRequest.of(page, size));
        return reactiveMongoTemplate.find(query, ChatItem.class).skip(page*size).take(size);
    }
}
