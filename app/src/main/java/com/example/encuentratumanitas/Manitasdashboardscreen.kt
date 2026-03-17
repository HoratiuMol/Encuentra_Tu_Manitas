package com.example.encuentratumanitas

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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

private val MAppGreen  = Color(0xFF155E38)
private val MAppAmber  = Color(0xFFF59E0B)
private val MBgGray    = Color(0xFFF3F4F6)
private val MCardWhite = Color.White
private val MTextDark  = Color(0xFF111827)
private val MTextMuted = Color(0xFF6B7280)
private val MRedColor  = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManitasDashboardScreen(
    onNavigateToAuth: () -> Unit,
    viewModel: ManitasDashboardViewModel = viewModel()
) {
    val uiState           by viewModel.uiState.collectAsStateWithLifecycle()
    val sendProposalState by viewModel.sendProposalState.collectAsStateWithLifecycle()

    var selectedJobForProposal by remember { mutableStateOf<Job?>(null) }
    var showSignOutDialog      by remember { mutableStateOf(false) }
    var selectedCategory       by remember { mutableStateOf<String?>(null) }

    // Cerrar diálogo al enviar propuesta con éxito
    LaunchedEffect(sendProposalState) {
        if (sendProposalState is SendProposalState.Success) {
            selectedJobForProposal = null
            viewModel.resetSendProposalState()
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MAppGreen)
            )
        },
        containerColor = MBgGray
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
                colors = CardDefaults.cardColors(containerColor = MCardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, null, tint = MTextDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mi Perfil", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MTextDark)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MBgGray)

                    Text("Nombre", fontSize = 12.sp, color = MTextMuted)
                    Text(
                        uiState.profile?.fullName ?: "—",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MTextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Tipo de cuenta", fontSize = 12.sp, color = MTextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MAppGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Manitas profesional",
                            color = MAppGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Estadísticas rápidas
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatChip(
                            label = "Propuestas enviadas",
                            value = "${uiState.myProposals.size}",
                            color = MAppGreen
                        )
                        StatChip(
                            label = "Aceptadas",
                            value = "${uiState.myProposals.count { it.status == "accepted" }}",
                            color = MAppAmber
                        )
                    }
                }
            }

            // ── Trabajos disponibles ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MCardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = MTextDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trabajos disponibles", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MTextDark)
                    }
                    Text(
                        "Encuentra trabajos y envía tus propuestas",
                        fontSize = 13.sp, color = MTextMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // ── Filtros por categoría ─────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryFilterChip(
                            label = "Todos",
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null; viewModel.filterByCategory(null) }
                        )
                        JobCategory.entries.forEach { cat ->
                            CategoryFilterChip(
                                label = "${cat.emoji} ${cat.label}",
                                selected = selectedCategory == cat.name.lowercase(),
                                onClick = {
                                    selectedCategory = cat.name.lowercase()
                                    viewModel.filterByCategory(cat.name.lowercase())
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Lista de trabajos ─────────────────────────────────
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(color = MAppGreen) }
                        }
                        uiState.availableJobs.isEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🔍", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No hay trabajos disponibles ahora",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MTextDark,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "Vuelve más tarde o cambia el filtro de categoría",
                                    fontSize = 13.sp, color = MTextMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                uiState.availableJobs.forEach { job ->
                                    AvailableJobCard(
                                        job = job,
                                        alreadyApplied = viewModel.hasProposalFor(job.id),
                                        onSendProposal = { selectedJobForProposal = job }
                                    )
                                }
                            }
                        }
                    }

                    uiState.error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = MRedColor, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // ── Diálogo enviar propuesta ──────────────────────────────────────────────
    selectedJobForProposal?.let { job ->
        SendProposalDialog(
            job               = job,
            sendProposalState = sendProposalState,
            onDismiss         = { selectedJobForProposal = null; viewModel.resetSendProposalState() },
            onSend            = { message, price -> viewModel.sendProposal(job.id, message, price) }
        )
    }

    // ── Diálogo cerrar sesión ─────────────────────────────────────────────────
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Cerrar sesión") },
            text  = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    viewModel.signOut { onNavigateToAuth() }
                }) { Text("Cerrar sesión", color = MRedColor) }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// ─── Tarjeta de trabajo disponible ───────────────────────────────────────────
@Composable
fun AvailableJobCard(
    job: Job,
    alreadyApplied: Boolean,
    onSendProposal: () -> Unit
) {
    val categoryEmoji = JobCategory.entries
        .firstOrNull { it.name.lowercase() == job.category }?.emoji ?: "🏠"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MBgGray),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Título + categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Text(categoryEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            job.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            job.category.replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            color = MTextMuted
                        )
                    }
                }
                job.budget?.let {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MAppAmber.copy(alpha = 0.12f)
                    ) {
                        Text(
                            "%.0f€".format(it),
                            color = MAppAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                job.description,
                fontSize = 13.sp,
                color = MTextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Ubicación
            if (!job.location.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = MTextMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(job.location, fontSize = 12.sp, color = MTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Spacer(modifier = Modifier.height(10.dp))

            // Botón enviar propuesta
            if (alreadyApplied) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = MAppGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Propuesta enviada", color = MAppGreen, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            } else {
                Button(
                    onClick = onSendProposal,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MAppGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Enviar propuesta", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─── Chip filtro categoría ────────────────────────────────────────────────────
@Composable
fun CategoryFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MAppGreen else MCardWhite,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MAppGreen else Color(0xFFD1D5DB)
        )
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else MTextMuted,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// ─── Chip estadística ─────────────────────────────────────────────────────────
@Composable
fun StatChip(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            Text(label, fontSize = 11.sp, color = MTextMuted, textAlign = TextAlign.Center)
        }
    }
}

// ─── Diálogo enviar propuesta ─────────────────────────────────────────────────
@Composable
fun SendProposalDialog(
    job: Job,
    sendProposalState: SendProposalState,
    onDismiss: () -> Unit,
    onSend: (String, Double?) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var price   by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MCardWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Enviar propuesta", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MTextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Para: ${job.title}",
                    fontSize = 13.sp,
                    color = MTextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Tu mensaje al cliente") },
                    placeholder = { Text("Describe tu experiencia y por qué eres el mejor para este trabajo...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MAppGreen,
                        focusedLabelColor  = MAppGreen
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Tu precio en € (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.Euro, null, tint = MTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MAppGreen,
                        focusedLabelColor  = MAppGreen
                    )
                )

                if (sendProposalState is SendProposalState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(sendProposalState.message, color = MRedColor, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onSend(message, price.toDoubleOrNull()) },
                        enabled = sendProposalState !is SendProposalState.Loading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MAppGreen)
                    ) {
                        if (sendProposalState is SendProposalState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Enviar", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}