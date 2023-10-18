/***
<p>
    Licensed under MIT License Copyright (c) 2023 Raja Kolli.
</p>
***/

package com.example.inventoryservice.repositories;

import static com.example.inventoryservice.jooq.tables.Inventory.INVENTORY;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.inventoryservice.entities.Inventory;
import java.util.List;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;

@JooqTest(
        properties = {
            "spring.test.database.replace=none",
            "spring.datasource.url=jdbc:tc:postgresql:16.0-alpine:///databasename"
        })
class JOOQInventoryRepositoryTest {

    @Autowired private DSLContext dslContext;

    @BeforeEach
    void setUpData() {
        dslContext.deleteFrom(INVENTORY).execute();
    }

    @Test
    void findByProductCodeInAndQuantityAvailable() {
        List<Inventory> inventoryList =
                List.of(
                        new Inventory(null, "product1", 10, 0),
                        new Inventory(null, "product2", 0, 0));
        inventoryList.forEach(inventory -> dslContext.newRecord(INVENTORY, inventory).insert());

        List<Inventory> findAvailableInventory =
                dslContext
                        .select()
                        .from(INVENTORY)
                        .where(
                                INVENTORY
                                        .PRODUCT_CODE
                                        .in(List.of("product1", "product2"))
                                        .and(INVENTORY.QUANTITY.gt(0)))
                        .fetchInto(Inventory.class);

        assertThat(findAvailableInventory).isNotEmpty().hasSize(1);
    }
}
