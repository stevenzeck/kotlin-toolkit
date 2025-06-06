package org.readium.r2.testapp.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.readium.r2.testapp.R
import org.readium.r2.testapp.utils.compose.AppTheme

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppVersionInfo()
        CopyrightInfo()
        AcknowledgementsInfo()
    }
}

@Composable
private fun AppVersionInfo() {
    Column {
        SectionTitle(text = stringResource(R.string.app_version_header))
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = stringResource(R.string.app_version_label),
            value = stringResource(R.string.app_version)
        )
        InfoRow(
            label = stringResource(R.string.github_tab_label),
            value = stringResource(R.string.github_tag)
        )
    }
}

@Composable
private fun CopyrightInfo() {
    Column {
        SectionTitle(text = stringResource(R.string.copyright_label))
        Spacer(modifier = Modifier.height(8.dp))
        InfoText(text = stringResource(R.string.copyright))
        InfoText(
            text = stringResource(R.string.bsd_license_label),
            contentDescription = stringResource(R.string.bsd_license_label_accessible)
        )
    }
}

@Composable
private fun AcknowledgementsInfo() {
    Column {
        SectionTitle(text = stringResource(R.string.acknowledgements_label))
        Spacer(modifier = Modifier.height(8.dp))
        InfoText(text = stringResource(R.string.acknowledgements_french_state))
        Image(
            painter = painterResource(id = R.drawable.repfr),
            contentDescription = stringResource(R.string.repfr),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            alignment = Alignment.Center
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 18.sp)
        Text(text = value, fontSize = 18.sp)
    }
}

@Composable
private fun InfoText(text: String, contentDescription: String? = null) {
    val modifier = if (contentDescription != null) {
        Modifier.padding(vertical = 4.dp)
    } else {
        Modifier.padding(vertical = 4.dp)
    }
    Text(
        text = text,
        fontSize = 18.sp,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    AppTheme {
        AboutScreen()
    }
}
