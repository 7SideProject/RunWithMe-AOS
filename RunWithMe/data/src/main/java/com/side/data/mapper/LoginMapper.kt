package com.side.data.mapper

import com.side.data.model.response.KaKaoLoginResponse
import com.side.domain.model.SocialLoginUser

fun KaKaoLoginResponse.mapperToSocialLogin() : SocialLoginUser = SocialLoginUser(
    this.id,
    this.isInitialized
)