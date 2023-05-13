package com.bangkit.ecoease.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoease.R
import com.bangkit.ecoease.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

enum class StatusItemHistory{
    NOT_TAKEN, ON_PROCESS, TAKEN, CANCELED
}

@Composable
fun ItemHistory(
    statusItemHistory: StatusItemHistory,
    modifier: Modifier = Modifier
){
    Card(
        border = BorderStroke(
            width = 1.dp,
            color = when(statusItemHistory){
                StatusItemHistory.NOT_TAKEN -> BlueSecondary
                StatusItemHistory.ON_PROCESS -> GreenSecondary
                StatusItemHistory.TAKEN -> GreenPrimary
                StatusItemHistory.CANCELED -> OrangeAccent
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum", modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Box(modifier = Modifier.width(16.dp))
                Text(
                    text = "20-12-2012",
                    style = MaterialTheme.typography.caption.copy(
                        color = DarkGrey
                    )
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "total",
                    style = MaterialTheme.typography.caption.copy(
                        color = DarkGrey
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "Rp12.000")
            }
            Row {
                Text(
                    text = "status",
                    style = MaterialTheme.typography.caption.copy(
                        color = DarkGrey
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Box(modifier = Modifier.width(16.dp))
                StatusOrder(statusItemHistory = statusItemHistory)
                Box(modifier = Modifier.weight(1f))
                PillWidget(color = MaterialTheme.colors.secondary, text = stringResource(R.string.detail), textColor = MaterialTheme.colors.onBackground)
            }
        }
    }
}

@Composable
fun StatusOrder(
    statusItemHistory: StatusItemHistory,
    modifier: Modifier = Modifier
){
    val animatedColor by animateColorAsState(
        targetValue = when(statusItemHistory){
            StatusItemHistory.NOT_TAKEN -> BlueSecondary
            StatusItemHistory.ON_PROCESS -> GreenSecondary
            StatusItemHistory.TAKEN -> GreenPrimary
            StatusItemHistory.CANCELED -> OrangeAccent
        },
        animationSpec = tween(200)
    )
    PillWidget(
        color = animatedColor,
        text =
            when(statusItemHistory){
                StatusItemHistory.NOT_TAKEN -> {
                    stringResource(R.string.order_not_taken)
                }
                StatusItemHistory.ON_PROCESS -> {
                    stringResource(R.string.order_on_process)
                }
                StatusItemHistory.TAKEN -> {
                    stringResource(R.string.order_taken)
                }
                StatusItemHistory.CANCELED -> {
                    stringResource(R.string.order_canceled)
                }
            },
        textColor = Color.White,
        modifier = modifier
    )
}

@Composable
fun PillWidget(
    color: Color,
    textColor: Color,
    text: String,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ){
        Text(text = text, color = textColor, style = MaterialTheme.typography.caption)
    }
}

@Preview(showBackground = true)
@Composable
fun ItemHistoryPreview(){
    EcoEaseTheme() {
        Column(Modifier.padding(16.dp)) {
            ItemHistory(statusItemHistory = StatusItemHistory.NOT_TAKEN)
        }
    }
}
