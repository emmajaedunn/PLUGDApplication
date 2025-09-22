package com.example.plugd.ui.screens.onboarding.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plugd.R
import com.exmaple.plugd.ui.screens.theme.Dimens.MediumPadding1
import com.exmaple.plugd.ui.screens.theme.Dimens.MediumPadding2
import com.example.plugd.ui.screens.onboarding.Page
import com.example.plugd.ui.screens.theme.PLUGDTheme
import com.example.plugd.ui.theme.Telegraf

@Composable
fun OnboardingPage(
    modifier: Modifier = Modifier,
    page: Page,
) {
    Column(modifier = modifier) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.60f),
            painter = painterResource(id = page.image),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(MediumPadding2))
        Text(
            modifier = Modifier.padding(horizontal = MediumPadding2),
            text = page.title,
            style = MaterialTheme.typography.displaySmall.copy(
                fontFamily = Telegraf,
                fontWeight = FontWeight.W800
            ),
            color = colorResource(id = R.color.display_small),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = MediumPadding2),
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = Telegraf,
                fontWeight = FontWeight.W400
            ),
            color = colorResource(id = R.color.text_medium),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OnBoardingPagePreview() {
    PLUGDTheme {
        OnboardingPage(
            page = Page(
                title = "Lorem Ipsum is simply dummy",
                description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                image = R.drawable.onboarding_current
            )
        )
    }
}