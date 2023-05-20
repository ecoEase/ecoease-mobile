package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.room.dao.Address
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.common.UiState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ChangeAddressScreen(
    savedAddressStateFlow: StateFlow<UiState<List<Address>>>,
    onAddNewAddress: (Address) -> Unit,
    onLoadSavedAddress: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    var name: String by rememberSaveable{ mutableStateOf("") }
    var detail: String by rememberSaveable{ mutableStateOf("") }
    var district: String by rememberSaveable{ mutableStateOf("") }
    var city: String by rememberSaveable{ mutableStateOf("") }

    LaunchedEffect(Unit){
        onLoadSavedAddress()
    }

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
                        )
                        onAddNewAddress(newAddress)
                    }
                )
            }
        }
    }
}