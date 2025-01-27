package com.springboot.order.controller;

import com.springboot.order.dto.OrderPatchDto;
import com.springboot.order.dto.OrderPostDto;
import com.springboot.order.dto.OrderResponseDto;
import com.springboot.order.entity.Order;
import com.springboot.order.mapper.OrderMapper;
import com.springboot.order.service.OrderService;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v11/orders")
@Validated
public class OrderController {

    private final static String ORDER_DEFAULT_URL = "/v11/orders";
    private final OrderService orderService;
    private final OrderMapper mapper;

    public OrderController(OrderService orderService, OrderMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postOrder(@Valid @RequestBody OrderPostDto orderPostDto) {
        Order order = orderService.createOrder(mapper.orderPostDtoToOrder(orderPostDto));
        URI location = UriCreator.createUri(ORDER_DEFAULT_URL, order.getOrderId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{order-id}")
    public ResponseEntity patchOrder(@Positive @PathVariable("order-id") long orderId,
                                     @Valid @RequestBody OrderPatchDto orderPatchDto) {
        //수정하려는 orderId가 있는지 확인
        orderPatchDto.setOrderId(orderId);
        Order order = orderService.updateOrder(mapper.orderPatchDtoToOrder(orderPatchDto));

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.orderToOrderResponseDto(order)),
                HttpStatus.OK);
    }

    @GetMapping("/{order-id}")
    public ResponseEntity getOrder(@Positive @PathVariable("order-id") long orderId) {

        Order order = orderService.findOrder(orderId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.orderToOrderResponseDto(order)),
                HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity getOrders(@Positive @RequestParam("page") int page,
                                    @Positive @RequestParam("size") int size){
        Page<Order> orderPage = orderService.findOrders(page, size);
        List<Order> orderList = orderPage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.orderResponseDtos(orderList), orderPage),
        HttpStatus.OK
    );
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity deleteOrder(@Positive @PathVariable("order-id") long orderId) {
        orderService.cancelOrder(orderId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
