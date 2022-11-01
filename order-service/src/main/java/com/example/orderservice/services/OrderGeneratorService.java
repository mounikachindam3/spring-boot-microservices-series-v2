/* Licensed under Apache-2.0 2022 */
package com.example.orderservice.services;

import com.example.orderservice.dtos.OrderDto;
import com.example.orderservice.dtos.OrderItemDto;
import com.example.orderservice.utils.AppConstants;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.datafaker.Faker;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrderGeneratorService {

    private static final Random RAND = new Random();
    private final AtomicLong id = new AtomicLong();

    private final OrderService orderService;

    private final StreamsBuilderFactoryBean kafkaStreamsFactory;

    public OrderGeneratorService(
            OrderService orderService, StreamsBuilderFactoryBean kafkaStreamsFactory) {
        this.orderService = orderService;
        this.kafkaStreamsFactory = kafkaStreamsFactory;
    }

    @Async
    public void generate() {
        Faker faker = new Faker();
        for (int i = 0; i < 10_000; i++) {
            OrderDto o = new OrderDto();
            o.setOrderId(id.incrementAndGet());
            o.setStatus("NEW");
            o.setCustomerId(RAND.nextLong(100) + 1);
            o.setCustomerEmail(faker.internet().safeEmailAddress());
            o.setCustomerAddress(faker.address().fullAddress());
            OrderItemDto orderItem = new OrderItemDto();
            int x = RAND.nextInt(5) + 1;
            orderItem.setProductPrice(new BigDecimal(100 * x));
            orderItem.setQuantity(x);
            orderItem.setProductId("Product" + RAND.nextInt(100) + 1);
            OrderItemDto orderItem1 = new OrderItemDto();
            int y = RAND.nextInt(5) + 1;
            orderItem1.setProductPrice(new BigDecimal(100 * y));
            orderItem1.setQuantity(y);
            orderItem1.setProductId("Product" + RAND.nextInt(100) + 1);
            List<OrderItemDto> oList = new ArrayList<>();
            oList.add(orderItem);
            oList.add(orderItem1);
            o.setItems(oList);
            orderService.saveOrder(o);
        }
    }

    public List<OrderDto> getAllOrders() {
        List<OrderDto> orders = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, OrderDto> store =
                Objects.requireNonNull(kafkaStreamsFactory.getKafkaStreams())
                        .store(
                                StoreQueryParameters.fromNameAndType(
                                        AppConstants.ORDERS_TOPIC,
                                        QueryableStoreTypes.keyValueStore()));
        KeyValueIterator<Long, OrderDto> it = store.all();
        it.forEachRemaining(kv -> orders.add(kv.value));
        return orders;
    }
}
