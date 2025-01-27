package com.springboot.order.mapper;

import com.springboot.coffee.entity.Coffee;
import com.springboot.order.dto.*;
import com.springboot.order.entity.Order;
import com.springboot.order.entity.OrderCoffee;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    //1. Order 가 가지고 있는 List<OrderCoffeeDto>  -> OrderCoffee 로 매핑
    default OrderCoffee orderCoffeeDtoToOrderCoffee(OrderCoffeeDto orderCoffeeDto, Order order) {
       OrderCoffee orderCoffee = new OrderCoffee();
       orderCoffee.setOrderCoffeeId(orderCoffeeDto.getCoffeeId());
       orderCoffee.setQuantity(orderCoffeeDto.getQuantity());
       orderCoffee.setOrder(order);
       //OrderCoffeeDto에 getCoffee() 메서드 만듬
       orderCoffee.setCoffee(orderCoffeeDto.getCoffee());

       return orderCoffee;
    }

    //2. Order 엔티티로 매핑
    default Order orderPostDtoToOrder (OrderPostDto orderPostDto) {
        Order order = new Order();
        //orderPostDto에 만든 getMember()
        order.setMember(orderPostDto.getMember());
        order.setOrderCoffees(orderPostDto.getOrderCoffees().stream().map(
                orderCoffee ->orderCoffeeDtoToOrderCoffee(orderCoffee, order))
                .collect(Collectors.toList())
        );

        //orderStatus는 초기값 지정되어있음
        //orderId는 자동 생성
        return order;

    }

    Order orderPatchDtoToOrder(OrderPatchDto orderPatchDto);


    //3. OrderCoffee -> OrderCoffeeResponseDto 매핑
    default OrderCoffeeResponseDto orderCoffeeToOrderCoffeeResponseDto(OrderCoffee orderCoffee) {
        OrderCoffeeResponseDto dto = new OrderCoffeeResponseDto(
                orderCoffee.getCoffee().getCoffeeId(),
                orderCoffee.getCoffee().getKorName(),
                orderCoffee.getCoffee().getEngName(),
                orderCoffee.getCoffee().getPrice(),
                orderCoffee.getQuantity()
        );

        return dto;
    }
    default OrderResponseDto orderToOrderResponseDto(Order order){
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setMemberId(order.getMember().getMemberId());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setOrderCoffees(order.getOrderCoffees().stream().map(
                orderCoffee -> orderCoffeeToOrderCoffeeResponseDto(orderCoffee))
                .collect(Collectors.toList())
        );

        return dto;
    }

    default  List<OrderResponseDto> orderResponseDtos(List<Order> orders){

        List<OrderResponseDto> dtos = orders.stream().map(
                order -> orderToOrderResponseDto(order))
                .collect(Collectors.toList());

        return dtos;
    }
}
