import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.policy.PointPolicy
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import io.hhplus.tdd.point.service.PointService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class PointServiceConcurrencyTest {
    private val pointService: PointService =
        PointService(
            pointHistoryRepo = PointHistoryRepository(
                PointHistoryTable()
            ),
            userPointRepo = UserPointRepository(
                UserPointTable()
            ),
            pointPolicy = PointPolicy()
        )

    @Test
    fun `should prevent exceeding point balance during concurrent usage requests`() {
        // given
        val userId: Long = 1
        val initialAmount: Long = 3000
        pointService.chargePoint(
            userId, initialAmount
        )

        val count = 2
        val latch =
            CountDownLatch(count) // 각각의 쓰레드의 동작 상태 관리 해주기 위한 객체
        val useAmount: Long = 3000
        val capturedError =
            AtomicReference<Exception>() // 멀티 쓰레드에서 일반 변수 사용 시 동시성 문제가 발생 할 수 있기 때문에 Atomic type의 변수 사용

        // when
        repeat(count) {
            thread {
                try {
                    pointService.usePoint(
                        userId, useAmount
                    )
                } catch (e: Exception) {
                    capturedError.set(e)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await() // 각 스레드에서 coundDown이 되어 0이 될 때 까지 기다림

        // then
        val result = pointService.getUserPoint(
            userId
        )

        assertThat(capturedError.get()).isInstanceOf(
            IllegalArgumentException::class.java
        )
        assertEquals(
            result.point, 0
        )
    }
}
