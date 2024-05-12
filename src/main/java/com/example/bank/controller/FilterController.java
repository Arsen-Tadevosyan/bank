package com.example.bank.controller;

import com.example.bank.dto.TransactionFilterDto;
import com.example.bank.entity.Transaction;
import com.example.bank.service.TransactionService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class FilterController {

    private final TransactionService transactionService;


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

    private Specification<Transaction> buildSpecification(TransactionFilterDto filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDto.getMoneyType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("moneyType"), filterDto.getMoneyType()));
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
