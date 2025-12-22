package com.sparkcreative.portfolio.order_management_service.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

// --- Entities (DBテーブル定義) ---

/**
 * 注文エンティティ (T-3: データ永続化)
 * データベースの "orders" テーブルに対応します。
 */
@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val customerId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: OrderStatus,

    @Column(nullable = false)
    var totalAmount: BigDecimal,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    // 注文には複数の明細(items)が含まれる
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var items: List<OrderItem> = mutableListOf()
)

/**
 * 注文明細エンティティ
 * データベースの "order_items" テーブルに対応します。
 */
@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = false)
    val unitPrice: BigDecimal // その時点での単価を保存
)

/**
 * 注文ステータス
 */
enum class OrderStatus {
    PENDING,   // 処理待ち
    PAID,      // 支払い済み
    SHIPPED,   // 発送済み
    COMPLETED, // 完了
    CANCELLED  // キャンセル
}

// --- DTOs (API入出力定義) ---

/**
 * F-1: 注文作成リクエスト
 * APIが受け取るデータの形です。
 */
data class CreateOrderRequest(
    val customerId: Long,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)

/**
 * F-2: 注文レスポンス
 * APIが返すデータの形です。
 */
data class OrderResponse(
    val orderId: Long,
    val customerId: Long,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val items: List<OrderItemResponse>,
    val createdAt: LocalDateTime
)

data class OrderItemResponse(
    val productId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal
)