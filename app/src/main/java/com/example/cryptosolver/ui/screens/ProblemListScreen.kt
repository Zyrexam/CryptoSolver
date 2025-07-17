package com.example.cryptosolver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptosolver.ui.viewmodel.ProblemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemListScreen(
    viewModel: ProblemViewModel = viewModel(),
    onNavigateToProblem: (String) -> Unit
) {
    val problems by viewModel.problems.collectAsState()
    val userLevel by viewModel.userLevel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProblems()
        viewModel.fetchUserLevel()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "🧩 Crypto Challenges",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Solve problems to level up and earn points",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(problems.entries.toList()) { (id, problem) ->
                ProblemListItem(
                    problem = problem,
                    isUnlocked = problem.requiredLevel <= userLevel,
                    onClick = {
                        if (problem.requiredLevel <= userLevel) {
                            // Navigate to problem detail
                            onNavigateToProblem(problem.id)

                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProblemListItem(
    problem: com.example.cryptosolver.data.Problem,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked) { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Problem Icon/Status
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isUnlocked) {
                            when (problem.difficulty.lowercase()) {
                                "easy" -> MaterialTheme.colorScheme.primaryContainer
                                "medium" -> MaterialTheme.colorScheme.secondaryContainer
                                "hard" -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(
                        text = problem.icon,
                        style = MaterialTheme.typography.titleLarge
                    )
                } else {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Problem Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = problem.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isUnlocked)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Difficulty Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isUnlocked) {
                                    when (problem.difficulty.lowercase()) {
                                        "easy" -> MaterialTheme.colorScheme.primaryContainer
                                        "medium" -> MaterialTheme.colorScheme.secondaryContainer
                                        "hard" -> MaterialTheme.colorScheme.errorContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = problem.difficulty.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isUnlocked) {
                                when (problem.difficulty.lowercase()) {
                                    "easy" -> MaterialTheme.colorScheme.onPrimaryContainer
                                    "medium" -> MaterialTheme.colorScheme.onSecondaryContainer
                                    "hard" -> MaterialTheme.colorScheme.onErrorContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = problem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnlocked)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 2
                )

                if (!isUnlocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Requires Level ${problem.requiredLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Points
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isUnlocked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${problem.points}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isUnlocked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                }

                if (problem.isSolved) {
                    Text(
                        text = "✓ Solved",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
