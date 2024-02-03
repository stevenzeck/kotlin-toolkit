package org.readium.r2.testapp.compose.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.readium.r2.testapp.R
import org.readium.r2.testapp.compose.Screen

@Composable
internal fun AboutScreen() {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(15.dp)) {
        SectionHeading(stringResource(id = R.string.app_version_header))
        Row(horizontalArrangement = Arrangement.spacedBy(50.dp)) {
            Column {
                Text(fontSize = 18.sp, text = stringResource(id = R.string.app_version_label))
            }
            Column {
                Text(fontSize = 18.sp, text = stringResource(id = R.string.app_version))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(50.dp)) {
            Column {
                Text(fontSize = 18.sp, text = stringResource(id = R.string.github_tab_label))
            }
            Column {
                Text(fontSize = 18.sp, text = stringResource(id = R.string.github_tag))
            }
        }
        SectionHeading(stringResource(id = R.string.copyright_label))
        Row {
            Text(fontSize = 18.sp, text = stringResource(id = R.string.copyright))
        }
        Row {
            Text(fontSize = 18.sp, text = stringResource(id = R.string.bsd_license_label))
        }
        SectionHeading(stringResource(id = R.string.acknowledgements_label))
        Row {
            Text(
                fontSize = 18.sp,
                text = stringResource(id = R.string.acknowledgements_french_state)
            )
        }
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.width(370.dp)) {
            Image(
                painter = painterResource(id = R.drawable.repfr),
                contentDescription = stringResource(id = R.string.repfr),
            )
        }
    }
}

@Composable
fun SectionHeading(headingText: String) {
    Row(modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)) {
        Text(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            text = headingText
        )
    }
}

fun NavController.navigateToAbout(navOptions: NavOptions) =
    navigate(Screen.BottomNav.About.route, navOptions)

fun NavGraphBuilder.aboutScreen() {
    composable(Screen.BottomNav.About.route) {
        AboutScreen()
    }
}
