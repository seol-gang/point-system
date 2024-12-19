package io.hhplus.tdd.point.unit

import io.hhplus.tdd.point.dto.entities.UserPoint
import io.hhplus.tdd.point.policy.PointPolicy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PointPolicyTest {
    @Nested
    inner class Charge {
        @Nested
        inner class ThrowIllegalArgumentException {
            @Test
            fun `charge amount less than MIN_AVAILABLE_POINT`() {
                val policy = PointPolicy()
                val userPoint = UserPoint(
                    0, 0, 0
                );
                val amount: Long = 999;

                assertThrows<IllegalArgumentException> {
                    policy.validateCharge(
                        userPoint, amount
                    )
                }
            }

            @Test
            fun `charge amount more than MAX_AVAILABLE_POINT`() {
                val policy = PointPolicy()
                val userPoint =
                    UserPoint(0, 500000, 0)
                val amount: Long = 700000

                assertThrows<IllegalArgumentException> {
                    policy.validateCharge(
                        userPoint, amount
                    )
                }
            }
        }
    }

    @Nested
    inner class Use {
        @Nested
        inner class ThrowIllegalArgumentException {
            @Test
            fun `use amount less than MIN_AVAILABLE_POINT`() {
                val policy = PointPolicy()
                val userPoint = UserPoint(
                    0, 1000, 0
                )
                val amount: Long = 999

                assertThrows<IllegalArgumentException> {
                    policy.validateUse(
                        userPoint, amount
                    )
                }
            }

            @Test
            fun `use amount more than current point`() {
                val policy = PointPolicy()
                val userPoint = UserPoint(
                    0, 1000, 0
                )
                val amount: Long = 1001

                assertThrows<IllegalArgumentException> {
                    policy.validateUse(
                        userPoint, amount
                    )
                }
            }
        }
    }
}