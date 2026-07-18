package com.fadhil.taba.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.fadhil.taba.data.SupabaseModule
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

class AuthRepository {
    private val auth = SupabaseModule.client.auth
    private val repositoryScope = CoroutineScope(Dispatchers.Main)
    
    private val _currentUser = MutableStateFlow<UserInfo?>(auth.currentUserOrNull())
    val currentUser: StateFlow<UserInfo?> = _currentUser

    init {
        repositoryScope.launch {
            auth.sessionStatus.collectLatest { status ->
                when (status) {
                    is SessionStatus.Authenticated -> _currentUser.value = status.session.user
                    is SessionStatus.NotAuthenticated -> _currentUser.value = null
                    else -> {}
                }
            }
        }
    }

    suspend fun signUpWithEmail(emailVal: String, passwordVal: String, usernameVal: String) {
        auth.signUpWith(Email) {
            email = emailVal
            password = passwordVal
            data = buildJsonObject {
                put("username", usernameVal)
            }
        }
        _currentUser.value = auth.currentUserOrNull()
    }

    suspend fun signInWithEmail(emailVal: String, passwordVal: String) {
        auth.signInWith(Email) {
            email = emailVal
            password = passwordVal
        }
        _currentUser.value = auth.currentUserOrNull()
    }

    suspend fun signInWithGoogle(context: Context, serverClientId: String) {
        val credentialManager = CredentialManager.create(context)
        
        // Kita coba buat opsi TANPA nonce dulu untuk testing jika macet
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        println("TABA_DEBUG: Memunculkan Google Picker...")
        val result = try {
            credentialManager.getCredential(context, request)
        } catch (e: Exception) {
            println("TABA_DEBUG: Google Picker Error: ${e.message}")
            throw Exception("Gagal mendapatkan akun Google: ${e.message}")
        }
        
        val credential = result.credential
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken
            println("TABA_DEBUG: Token diterima. Mengirim ke Supabase...")
            
            try {
                auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    // Nonce dilewati dulu
                }
                
                val user = auth.currentUserOrNull()
                if (user != null) {
                    println("TABA_DEBUG: Login Supabase BERHASIL: ${user.email}")
                    _currentUser.value = user
                } else {
                    throw Exception("Login sukses tapi data user kosong.")
                }
            } catch (e: Exception) {
                println("TABA_DEBUG: Error Supabase: ${e.message}")
                throw e
            }
        } else {
            throw Exception("Tipe login tidak didukung: ${credential.type}")
        }
    }

    suspend fun signOut(context: Context) {
        auth.signOut()
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        _currentUser.value = null
    }
}
