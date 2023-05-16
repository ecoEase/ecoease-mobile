package com.bangkit.ecoease.ui.screen.order

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.model.Address
import com.bangkit.ecoease.data.model.Garbage
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.ui.component.*


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){

    val dummyAddress = Address(
        name = "alamat 1",
        detail = "jalan yang lurus",
        district = "Besuki",
        city = "Tulungagung"
    )

    val listGarbage = listOf(
        Garbage(
            imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
            name = "Kantong plastik",
            price = 200
        ),Garbage(
            imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
            name = "Botol plastik",
            price = 300
        ),Garbage(
            imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
            name = "Kaleng",
            price = 700
        ),
    )

    var garbageTypes: MutableList<String> by rememberSaveable{
        mutableStateOf(mutableListOf())
    }

    var addedForm by rememberSaveable {
        mutableStateOf(0)
    }

    var openDialog by remember{
        mutableStateOf(false)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 52.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.address))
            AddressCard(
                name = dummyAddress.name,
                detail = dummyAddress.detail,
                district = dummyAddress.district,
                city = dummyAddress.city,
                onClickChange = { navHostController.navigate(Screen.ChangeAddress.route) }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = stringResource(R.string.garbage))
                RoundedButton(text = stringResource(R.string.add), onClick = {
                    addedForm += 1
                    garbageTypes.add(generateUUID())
                    Log.d("TAG", "OrderScreen: ${garbageTypes[0]}")
                })
            }
            AnimatedVisibility(visible = addedForm > 0) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 136.dp)
                ){
                    items(garbageTypes.toList(), key = { it }){
                        AddGarbageForm(
                            listGarbage = listGarbage,
                            onDelete = {
                                garbageTypes = garbageTypes.filter { elemen -> elemen != it} as MutableList<String>
                            },
                            modifier = Modifier
                                .animateItemPlacement(tween(durationMillis = 100))
                        )
                    }
                }
            }
        }
        BottomSheet(
            label = "Total",
            actionName = "buat order",
            onActionButtonClicked = { openDialog = true },
            information = "Rp2000",
            isActive = true,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        DialogBox(text = "Apakah anda sudah yakin?", isOpen = openDialog, onDissmiss = { openDialog = false }, onAccept = { navHostController.navigate(Screen.OrderSuccess.route) })
    }
}