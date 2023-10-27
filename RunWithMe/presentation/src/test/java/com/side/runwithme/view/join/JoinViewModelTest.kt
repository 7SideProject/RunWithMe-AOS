package com.side.runwithme.view.join

import androidx.test.filters.SmallTest
import com.side.domain.usecase.user.JoinUseCase
import io.mockk.mockk
import com.google.common.truth.Truth.assertThat
import com.side.runwithme.util.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
class JoinViewModelTest {

    private lateinit var joinViewModel: JoinViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        val joinUseCase = mockk<JoinUseCase>()
        joinViewModel = JoinViewModel(joinUseCase)
    }

    @Test
    fun `Speical Character NickName join return fail`() = runTest{

        // Given
        joinViewModel.nickname.value = "***"

        val eventFlow = joinViewModel.join3EventFlow
            .flowOn(Dispatchers.Default) // 특정 스레드에서 Flow 실행


        // When
        joinViewModel.join() // joinViewModel.join() 실행

        val event = eventFlow.first() // Flow가 완료된 후 List의 마지막 값을 가져옴


        // Then
        assertThat(event).isInstanceOf(JoinViewModel.JoinEvent.Fail::class.java)
    }

    @Test
    fun `Blank NickName join return fail`() = runTest{

        // Given
        joinViewModel.nickname.value = ""

        val eventFlow = joinViewModel.join3EventFlow
            .flowOn(Dispatchers.Default) // 특정 스레드에서 Flow 실행


        // When
        joinViewModel.join() // joinViewModel.join() 실행

        val event = eventFlow.first() // Flow가 완료된 후 List의 마지막 값을 가져옴


        // Then
        assertThat(event).isInstanceOf(JoinViewModel.JoinEvent.Fail::class.java)
    }

    @Test
    fun `BlankNickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("")

        assertThat(result).isFalse()
    }

    @Test
    fun `One Length English NickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("a")

        assertThat(result).isFalse()
    }

    @Test
    fun `One Length Korean NickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("아")

        assertThat(result).isFalse()
    }

    @Test
    fun `One Length Number NickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("2")

        assertThat(result).isFalse()
    }

    @Test
    fun `One Length Special Character NickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("*")

        assertThat(result).isFalse()
    }

    @Test
    fun `Two Length English NickName matchesNickNameRule return true`() {
        val result = joinViewModel.matchesNickNameRule("ab")

        assertThat(result).isTrue()
    }

    @Test
    fun `Two Length Korean NickName matchesNickNameRule return true`() {
        val result = joinViewModel.matchesNickNameRule("아이")

        assertThat(result).isTrue()
    }

    @Test
    fun `Two Length Number NickName matchesNickNameRule return true`() {
        val result = joinViewModel.matchesNickNameRule("23")

        assertThat(result).isTrue()
    }

    @Test
    fun `6 Length Special Character NickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("123&45")

        assertThat(result).isFalse()
    }


    @Test
    fun `8 Length English NickName matchesNickNameRule return true`() {
        val result = joinViewModel.matchesNickNameRule("abcdefgh")

        assertThat(result).isTrue()
    }

    @Test
    fun `8 Length Korean NickName matchesNickNameRule return true`() {
        val result = joinViewModel.matchesNickNameRule("아이우에오아이우")

        assertThat(result).isTrue()
    }

    @Test
    fun `8 Length Number NickName matchesNickNameRule return true`() {
        val result = joinViewModel.matchesNickNameRule("12345678")

        assertThat(result).isTrue()
    }

    @Test
    fun `9 Length English NickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("abcdefghi")

        assertThat(result).isFalse()
    }

    @Test
    fun `9 Length Korean NickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("아이우에오아이우에")

        assertThat(result).isFalse()
    }

    @Test
    fun `9 Length Number NickName matchesNickNameRule return false`() {
        val result = joinViewModel.matchesNickNameRule("123456789")

        assertThat(result).isFalse()
    }
}