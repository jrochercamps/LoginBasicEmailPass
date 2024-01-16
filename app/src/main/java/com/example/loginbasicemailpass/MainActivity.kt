package com.example.loginbasicemailpass

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
//import androidx.compose.material3.icons.filled.VisibilityOff
import com.example.loginbasicemailpass.ui.theme.LoginBasicEmailPassTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth



class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        setContent {
            MaterialTheme {
                // Llama a la función principal del login
                LoginScreen()
            }
        }
    }

    private fun showToast(message: String) {
        // Accede al contexto de la aplicación y muestra un Toast
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun signInWithFirebase(username: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    if(user!=null && user.isEmailVerified){
                        showToast("Login exitoso para usuario: $username. Cuenta verificada por correo.")
                    } else
                        showToast("Login exitoso para usuario: $username. Cuenta no verificada por correo.")
                        sendEmailVerification()
                } else {
                    showToast("Error en la autenticación: ${task.exception?.message}")
                }
            }
    }

    private fun signUpWithFirebase(username: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Usuario creado exitosamente: $username")
                } else {
                    showToast("Error en el registro: ${task.exception?.message}")
                }
            }
    }

    private fun sendEmailVerification() {
        val user = Firebase.auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Se ha enviado un correo de verificación a tu dirección de correo electrónico.")
                } else {
                    showToast("Error al enviar el correo de verificación: ${task.exception?.message}")
                }
            }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun LoginScreen() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo o imagen de la aplicación
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de texto para usuario y contraseña
            var keyboardController = LocalSoftwareKeyboardController.current

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            //var passwordVisibilityIcon = if (isPasswordVisible)
            //  Icons.Default.vi else Icons.Default.Visibility

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                //visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    //imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Puedes realizar alguna acción cuando se presiona "Done"
                        keyboardController?.hide()
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        isPasswordVisible = !isPasswordVisible
                    }) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Botón de login
            Button(
                onClick = {
                    // Realiza la autenticación con Firebase
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        signInWithFirebase(username, password)
                    } else {
                        println("Por favor, ingresa usuario y contraseña")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para dar de alta un nuevo usuario
            IconButton(
                onClick = {
                    signUpWithFirebase(username, password)
                },
                modifier = Modifier
                    .size(48.dp)
            ) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Dar de alta usuario")
            }
        }
    }





    @Preview(showBackground = true)
    @Composable
    fun PreviewLoginScreen() {
        MaterialTheme {
            LoginScreen()
        }
    }
}

