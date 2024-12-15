package io.hhplus.tdd.point.unit

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PointPolicyTest {
    @Nested
    inner class Charge {
        @Nested
        inner class ThrowIllegalArgumentException {
            @Test
            fun `charge amount less than MIN_AVAILABLE_POINT`() {}

            @Test
            fun `charge amount more than MAX_AVAILABLE_POINT`() {}
        }
    }

    @Nested
    inner class Use {
        @Nested inner class ThrowIllegalArgumentException {
            @Test
            fun `use amount less than MIN_AVAILABLE_POINT`() {}

            @Test
            fun `use amount more than current point`() {}
        }
    }
}