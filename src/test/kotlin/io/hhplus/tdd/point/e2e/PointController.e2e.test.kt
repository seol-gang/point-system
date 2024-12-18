package io.hhplus.tdd.point.e2e

import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PointControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userPointRepository: UserPointRepository

    @Autowired
    private lateinit var pointHistoryRepository: PointHistoryRepository

    companion object {
        const val BASE_URL = "/point"
        const val TEST_USER_ID: Long = 1
        const val TEST_INITIAL_POINT: Long = 3000
    }

    @BeforeEach
    fun setUp() {
        userPointRepository.upsert(
            TEST_USER_ID, TEST_INITIAL_POINT
        )
    }

    @Test
    fun `should get user point (200)`() {
        // when & then
        mockMvc.perform(get("$BASE_URL/$TEST_USER_ID"))
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id").value(
                    TEST_USER_ID
                )
            )
            .andExpect(
                jsonPath("$.point").value(
                    TEST_INITIAL_POINT
                )
            )

    }

    @Test
    fun `get point histories (200)`() {
        // given
        pointHistoryRepository.create(
            TEST_USER_ID,
            TEST_INITIAL_POINT,
            TransactionType.CHARGE
        )

        val useAmount: Long = 1000
        pointHistoryRepository.create(
            TEST_USER_ID,
            useAmount,
            TransactionType.USE
        )

        // when & then
        mockMvc.perform(get("$BASE_URL/$TEST_USER_ID/histories"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(
                jsonPath("$.length()").value(2)
            )
            .andExpect(
                jsonPath("$[0].userId").value(
                    TEST_USER_ID
                )
            )
            .andExpect(
                jsonPath("$[0].amount").value(
                    TEST_INITIAL_POINT
                )
            )
            .andExpect(
                jsonPath("$[0].type").value(
                    TransactionType.CHARGE.toString()
                )
            )
            .andExpect(
                jsonPath("$[1].userId").value(
                    TEST_USER_ID
                )
            )
            .andExpect(
                jsonPath("$[1].amount").value(
                    useAmount
                )
            )
            .andExpect(
                jsonPath("$[1].type").value(
                    TransactionType.USE.toString()
                )
            )
    }

    @Test
    fun `should charge point (200)`() {
        // given
        val amount: Long = 1000

        // when & then
        mockMvc.perform(
            patch("$BASE_URL/$TEST_USER_ID/charge").contentType(
                MediaType.APPLICATION_JSON
            ).content(amount.toString())
        ).andExpect(status().isOk).andExpect(
            jsonPath("$.id").value(
                TEST_USER_ID
            )
        ).andExpect(
            jsonPath("$.point").value(
                TEST_INITIAL_POINT + amount
            )
        )
    }

    @Test
    fun `throw exception when charge point under AVAILABLE_POINT_MINIMUM (400)`() {
        // given
        val amount: Long = 100

        // when & then
        mockMvc.perform(
            patch("$BASE_URL/$TEST_USER_ID/charge").contentType(
                MediaType.APPLICATION_JSON
            ).content(amount.toString())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `throw exception when charge point over AVAILABLE_POINT_MAXIMUM (400)`() {
        // given
        val amount: Long = 10000000

        // when & then
        mockMvc.perform(
            patch("$BASE_URL/$TEST_USER_ID/charge").contentType(
                MediaType.APPLICATION_JSON
            ).content(amount.toString())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should use point (200)`() {
        // given
        val useAmount: Long = 1000

        // when & then
        mockMvc.perform(
            patch("$BASE_URL/$TEST_USER_ID/use").contentType(
                MediaType.APPLICATION_JSON
            ).content(useAmount.toString())
        ).andExpect(status().isOk).andExpect(
            jsonPath("$.point").value(
                TEST_INITIAL_POINT - useAmount
            )
        )
    }

    @Test
    fun `throw exception when use point over available point (400)`() {
        // given
        val useAmount: Long = 4000

        // when & then
        mockMvc.perform(
            patch("$BASE_URL/$TEST_USER_ID/use").contentType(
                MediaType.APPLICATION_JSON
            ).content(useAmount.toString())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `throw exception when use point under AVAILABLE_POINT_MINIMUM (400)`() {
        // given
        val useAmount: Long = 100

        // when & then
        mockMvc.perform(
            patch("$BASE_URL/$TEST_USER_ID/use").contentType(
                MediaType.APPLICATION_JSON
            ).content(useAmount.toString())
        ).andExpect(status().isBadRequest)
    }
}
