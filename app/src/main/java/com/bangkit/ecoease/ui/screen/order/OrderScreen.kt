package com.bangkit.ecoease.ui.screen.order

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.model.Address
import com.bangkit.ecoease.data.model.GarbageAdded
import com.bangkit.ecoease.data.model.Order
import com.bangkit.ecoease.data.room.model.Garbage
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
    listGarbageFlow: StateFlow<UiState<List<Garbage>>>,
    loadListGarbage: () -> Unit,
    reloadListGarbage: () -> Unit,
    addGarbageOrderSlot: () -> Unit = {},
    deleteGarbageSlotAt: (Int) -> Unit = {},
    onAcceptResetOrder: () -> Unit = {},
    onLoadSelectedAddress: () -> Unit,
    onReloadSelectedAddress: () -> Unit,
    onMakeOrder: (List<Garbage>, Int) -> Unit,
    selectedAddressStateFlow: StateFlow<UiState<com.bangkit.ecoease.data.room.model.Address>>,
    updateGarbageAtIndex: (Int, GarbageAdded) -> Unit = { _, _, -> },
){
    val context = LocalContext.current
    val orderState by orderStateFlow.collectAsState()
    val lazyListState = rememberLazyListState()


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
                        if(!uiState.data.selected){
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

                        listGarbageFlow.collectAsState(initial = UiState.Loading).value.let {uiState ->
                            when(uiState){
                                is UiState.Loading -> loadListGarbage()
                                is UiState.Success -> {
                                    AddGarbageForm(
                                        initSelected = initialGarbageName,
                                        initAmount = initialGarbageAmount,
                                        initPrice = initialGarbagePrice,
                                        initTotalPrice = initialGarbageTotalPrice,
                                        listGarbage = uiState.data,
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
                                is UiState.Error -> ErrorHandler(
                                    errorText = uiState.errorMessage,
                                    onReload = { reloadListGarbage() })
                            }
                        }
                    }
                }
            }
        }
        BottomSheet(
            label = "Total",
            actionName = "buat order",
            onActionButtonClicked = {
                if(orderState.garbageList.contains(null)) {//prevent null value when user make new order
                    Toast.makeText(context, "Masih ada kolom sampah yg kosong, isi atau hapus kolom terlebih dahulu", Toast.LENGTH_SHORT).show()
                }else{
                    openDialog = true
                }
            },
            information = "Rp${orderState.total.toCurrency()}",
            isActive = orderState.total > 0,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        DialogBox(text = "Apakah anda sudah yakin?", isOpen = openDialog, onDissmiss = { openDialog = false }, onAccept = {

            onAcceptResetOrder()
            navHostController.navigate(Screen.OrderSuccess.route)
            onMakeOrder(orderState.garbageList.map { it!!.garbage }, orderState.total)
        })
        DialogBox(text = "Apakah anda yakin ingin membatalkan order anda", onDissmiss = { openDialogResetOrder = false }, onAccept = { onAcceptResetOrder() }, isOpen = openDialogResetOrder)
    }
}