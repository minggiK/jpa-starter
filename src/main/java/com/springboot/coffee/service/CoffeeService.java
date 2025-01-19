package com.springboot.coffee.service;

import com.springboot.coffee.dto.CoffeePostDto;
import com.springboot.coffee.entity.Coffee;
import com.springboot.coffee.repository.CoffeeRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;

    public CoffeeService(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    public Coffee createCoffee(Coffee coffee) {
    //기존의 등록된 커피가 아닌지(중복이 있는지) 확인
        //커피 중복 여부는 coffeeCode로 확인
        String coffeeCode = coffee.getCoffeeCode().toUpperCase();

        verifyExistCoffee(coffeeCode);
        //이미 등록된 커피코드라면 예외처리 발생 -> throw라 실행되면이 메서드가 종료되서 여기는 실행 안되나?
        coffee.setCoffeeCode(coffeeCode);

    //생성되면 레파지토리에 저장
        return coffeeRepository.save(coffee);
    }

    public Coffee updateCoffee(Coffee coffee) {
    //수정하려는 커피가 등록된 커피가 맞는지 확인
        //coffeeId 찾기
        Coffee findCoffee = findVerifiedCoffee(coffee.getCoffeeId());

        Optional.ofNullable(coffee.getKorName())
                .ifPresent(korName -> findCoffee.setKorName(korName));
        Optional.ofNullable(coffee.getEngName())
                .ifPresent(engName -> coffee.setEngName(engName));
        Optional.ofNullable(coffee.getPrice())
                .ifPresent(price -> coffee.setPrice(price));
        Optional.ofNullable(coffee.getCoffeeStatus())
                .ifPresent(coffeeStatus -> coffee.setCoffeeStatus(coffeeStatus));

        //수정 사항 레파지토리에 저장
        return coffeeRepository.save(findCoffee);
    }

    public Coffee findCoffee(Long coffeeId) {
        //존재하는 coffeeId라면 그걸 보내줘?
        return findVerifiedCoffee(coffeeId);
    }

    //전체조회는 Pagination
    public Page<Coffee> findCoffees(int page, int size) {
        return coffeeRepository.findAll(PageRequest.of(page, size, Sort.by("coffeeId").descending()));
    }

    public void deleteCoffee (Long coffeeId) {
        coffeeRepository.delete(findVerifiedCoffee(coffeeId));
    }

    //기존 Coffee에 파라미터로 받은 coffeecode가 존재하는지 확인
    public void verifyExistCoffee(String coffeeCode) {

        Optional<Coffee> coffee = coffeeRepository.findByCoffeeCode(coffeeCode);
        //이미 등록된 코드라면 예외처리
        if(coffee.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.COFFEE_CODE_EXISTS);
        }
    }

    //등록된 Coffee에서 찾는 coffeeId 찾을 수 없다면 예외처리
     public Coffee findVerifiedCoffee(long coffeeId) {
        Optional<Coffee> optionalCoffee = coffeeRepository.findById(coffeeId);
        return optionalCoffee.orElseThrow(() -> new BusinessLogicException(ExceptionCode.COFFEE_NOT_FOUND));
    }
}
