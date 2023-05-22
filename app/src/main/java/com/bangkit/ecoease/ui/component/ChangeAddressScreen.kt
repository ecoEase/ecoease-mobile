package com.bangkit.ecoease.ui.component

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
import com.bangkit.ecoease.helper.toCurrency
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.theme.DarkGrey
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

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
){
    var name: String by rememberSaveable{ mutableStateOf("") }
    var detail: String by rememberSaveable{ mutableStateOf("") }
    var district: String by rememberSaveable{ mutableStateOf("") }
    var city: String by rememberSaveable{ mutableStateOf("") }
    var selectedIndex: Int by rememberSaveable { mutableStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 52.dp)
        ) {
            CollapseContainer(label = stringResource(R.string.add_address)){
                TextInput(label = stringResource(R.string.address_name), value = name, onValueChange = {it -> name = it})
                TextInput(label = stringResource(R.string.address_city), value = city, onValueChange = {it -> city = it})
                TextInput(label = stringResource(R.string.address_district), value = district, onValueChange = {it -> district = it})
                TextInput(label = stringResource(R.string.address_detail), isTextArea = true, value = detail, onValueChange = {it -> detail = it})
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RoundedButton(
                        text = stringResource(R.string.reset),
                        type = RoundedButtonType.SECONDARY,
                        onClick = {//reset all text input state
                            name = ""
                            detail = ""
                            district = ""
                            city = ""
                        }
                    )
                    RoundedButton(
                        text = stringResource(R.string.added),
                        onClick = {
                            val newAddress = Address(
                                id = generateUUID(),
                                name = name,
                                detail = detail,
                                district = district,
                                city = city,
                                selected = false,
                            )
                            onAddNewAddress(newAddress)

                        }
                    )
                }
            }
            Box(modifier = Modifier.height(32.dp))
            Text(text = stringResource(R.string.saved_address))
            Box(modifier = Modifier.height(8.dp))
            savedAddressStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when(uiState){
                    is UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        onLoadSavedAddress()
                    }
                    is UiState.Success -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.data, key = { it.id }){
                                val currentIndex = if(uiState.data.isEmpty()) -1 else uiState.data.indexOf(it)
                                AddressChoice(
                                    name = it.name,
                                    detail = it.detail,
                                    district = it.district,
                                    city = it.city,
                                    checked =  currentIndex == selectedIndex,
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
                            if(uiState.data.isEmpty()){
                                item {
                                    Text(text = stringResource(R.string.no_saved_address), textAlign = TextAlign.Center, style = MaterialTheme.typography.caption.copy(
                                        color = DarkGrey
                                    ))
                                }
                            }
                        }
                    }
                    is UiState.Error -> ErrorHandler(errorText = uiState.errorMessage, onReload = { onReloadSavedAddress() })
                }
            }
        }

        BottomSheet(
            label = "Alamat",
            actionName = "pilih alamat",
            onActionButtonClicked = {
                    tempSelectedAddressStateFlow.value?.let {

                        navHostController.popBackStack()
                        onSaveSelectedAddress(it)

                    }
            },
            information = if(selectedIndex == -1) "" else  tempSelectedAddressStateFlow.value?.name ?: "",
            isActive = selectedIndex != -1,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}