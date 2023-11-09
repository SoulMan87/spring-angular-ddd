package org.soulrebel.company.model;

import lombok.Getter;
import lombok.val;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public class ProductionOrder {

    @Id
    private Long id;
    private String name;
    private LocalDate expectedCompletionDate;
    private ProductionOrderState state;

    public static ProductionOrder create(String name) {
        val result = new ProductionOrder ();
        result.name = name;
        result.state = ProductionOrderState.DRAFT;
        return result;
    }

    public ProductionOrder renameTo(String newName) {
        if (state != ProductionOrderState.DRAFT) {
            throw new IllegalStateException("Cannot rename production order in state " + state);
        }
        name = newName;
        return this;
    }

    public ProductionOrder submit() {
        if (state != ProductionOrderState.DRAFT) {
            throw new IllegalStateException ("Cannot submit production order in state " + state);
        }
        state = ProductionOrderState.SUBMITTED;
        return this;
    }

    public ProductionOrder accept(LocalDate expectedCompletionDate) {
        if (state != ProductionOrderState.SUBMITTED) {
            throw new IllegalStateException ("Cannot accept production order in state " + state);
        }
        Objects.requireNonNull (expectedCompletionDate, "expectedCompletionDate is required to submit" +
                " a production order");
        if (expectedCompletionDate.isBefore (LocalDate.now ())) {
            throw new IllegalArgumentException ("Expected completion date must be in the future, but was "
                    + expectedCompletionDate);
        }
        state = ProductionOrderState.ACCEPTED;
        this.expectedCompletionDate = expectedCompletionDate;
        return this;
    }
}
