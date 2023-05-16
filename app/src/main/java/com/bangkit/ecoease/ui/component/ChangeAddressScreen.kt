package com.bangkit.ecoease.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R

@Composable
fun ChangeAddressScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    // TODO: add reset state
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 52.dp)
    ) {
        CollapseContainer(label = stringResource(R.string.add_address)){
            TextInput(label = stringResource(R.string.address_name))
            TextInput(label = stringResource(R.string.address_city))
            TextInput(label = stringResource(R.string.address_district))
            TextInput(label = stringResource(R.string.address_detail), isTextArea = true)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoundedButton(text = stringResource(R.string.reset), type = RoundedButtonType.SECONDARY)
                RoundedButton(text = stringResource(R.string.added), type = RoundedButtonType.PRIMARY)
            }
        }
    }
}