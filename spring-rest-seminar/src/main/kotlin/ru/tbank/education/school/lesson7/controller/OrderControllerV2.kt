package ru.tbank.education.school.lesson7.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.tbank.education.school.lesson7.dto.CreateOrderRequest
import ru.tbank.education.school.lesson7.dto.Order
import ru.tbank.education.school.lesson7.dto.OrderStatus
import ru.tbank.education.school.lesson7.service.OrderService

@RestController
@RequestMapping("/api/v2/orders")
@Tag(name = "OrdersV2", description = "CRUD operations for orders")
@Validated
class OrderControllerV2(
    private val orderService: OrderService
) {

    @GetMapping
    @Operation(summary = "Get all orders")
    fun getAll(@RequestParam(required = false) status: OrderStatus?): List<Order> =
        orderService.getAll(status)

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    fun getById(@PathVariable @Positive id: Long): Order =
        orderService.getById(id)

    @PostMapping
    @Operation(summary = "Create order")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateOrderRequest): Order =
        orderService.create(request)

    @PutMapping("/{id}")
    @Operation(summary = "Update order")
    fun update(
        @PathVariable @Positive id: Long,
        @Valid @RequestBody request: CreateOrderRequest
    ): Order = orderService.update(id, request)

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable @Positive id: Long) =
        orderService.delete(id)
}
