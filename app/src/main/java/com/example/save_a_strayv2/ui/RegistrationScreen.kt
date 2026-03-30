package com.example.save_a_strayv2.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.save_a_strayv2.model.UserRole
import com.example.save_a_strayv2.ui.theme.SaveaStrayV2Theme
import com.example.save_a_strayv2.viewmodel.RegistrationViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Role metadata helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun UserRole.icon(): ImageVector = when (this) {
    UserRole.ADOPTER    -> Icons.Filled.Favorite
    UserRole.INDIVIDUAL -> Icons.Filled.Home
    UserRole.SHELTER    -> Icons.Filled.Business
}

// ─────────────────────────────────────────────────────────────────────────────
// Root screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // ── Hero header ───────────────────────────────────────────────────
            RegistrationHeader()

            Spacer(Modifier.height(32.dp))

            // ── Role selector cards ───────────────────────────────────────────
            Text(
                text  = "I am a…",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            RoleSelectionRow(
                selectedRole = viewModel.selectedRole,
                onRoleSelected = viewModel::onRoleSelected
            )

            // ── Dynamic form fields ───────────────────────────────────────────
            AnimatedVisibility(
                visible = viewModel.selectedRole != null,
                enter   = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit    = fadeOut(tween(200)) + shrinkVertically(tween(200))
            ) {
                Column {
                    Spacer(Modifier.height(28.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(24.dp))

                    // ── Role-branched name field ──────────────────────────────
                    AnimatedContent(
                        targetState = viewModel.selectedRole,
                        transitionSpec = {
                            fadeIn(tween(250)) togetherWith fadeOut(tween(200))
                        },
                        label = "RoleBranch"
                    ) { role ->
                        when (role) {
                            UserRole.ADOPTER, UserRole.INDIVIDUAL -> {
                                StrayTextField(
                                    value       = viewModel.name,
                                    onValueChange = viewModel::onNameChanged,
                                    label       = "Full Name",
                                    leadingIcon = Icons.Filled.Person,
                                    imeAction   = ImeAction.Next
                                )
                            }
                            UserRole.SHELTER -> {
                                StrayTextField(
                                    value       = viewModel.orgName,
                                    onValueChange = viewModel::onOrgNameChanged,
                                    label       = "Organization Name",
                                    leadingIcon = Icons.Filled.Business,
                                    imeAction   = ImeAction.Next
                                )
                            }
                            null -> {}
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Email ─────────────────────────────────────────────────
                    StrayTextField(
                        value         = viewModel.email,
                        onValueChange = viewModel::onEmailChanged,
                        label         = "Email Address",
                        leadingIcon   = Icons.Filled.Email,
                        keyboardType  = KeyboardType.Email,
                        imeAction     = ImeAction.Next
                    )
                    Spacer(Modifier.height(16.dp))

                    // ── Password ──────────────────────────────────────────────
                    StrayTextField(
                        value         = viewModel.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label         = "Password",
                        leadingIcon   = Icons.Filled.Lock,
                        isPassword    = true,
                        passwordVisible = viewModel.passwordVisible,
                        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                        imeAction     = ImeAction.Next
                    )
                    Spacer(Modifier.height(16.dp))

                    // ── Confirm Password ──────────────────────────────────────
                    StrayTextField(
                        value         = viewModel.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChanged,
                        label         = "Confirm Password",
                        leadingIcon   = Icons.Filled.Lock,
                        isPassword    = true,
                        passwordVisible = viewModel.confirmPasswordVisible,
                        onTogglePasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
                        imeAction     = ImeAction.Done
                    )

                    // ── Form error banner ─────────────────────────────────────
                    AnimatedVisibility(
                        visible = viewModel.formError != null,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically()
                    ) {
                        viewModel.formError?.let { error ->
                            Spacer(Modifier.height(12.dp))
                            ErrorBanner(message = error)
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    // ── Submit button ─────────────────────────────────────────
                    Button(
                        onClick  = viewModel::onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector  = Icons.Filled.Pets,
                            contentDescription = null,
                            modifier     = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text     = "Create Account",
                            style    = MaterialTheme.typography.labelLarge,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Login link ────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text  = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text  = "Sign In",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RegistrationHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Gradient paw icon badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .shadow(8.dp, RoundedCornerShape(28.dp))
        ) {
            Icon(
                imageVector        = Icons.Filled.Pets,
                contentDescription = "Save a Stray",
                tint               = Color.White,
                modifier           = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text      = "Join Save a Stray",
            style     = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color     = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text      = "Create your account and help find\nevery stray a loving home.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Role selection row — three animated cards
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RoleSelectionRow(
    selectedRole: UserRole?,
    onRoleSelected: (UserRole) -> Unit
) {
    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        UserRole.entries.forEach { role ->
            RoleCard(
                role       = role,
                isSelected = selectedRole == role,
                onClick    = { onRoleSelected(role) },
                modifier   = Modifier.weight(1f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Individual role card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RoleCard(
    role: UserRole,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 8f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardElevation"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardScale"
    )

    Card(
        onClick   = onClick,
        modifier  = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border    = if (isSelected) BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary
        ) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = role.icon(),
                contentDescription = role.displayName,
                tint               = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(28.dp)
            )
            Text(
                text      = role.displayName,
                style     = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color     = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            // Checkmark badge for selected state
            AnimatedVisibility(visible = isSelected) {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared text field component
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StrayTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    val visualTransformation = if (isPassword && !passwordVisible)
        PasswordVisualTransformation()
    else
        VisualTransformation.None

    OutlinedTextField(
        value             = value,
        onValueChange     = onValueChange,
        label             = { Text(label) },
        leadingIcon       = {
            Icon(imageVector = leadingIcon, contentDescription = label)
        },
        trailingIcon      = if (isPassword) {
            {
                IconButton(onClick = { onTogglePasswordVisibility?.invoke() }) {
                    Icon(
                        imageVector        = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible)
                            "Hide password"
                        else
                            "Show password"
                    )
                }
            }
        } else null,
        visualTransformation = visualTransformation,
        keyboardOptions   = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            imeAction    = imeAction
        ),
        singleLine        = true,
        shape             = RoundedCornerShape(14.dp),
        modifier          = modifier.fillMaxWidth()
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Error banner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector        = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.error,
            modifier           = Modifier.size(20.dp)
        )
        Text(
            text  = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    SaveaStrayV2Theme {
        RegistrationScreen()
    }
}
