package io.hhplus.tdd.point.integration

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.policy.PointPolicy
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import io.hhplus.tdd.point.service.PointService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.*

class PointServiceTest {
    companion object {
        const val TEST_USER_ID: Long = 1
        const val TEST_INITIAL_POINT: Long = 3000
    }

    @Nested
    inner class Charge {
        @Test
        fun `should charge point`() {
            // given
            val userPointRepo: UserPointRepository = UserPointRepository(UserPointTable());
            val pointHistoryRepo: PointHistoryRepository = PointHistoryRepository(PointHistoryTable());
            val pointService: PointService = PointService(
                pointHistoryRepo,
                userPointRepo,
                PointPolicy()
            );

            // when
            val amount: Long = 1000;
            val userPoint = pointService.chargePoint(TEST_USER_ID, amount);

            // then
            assertThat(userPoint.point).isEqualTo(amount);
        }

        @Test
        fun `should write log to point history`() {
            // given
            val userPointRepo: UserPointRepository = UserPointRepository(UserPointTable());
            val pointHistoryRepo: PointHistoryRepository = PointHistoryRepository(PointHistoryTable());
            val pointService: PointService = PointService(
                pointHistoryRepo,
                userPointRepo,
                PointPolicy()
            );

            // when
            val amount: Long = 1000;
            pointService.chargePoint(TEST_USER_ID, amount);

            // then
            val pointHistory = pointHistoryRepo.findAllByUserId(TEST_USER_ID);

            assertThat(pointHistory.size).isEqualTo(1);
            assertThat(pointHistory[0].amount).isEqualTo(amount);
            assertThat(pointHistory[0].type).isEqualTo(TransactionType.CHARGE);
            assertThat(pointHistory[0].userId).isEqualTo(TEST_USER_ID);
        }
    }

    @Nested
    inner class Use {
        @Test
        fun `should use point`() {
            //given
            val userPointRepo: UserPointRepository = UserPointRepository(UserPointTable());
            val pointHistoryRepo: PointHistoryRepository = PointHistoryRepository(PointHistoryTable());
            val pointService: PointService = PointService(
                pointHistoryRepo,
                userPointRepo,
                PointPolicy()
            );
            userPointRepo.upsert(TEST_USER_ID, TEST_INITIAL_POINT);

            // when
            val amount: Long = 1000;
            val updatedUserPoint = pointService.usePoint(TEST_USER_ID, amount);

            // then
            assertThat(updatedUserPoint.point).isEqualTo(TEST_INITIAL_POINT - amount);
        }

        @Test
        fun `should write log to point history`() {
            // given
            val userPointRepo: UserPointRepository = UserPointRepository(UserPointTable());
            val pointHistoryRepo: PointHistoryRepository = PointHistoryRepository(PointHistoryTable());
            val pointService: PointService = PointService(
                pointHistoryRepo,
                userPointRepo,
                PointPolicy()
            );
            userPointRepo.upsert(TEST_USER_ID, TEST_INITIAL_POINT);

            // when
            val amount: Long = 1000;
            pointService.usePoint(TEST_USER_ID, amount);

            // then
            val pointHistory = pointHistoryRepo.findAllByUserId(TEST_USER_ID);
            assertThat(pointHistory.size).isEqualTo(1);
            assertThat(pointHistory[0].amount).isEqualTo(amount);
            assertThat(pointHistory[0].type).isEqualTo(TransactionType.USE);
            assertThat(pointHistory[0].userId).isEqualTo(TEST_USER_ID);
        }
    }

    @Nested
    inner class Get {
        @Test
        fun `should get user point`() {
            // given
            val userPointRepo: UserPointRepository = UserPointRepository(UserPointTable());
            val pointHistoryRepo: PointHistoryRepository = PointHistoryRepository(PointHistoryTable());
            val pointService: PointService = PointService(
                pointHistoryRepo,
                userPointRepo,
                PointPolicy()
            );
            userPointRepo.upsert(
                TEST_USER_ID,
                TEST_INITIAL_POINT,
            );

            // when
            val userPoint = pointService.getUserPoint(TEST_USER_ID);

            // then
            assertThat(userPoint.id).isEqualTo(TEST_USER_ID);
            assertThat(userPoint.point).isEqualTo(TEST_INITIAL_POINT);
        }

        @Test
        fun `should get user point history`() {
            // given
            val userPointRepo: UserPointRepository = UserPointRepository(UserPointTable());
            val pointHistoryRepo: PointHistoryRepository = PointHistoryRepository(PointHistoryTable());
            val pointService: PointService = PointService(
                pointHistoryRepo,
                userPointRepo,
                PointPolicy()
            );

            pointHistoryRepo.create(TEST_USER_ID, 1000, TransactionType.CHARGE);
            pointHistoryRepo.create(TEST_USER_ID, 500, TransactionType.USE);

            // when
            val pointHistory = pointService.getUserPointHistory(TEST_USER_ID);

            // then
            assertThat(pointHistory.size).isEqualTo(2);
            assertThat(pointHistory[0].amount).isEqualTo(1000);
            assertThat(pointHistory[0].type).isEqualTo(TransactionType.CHARGE);
            assertThat(pointHistory[0].userId).isEqualTo(TEST_USER_ID);
            assertThat(pointHistory[1].amount).isEqualTo(500);
            assertThat(pointHistory[1].type).isEqualTo(TransactionType.USE);
            assertThat(pointHistory[1].userId).isEqualTo(TEST_USER_ID);
        }
    }
}