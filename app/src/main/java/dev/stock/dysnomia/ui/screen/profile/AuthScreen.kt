package dev.stock.dysnomia.ui.screen.profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stock.dysnomia.R
import dev.stock.dysnomia.ui.composable.AnimatedErrorCard
import dev.stock.dysnomia.ui.composable.DysnomiaButton
import dev.stock.dysnomia.ui.composable.DysnomiaLogo
import dev.stock.dysnomia.ui.composable.DysnomiaTextField
import dev.stock.dysnomia.ui.theme.DysnomiaTheme

@Composable
fun AuthScreen(
    authUiState: AuthUiState,
    username: TextFieldValue,
    email: TextFieldValue,
    password: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit,
    onEmailChange: (TextFieldValue) -> Unit,
    onPasswordChange: (TextFieldValue) -> Unit,
    onChangeAuthScreen: (Boolean) -> Unit,
    onProceed: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(authUiState.isSignUp) {
        onChangeAuthScreen(false)
    }

    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            DysnomiaLogo(modifier = Modifier.padding(32.dp))

            DysnomiaTextField(
                value = username,
                onValueChange = onNameChange,
                label = stringResource(R.string.username),
                leadingIcon = Icons.Default.AccountCircle,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            AnimatedVisibility(authUiState.isSignUp) {
                DysnomiaTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.email),
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
            }

            DysnomiaTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.password),
                leadingIcon = Icons.Default.Lock,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onProceed() }
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(16.dp),
                thickness = 2.dp
            )

            DysnomiaButton(
                text = if (authUiState.isSignUp) {
                    stringResource(R.string.sign_up)
                } else {
                    stringResource(R.string.sign_in)
                },
                enabled = !authUiState.isInProgress,
                onClick = onProceed,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedErrorCard(authUiState.errorMessage)

            AnimatedVisibility(!authUiState.isSignUp) {
                Box(
                    modifier = Modifier.clickable { onChangeAuthScreen(true) }
                ) {
                    val annotatedText = buildAnnotatedString {
                        append(stringResource(R.string.maybe_you_want_to))
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append(stringResource(R.string.sign_up_question))
                        }
                    }
                    Text(annotatedText)
                }
            }
        }

        AnimatedVisibility(
            visible = authUiState.isSignUp,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(
                onClick = { onChangeAuthScreen(false) },
                modifier = Modifier.statusBarsPadding()
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, null)
            }
        }
    }
}

@Preview
@Composable
private fun SignInScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            AuthScreen(
                authUiState = AuthUiState(
                    errorMessage = "Some error occurred"
                ),
                username = TextFieldValue("username"),
                email = TextFieldValue("email@test.com"),
                password = TextFieldValue("password"),
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onProceed = {},
                onChangeAuthScreen = {}
            )
        }
    }
}

@Preview
@Composable
private fun SignInScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            AuthScreen(
                authUiState = AuthUiState(
                    errorMessage = "Some error occurred"
                ),
                username = TextFieldValue("username"),
                email = TextFieldValue("email@test.com"),
                password = TextFieldValue("password"),
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onProceed = {},
                onChangeAuthScreen = {}
            )
        }
    }
}


@Preview
@Composable
private fun SignUpScreenLightPreview() {
    DysnomiaTheme(darkTheme = false) {
        Surface {
            AuthScreen(
                authUiState = AuthUiState(
                    isSignUp = true,
                    errorMessage = "Some error occurred"
                ),
                username = TextFieldValue("username"),
                email = TextFieldValue("email@test.com"),
                password = TextFieldValue("password"),
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onProceed = {},
                onChangeAuthScreen = {}
            )
        }
    }
}

@Preview
@Composable
private fun SignUpScreenDarkPreview() {
    DysnomiaTheme(darkTheme = true) {
        Surface {
            AuthScreen(
                authUiState = AuthUiState(
                    isSignUp = true,
                    errorMessage = "Some error occurred"
                ),
                username = TextFieldValue("username"),
                email = TextFieldValue("email@test.com"),
                password = TextFieldValue("password"),
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onProceed = {},
                onChangeAuthScreen = {}
            )
        }
    }
}
