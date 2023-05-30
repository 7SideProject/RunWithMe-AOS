package com.side.runwithme.view.login

import com.google.common.truth.Truth.assertThat
import com.side.domain.model.User
import com.side.domain.repository.UserRepository
import com.side.domain.usecase.user.LoginWithEmailUseCase
import com.side.runwithme.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest() {

    private lateinit var userRepository: UserRepository
    private lateinit var loginViewModel: LoginViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        userRepository = mockk()
        val loginWithEmailUseCase = LoginWithEmailUseCase(userRepository)
        loginViewModel = LoginViewModel(loginWithEmailUseCase)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `User EmailLogin returns Success`() = runTest {
        // Given
        loginViewModel.email.value = "abcdef@naver.com"
        loginViewModel.password.value = "12341234"

        // When
        loginViewModel.loginWithEmail()

        var emptyUser = User(
            seq = 0L,
            email = "",
            password = "",
            nickname = "",
            height = 0,
            weight = 0,
            point = 0,
            profileImgSeq = 0L
        )

        val event = loginViewModel.loginEventFlow.first()
        val result = if (event is LoginViewModel.Event.Success) {
            event.data
        } else {
            emptyUser
        }

        // Then
        // Truth의 isEqualTo는 객체의 필드 값을 비교하여 일치하는 지 확인함
        assertThat(result).isNotEqualTo(emptyUser)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `User EmailLogin returns Fail`() = runTest {
        // Given
        loginViewModel.email.value = "ab@naver.com"
        loginViewModel.password.value = "123"

        // When
        loginViewModel.loginWithEmail()
        val result = loginViewModel.loginEventFlow.first()


        // Then
        assertThat(result).isInstanceOf(LoginViewModel.Event.Fail::class.java)
    }
}