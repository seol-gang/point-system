package io.hhplus.tdd.point.controller

import io.hhplus.tdd.point.dto.entities.PointHistory
import io.hhplus.tdd.point.dto.response.UserPointResponse
import io.hhplus.tdd.point.service.PointService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(
    private val pointService: PointService,
) {
    private val logger: Logger = LoggerFactory.getLogger(
        javaClass
    )

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    fun point(@PathVariable id: Long): ResponseEntity<UserPointResponse> {
        val userPoint = pointService.getUserPoint(
            id
        )
        return ResponseEntity.ok(
            UserPointResponse.toResponse(
                userPoint
            )
        )
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    fun history(@PathVariable id: Long): ResponseEntity<List<PointHistory>> {
        val pointHistories = pointService.getUserPointHistory(
            id
        )
        return ResponseEntity.ok(pointHistories)
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long
    ): ResponseEntity<UserPointResponse> {
        val chargedUserPoint = pointService.chargePoint(
            id, amount
        )
        return ResponseEntity.ok(
            UserPointResponse.toResponse(
                chargedUserPoint
            )
        )
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): ResponseEntity<UserPointResponse> {
        val usedUserPoint = pointService.usePoint(
            id, amount
        )
        return ResponseEntity.ok(
            UserPointResponse.toResponse(
                usedUserPoint
            )
        )
    }
}