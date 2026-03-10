package com.example.encuentratumanitas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

val PrimaryGreen    = Color(0xFF155E38)
val SecondaryYellow = Color(0xFFF59E0B)

@Composable
fun AuthScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToManitas: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navegar según rol cuando el estado cambie
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.NavigateToRole -> when (state.role) {
                UserRole.ADMIN   -> onNavigateToAdmin()
                UserRole.MANITAS -> onNavigateToManitas()
                UserRole.CLIENT  -> onNavigateToDashboard()
            }
            else -> Unit
        }
    }

    // Comprobar sesión existente al entrar
    LaunchedEffect(Unit) { viewModel.checkExistingSession() }

    var isLoginMode by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PrimaryGreen, Color(0xFF0D3D24))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo / título
            Text(
                text = "🔧",
                fontSize = 48.sp
            )
            Text(
                text = "Un Manitas\ncerca de ti",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tarjeta del formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Tabs Login / Registro
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TabButton(
                            text = "Iniciar Sesión",
                            selected = isLoginMode,
                            modifier = Modifier.weight(1f),
                            onClick = { isLoginMode = true; viewModel.clearError() }
                        )
                        TabButton(
                            text = "Registrarse",
                            selected = !isLoginMode,
                            modifier = Modifier.weight(1f),
                            onClick = { isLoginMode = false; viewModel.clearError() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoginMode) {
                        LoginForm(
                            isLoading = uiState is AuthUiState.Loading,
                            onLogin   = { email, pass -> viewModel.signIn(email, pass) }
                        )
                    } else {
                        RegisterForm(
                            isLoading  = uiState is AuthUiState.Loading,
                            onRegister = { email, pass, confirm, name, role ->
                                viewModel.signUp(email, pass, confirm, name, role)
                            }
                        )
                    }

                    // Error message
                    AnimatedVisibility(visible = uiState is AuthUiState.Error) {
                        val msg = (uiState as? AuthUiState.Error)?.message ?: ""
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = msg,
                                color = Color(0xFFC62828),
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Loading overlay
        if (uiState is AuthUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

// ─── Login Form ──────────────────────────────────────────────────────────────
@Composable
fun LoginForm(isLoading: Boolean, onLogin: (String, String) -> Unit) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    AuthTextField(value = email, onValueChange = { email = it },
        label = "Email", keyboardType = KeyboardType.Email)
    Spacer(modifier = Modifier.height(12.dp))
    AuthTextField(
        value = password, onValueChange = { password = it },
        label = "Contraseña",
        keyboardType = KeyboardType.Password,
        isPassword = true, showPassword = showPass,
        onTogglePassword = { showPass = !showPass }
    )
    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = { onLogin(email, password) },
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text("Iniciar Sesión", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

// ─── Register Form ───────────────────────────────────────────────────────────
@Composable
fun RegisterForm(
    isLoading: Boolean,
    onRegister: (String, String, String, String, UserRole) -> Unit
) {
    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPass        by remember { mutableStateOf(false) }
    var selectedRole    by remember { mutableStateOf(UserRole.CLIENT) }

    AuthTextField(value = fullName, onValueChange = { fullName = it }, label = "Nombre completo")
    Spacer(modifier = Modifier.height(12.dp))
    AuthTextField(value = email, onValueChange = { email = it },
        label = "Email", keyboardType = KeyboardType.Email)
    Spacer(modifier = Modifier.height(12.dp))
    AuthTextField(
        value = password, onValueChange = { password = it },
        label = "Contraseña", keyboardType = KeyboardType.Password,
        isPassword = true, showPassword = showPass,
        onTogglePassword = { showPass = !showPass }
    )
    Spacer(modifier = Modifier.height(12.dp))
    AuthTextField(
        value = confirmPassword, onValueChange = { confirmPassword = it },
        label = "Confirmar contraseña", keyboardType = KeyboardType.Password,
        isPassword = true, showPassword = showPass,
        onTogglePassword = { showPass = !showPass }
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Selector de rol
    Text(text = "Soy...", fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        RoleCard(
            label = "Cliente",
            emoji = "🏠",
            description = "Necesito un manitas",
            selected = selectedRole == UserRole.CLIENT,
            modifier = Modifier.weight(1f),
            onClick = { selectedRole = UserRole.CLIENT }
        )
        RoleCard(
            label = "Manitas",
            emoji = "🔧",
            description = "Ofrezco mis servicios",
            selected = selectedRole == UserRole.MANITAS,
            modifier = Modifier.weight(1f),
            onClick = { selectedRole = UserRole.MANITAS }
        )
    }

    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = { onRegister(email, password, confirmPassword, fullName, selectedRole) },
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text("Crear cuenta", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

// ─── Componentes reutilizables ───────────────────────────────────────────────
@Composable
fun TabButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) PrimaryGreen else Color(0xFFF3F4F6),
            contentColor   = if (selected) Color.White  else Color(0xFF6B7280)
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        Text(text, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !showPassword)
            PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onTogglePassword?.invoke() }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            focusedLabelColor  = PrimaryGreen
        )
    )
}

@Composable
fun RoleCard(
    label: String,
    emoji: String,
    description: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(2.dp, PrimaryGreen) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFECFDF5) else Color(0xFFF9FAFB)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Text(description, fontSize = 11.sp, color = Color(0xFF6B7280),
                textAlign = TextAlign.Center)
        }
    }
}
