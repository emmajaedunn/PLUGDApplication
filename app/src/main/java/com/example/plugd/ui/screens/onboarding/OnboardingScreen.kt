package com.example.plugd.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plugd.ui.screens.onboarding.common.PLUGDButton
import com.example.plugd.ui.screens.onboarding.common.PLUGDTextButton
import com.example.plugd.ui.screens.onboarding.components.OnboardingPage
import com.example.plugd.ui.screens.onboarding.components.PagerIndicator
import com.exmaple.plugd.ui.screens.theme.Dimens.MediumPadding2
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavHostController,
    onFinish: () -> Unit // <- remove @Composable
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = 0) {
            pages.size
        }
        val buttonsState = remember {
            derivedStateOf {
                when (pagerState.currentPage) {
                    0 -> listOf("", "Next")
                    1 -> listOf("Back", "Next")
                    2 -> listOf("Back", "Next")
                    3 -> listOf("Back", "Next")
                    4 -> listOf("Back", "Next")
                    5 -> listOf("Back", "Get Started")
                    else -> listOf("", "")
                }
            }
        }

        HorizontalPager(state = pagerState) { index ->
            OnboardingPage(page = pages[index])
        }

        Spacer(modifier = Modifier.weight(1f))

// Pager indicator (centered)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // add spacing from the bottom
            horizontalArrangement = Arrangement.Center
        ) {
            PagerIndicator(
                modifier = Modifier.width(60.dp),
                pagesSize = pages.size,
                selectedPage = pagerState.currentPage
            )
        }

// Arrow button (bottom right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MediumPadding2)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.End
        ) {
            val scope = rememberCoroutineScope()
            if (buttonsState.value[0].isNotEmpty()) {
                PLUGDTextButton(
                    text = buttonsState.value[0],
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage - 1
                            )
                        }
                    }
                )
            }
            PLUGDButton(
                text = buttonsState.value[1],
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage == pages.lastIndex) {
                            onFinish()
                        } else {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage + 1
                            )
                        }
                    }
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
