package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthViewModel
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.GlassCard
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldGlassBrush
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LiquidGlassBackground
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLiquid
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmCreamSurface
import com.example.ui.theme.YellowGlow
import com.example.ui.theme.PlusJakartaSans

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.uiState.collectAsState()
    val activeSalesmen by authViewModel.activeSalesmen.collectAsState()
    val focusManager = LocalFocusManager.current

    LiquidGlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Minimal Vibrant Yellow Luminous Logo Header
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(8.dp, CircleShape, spotColor = GoldPrimary)
                            .clip(CircleShape)
                            .background(GoldGlassBrush)
                            .border(BorderStroke(2.5.dp, PureWhite), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "MJ GARMENTS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = PlusJakartaSans,
                        color = TextPrimary,
                        letterSpacing = 3.sp
                    )

                    Text(
                        text = "BROADWAY • RETAIL SALES DESK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = GoldDeep,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
                    )

                    // Minimal White Card with Vibrant Yellow Glow Border
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = YellowGlow),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = 22.dp,
                        backgroundColor = PureWhite,
                        borderColor = GlassBorderGold
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Sign In",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = PlusJakartaSans,
                                color = TextPrimary
                            )

                            Text(
                                text = "Enter your registered mobile number and 4-digit PIN.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = PlusJakartaSans,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                            )

                            // Error Message Banner
                            if (authState.errorMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFF1F2))
                                        .border(BorderStroke(1.dp, RoseLiquid), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = authState.errorMessage ?: "",
                                        color = RoseLiquid,
                                        fontSize = 13.sp,
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // Mobile Number Field
                            Text(
                                text = "MOBILE NUMBER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSans,
                                color = TextSecondary,
                                letterSpacing = 0.8.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = authState.enteredPhone,
                                onValueChange = { authViewModel.onPhoneChanged(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("login_phone_input"),
                                placeholder = {
                                    Text(
                                        "10-digit mobile number",
                                        color = TextMuted,
                                        fontSize = 15.sp,
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PlusJakartaSans,
                                    color = TextPrimary
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x26000000),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = PureWhite,
                                    unfocusedContainerColor = PureWhite
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // 4-Digit Security PIN
                            Text(
                                text = "4-DIGIT PIN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.8.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = authState.enteredPin,
                                onValueChange = { authViewModel.onPinChanged(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("login_pin_input"),
                                placeholder = {
                                    Text(
                                        "Enter 4-digit PIN",
                                        color = TextMuted,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = GoldDeep,
                                        modifier = Modifier
                                            .padding(start = 12.dp, end = 6.dp)
                                            .size(20.dp)
                                    )
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.NumberPassword,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        authViewModel.loginWithPhoneAndPin()
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x26000000),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = PureWhite,
                                    unfocusedContainerColor = PureWhite
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            // Vibrant Glowing Yellow Login Button
                            if (authState.isAuthenticating) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = GoldPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            } else {
                                LiquidGlassButton(
                                    text = "LOGIN",
                                    onClick = {
                                        focusManager.clearFocus()
                                        authViewModel.loginWithPhoneAndPin()
                                    },
                                    gradientBrush = GoldGlassBrush,
                                    icon = Icons.Default.ArrowForward,
                                    height = 56.dp,
                                    fontSize = 16,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_submit_button")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
