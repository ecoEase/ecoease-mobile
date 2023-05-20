package com.bangkit.ecoease.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.model.Garbage
import com.bangkit.ecoease.helper.toCurrency
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.Banner
import com.bangkit.ecoease.ui.component.CardPrice
import com.bangkit.ecoease.ui.component.RoundedButton
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DashboardScreen(
    garbageStateFlow: StateFlow<UiState<List<Garbage>>>,
    loadGarbage: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 32.dp)
    ) {
        Text(text = stringResource(R.string.good_morning), style = MaterialTheme.typography.h4)
        Box(modifier = Modifier.height(42.dp))
        Banner(bannerAction = { navHostController.navigate(Screen.Order.route) })
        Box(modifier = Modifier.height(42.dp))
        Text(text = stringResource(R.string.garbage_price), style = MaterialTheme.typography.h5)
        Box(modifier = Modifier.height(16.dp))

        garbageStateFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when(uiState){
                is UiState.Loading -> {
                    CircularProgressIndicator()
                    loadGarbage()
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                        ,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 64.dp)
                    ){
                        items(uiState.data){item ->
                            CardPrice(imageUrl = item.imageUrl, name = item.name, price = "Rp${item.price.toCurrency()}")
                        }
                    }
                }
                is UiState.Error -> {
                    Text(text = uiState.errorMessage)
                }
            }
        }
    }
}