package com.sparkcreative.portfolio.order_management_service.service

import com.sparkcreative.portfolio.order_management_service.model.*
import com.sparkcreative.portfolio.order_management_service.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository
    // 本来はここに在庫サービスや価格サービスのクライアント(DI)が入ります
) {
    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    /**
     * F-1, T-4: 注文作成処理
     * @Transactionalにより、DB登録と(将来的な)在庫引き当ての整合性を保証します。
     * 万が一エラーが発生した場合は、自動的にロールバック（取り消し）されます。
     */
    @Transactional
    fun createOrder(request: CreateOrderRequest): OrderResponse {
        logger.info("Starting order creation for customer: ${request.customerId}")

        // 1. F-5: 価格計算ロジック (本来は商品マスタから取得。ここではモック価格を使用)
        val orderItems = request.items.map { itemReq ->
            val mockPrice = getProductPriceMock(itemReq.productId)
            OrderItem(
                productId = itemReq.productId,
                quantity = itemReq.quantity,
                unitPrice = mockPrice
            )
        }

        // 合計金額の計算
        val totalAmount = orderItems.fold(BigDecimal.ZERO) { acc, item ->
            acc.add(item.unitPrice.multiply(BigDecimal(item.quantity)))
        }

        // 2. Q-2: 在庫引き当て (モック処理)
        // 実際には外部サービスを呼び出すか、例外をスローしてロールバックさせます
        reserveStockMock(request.items)

        // 3. 注文エンティティの生成
        val order = Order(
            customerId = request.customerId,
            status = OrderStatus.PENDING,
            totalAmount = totalAmount,
            items = orderItems
        )
        // 親子関係のリンク（JPAで正しく保存するために必要）
        orderItems.forEach { it.order = order }

        // 4. DB保存
        val savedOrder = orderRepository.save(order)
        logger.info("Order created successfully. ID: ${savedOrder.id}")

        // 5. Q-3: 構造化ログ出力 (監査用)
        // 本来はLogstash用のJSONフォーマッタなどを使いますが、簡易的にJSON文字列で出力します
        logger.info("""{"event": "ORDER_CREATED", "orderId": ${savedOrder.id}, "amount": $totalAmount}""")

        return savedOrder.toResponse()
    }

    /**
     * F-2: 注文取得
     */
    @Transactional(readOnly = true)
    fun getOrder(orderId: Long): OrderResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found with ID: $orderId") }
        return order.toResponse()
    }

    // --- Private Helpers & Mocks (補助メソッドとモック) ---

    private fun getProductPriceMock(productId: Long): BigDecimal {
        // 商品IDに基づいて適当な価格を返すモックロジック
        // (ID * 100) + 1000円
        return BigDecimal(1000).add(BigDecimal(productId * 100))
    }

    private fun reserveStockMock(items: List<OrderItemRequest>) {
        // 在庫連携のモック。
        // 例えば数量が100を超えたら「在庫切れ」としてエラーにする
        if (items.any { it.quantity > 100 }) {
            throw IllegalStateException("Stock limit exceeded for implementation check")
        }
    }

    private fun Order.toResponse(): OrderResponse {
        return OrderResponse(
            orderId = this.id,
            customerId = this.customerId,
            status = this.status,
            totalAmount = this.totalAmount,
            items = this.items.map {
                OrderItemResponse(it.productId, it.quantity, it.unitPrice)
            },
            createdAt = this.createdAt
        )
    }
}