package com.jdefey.upsplash.data

import net.openid.appauth.ResponseTypeValues

object AuthConfig {

    const val AUTH_URI = "https://unsplash.com/oauth/authorize"
    const val TOKEN_URI = "https://unsplash.com/oauth/token"
    const val LOGOUT_URI = "https://unsplash.com/oauth/authorize"
    const val END_POINT_URI = "https://unsplash.com/oauth/authorize"

    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val SCOPE = "public read_user write_user read_photos write_likes read_collections"
    const val CLIENT_ID = "Your client ID"
    const val CLIENT_SECRET = "Your client Secret code"
    const val CALLBACK_END_POINT = "upsplash://com.jdefey.upsplash/callback"

}