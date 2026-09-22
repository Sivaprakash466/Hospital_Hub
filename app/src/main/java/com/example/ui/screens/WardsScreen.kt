package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WardRoom
import com.example.ui.theme.HospitalCriticalRed
import com.example.ui.theme.HospitalSuccessGreen
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.HospitalWarningOrange

@Composable
fun WardsScreen(
    wards: List<WardRoom>,
    onUpdateOccupancy: (WardRoom, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalHospitalBeds = wards.sumOf { it.totalBeds }
    val totalOccupiedBeds = wards.sumOf { it.occupiedBeds }
    val totalAvailableBeds = (totalHospitalBeds - totalOccupiedBeds).coerceAtLeast(0)
    val overallOccupancy = if (totalHospitalBeds > 0) totalOccupiedBeds.toFloat() / totalHospitalBeds else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wards_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overall Bed Occupancy Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hospital Bed Capacity",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Real-time occupancy across all wings",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HospitalTealPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hotel,
                                contentDescription = null,
                                tint = HospitalTealPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "OCCUPIED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$totalOccupiedBeds Beds",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (overallOccupancy > 0.85f) HospitalCriticalRed else HospitalTealPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AVAILABLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$totalAvailableBeds Beds",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = HospitalSuccessGreen
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TOTAL CAPACITY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$totalHospitalBeds Beds",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { overallOccupancy.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = when {
                            overallOccupancy > 0.9f -> HospitalCriticalRed
                            overallOccupancy > 0.75f -> HospitalWarningOrange
                            else -> HospitalTealPrimary
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "Clinical Wards & Care Units (${wards.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(wards, key = { it.id }) { ward ->
            WardItemCard(
                ward = ward,
                onUpdateOccupancy = { newCount -> onUpdateOccupancy(ward, newCount) }
            )
        }
    }
}

@Composable
fun WardItemCard(
    ward: WardRoom,
    onUpdateOccupancy: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val occupancyRate = ward.occupancyPercentage
    val barColor = when {
        occupancyRate >= 0.9f -> HospitalCriticalRed
        occupancyRate >= 0.75f -> HospitalWarningOrange
        else -> HospitalSuccessGreen
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("ward_card_${ward.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ward.wardName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${ward.floor} • ${ward.department}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = barColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${(occupancyRate * 100).toInt()}% OCCUPIED",
                        color = barColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { occupancyRate.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = barColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${ward.occupiedBeds}/${ward.totalBeds} Beds Occupied (${ward.availableBeds} Available)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "👩‍⚕️ Nurse: ${ward.nurseInCharge}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quick bed adjust +/-
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            if (ward.occupiedBeds > 0) {
                                onUpdateOccupancy(ward.occupiedBeds - 1)
                            }
                        },
                        enabled = ward.occupiedBeds > 0,
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Discharge Bed",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${ward.occupiedBeds}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    FilledIconButton(
                        onClick = {
                            if (ward.occupiedBeds < ward.totalBeds) {
                                onUpdateOccupancy(ward.occupiedBeds + 1)
                            }
                        },
                        enabled = ward.occupiedBeds < ward.totalBeds,
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = HospitalTealPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Admit Bed",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
