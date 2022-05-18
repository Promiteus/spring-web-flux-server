package com.romanm.jwtservicedata.configs.routes;

import com.romanm.jwtservicedata.configs.auth.filters.UserProfileTokenOwnerFilter;
import com.romanm.jwtservicedata.constants.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@Configuration
public class UserProfileRoutes {

    @Bean
    public RouterFunction<ServerResponse> profileRoutes(UserProfileRoutesHandler userProfileRoutesHandler) {
        return route(GET(Api.API_PREFIX+Api.API_USER_PROFILE_USER_ID).and(accept(MediaType.APPLICATION_JSON)), userProfileRoutesHandler::getUserProfile)
               .filter(new UserProfileTokenOwnerFilter())
               .andRoute(POST(Api.API_PREFIX+Api.API_USER_PROFILE).and(accept(MediaType.APPLICATION_JSON)), userProfileRoutesHandler::saveUserProfile)
               .andRoute(DELETE(Api.API_PREFIX+Api.API_USER_PROFILE_USER_ID).and(accept(MediaType.APPLICATION_JSON)), userProfileRoutesHandler::removeUserProfile)
               .andRoute(GET(Api.API_PREFIX+Api.API_USER_PROFILES).and(accept(MediaType.APPLICATION_JSON)), userProfileRoutesHandler::getUserProfilesByPage)
               .andRoute(POST(Api.API_PREFIX+Api.API_POST_USER_PROFILES).and(accept(MediaType.APPLICATION_JSON)), userProfileRoutesHandler::getUserProfilesByPageWithSearchBody);

    }
}