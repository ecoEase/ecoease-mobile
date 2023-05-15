package com.bangkit.ecoease.ui.component

import androidx.compose.ui.Alignment
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.R
import com.bangkit.ecoease.ui.theme.BluePrimary
import com.bangkit.ecoease.ui.theme.DarkGrey
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

@Composable
fun AddressCard(
    name: String,
    detail: String,
    district: String,
    city: String,
    onClickChange: () -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        border = BorderStroke(width = 1.dp, color = BluePrimary),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
        ,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
            ,
        ) {
            Text(text = name)
            Box(modifier = Modifier.height(8.dp))
            Text(text = detail, style = MaterialTheme.typography.body2)
            Box(modifier = Modifier.height(4.dp))
            Text(text = "$district, $city", style = MaterialTheme.typography.body2.copy(
                color = DarkGrey
            ))
            RoundedButton(
                text = stringResource(R.string.change),
                onClick = onClickChange,
                modifier = Modifier.align(Alignment.End),
                type = RoundedButtonType.SECONDARY
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddressPreview(){
    EcoEaseTheme() {
        AddressCard(name = "Alamat 1", detail = "Jalan yang lurus lorem ipsum blabal", district = "Candi 2", city = "Malang", onClickChange = {})
    }
}