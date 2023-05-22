package com.bangkit.ecoease.ui.screen.order

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.model.Address
import com.bangkit.ecoease.data.model.Garbage
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.Order
import com.bangkit.ecoease.helper.generateUUID
import com.bangkit.ecoease.helper.toCurrency
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.*
import com.bangkit.ecoease.ui.theme.LightGrey
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    orderStateFlow: StateFlow<Order>,
    addGarbageOrderSlot: () -> Unit = {},
    deleteGarbageSlotAt: (Int) -> Unit = {},
    onAcceptResetOrder: () -> Unit = {},
    onLoadSelectedAddress: () -> Unit,
    onReloadSelectedAddress: () -> Unit,
    selectedAddressStateFlow: StateFlow<UiState<com.bangkit.ecoease.data.room.model.Address?>>,
    updateGarbageAtIndex: (Int, GarbageAdded) -> Unit = { _, _, -> },
){

    val orderState by orderStateFlow.collectAsState()
    val lazyListState = rememberLazyListState()

//    val dummyAddress = Address(
//        id = generateUUID(),
//        name = "alamat 1",
//        detail = "jalan yang lurus",
//        district = "Besuki",
//        city = "Tulungagung"
//    )

    val listGarbage = listOf(
        Garbage(
            id = generateUUID(),
            imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
            name = "Kantong plastik",
            price = 200
        ),Garbage(
            id = generateUUID(),
            imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
            name = "Botol plastik",
            price = 300
        ),Garbage(
            id = generateUUID(),
            imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
            name = "Kaleng",
            price = 700
        ),
    )

    var garbageTypes: MutableList<String> by rememberSaveable{ mutableStateOf(mutableListOf()) }
    var addedForm by rememberSaveable { mutableStateOf(0) }
    var openDialog by remember{ mutableStateOf(false) }
    var openDialogResetOrder by remember{ mutableStateOf(false) }

    LaunchedEffect(garbageTypes.toList()){
        lazyListState.animateScrollToItem(garbageTypes.size)
    }

    DisposableEffect(navHostController){
        val listener = NavController.OnDestinationChangedListener { controller, destination, _ ->
            Log.d("TAG", "OrderScreen: screen will change $destination")
        }
        navHostController.addOnDestinationChangedListener(listener)
        onDispose {
            navHostController.removeOnDestinationChangedListener(listener)
        }
    }

    BackHandler(orderState.total > 0) {//handle physical back button
        openDialogResetOrder = true
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
            selectedAddressStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when(uiState){
                    is UiState.Loading -> {
                        CircularProgressIndicator()
                        onLoadSelectedAddress()
                    }
                    is UiState.Success -> {
                        if(uiState.data == null){
                            Text("alamat masih kosong")
                            RoundedButton(text = "pilih alamat",  onClick = { navHostController.navigate(Screen.ChangeAddress.route) })
                        }else{
                            AddressCard(
                                name = uiState.data.name,
                                detail = uiState.data.detail,
                                district = uiState.data.district,
                                city = uiState.data.city,
                                onClickChange = { navHostController.navigate(Screen.ChangeAddress.route) }
                            )
                        }
                    }
                    is UiState.Error -> ErrorHandler(errorText = uiState.errorMessage, onReload = { onReloadSelectedAddress() })
                }
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val borderSize = 1.dp.toPx()
                    drawLine(
                        color = LightGrey,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = borderSize
                    )
                },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.garbage))
                RoundedButton(text = stringResource(R.string.add), onClick = {
                    addGarbageOrderSlot()//add new slot garbage in viewmodel
                    addedForm += 1
                    garbageTypes.add(generateUUID())
                })
            }
            AnimatedVisibility(visible = addedForm > 0) {
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 136.dp)
                ){
                    items(garbageTypes.toList(), key = { it }){
                        val index = garbageTypes.indexOf(it)
                        //populate the addGarbageForm with stateflow order when there is data from it
                        val addedGarbage = if(orderState.garbageList.isNotEmpty()) orderState.garbageList[index] else null
                        val initialGarbageName = addedGarbage?.garbage?.name
                        val initialGarbageAmount = addedGarbage?.amount
                        val initialGarbagePrice = addedGarbage?.garbage?.price
                        val initialGarbageTotalPrice = addedGarbage?.totalPrice

                        AddGarbageForm(
                            initSelected = initialGarbageName,
                            initAmount = initialGarbageAmount,
                            initPrice = initialGarbagePrice,
                            initTotalPrice = initialGarbageTotalPrice,
                            listGarbage = listGarbage,
                            onDelete = {
                                garbageTypes = garbageTypes.filter { element -> element != it} as MutableList<String>
                                deleteGarbageSlotAt(index)
                            },
                            onUpdate = { newUpdateGarbageData ->
                                updateGarbageAtIndex(index, newUpdateGarbageData)
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
            information = "Rp${orderState.total.toCurrency()}",
            isActive = orderState.total > 0,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        DialogBox(text = "Apakah anda sudah yakin?", isOpen = openDialog, onDissmiss = { openDialog = false }, onAccept = {
            onAcceptResetOrder()
            navHostController.navigate(Screen.OrderSuccess.route)
        })
        DialogBox(text = "Apakah anda yakin ingin membatalkan order anda", onDissmiss = { openDialogResetOrder = false }, onAccept = { onAcceptResetOrder() }, isOpen = openDialogResetOrder)
    }
}