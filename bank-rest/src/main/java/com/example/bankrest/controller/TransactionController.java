package com.example.bankrest.controller;


import com.example.bankcommon.dto.TransactionFilterDto;
import com.example.bankcommon.dto.TransferFilterDto;
import com.example.bankcommon.entity.QTransfer;
import com.example.bankcommon.entity.Transaction;
import com.example.bankcommon.entity.Transfer;
import com.example.bankcommon.service.TransactionService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final EntityManager entityManager;


    @GetMapping("/filterTransactions")
    public ResponseEntity<?> filterTransactions(@Validated TransactionFilterDto transactionFilterDto,
                                                BindingResult bindingResult,
                                                @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            }
            redirectAttributes.addFlashAttribute("msg", errorMessage.toString());
            return ResponseEntity.badRequest().body("Validation error occurred");
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("issueDate").descending());
        Specification<Transaction> specification = buildSpecification(transactionFilterDto);
        Page<Transaction> transactions = transactionService.findBySpecification(specification, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactions.getContent());
        response.put("totalPages", transactions.getTotalPages());
        response.put("currentPage", transactions.getNumber() + 1);
        response.put("totalItems", transactions.getTotalElements());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/filterTransfers")
    public ResponseEntity<Page<Transfer>> filterTransfers(TransferFilterDto transferFilterDto,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        BooleanExpression predicate = transfersFilter(transferFilterDto);

        Pageable pageable = PageRequest.of(page, size);

        QueryResults<Transfer> queryResults = queryFactory.selectFrom(QTransfer.transfer)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        Page<Transfer> transferPage = new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());

        return ResponseEntity.ok(transferPage);
    }

    private BooleanExpression transfersFilter(TransferFilterDto transferFilterDto) {
        QTransfer qTransfer = QTransfer.transfer;
        BooleanExpression predicate = qTransfer.isNotNull();



        if (transferFilterDto.getFrom() != null && !transferFilterDto.getFrom().isEmpty()) {
            predicate = predicate.and(qTransfer.from.email.containsIgnoreCase(transferFilterDto.getFrom()));
        }
        if (transferFilterDto.getTo() != null && !transferFilterDto.getTo().isEmpty()) {
            predicate = predicate.and(qTransfer.to.email.containsIgnoreCase(transferFilterDto.getTo()));
        }
        if (transferFilterDto.getMoneyType() != null && !transferFilterDto.getMoneyType().equals(null)) {
            predicate = predicate.and(qTransfer.moneyType.stringValue().containsIgnoreCase(String.valueOf(transferFilterDto.getMoneyType())));
        }
        if (transferFilterDto.getMinSize() != null && transferFilterDto.getMinSize() > 0) {
            predicate = predicate.and(qTransfer.size.goe(transferFilterDto.getMinSize().doubleValue()));
        }
        if (transferFilterDto.getMaxSize() != null && transferFilterDto.getMaxSize() > 0) {
            predicate = predicate.and(qTransfer.size.loe(transferFilterDto.getMaxSize().doubleValue()));
        }
        return predicate;
    }

    private Specification<Transaction> buildSpecification(TransactionFilterDto filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDto.getMoneyType() != null) {predicates.add(criteriaBuilder.equal(root.get("moneyType"), filterDto.getMoneyType()));
            }

            if (filterDto.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filterDto.getStatus()));
            }

            if (filterDto.getMinSize() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("size"), filterDto.getMinSize()));
            }

            if (filterDto.getMaxSize() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("size"), filterDto.getMaxSize()));
            }

            if (filterDto.getTransactionType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), filterDto.getTransactionType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
