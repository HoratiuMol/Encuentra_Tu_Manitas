package com.example.encuentratumanitas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val AppGreen  = Color(0xFF155E38)
private val AppAmber  = Color(0xFFF59E0B)
private val BgGray    = Color(0xFFF3F4F6)
private val CardWhite = Color.White
private val TextDark  = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)
private val RedDelete = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboardScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ClientDashboardViewModel = viewModel()
) {
    val uiState        by viewModel.uiState.collectAsStateWithLifecycle()
    val createJobState by viewModel.createJobState.collectAsStateWithLifecycle()
    var showCreateDialog  by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(createJobState) {
        if (createJobState is CreateJobState.Success) {
            showCreateDialog = false
            viewModel.resetCreateJobState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Un Manitas cerca de ti",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                actions = {
                    OutlinedButton(
                        onClick = { showSignOutDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Logout, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cerrar Sesión", fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppGreen)
            )
        },
        containerColor = BgGray
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Tarjeta Perfil ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, tint = TextDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mi Perfil", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BgGray)

                    Text("Nombre", fontSize = 12.sp, color = TextMuted)
                    Text(
                        uiState.profile?.fullName ?: "—",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Tipo de cuenta", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AppAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (uiState.profile?.role == "manitas") "Manitas" else "Cliente",
                            color = AppAmber,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BgGray)
                    TextButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar perfil")
                    }
                }
            }

            // ── Tarjeta Mis Trabajos ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, null, tint = TextDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mis Trabajos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        }
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Publicar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Text(
                        "Gestiona tus solicitudes y revisa propuestas",
                        fontSize = 13.sp, color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                    )

                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(color = AppGreen) }
                        }
                        uiState.jobs.isEmpty() -> EmptyJobsPlaceholder(onClick = { showCreateDialog = true })
                        else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.jobs.forEach { job ->
                                JobListItem(job = job, onDelete = { viewModel.deleteJob(job.id) })
                            }
                        }
                    }

                    uiState.error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = RedDelete, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showCreateDialog) {
        CreateJobDialog(
            createJobState = createJobState,
            onDismiss = { showCreateDialog = false; viewModel.resetCreateJobState() },
            onCreate  = { title, desc, cat, loc, budget ->
                viewModel.createJob(title, desc, cat, loc, budget)
            }
        )
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Cerrar sesión") },
            text  = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    viewModel.signOut { onNavigateToAuth() }
                }) { Text("Cerrar sesión", color = RedDelete) }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// ─── Job item ─────────────────────────────────────────────────────────────────
@Composable
fun JobListItem(job: Job, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val statusColor = when (job.status) {
        "open"        -> Color(0xFF16A34A)
        "in_progress" -> AppAmber
        "completed"   -> TextMuted
        else          -> RedDelete
    }
    val statusLabel = when (job.status) {
        "open"        -> "Abierto"
        "in_progress" -> "En curso"
        "completed"   -> "Completado"
        else          -> "Cancelado"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BgGray),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    job.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        statusLabel, color = statusColor,
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (!job.location.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = TextMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(job.location, fontSize = 12.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                job.description, fontSize = 13.sp, color = TextMuted,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AppGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        job.category.replaceFirstChar { it.uppercase() },
                        color = AppGreen, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    job.budget?.let {
                        Text("%.0f€".format(it), fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null, tint = RedDelete, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar trabajo") },
            text  = { Text("¿Eliminar \"${job.title}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete() }) {
                    Text("Eliminar", color = RedDelete)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}

// ─── Estado vacío ─────────────────────────────────────────────────────────────
@Composable
fun EmptyJobsPlaceholder(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔍", fontSize = 36.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Aún no tienes trabajos publicados", fontWeight = FontWeight.SemiBold, color = TextDark)
        Text(
            "Publica tu primer trabajo y empieza a recibir ofertas",
            fontSize = 13.sp, color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )
        Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = AppGreen)) {
            Text("Publicar trabajo")
        }
    }
}

// ─── Diálogo crear trabajo ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobDialog(
    createJobState: CreateJobState,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String?, Double?) -> Unit
) {
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location    by remember { mutableStateOf("") }
    var budget      by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(JobCategory.OTROS) }
    var expanded    by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())
            ) {
                Text("Publicar trabajo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Título del trabajo") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, focusedLabelColor = AppGreen)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Descripción detallada") },
                    modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, focusedLabelColor = AppGreen)
                )
                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = "${selectedCat.emoji} ${selectedCat.label}",
                        onValueChange = {}, readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, focusedLabelColor = AppGreen)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        JobCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.emoji} ${cat.label}") },
                                onClick = { selectedCat = cat; expanded = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = location, onValueChange = { location = it },
                    label = { Text("Ubicación (opcional)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, focusedLabelColor = AppGreen)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = budget, onValueChange = { budget = it },
                    label = { Text("Presupuesto en € (opcional)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.Euro, null, tint = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, focusedLabelColor = AppGreen)
                )

                if (createJobState is CreateJobState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(createJobState.message, color = RedDelete, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            onCreate(
                                title, description,
                                selectedCat.name.lowercase(),
                                location.ifBlank { null },
                                budget.toDoubleOrNull()
                            )
                        },
                        enabled = createJobState !is CreateJobState.Loading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
                    ) {
                        if (createJobState is CreateJobState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Publicar")
                        }
                    }
                }
            }
        }
    }
}