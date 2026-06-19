package com.harikrishnan.bookstore.controller;

import com.harikrishnan.bookstore.dto.CustomerRequestDto;
import com.harikrishnan.bookstore.dto.CustomerResponseDto;
import com.harikrishnan.bookstore.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

//    @PostMapping
//    public ResponseEntity<CustomerResponseDto> registerCustomer (@Valid @RequestBody CustomerRequestDto customerRequestDto) {
//        CustomerResponseDto customerResponseDto = customerService.saveCustomer(customerRequestDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponseDto);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerDetails(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomerDetails(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponseDto>> getAllCustomers (@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                      @RequestParam(defaultValue = "10") Integer size,
                                                                      @RequestParam(defaultValue = "id") String sortBy,
                                                                      @RequestParam(defaultValue = "asc") String sortType,
                                                                      @RequestParam(required = false) String email) {
        Sort sort = "desc".equalsIgnoreCase(sortType) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber,size,sort);
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomers(pageable,email));
    }


}
