package com.example.bankrest.controller;


import com.example.bankcommon.dto.UserDto;
import com.example.bankcommon.dto.UserFilterDto;
import com.example.bankcommon.entity.ChatRoom;
import com.example.bankcommon.entity.QUser;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.security.CurrentUser;
import com.example.bankcommon.service.ChatRoomService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final EntityManager entityManager;
    private final ChatRoomService chatRoomService;


    @GetMapping("/filterUsers")
    public ResponseEntity<Page<User>> filterUsers(UserFilterDto userFilterDto,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        BooleanExpression predicate = buildPredicate(userFilterDto);

        Pageable pageable = PageRequest.of(page, size);

        QueryResults<User> queryResults = queryFactory.selectFrom(QUser.user)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        Page<User> userPage = new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());

        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> findConnectedUsers(@AuthenticationPrincipal CurrentUser currentUser) {
        List<ChatRoom> byRecipientId = chatRoomService.findByRecipientId(currentUser.getUser().getEmail());
        List<UserDto> users = new ArrayList<>();
        for (ChatRoom chatRoom : byRecipientId) {
            String senderId = chatRoom.getSenderId();
            users.add(UserDto.builder()
                    .nickName(senderId)
                    .build());
        }
        return ResponseEntity.ok(users);
    }



    private BooleanExpression buildPredicate(UserFilterDto userFilterDto) {
        QUser qUser = QUser.user;
        BooleanExpression predicate = qUser.isNotNull();

        if (userFilterDto.getName() != null && !userFilterDto.getName().isEmpty()) {
            predicate = predicate.and(qUser.name.containsIgnoreCase(userFilterDto.getName()));
        }
        if (userFilterDto.getSurname() != null && !userFilterDto.getSurname().isEmpty()) {
            predicate = predicate.and(qUser.surname.containsIgnoreCase(userFilterDto.getSurname()));
        }
        if (userFilterDto.getEmail() != null && !userFilterDto.getEmail().isEmpty()) {
            predicate = predicate.and(qUser.email.containsIgnoreCase(userFilterDto.getEmail()));
        }
        if (userFilterDto.getMinRating() > 0) {
            predicate = predicate.and(qUser.rating.goe(userFilterDto.getMinRating()));
        }
        if (userFilterDto.getMaxRating() > 0) {
            predicate = predicate.and(qUser.rating.loe(userFilterDto.getMaxRating()));
        }
        return predicate;
    }
}
