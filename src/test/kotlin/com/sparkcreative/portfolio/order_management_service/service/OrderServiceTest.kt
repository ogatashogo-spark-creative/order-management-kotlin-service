package com.sparkcreative.portfolio.order_management_service.service

import com.sparkcreative.portfolio.order_management_service.model.CreateOrderRequest
import com.sparkcreative.portfolio.order_management_service.model.OrderItemRequest
import com.sparkcreative.portfolio.order_management_service.repository.OrderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * Q-4: ユニットテスト
 * Mockkを使用してリポジトリ層をモック化し、ビジネスロジックのみを検証します。
 */
class OrderServiceTest {

    // 1. 依存オブジェクトのモック（偽物）を作成
    // 本物のDBには繋ぎたくないので、mockk()を使います
    private val orderRepository = mockk<OrderRepository>()
    
    // 2. テスト対象のサービスにモックを渡す
    private val orderService = OrderService(orderRepository)

    @Test
    fun `createOrder should calculate total amount and save order`() {
        // --- 1. 準備 (Given) ---
        // テスト用のリクエストデータを作成
        val request = CreateOrderRequest(
            customerId = 100L,
            items = listOf(
                OrderItemRequest(productId = 1L, quantity = 2), // 単価(1000 + 100) * 2 = 2200
                OrderItemRequest(productId = 2L, quantity = 1)  // 単価(1000 + 200) * 1 = 1200
            )
        )

        // モックの振る舞いを定義
        // 「saveメソッドが呼ばれたら、渡された引数(Order)をそのまま返す」ように設定
        every { orderRepository.save(any()) } answers { firstArg() }

        // --- 2. 実行 (When) ---
        // 実際にメソッドを実行
        val response = orderService.createOrder(request)

        // --- 3. 検証 (Then) ---
        
        // 合計金額の検証: 2200 + 1200 = 3400 になっているはず
        val expectedTotal = BigDecimal("3400") 
        assertEquals(0, expectedTotal.compareTo(response.totalAmount), "合計金額が正しく計算されていること")
        
        // ステータスの検証: 最初は PENDING (処理待ち) であるはず
        assertEquals("PENDING", response.status.name)
        
        // リポジトリの save メソッドが「1回だけ」呼ばれたことを確認
        verify(exactly = 1) { orderRepository.save(any()) }
    }
}