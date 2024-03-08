package org.readium.r2.testapp.compose.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.readium.r2.testapp.R

@Composable
internal fun AboutScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .padding(15.dp)
            .fillMaxSize()
    ) {
        HeadingText(text = stringResource(R.string.app_version_header))
        InfoRow(
            label = stringResource(R.string.app_version_label),
            value = stringResource(R.string.app_version)
        )
        InfoRow(
            label = stringResource(R.string.github_tab_label),
            value = stringResource(R.string.github_tag)
        )
        HeadingText(text = stringResource(R.string.copyright_label))
        Text(text = stringResource(R.string.copyright), fontSize = 18.sp)
        Text(text = stringResource(R.string.bsd_license_label), fontSize = 18.sp)
        HeadingText(text = stringResource(R.string.acknowledgements_label))
        Text(text = stringResource(R.string.acknowledgements_french_state), fontSize = 18.sp)
        Image(
            painter = painterResource(id = R.drawable.repfr),
            contentDescription = stringResource(id = R.string.repfr),
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun HeadingText(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun InfoRow(modifier: Modifier = Modifier, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 18.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .width(120.dp)
                .padding(end = 8.dp)
        )
        Spacer(modifier = modifier.width(16.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            textAlign = TextAlign.Start
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}
