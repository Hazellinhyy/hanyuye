package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.DonationRequest;
import com.hfut.cat_adoption_system.dto.OrderRequest;
import com.hfut.cat_adoption_system.dto.ProductRequest;
import com.hfut.cat_adoption_system.model.DonationRecord;
import com.hfut.cat_adoption_system.model.DonationChannel;
import com.hfut.cat_adoption_system.model.Product;
import com.hfut.cat_adoption_system.model.ProductOrder;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {
    private final CatAdoptionService service;

    public ShopController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/products")
    @PublicApi
    public ApiResponse<List<Product>> products() {
        return ApiResponse.ok(service.listProducts());
    }

    @GetMapping("/admin/products")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<Product>> allProducts() {
        return ApiResponse.ok(service.listAllProducts());
    }

    @PostMapping("/admin/products")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.created(service.saveProduct(request));
    }

    @PutMapping("/admin/products/{productId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Product> updateProduct(@PathVariable String productId, @Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok(service.updateProduct(productId, request));
    }

    @PatchMapping("/admin/products/{productId}/status")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Product> updateProductStatus(@PathVariable String productId, @RequestParam boolean status) {
        return ApiResponse.ok(service.updateProductStatus(productId, status));
    }

    @DeleteMapping("/admin/products/{productId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteProduct(@PathVariable String productId) {
        service.deleteProduct(productId);
        return ApiResponse.ok(true);
    }

    @PostMapping("/orders")
    public ApiResponse<ProductOrder> createOrder(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.created(service.createOrder(request));
    }

    @GetMapping("/orders")
    public ApiResponse<List<ProductOrder>> myOrders() {
        return ApiResponse.ok(service.listOrders(true));
    }

    @GetMapping("/admin/orders")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<ProductOrder>> allOrders() {
        return ApiResponse.ok(service.listOrders(false));
    }

    @PatchMapping("/admin/orders/{orderId}/status")
    @RequireRole(Role.ADMIN)
    public ApiResponse<ProductOrder> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        return ApiResponse.ok(service.updateOrderStatus(orderId, status));
    }

    @GetMapping("/donation-channel")
    @PublicApi
    public ApiResponse<DonationChannel> donationChannel() {
        return ApiResponse.ok(service.getDonationChannel());
    }

    @PostMapping("/donations")
    public ApiResponse<DonationRecord> createDonation(@Valid @RequestBody DonationRequest request) {
        return ApiResponse.created(service.createDonation(request));
    }

    @GetMapping("/donations")
    public ApiResponse<List<DonationRecord>> myDonations() {
        return ApiResponse.ok(service.listDonations(true));
    }

    @GetMapping("/admin/donations")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<DonationRecord>> allDonations() {
        return ApiResponse.ok(service.listDonations(false));
    }

    @PatchMapping("/admin/donations/{donationId}/status")
    @RequireRole(Role.ADMIN)
    public ApiResponse<DonationRecord> updateDonationStatus(@PathVariable String donationId, @RequestParam String status) {
        return ApiResponse.ok(service.updateDonationStatus(donationId, status));
    }
}
