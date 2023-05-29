package com.bangkit.ecoease.ui.component

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.room.model.Address
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.utils.WindowInfo
import com.bangkit.ecoease.utils.rememberWindowInfo
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChangeAddressScreen(
    savedAddressStateFlow: StateFlow<UiState<List<Address>>>,
    tempSelectedAddressStateFlow: StateFlow<Address?>,
    onLoadSavedAddress: () -> Unit,
    onAddNewAddress: (Address) -> Unit,
    onDeleteAddress: (Address) -> Unit,
    onSelectedAddress: (Address) -> Unit,
    onSaveSelectedAddress: (Address) -> Unit,
    onReloadSavedAddress: () -> Unit,
    toastMessageState: StateFlow<String>,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expandContainer: Boolean by rememberSaveable { mutableStateOf(false) }
    var name: String by rememberSaveable { mutableStateOf("") }
    var detail: String by rememberSaveable { mutableStateOf("") }
    var district: String by rememberSaveable { mutableStateOf("") }
    var city: String by rememberSaveable { mutableStateOf("") }
    var selectedIndex: Int by rememberSaveable { mutableStateOf(-1) }

    val windowInfo = rememberWindowInfo()

    fun resetFieldHandler() {
        name = ""
        detail = ""
        district = ""
        city = ""
    }

    ScreenModeContainer(
        windowType = windowInfo.screenWidthInfo,
        modifier = modifier,
        content = {
            savedAddressStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
            LazyColumn(
                contentPadding = PaddingValues(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 32.dp)
                    .padding(top = 52.dp)
                    .fillMaxWidth()
            ) {
                item {
                    NewAddressForm(
                        expanded = expandContainer,
                        onToggle = { expandContainer = !expandContainer },
                        name = name,
                        onNameChange = { name = it },
                        city = city,
                        onCityChange = { city = it },
                        district = district,
                        onDistrictChange = { district = it },
                        detail = detail,
                        onDetailChange = { detail = it },
                        resetFieldHandler = { resetFieldHandler() },
                        onAddNewAddress = { onAddNewAddress(it) },
                        context = context
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = stringResource(R.string.saved_address))
                    Spacer(modifier = Modifier.height(8.dp))
                }
                when (uiState) {
                    is UiState.Loading -> {
                        item {
                            Column(Modifier.fillMaxWidth()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                onLoadSavedAddress()
                            }
                        }
                    }
                    is UiState.Success -> {
                        items(uiState.data, key = { it.id }) {
                            val currentIndex =
                                if (uiState.data.isEmpty()) -1 else uiState.data.indexOf(it)
                            AddressChoice(
                                name = it.name,
                                detail = it.detail,
                                district = it.district,
                                city = it.city,
                                checked = currentIndex == selectedIndex,
                                onDelete = {
                                    selectedIndex = -1
                                    onDeleteAddress(it)
                                },
                                onSelected = {
                                    selectedIndex = currentIndex
                                    onSelectedAddress(it)
                                },
                                modifier = Modifier.animateItemPlacement(tween(durationMillis = 100))
                            )
                        }
                        if (uiState.data.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_saved_address),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.caption.copy(
                                        color = DarkGrey
                                    )
                                )
                            }
                        }
                    }
                    is UiState.Error -> item {
                        ErrorHandler(
                            errorText = uiState.errorMessage,
                            onReload = { onReloadSavedAddress() })
                    }
                }
            }
        }
        },
        bottomSheet = {
            BottomSheet(
                label = "Alamat",
                actionName = "pilih alamat",
                onActionButtonClicked = {
                    tempSelectedAddressStateFlow.value?.let {
                        navHostController.popBackStack()
                        onSaveSelectedAddress(it)
                    }
                },
                information = if (selectedIndex == -1) "" else tempSelectedAddressStateFlow.value?.name
                    ?: "",
                isActive = selectedIndex != -1,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}

@Composable
fun NewAddressForm(
    expanded: Boolean,
    onToggle: () -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    district: String,
    onDistrictChange: (String) -> Unit,
    detail: String,
    onDetailChange: (String) -> Unit,
    resetFieldHandler: () -> Unit,
    onAddNewAddress: (Address) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    CollapseContainer(
        modifier = modifier,
        label = stringResource(R.string.add_address),
        expanded = expanded,
        onToggle = { onToggle() }
    ) {
        TextInput(
            label = stringResource(R.string.address_name),
            value = name,
            onValueChange = { onNameChange(it) })
        TextInput(
            label = stringResource(R.string.address_city),
            value = city,
            onValueChange = { onCityChange(it) })
        TextInput(
            label = stringResource(R.string.address_district),
            value = district,
            onValueChange = { onDistrictChange(it) })
        TextInput(
            label = stringResource(R.string.address_detail),
            isTextArea = true,
            value = detail,
            onValueChange = { onDetailChange(it) })
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoundedButton(
                text = stringResource(R.string.reset),
                type = RoundedButtonType.SECONDARY,
                onClick = { resetFieldHandler() }
            )
            RoundedButton(
                text = stringResource(R.string.added),
                onClick = {
                    try {
                        val newAddress = Address(
                            id = generateUUID(),
                            name = name,
                            detail = detail,
                            district = district,
                            city = city,
                            selected = false,
                        )
                        onAddNewAddress(newAddress)
                        resetFieldHandler()
                        onToggle()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }
}

@Composable
private fun ScreenModeContainer(
    windowType: WindowInfo.WindowType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    bottomSheet: @Composable () -> Unit,
) {
    when (windowType) {
        is WindowInfo.WindowType.Compact -> Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                content()
            }
            Box(modifier = Modifier
                .height(120.dp)
                .align(Alignment.BottomCenter)
            ) {
                bottomSheet()
            }
        }
        else -> Row(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.weight(1.4f)) {
                content()
            }
            Column(modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
            ) {
                bottomSheet()
            }
        }
    }
}