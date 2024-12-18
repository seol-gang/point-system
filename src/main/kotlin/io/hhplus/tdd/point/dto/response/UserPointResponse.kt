package io.hhplus.tdd.point.dto.response

import io.hhplus.tdd.point.UserPoint

class UserPointResponse(
    val id: Long, val point: Long
) {
    companion object {
        fun toResponse(userPoint: UserPoint) = UserPointResponse(
            id = userPoint.id,
            point = userPoint.point
        )
    }
}