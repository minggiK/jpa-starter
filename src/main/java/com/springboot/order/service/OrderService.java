package com.springboot.order.service;

import com.springboot.coffee.service.CoffeeService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.service.MemberService;
import com.springboot.order.entity.Order;
import com.springboot.order.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final CoffeeService coffeeService;

    public OrderService(OrderRepository orderRepository, MemberService memberService, CoffeeService coffeeService) {
        this.orderRepository = orderRepository;
        this.memberService = memberService;
        this.coffeeService = coffeeService;
    }

    public Order createOrder(Order order) {
        //주문한 회원이 존재하는지 조회
        memberService.findVerfiedMember(order.getMember().getMemberId());
        //주문한 커피가 존재하는지 조회
        order.getOrderCoffees().stream()
                .forEach(orderCoffee -> coffeeService.findVerifiedCoffee(orderCoffee.getCoffee().getCoffeeId()));

        //새로 생성되면 레파지토리에 저장
        return orderRepository.save(order);
    }


    public Order updateOrder(Order order) {
        //존재여부 확인
        Order findOrder = findVerifiedOrder(order.getOrderId());
        Optional.ofNullable(order.getOrderStatus())
                .ifPresent(orderStatus -> findOrder.setOrderStatus(orderStatus));

        return orderRepository.save(findOrder);

    }

    public Order findOrder(Long orderId) {
        return findVerifiedOrder(orderId);
    }

    public Page<Order> findOrders(int page, int size) {
       return orderRepository.findAll(PageRequest.of(page, size, Sort.by("orderId").descending()));

    }

    //주문취소 ->
    public void cancelOrder(Long orderId) {
        Order findOrder = findVerifiedOrder(orderId);
        //orderStatus의 상태번호 stepNumber
        int stepN = findOrder.getOrderStatus().getStepNumber();

        //status가 1이면 상태변경, 2,3,4면 상태 변경 불가
        if(stepN > 1) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_ORDER);
        }
        findOrder.setOrderStatus(Order.OrderStatus.ORDER_CANCEL);

        orderRepository.save(findOrder);
    }

    public Order findVerifiedOrder(long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));

    }
}
