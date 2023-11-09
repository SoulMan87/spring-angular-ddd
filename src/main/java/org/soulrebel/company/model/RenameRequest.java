package org.soulrebel.company.model;

import org.springframework.lang.NonNull;

public record RenameRequest(@NonNull String newName) {
}
