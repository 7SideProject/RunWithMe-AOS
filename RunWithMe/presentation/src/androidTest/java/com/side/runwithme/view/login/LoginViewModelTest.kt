//package com.side.runwithme.view.login
//
//import com.google.common.truth.Truth.assertThat
//import com.side.domain.repository.UserRepository
//import com.side.domain.usecase.user.LoginWithEmailUseCase
//import com.side.runwithme.util.MainDispatcherRule
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.test.runTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import javax.inject.Inject
//import javax.inject.Named
//
//@HiltAndroidTest
//class LoginViewModelTest {
//
//    @Inject
//    lateinit var loginWithEmailUseCase: LoginWithEmailUseCase
//
//    private lateinit var loginViewModel: LoginViewModel
//
//    @get:Rule
//    val mainDispatcherRule = MainDispatcherRule()
//
//    @get:Rule
//    val hiltRule = HiltAndroidRule(this)
//
//    @Before
//    fun setUp() {
//        hiltRule.inject()
//        loginViewModel = LoginViewModel(loginWithEmailUseCase)
//    }
//
////    @Test
////    @ExperimentalCoroutinesApi
////    fun JoinUser_EmailLogin_returns_Success() = runTest {
////        // Given
////        loginViewModel.apply {
////            email.value = "abcdef@naver.com"
////            password.value = "12341234"
////        }
////
////        // When
////        loginViewModel.loginWithEmail()
////        val result = loginViewModel.loginEventFlow.first()
////
////        // Then
////        assertThat(result).isInstanceOf(LoginViewModel.Event.Success::class.java)
////    }
////
////    @Test
////    @ExperimentalCoroutinesApi
////    fun NotJoinUser_EmailLogin_returns_Fail() = runTest {
////        // Given
////        loginViewModel.apply {
////            email.value = "ab@naver.com"
////            password.value = "123"
////        }
////
////        // When
////        loginViewModel.loginWithEmail()
////        val result = loginViewModel.loginEventFlow.first()
////
////        // Then
////        assertThat(result).isInstanceOf(LoginViewModel.Event.Fail::class.java)
////    }
////
////    @Test
////    @ExperimentalCoroutinesApi
////    fun FaultPasswordUser_EmailLogin_returns_Fail() = runTest {
////        // Given
////        loginViewModel.apply {
////            email.value = "abcdef@naver.com"
////            password.value = "123"
////        }
////
////        // When
////        loginViewModel.loginWithEmail()
////        val result = loginViewModel.loginEventFlow.first()
////
////        // Then
////        assertThat(result).isInstanceOf(LoginViewModel.Event.Fail::class.java)
////    }
//
//}