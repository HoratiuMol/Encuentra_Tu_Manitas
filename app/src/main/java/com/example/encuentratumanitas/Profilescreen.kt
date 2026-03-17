package com.example.encuentratumanitas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

private val PGreen   = Color(0xFF155E38)
private val PAmber   = Color(0xFFF59E0B)
private val PBgGray  = Color(0xFFF3F4F6)
private val PWhite   = Color.White
private val PDark    = Color(0xFF111827)
private val PMuted   = Color(0xFF6B7280)
private val PRed     = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Campos editables — se inicializan cuando carga el perfil
    var fullName    by remember { mutableStateOf("") }
    var city        by remember { mutableStateOf("") }
    var bio         by remember { mutableStateOf("") }
    var specialties by remember { mutableStateOf("") }
    var avatarUri   by remember { mutableStateOf<Uri?>(null) }

    // Inicializar campos cuando el perfil carga
    LaunchedEffect(uiState.profile) {
        uiState.profile?.let { p ->
            fullName    = p.fullName
            city        = p.city ?: ""
            bio         = p.bio ?: ""
            specialties = p.specialties ?: ""
        }
    }

    // Volver atrás al guardar con éxito
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSaveSuccess()
            onNavigateBack()
        }
    }

    // Launcher para seleccionar imagen de galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> avatarUri = uri }

    val isManitas = uiState.profile?.role == "manitas"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil", fontWeight = FontWeight.Bold, color = PWhite) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = PWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PGreen)
            )
        },
        containerColor = PBgGray
    ) { padding ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PGreen)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Foto de perfil ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        // Avatar
                        if (avatarUri != null) {
                            AsyncImage(
                                model = avatarUri,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, PGreen, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else if (!uiState.profile?.avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = uiState.profile?.avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, PGreen, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Placeholder con inicial
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(PGreen)
                                    .border(3.dp, PGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fullName.firstOrNull()?.uppercase() ?: "?",
                                    color = PWhite,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Botón editar foto
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PAmber)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt, null,
                                tint = PWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Toca el icono para cambiar tu foto",
                        fontSize = 12.sp,
                        color = PMuted,
                        textAlign = TextAlign.Center
                    )

                    // Badge de rol
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isManitas) PGreen.copy(alpha = 0.12f) else PAmber.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = if (isManitas) "🔧 Manitas profesional" else "🏠 Cliente",
                            color = if (isManitas) PGreen else PAmber,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // ── Información personal ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(icon = Icons.Default.Person, title = "Información personal")
                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Nombre completo",
                        icon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "Ciudad",
                        icon = Icons.Default.LocationOn
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = "Bio / descripción",
                        icon = Icons.Default.Info,
                        singleLine = false,
                        minLines = 3
                    )
                }
            }

            // ── Especialidades (solo manitas) ─────────────────────────────
            if (isManitas) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(icon = Icons.Default.Build, title = "Especialidades")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Selecciona las categorías en las que trabajas",
                            fontSize = 12.sp,
                            color = PMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Chips de especialidades
                        val selectedSpecs = remember(specialties) {
                            specialties.split(",").map { it.trim() }.filter { it.isNotBlank() }.toMutableStateList()
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Fila 1
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Fontanería", "Electricidad", "Carpintería").forEach { spec ->
                                    val selected = selectedSpecs.contains(spec)
                                    SpecialtyChip(
                                        label = spec,
                                        selected = selected,
                                        onClick = {
                                            if (selected) selectedSpecs.remove(spec)
                                            else selectedSpecs.add(spec)
                                            specialties = selectedSpecs.joinToString(", ")
                                        }
                                    )
                                }
                            }
                            // Fila 2
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Pintura", "Albañilería", "Otros").forEach { spec ->
                                    val selected = selectedSpecs.contains(spec)
                                    SpecialtyChip(
                                        label = spec,
                                        selected = selected,
                                        onClick = {
                                            if (selected) selectedSpecs.remove(spec)
                                            else selectedSpecs.add(spec)
                                            specialties = selectedSpecs.joinToString(", ")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Error ─────────────────────────────────────────────────────
            uiState.error?.let {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = PRed.copy(alpha = 0.08f))
                ) {
                    Text(
                        it, color = PRed, fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // ── Botón guardar ─────────────────────────────────────────────
            Button(
                onClick = {
                    viewModel.saveProfile(fullName, city, bio, specialties, avatarUri, context)
                },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PGreen),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PWhite,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar cambios", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            var showDeleteDialog by remember { mutableStateOf(false) }

            // Botón eliminar cuenta — al final de la Column
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PRed),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar mi cuenta", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

// Diálogo confirmación
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Eliminar cuenta") },
                    text  = { Text("¿Estás seguro? Esta acción es permanente y eliminará todos tus datos.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            viewModel.deleteAccount { onAccountDeleted() }  // ← onAccountDeleted, no onNavigateBack
                        }) { Text("Eliminar", color = PRed) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}

// ─── Componentes reutilizables ────────────────────────────────────────────────

@Composable
fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = PDark, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PDark)
    }
}

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = PMuted) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PGreen,
            focusedLabelColor  = PGreen
        )
    )
}

@Composable
fun SpecialtyChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) PGreen else PWhite,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) PGreen else Color(0xFFD1D5DB)
        )
    ) {
        Text(
            text = label,
            color = if (selected) PWhite else PMuted,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}