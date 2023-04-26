package com.learning.beerorderservice.services;

import com.learning.beerorderservice.bootstrap.BeerOrderBootStrap;
import com.learning.beerorderservice.domain.Customer;
import com.learning.beerorderservice.repositories.BeerOrderRepository;
import com.learning.beerorderservice.repositories.CustomerRepository;
import com.learning.beerorderservice.web.model.BeerOrderDto;
import com.learning.beerorderservice.web.model.BeerOrderLineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class TastingRoomService {

    private final BeerOrderService beerOrderService;
    private final CustomerRepository customerRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final List<String> beerUpcs = new ArrayList<>(3);

    public TastingRoomService(BeerOrderService beerOrderService, CustomerRepository customerRepository, BeerOrderRepository beerOrderRepository) {
        this.beerOrderService = beerOrderService;
        this.customerRepository = customerRepository;
        this.beerOrderRepository = beerOrderRepository;

        beerUpcs.add(BeerOrderBootStrap.BEER_1_UPC);
        beerUpcs.add(BeerOrderBootStrap.BEER_2_UPC);
        beerUpcs.add(BeerOrderBootStrap.BEER_3_UPC);
    }

    @Transactional
//    @Scheduled(fixedRate = 2000)
    @Scheduled(fixedRate = 2000)
    public void placeTastingRoomOrder() {

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(BeerOrderBootStrap.TASTING_ROOM);

        if (customerList.size() == 1) {
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String beerToOrder = getRandomBeerUpc();

        BeerOrderLineDto beerOrderLineDto =
                BeerOrderLineDto
                        .builder()
                        .upc(beerToOrder)
                        .orderQuantity(new Random().nextInt(6))
                        .build();

        List<BeerOrderLineDto> beerOrderLineDtoList = new ArrayList<>();
        beerOrderLineDtoList.add(beerOrderLineDto);

        BeerOrderDto beerOrderDto =
                BeerOrderDto
                        .builder()
                        .customerId(customer.getId())
                        .customerRef(UUID.randomUUID().toString())
                        .beerOrderLines(beerOrderLineDtoList)
                        .build();

        BeerOrderDto savedOrder = beerOrderService.placeOrder(customer.getId(), beerOrderDto);
    }

    private String getRandomBeerUpc() {
        return beerUpcs.get(new Random().nextInt(beerUpcs.size() -0));
    }

}
