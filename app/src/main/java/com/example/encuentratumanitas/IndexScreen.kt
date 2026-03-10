package com.example.encuentratumanitas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors (matching the React/Tailwind theme) ──────────────────────────────
private val PrimaryGreenIndex    = Color(0xFF155E38)   // bg-primary  (dark green)
private val SecondaryYellowIndex = Color(0xFFF59E0B)   // bg-secondary (amber)
private val AccentGreenIndex     = Color(0xFF16A34A)   // bg-accent   (medium green)
private val MutedBgIndex         = Color(0xFFF3F4F6)
private val CardBgIndex          = Color.White
private val FooterBgIndex        = Color(0xFF1F2937)

@Composable
fun IndexScreen(
    onNavigateToAuth: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeroSection(onNavigateToAuth)
        HowItWorksSection()
        BenefitsSection(onNavigateToAuth)
        CtaSection(onNavigateToAuth)
        FooterSection()
    }
}

// ─── 1. HERO ─────────────────────────────────────────────────────────────────
@Composable
fun HeroSection(onNavigateToAuth: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(680.dp)
    ) {
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryGreenIndex.copy(alpha = 0.95f),
                            PrimaryGreenIndex.copy(alpha = 0.70f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Un Manitas\ncerca de ti",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 56.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Conecta con profesionales expertos en mantenimiento y reparaciones del hogar",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 480.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary actions row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryYellowIndex),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Publicar Trabajo",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToAuth,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryGreenIndex
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Text(text = "Soy Manitas", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary login button
            OutlinedButton(
                onClick = onNavigateToAuth,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
            ) {
                Text(text = "Iniciar Sesión", fontSize = 16.sp)
            }
        }
    }
}

// ─── 2. HOW IT WORKS ─────────────────────────────────────────────────────────
@Composable
fun HowItWorksSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MutedBgIndex)
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Cómo funciona?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureCard(
                icon = Icons.Default.Build,
                iconBg = PrimaryGreenIndex,
                title = "Publica tu necesidad",
                description = "Describe el trabajo que necesitas con fotos, ubicación y horario disponible"
            )
            FeatureCard(
                icon = Icons.Default.People,
                iconBg = SecondaryYellowIndex,
                title = "Recibe ofertas",
                description = "Los manitas profesionales revisarán tu solicitud y te enviarán propuestas"
            )
            FeatureCard(
                icon = Icons.Default.CheckCircle,
                iconBg = AccentGreenIndex,
                title = "Elige y contrata",
                description = "Revisa perfiles, reseñas y elige al profesional que mejor se adapte a tus necesidades"
            )
        }
    }
}

@Composable
fun FeatureCard(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBgIndex),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── 3. BENEFITS ─────────────────────────────────────────────────────────────
@Composable
fun BenefitsSection(onNavigateToAuth: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 48.dp, horizontal = 24.dp)
    ) {
        Text(
            text = "Profesionales verificados y valorados",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )

        Spacer(modifier = Modifier.height(24.dp))

        BenefitItem(
            icon = Icons.Default.Star,
            iconTint = SecondaryYellowIndex,
            text = "Sistema de reseñas detallado con múltiples criterios de evaluación"
        )
        Spacer(modifier = Modifier.height(16.dp))
        BenefitItem(
            icon = Icons.Default.CheckCircle,
            iconTint = PrimaryGreenIndex,
            text = "Portafolio de trabajos completados con fotos de resultados"
        )
        Spacer(modifier = Modifier.height(16.dp))
        BenefitItem(
            icon = Icons.Default.People,
            iconTint = AccentGreenIndex,
            text = "Comunidad de profesionales en carpintería, electricidad, fontanería y más"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Professional CTA card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGreenIndex)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Para Manitas Profesionales",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Únete a nuestra comunidad y accede a nuevos clientes cada día",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                listOf(
                    "Crea tu perfil profesional",
                    "Muestra tus especialidades",
                    "Construye tu reputación",
                    "Gestiona tus trabajos"
                ).forEach { item ->
                    Text(
                        text = "✓ $item",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onNavigateToAuth,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryYellowIndex),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text(
                        text = "Registrarme como Manitas",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BenefitItem(icon: ImageVector, iconTint: Color, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 16.sp, color = Color(0xFF111827))
    }
}

// ─── 4. CTA ──────────────────────────────────────────────────────────────────
@Composable
fun CtaSection(onNavigateToAuth: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryGreenIndex)
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Listo para encontrar tu manitas ideal?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Miles de profesionales están esperando para ayudarte con tus proyectos de mantenimiento y reparación",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 480.dp)
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onNavigateToAuth,
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryYellowIndex),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Text(
                text = "Comenzar Ahora",
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

// ─── 5. FOOTER ───────────────────────────────────────────────────────────────
@Composable
fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FooterBgIndex)
            .padding(vertical = 28.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Un Manitas cerca de ti",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Conectando profesionales con hogares que necesitan sus servicios",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun IndexScreenPreview() {
    IndexScreen()
}
