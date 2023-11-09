package org.soulrebel.company.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.soulrebel.company.model.ProductionOrder;
import org.soulrebel.company.model.ProductionOrderState;
import org.soulrebel.company.repository.ProductionOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.soulrebel.company.rest.AbstractProductionOrderController.REL_ACCEPT;
import static org.soulrebel.company.rest.AbstractProductionOrderController.REL_RENAME;
import static org.soulrebel.company.rest.AbstractProductionOrderController.REL_SUBMIT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductionOrderControllerIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LinkDiscoverers links;

    @Autowired
    private ProductionOrders productionOrders;

    @Test
    void api_linkToProductionOrdersContained() throws Exception {
        mvc.perform(get("/api"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent("productionOrders"));
    }

    @Test
    void getProductionOrders_ordersPresent_ordersReturnedIncludingLinks() throws Exception {
        productionOrders.save(ProductionOrder.create("Test"));

        mvc.perform(get("/api/productionOrders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded['productionOrders']", hasSize((int)productionOrders.count())))
                .andExpect(linkWithRelIsPresent("self"))
                .andExpect(linkWithRelIsPresent("productionOrders"));
    }

    @Test
    void getProductionOrder_orderInStateDraft_renameAndSubmitRelPresent() throws Exception {
        val id = productionOrders.save(ProductionOrder.create("Test")).getId();

        mvc.perform(get("/api/productionOrders/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent("self"))
                .andExpect(linkWithRelIsPresent("productionOrder"))
                .andExpect(linkWithRelIsPresent(REL_RENAME))
                .andExpect(linkWithRelIsPresent(REL_SUBMIT))
                .andExpect(linkWithRelIsNotPresent(REL_ACCEPT));
    }

    @Test
    void getProductionOrder_orderInStateSubmitted_acceptRelPresent() throws Exception {
        val id = productionOrders.save(ProductionOrder.create("Test").submit()).getId();

        mvc.perform(get("/api/productionOrders/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent("self"))
                .andExpect(linkWithRelIsPresent("productionOrder"))
                .andExpect(linkWithRelIsNotPresent(REL_RENAME))
                .andExpect(linkWithRelIsNotPresent(REL_SUBMIT))
                .andExpect(linkWithRelIsPresent(REL_ACCEPT));
    }

    @Test
    void postRenameProductionOrder_orderInStateDraft_orderRenamed() throws Exception {
        val id = productionOrders.save(ProductionOrder.create("Test")).getId();

        mvc.perform(post("/api/productionOrders/" + id + "/rename")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
						 {
						   "newName":"Renamed"
						 }
						 """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent("self"))
                .andExpect(linkWithRelIsPresent("productionOrder"))
                .andExpect(linkWithRelIsNotPresent(REL_RENAME))
                .andExpect(linkWithRelIsNotPresent(REL_SUBMIT))
                .andExpect(linkWithRelIsPresent(REL_ACCEPT));

        assertThat(productionOrders.findById(id)).hasValueSatisfying(
                it -> assertThat(it.getName()).isEqualTo("Renamed"));
    }

    @Test
    void postSubmitProductionOrder_orderInStateDraft_orderSubmitted() throws Exception {
        val id = productionOrders.save(ProductionOrder.create("Test")).getId();

        mvc.perform(post("/api/productionOrders/" + id + "/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent("self"))
                .andExpect(linkWithRelIsPresent("productionOrder"))
                .andExpect(linkWithRelIsNotPresent(REL_RENAME))
                .andExpect(linkWithRelIsNotPresent(REL_SUBMIT))
                .andExpect(linkWithRelIsPresent(REL_ACCEPT));

        assertThat(productionOrders.findById(id)).hasValueSatisfying(
                it -> assertThat(it.getState()).isEqualTo(ProductionOrderState.SUBMITTED));
    }

    @Test
    void postAcceptProductionOrder_orderInStateSubmitted_orderAccepted() throws Exception {
        val id = productionOrders.save(ProductionOrder.create("Test").submit()).getId();

        mvc.perform(post("/api/productionOrders/" + id + "/accept")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
						  {
						  	"expectedCompletionDate": "%s"
						  }
						""".formatted(LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_DATE))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent("self"))
                .andExpect(linkWithRelIsPresent("productionOrder"))
                .andExpect(linkWithRelIsNotPresent(REL_RENAME))
                .andExpect(linkWithRelIsNotPresent(REL_SUBMIT))
                .andExpect(linkWithRelIsNotPresent(REL_ACCEPT));

        assertThat(productionOrders.findById(id)).hasValueSatisfying(
                it -> assertThat(it.getState()).isEqualTo(ProductionOrderState.ACCEPTED));
    }

    @Test
    void getProductionOrder_orderInStateAccepted_noActionRelsPresent() throws Exception {
        val id = productionOrders.save(ProductionOrder.create("Test").submit()).getId();

        mvc.perform(get("/api/productionOrders/" + id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(linkWithRelIsPresent("self"))
                .andExpect(linkWithRelIsPresent("productionOrder"))
                .andExpect(linkWithRelIsNotPresent(REL_RENAME))
                .andExpect(linkWithRelIsNotPresent(REL_SUBMIT))
                .andExpect(linkWithRelIsNotPresent(REL_ACCEPT));
    }

    ResultMatcher linkWithRelIsPresent(final String rel) {
        return new LinkWithRelMatcher(rel, true);
    }

    ResultMatcher linkWithRelIsNotPresent(String rel) {
        return new LinkWithRelMatcher(rel, false);
    }

    @RequiredArgsConstructor
    private class LinkWithRelMatcher implements ResultMatcher {

        private final String rel;
        private final boolean present;

        @Override
        public void match(MvcResult result) throws Exception {

            val response = result.getResponse();
            val content = response.getContentAsString();
            val discoverer = links.getLinkDiscovererFor(Objects.
                    requireNonNull (response.getContentType ()));
            if (discoverer.isEmpty()) {
                throw new RuntimeException(
                        "there is seemingly no link discoverer configured. Please check your Spring setup.");
            }

            if (present) {
                assertThat(discoverer.get().findLinkWithRel(rel, content)).isNotNull();
            } else {
                assertThat(discoverer.get().findLinkWithRel(rel, content)).isNotNull();
            }
        }
    }

}
