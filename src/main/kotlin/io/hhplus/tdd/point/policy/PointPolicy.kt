package io.hhplus.tdd.point.policy

import io.hhplus.tdd.point.dto.entities.UserPoint
import org.springframework.stereotype.Component

@Component
class PointPolicy {
    companion object {
        const val MAX_AVAILABLE_POINT = 1000000L;
        const val MIN_AVAILABLE_POINT = 1000L;
    }

    fun validateCharge(
        userPoint: UserPoint,
        amount: Long
    ) {
        if (amount < MIN_AVAILABLE_POINT) {
            throw IllegalArgumentException("Charge amount must be greater than $MIN_AVAILABLE_POINT points")
        }

        if (amount > MAX_AVAILABLE_POINT) {
            throw IllegalArgumentException("Cannot exceed maximum available point limit")
        }
    }

    fun validateUse(
        userPoint: UserPoint,
        amount: Long
    ) {
        if (amount < MIN_AVAILABLE_POINT) {
            throw IllegalArgumentException("Use amount must be greater than $MIN_AVAILABLE_POINT points")
        }

        if (amount > userPoint.point) {
            throw IllegalArgumentException("Cannot use more points than your current point")
        }
    }
}