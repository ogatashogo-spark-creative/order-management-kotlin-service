package com.sparkcreative.portfolio.order_management_service.controller

import com.sparkcreative.portfolio.order_management_service.model.CreateOrderRequest
import com.sparkcreative.portfolio.order_management_service.model.OrderResponse
import com.sparkcreative.portfolio.order_management_service.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 注文管理API
 * RESTful設計原則に基づくエンドポイントを提供します。
 */
@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {

    /**
     * F-1: 注文作成API
     * POST /orders
     * リクエストボディで注文情報を受け取り、注文を作成します。
     */
    @PostMapping
    fun createOrder(
        @RequestBody request: CreateOrderRequest,
        @RequestHeader(value = "Authorization", required = false) token: String?
    ): ResponseEntity<OrderResponse> {
        // Q-1: 認証トークンチェック (簡易モック)
        // 本来はSecurityFilterで行いますが、今回は実装イメージとして簡易的なチェックロジックを置いています。
        if (token != null && !token.startsWith("Bearer ")) {
             // 認証エラーのシミュレーション（必要に応じて有効化）
             // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val response = orderService.createOrder(request)
        // 成功時は 201 Created を返します
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * F-2: 注文取得API
     * GET /orders/{orderId}
     * 指定されたIDの注文詳細を返します。
     */
    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: Long): ResponseEntity<OrderResponse> {
        val response = orderService.getOrder(orderId)
        return ResponseEntity.ok(response)
    }

    /**
     * T-5: エラーハンドリング
     * データが見つからない場合（IllegalArgumentException）に 404 Not Found を返します。
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleNotFound(e: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to (e.message ?: "Not Found")))
    }
    
    /**
     * その他の予期せぬエラーが発生した場合に 500 Internal Server Error を返します。
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralError(e: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "Internal Server Error", "details" to (e.message ?: "")))
    }
}