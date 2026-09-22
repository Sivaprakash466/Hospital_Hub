package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HospitalCriticalRed
import com.example.ui.theme.HospitalCriticalRedContainer
import com.example.ui.theme.HospitalInfoBlue
import com.example.ui.theme.HospitalInfoBlueContainer
import com.example.ui.theme.HospitalSuccessGreen
import com.example.ui.theme.HospitalSuccessGreenContainer
import com.example.ui.theme.HospitalWarningOrange
import com.example.ui.theme.HospitalWarningOrangeContainer

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "critical" -> Triple(HospitalCriticalRedContainer, HospitalCriticalRed, "CRITICAL")
        "admitted" -> Triple(HospitalInfoBlueContainer, HospitalInfoBlue, "ADMITTED")
        "recovering" -> Triple(HospitalWarningOrangeContainer, HospitalWarningOrange, "RECOVERING")
        "stable" -> Triple(HospitalSuccessGreenContainer, HospitalSuccessGreen, "STABLE")
        "discharged" -> Triple(Color(0xFFE2E8F0), Color(0xFF475569), "DISCHARGED")
        else -> Triple(Color(0xFFE2E8F0), Color(0xFF475569), status.uppercase())
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (status.equals("critical", ignoreCase = true)) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(HospitalCriticalRed)
                )
            }
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun UrgencyBadge(
    urgency: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (urgency.lowercase()) {
        "urgent" -> Pair(HospitalCriticalRedContainer, HospitalCriticalRed)
        "follow-up" -> Pair(Color(0xFFEDE9FE), Color(0xFF6D28D9))
        else -> Pair(HospitalInfoBlueContainer, HospitalInfoBlue)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = urgency,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DoctorDutyBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, dotColor) = when (status) {
        "On Duty" -> Triple(HospitalSuccessGreenContainer, HospitalSuccessGreen, HospitalSuccessGreen)
        "In Surgery" -> Triple(HospitalWarningOrangeContainer, HospitalWarningOrange, HospitalWarningOrange)
        else -> Triple(Color(0xFFE2E8F0), Color(0xFF64748B), Color(0xFF94A3B8))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = status,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VitalsItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PatientVitalsGrid(
    bp: String,
    pulse: Int,
    spO2: Int,
    temp: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        VitalsItem(
            icon = Icons.Default.Speed,
            label = "BP",
            value = bp,
            iconTint = Color(0xFF0284C7),
            modifier = Modifier.weight(1f)
        )
        VitalsItem(
            icon = Icons.Default.Favorite,
            label = "Pulse",
            value = "$pulse bpm",
            iconTint = HospitalCriticalRed,
            modifier = Modifier.weight(1f)
        )
        VitalsItem(
            icon = Icons.Default.Air,
            label = "SpO2",
            value = "$spO2%",
            iconTint = Color(0xFF0D9488),
            modifier = Modifier.weight(1f)
        )
        VitalsItem(
            icon = Icons.Default.Thermostat,
            label = "Temp",
            value = "$temp°F",
            iconTint = Color(0xFFD97706),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("metric_${title.lowercase().replace(" ", "_")}")
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
