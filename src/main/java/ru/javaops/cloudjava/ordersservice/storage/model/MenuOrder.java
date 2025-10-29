package ru.javaops.cloudjava.ordersservice.storage.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("orders")
public class MenuOrder {
    @Id
    private Long id;
    @Column("total_price")
    @NotNull
    private BigDecimal totalPrice;
    @NotBlank
    private String city;
    @NotBlank
    private String street;
    @NotNull
    private Integer house;
    @NotNull
    private Integer apartment;
    @Column("menu_line_items")
    private List<MenuLineItem> menuLineItems;
    @NotBlank
    private String status;
    @Column("created_by")
    @CreatedBy
    private String createdBy;
    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
