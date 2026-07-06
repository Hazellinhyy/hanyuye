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

/**
 * 商城管理控制器
 * 
 * 提供商城相关的功能，包括商品管理、订单管理和捐赠管理：
 * 
 * 【商品管理】：
 * - 查询商品列表（公开接口）
 * - 查询所有商品（管理员权限）
 * - 创建商品（管理员权限）
 * - 更新商品（管理员权限）
 * - 更新商品状态（管理员权限）
 * - 删除商品（管理员权限）
 * 
 * 【订单管理】：
 * - 创建订单（登录用户）
 * - 查询我的订单（登录用户）
 * - 查询所有订单（管理员权限）
 * - 更新订单状态（管理员权限）
 * 
 * 【捐赠管理】：
 * - 查询捐赠渠道（公开接口）
 * - 创建捐赠记录（登录用户）
 * - 查询我的捐赠记录（登录用户）
 * - 查询所有捐赠记录（管理员权限）
 * - 更新捐赠状态（管理员权限）
 */
@RestController
@RequestMapping("/api/shop")
public class ShopController {

    /** 认养服务：处理商城相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public ShopController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询商品列表（公开接口）
     * 返回上架状态的商品列表
     */
    @GetMapping("/products")
    @PublicApi
    public ApiResponse<List<Product>> products() {
        return ApiResponse.ok(service.listProducts());
    }

    /**
     * 查询所有商品（管理员权限）
     * 返回包含下架商品在内的所有商品
     */
    @GetMapping("/admin/products")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<Product>> allProducts() {
        return ApiResponse.ok(service.listAllProducts());
    }

    /**
     * 创建商品（管理员权限）
     * 
     * @param request 商品创建请求（包含商品名称、价格、库存等）
     */
    @PostMapping("/admin/products")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.created(service.saveProduct(request));
    }

    /**
     * 更新商品（管理员权限）
     * 
     * @param productId 商品ID
     * @param request   商品更新请求
     */
    @PutMapping("/admin/products/{productId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Product> updateProduct(@PathVariable String productId,
            @Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok(service.updateProduct(productId, request));
    }

    /**
     * 更新商品状态（管理员权限）
     * 
     * @param productId 商品ID
     * @param status    是否上架
     */
    @PatchMapping("/admin/products/{productId}/status")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Product> updateProductStatus(@PathVariable String productId, @RequestParam boolean status) {
        return ApiResponse.ok(service.updateProductStatus(productId, status));
    }

    /**
     * 删除商品（管理员权限）
     * 
     * @param productId 商品ID
     */
    @DeleteMapping("/admin/products/{productId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteProduct(@PathVariable String productId) {
        service.deleteProduct(productId);
        return ApiResponse.ok(true);
    }

    /**
     * 创建订单（登录用户）
     * 用户购买商品时创建订单
     * 
     * @param request 订单创建请求（包含商品ID、数量等）
     */
    @PostMapping("/orders")
    public ApiResponse<ProductOrder> createOrder(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.created(service.createOrder(request));
    }

    /**
     * 查询我的订单（登录用户）
     * 返回当前用户的订单列表
     */
    @GetMapping("/orders")
    public ApiResponse<List<ProductOrder>> myOrders() {
        return ApiResponse.ok(service.listOrders(true));
    }

    /**
     * 查询所有订单（管理员权限）
     * 返回系统所有订单列表
     */
    @GetMapping("/admin/orders")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<ProductOrder>> allOrders() {
        return ApiResponse.ok(service.listOrders(false));
    }

    /**
     * 更新订单状态（管理员权限）
     * 
     * @param orderId 订单ID
     * @param status  目标状态
     */
    @PatchMapping("/admin/orders/{orderId}/status")
    @RequireRole(Role.ADMIN)
    public ApiResponse<ProductOrder> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        return ApiResponse.ok(service.updateOrderStatus(orderId, status));
    }

    /**
     * 查询捐赠渠道（公开接口）
     * 返回捐赠支付渠道信息
     */
    @GetMapping("/donation-channel")
    @PublicApi
    public ApiResponse<DonationChannel> donationChannel() {
        return ApiResponse.ok(service.getDonationChannel());
    }

    /**
     * 创建捐赠记录（登录用户）
     * 用户进行爱心捐赠时创建捐赠记录
     * 
     * @param request 捐赠请求（包含金额、渠道等）
     */
    @PostMapping("/donations")
    public ApiResponse<DonationRecord> createDonation(@Valid @RequestBody DonationRequest request) {
        return ApiResponse.created(service.createDonation(request));
    }

    /**
     * 查询我的捐赠记录（登录用户）
     * 返回当前用户的捐赠记录列表
     */
    @GetMapping("/donations")
    public ApiResponse<List<DonationRecord>> myDonations() {
        return ApiResponse.ok(service.listDonations(true));
    }

    /**
     * 查询所有捐赠记录（管理员权限）
     * 返回系统所有捐赠记录列表
     */
    @GetMapping("/admin/donations")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<DonationRecord>> allDonations() {
        return ApiResponse.ok(service.listDonations(false));
    }

    /**
     * 更新捐赠状态（管理员权限）
     * 
     * @param donationId 捐赠记录ID
     * @param status     目标状态
     */
    @PatchMapping("/admin/donations/{donationId}/status")
    @RequireRole(Role.ADMIN)
    public ApiResponse<DonationRecord> updateDonationStatus(@PathVariable String donationId,
            @RequestParam String status) {
        return ApiResponse.ok(service.updateDonationStatus(donationId, status));
    }
}