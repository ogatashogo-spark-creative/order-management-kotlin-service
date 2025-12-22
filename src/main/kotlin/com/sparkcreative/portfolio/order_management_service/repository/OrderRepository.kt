package com.sparkcreative.portfolio.order_management_service.repository

import com.sparkcreative.portfolio.order_management_service.model.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 注文リポジトリ
 * データベースへのアクセスを担当します。
 * JpaRepositoryを継承することで、基本的なCRUD（保存、検索、削除など）が自動的に使えるようになります。
 */
@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    // 必要に応じてクエリメソッドを追加（例: 顧客IDで検索など）
    // メソッド名を工夫するだけで、Spring Bootが自動的にSQLを生成してくれます。
    fun findByCustomerId(customerId: Long): List<Order>
}