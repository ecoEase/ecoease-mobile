package com.bangkit.ecoease.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.model.Garbage
import com.bangkit.ecoease.ui.component.Banner
import com.bangkit.ecoease.ui.component.CardPrice
import com.bangkit.ecoease.ui.component.RoundedButton


val listGarbages = listOf(
    Garbage(
        imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
        name = "Garbage",
        price = 200
    ),Garbage(
        imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
        name = "Garbage",
        price = 200
    ),Garbage(
        imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
        name = "Garbage",
        price = 200
    ),Garbage(
        imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
        name = "Garbage",
        price = 200
    ),Garbage(
        imageUrl = "https://images.unsplash.com/photo-1528190336454-13cd56b45b5a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=870&q=80",
        name = "Garbage",
        price = 200
    ),
)

@Composable
fun DashboardScreen(
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
        RoundedButton(text = "to chat screen", onClick = {navHostController.navigate(Screen.UsersChats.route)})
        Banner(bannerAction = { navHostController.navigate(Screen.Order.route) })
        Box(modifier = Modifier.height(42.dp))
        Text(text = stringResource(R.string.garbage_price), style = MaterialTheme.typography.h5)
        Box(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 64.dp)
        ){
            items(listGarbages){item ->
                CardPrice(imageUrl = item.imageUrl, name = item.name, price = item.price.toString())
            }
        }
    }
}