package br.edu.ifpe.jsreboques.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AllInbox
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.navigation.NavTarget
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsSecondary
import br.edu.ifpe.jsreboques.ui.theme.JsSurface
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

data class BottomNavItem(
    val title: String,
    val target: NavTarget,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Painel", NavTarget.PAINEL, Icons.Outlined.GridView),
    BottomNavItem("Reboques", NavTarget.REBOQUES, Icons.Outlined.LocalShipping),
    BottomNavItem("Estoque", NavTarget.ESTOQUE, Icons.Outlined.AllInbox),
    BottomNavItem("Clientes", NavTarget.CLIENTES, Icons.Outlined.People)
)

@Composable
fun JsBottomBar(
    currentRoute: String?,
    onNavigate: (NavTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = JsSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = when (item.target) {
                    NavTarget.PAINEL -> currentRoute == NavTarget.PAINEL.route
                    NavTarget.REBOQUES -> currentRoute == NavTarget.REBOQUES.route
                    NavTarget.ESTOQUE -> currentRoute == NavTarget.ESTOQUE.route
                    NavTarget.CLIENTES -> currentRoute == NavTarget.CLIENTES.route
                    else -> false
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigate(item.target) }
                ) {
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) JsSecondary else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) JsPrimary else JsTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) JsPrimary else JsTextSecondary
                    )
                }
            }
        }
    }
}
