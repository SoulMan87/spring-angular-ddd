package org.soulrebel.company.rest;

import lombok.RequiredArgsConstructor;
import org.soulrebel.company.model.CompleteRequest;
import org.soulrebel.company.model.ProductionOrder;
import org.soulrebel.company.model.ProductionOrderState;
import org.soulrebel.company.model.RenameRequest;
import org.soulrebel.company.repository.ProductionOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductionOrderController extends AbstractProductionOrderController implements
        RepresentationModelProcessor<EntityModel<ProductionOrder>> {

    private final ProductionOrders productionOrders;

    @PostMapping(value = "/{id}/rename")
    public ResponseEntity<?> rename(@PathVariable Long id, @RequestBody RenameRequest request) {
        return productionOrders.findById (id)
                .map (productionOrder -> productionOrders.save (productionOrder.renameTo (request.newName ())))
                .map (productionOrder -> ResponseEntity.ok ().body (EntityModel.of (productionOrder)))
                .orElse (ResponseEntity.notFound ().build ());
    }

    @PostMapping(value = "/{id}/submit")
    public ResponseEntity<?> submit(@PathVariable Long id) {
        return productionOrders.findById (id)
                .map (productionOrder -> productionOrders.save (productionOrder.submit ()))
                .map (productionOrder -> ResponseEntity.ok ().body (EntityModel.of (productionOrder)))
                .orElse (ResponseEntity.notFound ().build ());
    }

    @PostMapping(value = "/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id, @RequestBody CompleteRequest request) {
        return productionOrders.findById (id)
                .map (productionOrder -> productionOrders
                        .save (productionOrder.accept (request.expectedCompletionDate ())))
                .map (productionOrder -> ResponseEntity.ok ().body (EntityModel.of (productionOrder)))
                .orElse (ResponseEntity.notFound ().build ());
    }

    @Override
    public EntityModel<ProductionOrder> process(EntityModel<ProductionOrder> model) {

        var order = model.getContent ();
        if (Objects.requireNonNull (order).getState () == ProductionOrderState.DRAFT) {
            model.add (linkTo (methodOn (getClass ()).rename (order.getId (), null)).withRel (REL_RENAME));
            model.add (linkTo (methodOn (getClass ()).submit (order.getId ())).withRel (REL_SUBMIT));
        }
        if (order.getState () == ProductionOrderState.SUBMITTED) {
            model.add (linkTo (methodOn (getClass ()).accept (order.getId (), null)).withRel (REL_ACCEPT));
        }
        return model;
    }
}
