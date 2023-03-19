package io.github.curo.ui.base

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.data.FABMenuItem

@Composable
fun FABAddMenu(
    isOpen: Boolean,
    onClose: (state: Boolean, selected: FABMenuItem) -> Unit,
    onToggle: () -> Unit,
) {
    val transition = updateTransition(targetState = isOpen, label = "FABAddMenuTransition")
    val (rotation, backgroundAlpha, actionMenuScale) = animationProperties(transition)

    Box(Modifier.fabBackgroundModifier(isOpen, onClose, backgroundAlpha)) {
        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .wrapContentSize(),
            horizontalAlignment = Alignment.End
        ) {
            if (isOpen) {
                ActiveFABMenu(
                    isOpen = true,
                    actionMenuScale = actionMenuScale,
                    onClose = onClose,
                )
            }
            MainAddButton(
                onToggle = onToggle,
                rotation = rotation,
            )
        }
    }
}

@Composable
private fun MainAddButton(onToggle: () -> Unit, rotation: Float) {
    FloatingActionButton(
        modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
        onClick = { onToggle() },
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "MainAddButton",
            modifier = Modifier.rotate(rotation)
        )
    }
}

@Composable
private fun animationProperties(transition: Transition<Boolean>) = FABAnimationProperties(
    rotation = transitionAnimation(
        transition = transition,
        trueValue = 45f
    ),
    backgroundAlpha = transitionAnimation(
        transition = transition,
        trueValue = 0.5f
    ),
    actionMenuScale = transitionAnimation(
        transition = transition,
        trueValue = 1f
    ),
)

@Suppress("SameParameterValue")
// isOpen is delegated
@Composable
private fun ActiveFABMenu(
    isOpen: Boolean,
    actionMenuScale: Float,
    onClose: (state: Boolean, selected: FABMenuItem) -> Unit,
) {
    ActiveFABMenuItem(
        item = FABMenuItem.ShoppingList,
        icon = Icons.Filled.ShoppingCart,
        isOpen = isOpen,
        actionMenuScale = actionMenuScale,
        onClose = onClose,
    )
    ActiveFABMenuItem(
        item = FABMenuItem.TODOList,
        icon = Icons.Filled.Menu,
        isOpen = isOpen,
        actionMenuScale = actionMenuScale,
        onClose = onClose,
    )
    ActiveFABMenuItem(
        item = FABMenuItem.Note,
        icon = Icons.Filled.Edit,
        isOpen = isOpen,
        actionMenuScale = actionMenuScale,
        onClose = onClose,
    )
}

@Composable
private fun ActiveFABMenuItem(
    isOpen: Boolean,
    actionMenuScale: Float,
    onClose: (state: Boolean, selected: FABMenuItem) -> Unit,
    item: FABMenuItem,
    icon: ImageVector,
) {
    ExtendedFloatingActionButton(
        modifier = Modifier
            .scale(actionMenuScale)
            .padding(end = 16.dp)
            .height(40.dp),
        onClick = {
            onClose(isOpen, item)
        },
    ) {
        val name = stringResource(item.id)
        Icon(icon, name)
        Spacer(modifier = Modifier.padding(start = 6.dp))
        Text(text = name)
    }
    Spacer(modifier = Modifier.padding(vertical = 5.dp))
}

@Composable
private fun transitionAnimation(
    transition: Transition<Boolean>,
    trueValue: Float,
): Float {
    val animationValue: Float by transition.animateFloat(
        label = "FABAddMenuTransition",
        transitionSpec = {
            tween(delayMillis = 100)
        }
    ) {
        if (it) {
            trueValue
        } else {
            0f
        }
    }

    return animationValue
}

private fun Modifier.fabBackgroundModifier(
    isOpen: Boolean,
    onClose: (state: Boolean, selected: FABMenuItem) -> Unit,
    backgroundAlpha: Float,
): Modifier =
    if (isOpen) {
        clickable(
            indication = null,
            interactionSource = MutableInteractionSource(),
            onClick = {
                @Suppress("KotlinConstantConditions")
                // isOpen is delegated
                onClose(isOpen, FABMenuItem.None)
            }
        )
    } else {
        this
    }
        .fillMaxSize()
        .background(Color.Black.copy(alpha = backgroundAlpha))

data class FABAnimationProperties(
    val rotation: Float,
    val backgroundAlpha: Float,
    val actionMenuScale: Float,
)

@Preview
@Composable
fun Preview_FABMenu() {
    var isOpen by remember {
        mutableStateOf(false)
    }

    FABAddMenu(
        isOpen = isOpen,
        onToggle = { isOpen = !isOpen },
        onClose = { state, _ ->
            if (state) {
                isOpen = !isOpen
            }
        })

    BackHandler(enabled = isOpen, onBack = {
        isOpen = !isOpen
    })
}