package com.example.plugd.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.theme.Telegraf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutHelpPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    // small menu icon on the right – matches mock
                    IconButton(onClick = { /* open settings/help menu if needed */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ───────── Big title like mock ─────────
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "THE PLUG\nABOUT &\nHELP",
                    style = MaterialTheme.typography.displayLarge.copy(fontFamily = Telegraf
                    )
                )
            }

            // ───────── FAQ section ─────────
            Text(
                text = "FAQ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Telegraf,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FaqCard(
                    title = "How do I create a plug listing?",
                    subtitle = "Get PLUGD",
                ) {
                    // later you can navigate to a detailed FAQ screen
                }

                FaqCard(
                    title = "How do I change my theme?",
                    subtitle = "Customise your PLUGD look"
                ) {
                    // navigate to theme settings
                    navController.navigate("settings_theme")
                }

                FaqCard(
                    title = "Where can I view my community's activity?",
                    subtitle = "Find all your community spaces"
                ) {
                    // navigate to communities screen
                    navController.navigate("community_screen")
                }
            }

            // ───────── Privacy Policy ─────────
            SectionCard(
                heading = "Privacy Policy",
                body = """
We only collect the information needed to run PLUGD – like your email, basic profile details and activity inside the app (for example plugs you create, communities you join and reactions you send).

Your data is:
• Used to provide and improve the app  
• Never sold to third parties  
• Only shared with trusted service providers that help us run PLUGD (for example cloud hosting and analytics)  

You can request a copy of your data, update your information or ask us to delete your account at any time from Settings > Account.  When you delete your account we remove or anonymise your personal data unless we are required to keep it for legal or security reasons.
                """.trimIndent()
            )

            // ───────── Terms of Service ─────────
            SectionCard(
                heading = "Terms of Service",
                body = """
By using PLUGD you agree to:

• Follow all applicable laws when using the app  
• Only post content you have the right to share  
• Respect other users – no harassment, hate speech, spam or illegal content  

We may remove content or suspend accounts that break these rules or put our community at risk.

PLUGD is provided “as is”.  We work hard to keep the app available and secure, but we cannot guarantee uninterrupted access or accept responsibility for any loss caused by outages or misuse of the app.

If you continue to use PLUGD after we update these terms or our privacy policy, you are accepting the latest version.  For questions, contact support@getplugd.app.
                """.trimIndent()
            )

            // ───────── Contact row ─────────
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.size(25.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_phone),
                            contentDescription = "Call",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                }
                Spacer(Modifier.width(15.dp))
                Text(
                    text = "Call us at (083 7389 7390)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FaqCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFFFF9800),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // left icon “document” style
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_faq),
                        contentDescription = "FAQ",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Telegraf
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF000000),
                        fontFamily = Telegraf
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    heading: String,
    body: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = heading,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Telegraf,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = body,
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = Telegraf
            )
        }
    }
}




















/*package com.example.plugd.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.theme.Telegraf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutHelpPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title */ },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.about_help_title),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = Telegraf
                )
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = stringResource(R.string.about_plugd_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.about_plugd_body),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(1.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = stringResource(R.string.support_plugd_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.support_plugd_intro),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.support_plugd_email),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.support_plugd_website),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.support_plugd_phone),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.support_plugd_socials),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(1.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(1.dp))

        }
    }
}*/