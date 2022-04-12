package com.jdefey.upsplash.repository

import android.content.Context
import android.net.Uri
import com.jdefey.upsplash.auth.AuthStateManager
import com.jdefey.upsplash.data.AuthConfig
import com.jdefey.upsplash.data.Networking
import net.openid.appauth.*

class AuthRepository(context: Context) {
    private val authFrag = AuthStateManager(context)
    private lateinit var authState: AuthState
    fun getAuthRequest(): AuthorizationRequest {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTH_URI),
            Uri.parse(AuthConfig.TOKEN_URI),
        )
        val redirectUri = Uri.parse(AuthConfig.CALLBACK_END_POINT)
        authState = AuthState(serviceConfiguration)
        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri,
        )
            .setScope(AuthConfig.SCOPE)
            .setCodeVerifier(null)
            .build()
    }

    fun  clearTokenLogout(){
        authFrag.clearAuthState()
    }

    fun authAppLogout(): EndSessionRequest {
        val serviceConfiguration =
            AuthorizationServiceConfiguration(
                Uri.parse(AuthConfig.AUTH_URI),
                Uri.parse(AuthConfig.TOKEN_URI),
                Uri.parse(AuthConfig.END_POINT_URI),
                Uri.parse(AuthConfig.LOGOUT_URI)
            )
        authState = AuthState(serviceConfiguration)
        val endSessionRedirectUri = Uri.parse(AuthConfig.CALLBACK_END_POINT)
        return EndSessionRequest.Builder(serviceConfiguration)
            .setIdTokenHint(Networking.token)
            .setPostLogoutRedirectUri(endSessionRedirectUri)
            .build()
    }

    fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: () -> Unit,
        onError: () -> Unit,
    ) {
        authService.performTokenRequest(
            tokenRequest,
            getClientAuthentication()
        ) { response, ex ->
            when {
                response != null -> {
                    authState.update(response, ex)
                    authFrag.writeState(authState)
                    response.accessToken.orEmpty()
                    Networking.token = authState.accessToken.toString()
                    onComplete()
                }
                else -> onError()
            }
        }
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }

    fun tokenSusses(
        onComplete: () -> Unit,
        onError: () -> Unit,
    ) {
        val auth = authFrag.readState()
        Networking.token = auth.accessToken.toString()
        if (Networking.token.length > 5) {
            onComplete()
        } else {
            onError()
        }
    }
}
