package io.github.curo.ui.base

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.curo.data.FABMenu

@Composable
fun FABAddMenu(
    fabButtonState: FABButtonState,
    onClose: (selected: FABMenu) -> Unit,
    onToggle: () -> Unit,
    properties: FABAnimationProperties,
) {
    if (fabButtonState == FABButtonState.Hidden) {
        return
    }
    Column(
        Modifier
            .wrapContentSize()
            .padding(end = 16.dp, bottom = 96.dp),
        horizontalAlignment = Alignment.End
    ) {
        if (fabButtonState == FABButtonState.Opened) {
            ActiveFABMenu(
                actionMenuScale = properties.actionMenuScale,
                onClose = onClose,
            )
        }
        MainAddButton(
            onToggle = onToggle,
            rotation = properties.rotation,
        )
    }
}

@Composable
private fun MainAddButton(onToggle: () -> Unit, rotation: Float) {
    FloatingActionButton(
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
fun fabAnimationProperties(transition: Transition<Boolean>) = FABAnimationProperties(
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

val fabMenu = listOf(FABMenu.Note, FABMenu.ShoppingList, FABMenu.TODOList)

@Composable
private fun ActiveFABMenu(
    actionMenuScale: Float,
    onClose: (selected: FABMenu) -> Unit,
) {
    fabMenu.forEach {
        ActiveFABMenuItem(
            actionMenuScale = actionMenuScale,
            onClose = onClose,
            item = it,
        )
    }
}

@Composable
private fun ActiveFABMenuItem(
    actionMenuScale: Float,
    onClose: (selected: FABMenu) -> Unit,
    item: FABMenu,
) {
    ExtendedFloatingActionButton(
        modifier = Modifier
            .scale(actionMenuScale)
            .padding(end = 16.dp)
            .height(40.dp),
        onClick = {
            onClose(item)
        },
    ) {
        val name = stringResource(item.name)
        Icon(item.icon, name)
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

fun Modifier.fabBackgroundModifier(
    isOpen: Boolean,
    onClose: () -> Unit,
): Modifier =
    if (isOpen) {
        this
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            .semantics(mergeDescendants = true) {
                onClick { onClose(); true }
            }
    } else {
        this
    }

data class FABAnimationProperties(
    val rotation: Float,
    val backgroundAlpha: Float,
    val actionMenuScale: Float,
)

@Immutable
sealed class FABButtonState {
    @Immutable
    object Opened : FABButtonState()

    @Immutable
    object Closed : FABButtonState()

    @Immutable
    object Hidden : FABButtonState()

    fun act() = when (this) {
        Closed -> Opened
        Hidden -> Hidden
        Opened -> Closed
    }

    fun opened() = this == Opened
}

@Preview
@Composable
fun Preview_FABMenu() {
    var fabButtonState: FABButtonState by remember { mutableStateOf(FABButtonState.Opened) }

    val transition = updateTransition(targetState = fabButtonState.opened(), label = "FABAddMenuTransition")
    val fabAnimationProperties = fabAnimationProperties(transition)

    FABAddMenu(
        fabButtonState = fabButtonState,
        onToggle = { fabButtonState = fabButtonState.act() },
        onClose = {},
        properties = fabAnimationProperties,
    )

    BackHandler(enabled = fabButtonState.opened(), onBack = {
        fabButtonState = fabButtonState.act()
    })
}