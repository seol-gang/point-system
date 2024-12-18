package io.hhplus.tdd.point.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.dto.entities.PointHistory
import io.hhplus.tdd.point.dto.entities.TransactionType
import org.springframework.stereotype.Repository

@Repository
class PointHistoryRepository(private val pointHistoryConn: PointHistoryTable) {
    fun create(
        userId: Long,
        amount: Long,
        transactionType: TransactionType
    ): PointHistory {
        return pointHistoryConn.insert(
            userId,
            amount,
            transactionType,
            0
        );
    }

    fun findAllByUserId(userId: Long): List<PointHistory> {
        return pointHistoryConn.selectAllByUserId(
            userId
        );
    }
}