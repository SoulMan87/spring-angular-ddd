package org.soulrebel.company.model;

import org.springframework.lang.NonNull;

import java.time.LocalDate;

public record CompleteRequest(@NonNull LocalDate expectedCompletionDate) {
}
