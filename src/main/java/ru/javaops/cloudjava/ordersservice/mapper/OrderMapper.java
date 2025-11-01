package ru.javaops.cloudjava.ordersservice.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.javaops.cloudjava.ordersservice.dto.*;
import ru.javaops.cloudjava.ordersservice.exception.OrderServiceException;
import ru.javaops.cloudjava.ordersservice.storage.model.MenuLineItem;
import ru.javaops.cloudjava.ordersservice.storage.model.MenuOrder;
import ru.javaops.cloudjava.ordersservice.storage.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public MenuOrder mapToOrder(CreateOrderRequest request,
                                String username,
                                GetMenuInfoResponse infoResponse) {
        List<MenuInfo> menuInfos = infoResponse.getMenuInfos();
        checkUnavailableMenuItems(menuInfos);

        List<MenuLineItem> menuLineItems = menuInfos.stream()
                .map(info -> {
                    int quantity = request.getNameToQuantity().get(info.getName());
                    return MenuLineItem.builder()
                            .menuItemName(info.getName())
                            .price(info.getPrice())
                            .quantity(quantity)
                            .build();
                })
                .toList();
        BigDecimal totalPrice = menuLineItems.stream()
                .map(mi -> mi.getPrice().multiply(BigDecimal.valueOf(mi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return MenuOrder.builder()
                .totalPrice(totalPrice)
                .city(request.getAddress().getCity())
                .street(request.getAddress().getStreet())
                .house(request.getAddress().getHouse())
                .apartment(request.getAddress().getApartment())
                .status(OrderStatus.NEW)
                .createdBy(username)
                .menuLineItems(menuLineItems)
                .build();
    }

    public OrderResponse mapToResponse(MenuOrder order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .totalPrice(order.getTotalPrice())
                .menuLineItems(order.getMenuLineItems())
                .address(Address.builder()
                        .city(order.getCity())
                        .street(order.getStreet())
                        .house(order.getHouse())
                        .apartment(order.getApartment())
                        .build())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private void checkUnavailableMenuItems(List<MenuInfo> infos) {
        boolean hasUnavailable = infos.stream().anyMatch(m -> !m.getIsAvailable());
        if (hasUnavailable) {
            String msg = String.format("Cannot create order, some menu items are not available: %s", infos);
            throw new OrderServiceException(msg, HttpStatus.NOT_FOUND);
        }
    }
}
