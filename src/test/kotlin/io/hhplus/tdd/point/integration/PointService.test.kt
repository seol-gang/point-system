package io.hhplus.tdd.point.integration

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.dto.entities.TransactionType
import io.hhplus.tdd.point.policy.PointPolicy
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import io.hhplus.tdd.point.service.PointService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PointServiceTest {
    companion object {
        const val TEST_USER_ID: Long = 1
        const val TEST_INITIAL_POINT: Long = 3000
    }

    private lateinit var userPointRepo: UserPointRepository
    private lateinit var pointHistoryRepo: PointHistoryRepository
    private lateinit var pointService: PointService

    @BeforeEach
    fun setUp() {
        userPointRepo = UserPointRepository(
            UserPointTable()
        )
        pointHistoryRepo = PointHistoryRepository(
            PointHistoryTable()
        )
        pointService = PointService(
            pointHistoryRepo,
            userPointRepo,
            PointPolicy()
        )
    }

    @Nested
    inner class Charge {
        @Test
        fun `should charge point`() {
            // given
            val amount: Long = 1000;

            // when
            val userPoint =
                pointService.chargePoint(
                    TEST_USER_ID, amount
                );

            // then
            assertThat(userPoint.point).isEqualTo(
                amount
            );
        }

        @Test
        fun `should write log to point history`() {
            // given
            val amount: Long = 1000;

            // when
            pointService.chargePoint(
                TEST_USER_ID, amount
            );

            // then
            val pointHistory =
                pointHistoryRepo.findAllByUserId(
                    TEST_USER_ID
                );

            assertThat(pointHistory.size).isEqualTo(
                1
            );
            assertThat(pointHistory[0].amount).isEqualTo(
                amount
            );
            assertThat(pointHistory[0].type).isEqualTo(
                TransactionType.CHARGE
            );
            assertThat(pointHistory[0].userId).isEqualTo(
                TEST_USER_ID
            );
        }
    }

    @Nested
    inner class Use {
        @Test
        fun `should use point`() {
            //given
            userPointRepo.upsert(
                TEST_USER_ID, TEST_INITIAL_POINT
            );
            val amount: Long = 1000;

            // when
            val updatedUserPoint =
                pointService.usePoint(
                    TEST_USER_ID, amount
                );

            // then
            assertThat(updatedUserPoint.point).isEqualTo(
                TEST_INITIAL_POINT - amount
            );
        }

        @Test
        fun `should write log to point history`() {
            // given
            userPointRepo.upsert(
                TEST_USER_ID, TEST_INITIAL_POINT
            );

            // when
            val amount: Long = 1000;
            pointService.usePoint(
                TEST_USER_ID, amount
            );

            // then
            val pointHistory =
                pointHistoryRepo.findAllByUserId(
                    TEST_USER_ID
                );
            assertThat(pointHistory.size).isEqualTo(
                1
            );
            assertThat(pointHistory[0].amount).isEqualTo(
                amount
            );
            assertThat(pointHistory[0].type).isEqualTo(
                TransactionType.USE
            );
            assertThat(pointHistory[0].userId).isEqualTo(
                TEST_USER_ID
            );
        }
    }

    @Nested
    inner class Get {
        @Test
        fun `should get user point`() {
            // given
            userPointRepo.upsert(
                TEST_USER_ID,
                TEST_INITIAL_POINT,
            );

            // when
            val userPoint =
                pointService.getUserPoint(
                    TEST_USER_ID
                );

            // then
            assertThat(userPoint.id).isEqualTo(
                TEST_USER_ID
            );
            assertThat(userPoint.point).isEqualTo(
                TEST_INITIAL_POINT
            );
        }

        @Test
        fun `should get user point history`() {
            // given
            pointHistoryRepo.create(
                TEST_USER_ID,
                1000,
                TransactionType.CHARGE
            );
            pointHistoryRepo.create(
                TEST_USER_ID,
                500,
                TransactionType.USE
            );

            // when
            val pointHistory =
                pointService.getUserPointHistories(
                    TEST_USER_ID
                );

            // then
            assertThat(pointHistory.size).isEqualTo(
                2
            );
            assertThat(pointHistory[0].amount).isEqualTo(
                1000
            );
            assertThat(pointHistory[0].type).isEqualTo(
                TransactionType.CHARGE
            );
            assertThat(pointHistory[0].userId).isEqualTo(
                TEST_USER_ID
            );
            assertThat(pointHistory[1].amount).isEqualTo(
                500
            );
            assertThat(pointHistory[1].type).isEqualTo(
                TransactionType.USE
            );
            assertThat(pointHistory[1].userId).isEqualTo(
                TEST_USER_ID
            );
        }
    }
}