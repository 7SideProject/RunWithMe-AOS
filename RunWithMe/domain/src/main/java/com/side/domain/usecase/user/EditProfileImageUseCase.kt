package com.side.domain.usecase.user

import com.side.domain.repository.UserRepository
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    operator fun invoke(userSeq:Long, image: MultipartBody.Part) = userRepository.editProfileImage(userSeq, image)
}