package com.bangkit.ecoease.ui.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.model.Garbage
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.component.*
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.bangkit.ecoease.ui.theme.LightGrey

@Composable
fun DetailOrderScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    var openDialog by remember{
        mutableStateOf(false)
    }

    val garbages = listOf<GarbageAdded>(
        GarbageAdded(
            garbage = Garbage(id = generateUUID(), imageUrl = "", name = "kaleng", price = 700),
            amount = 2,
            totalPrice = 1400
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(vertical = 32.dp)
    ) {
        Text(text = stringResource(R.string.status), style = MaterialTheme.typography.body1.copy(
            color = DarkGrey
        ))
        StatusOrder(statusItemHistory = StatusItemHistory.NOT_TAKEN)
        Text(text = stringResource(R.string.address_info), style = MaterialTheme.typography.body1.copy(
            color = DarkGrey
        ))
        DetailAddressCard(name = "Alamat 1", detail = "jalan yg lurus", city = "Tulungagung")
        Box(modifier = Modifier.height(30.dp))
        Text(text = stringResource(R.string.detail), style = MaterialTheme.typography.body1.copy(
            color = DarkGrey
        ))
        LazyColumn(modifier = Modifier.weight(1f)){
            items(garbages){
                DetailCardGarbage(garbageName = it.garbage.name, amount = it.amount, price = it.garbage.price, total = it.totalPrice)
            }
        }
        RoundedButton(
            text = "batalkan pesanan",
            type = RoundedButtonType.SECONDARY,
            onClick = {
                openDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        )
        DialogBox(text = "Apakah anda yakin untuk membatalkan pesanan anda?", onDissmiss = { openDialog = false }, isOpen = openDialog)
    }
}

@Preview(showBackground = true)
@Composable
fun DetailOrderScreenPreview(){
    EcoEaseTheme() {
        DetailOrderScreen(rememberNavController())
    }
}