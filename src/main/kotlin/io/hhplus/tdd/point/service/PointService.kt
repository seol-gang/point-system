package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.dto.entities.PointHistory
import io.hhplus.tdd.point.dto.entities.TransactionType
import io.hhplus.tdd.point.dto.entities.UserPoint
import io.hhplus.tdd.point.policy.PointPolicy
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointHistoryRepo: PointHistoryRepository,
    private val userPointRepo: UserPointRepository,
    private val pointPolicy: PointPolicy
) {
    fun getUserPointHistories(userId: Long): List<PointHistory> {
        return pointHistoryRepo.findAllByUserId(
            userId
        )
    }

    fun getUserPoint(userId: Long): UserPoint {
        return userPointRepo.findById(userId)
    }

    @Synchronized
    fun chargePoint(
        userId: Long, amount: Long
    ): UserPoint {
        val userPoint = userPointRepo.findById(
            userId
        )
        pointPolicy.validateCharge(
            userPoint, amount
        )
        val result = userPointRepo.upsert(
            userId, userPoint.point + amount
        )
        pointHistoryRepo.create(
            userId, amount, TransactionType.CHARGE
        )
        return result;
    }

    @Synchronized
    fun usePoint(
        userId: Long, amount: Long
    ): UserPoint {
        val userPoint = userPointRepo.findById(
            userId
        )
        pointPolicy.validateUse(userPoint, amount)
        val result = userPointRepo.upsert(
            userId, userPoint.point - amount
        )
        pointHistoryRepo.create(
            userId, amount, TransactionType.USE
        )
        return result;
    }
}