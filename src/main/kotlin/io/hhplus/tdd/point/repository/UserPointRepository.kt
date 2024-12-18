package io.hhplus.tdd.point.repository

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.dto.entities.UserPoint
import org.springframework.stereotype.Repository

@Repository
class UserPointRepository(private val userPointConn: UserPointTable) {
    fun upsert(
        userId: Long,
        amount: Long
    ): UserPoint {
        return userPointConn.insertOrUpdate(
            userId,
            amount
        );
    }

    fun findById(userId: Long): UserPoint {
        return userPointConn.selectById(userId);
    }
}
