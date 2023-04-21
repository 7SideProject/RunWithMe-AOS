package com.side.runwithme.view.login


import com.google.common.truth.Truth.assertThat
import com.side.domain.repository.UserRepository
import com.side.domain.usecase.user.LoginUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

class LoginViewModelTest {

//    @Inject
//    @Named("test_repo")
//    lateinit var userRepository: UserRepository
//    val usecase = LoginUseCase(userRepository)
//
//    @MockK
//    private lateinit var useCase: LoginUseCase
//
//    val uc : LoginUseCase = mockk()
//
//    @get:Rule
//    var viewModel : LoginViewModel = LoginViewModel(uc)
//
//    @Test
//    fun `회원가입안한유저 login return email`() = runTest {
//
//        val result = uc.invoke("예시", "예시").first()
//
//        every { uc.invoke("예시", "예시") } returns "예시"
//
//        assertThat(result).isEqualTo(true)
//        verify { uc.invoke("예시", "예시") }
//    }

}