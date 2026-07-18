//package com.fadhil.taba.ui.auth
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@Composable
//fun LoginScreen(
//    viewModel: AuthViewModel = viewModel(),
//    initialIsSignUp: Boolean = false,
//    onLoginSuccess: () -> Unit
//) {
//    val context = LocalContext.current
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var username by remember { mutableStateOf("") }
//    var isSignUp by remember { mutableStateOf(initialIsSignUp) }
//
//    val user by viewModel.currentUser.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    LaunchedEffect(user) {
//        if (user != null) {
//            onLoginSuccess()
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = if (isSignUp) "Buat Akun TABA" else "Masuk ke TABA",
//            style = MaterialTheme.typography.headlineMedium,
//            color = MaterialTheme.colorScheme.primary
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        if (isSignUp) {
//            OutlinedTextField(
//                value = username,
//                onValueChange = { username = it },
//                label = { Text("Username") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//        }
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true
//        )
//
//        if (error != null) {
//            Spacer(modifier = Modifier.height(8.dp))
//            val errorMessage = when {
//                error!!.contains("email_not_confirmed") -> "Email belum dikonfirmasi. Silakan cek kotak masuk email Anda."
//                error!!.contains("Invalid login credentials") -> "Email atau password salah."
//                error!!.contains("over_email_send_rate_limit") -> "Terlalu banyak permintaan kirim email. Silakan tunggu beberapa menit atau gunakan email lain."
//                error!!.contains("User already registered") -> "Email ini sudah terdaftar. Silakan masuk."
//                else -> error!!
//            }
//            Text(
//                text = errorMessage,
//                color = MaterialTheme.colorScheme.error,
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.padding(horizontal = 8.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(
//            onClick = {
//                if (isSignUp) {
//                    viewModel.signUp(email, password, username)
//                } else {
//                    viewModel.signIn(email, password)
//                }
//            },
//            enabled = !isLoading,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            shape = MaterialTheme.shapes.medium
//        ) {
//            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
//            else Text(if (isSignUp) "Daftar Sekarang" else "Masuk")
//        }
//
//        TextButton(onClick = { isSignUp = !isSignUp }) {
//            Text(if (isSignUp) "Sudah punya akun? Masuk di sini" else "Belum punya akun? Daftar gratis")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            HorizontalDivider(modifier = Modifier.weight(1f))
//            Text(" atau ", modifier = Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//            HorizontalDivider(modifier = Modifier.weight(1f))
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedButton(
//            onClick = {
//                // Ganti dengan Web Client ID Anda dari Google Cloud Console
//                viewModel.signInWithGoogle(context, "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            shape = MaterialTheme.shapes.medium
//        ) {
//            Text("Lanjutkan dengan Google")
//        }
//    }
//}
