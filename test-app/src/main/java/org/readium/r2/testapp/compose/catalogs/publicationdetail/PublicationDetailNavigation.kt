package org.readium.r2.testapp.compose.catalogs.publicationdetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.readium.r2.testapp.compose.Screen

fun NavController.navigateToPublicationDetail() =
    this.navigate(Screen.PublicationDetail.route)

fun NavGraphBuilder.publicationDetailScreen(publicationDetailViewModel: PublicationDetailViewModel) {
    composable(route = Screen.PublicationDetail.route) {
        PublicationDetailScreen(publicationDetailViewModel = publicationDetailViewModel)
    }
}
