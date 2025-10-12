package dev.stock.dysnomia.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import dev.stock.dysnomia.model.DeliveryStatus

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}

@Composable
fun Modifier.shimmer(
    isActive: Boolean,
    customShimmer: Shimmer = rememberShimmer(ShimmerBounds.View)
) = then(
    if (isActive) Modifier.shimmer(customShimmer)
    else Modifier
)

@Composable
fun Modifier.setVisualsBasedOfMessageStatus(deliveryStatus: DeliveryStatus) =
    this then when (deliveryStatus) {
        DeliveryStatus.DELIVERED -> {
            Modifier.padding(4.dp)
        }

        DeliveryStatus.PENDING -> {
            Modifier
                .alpha(0.5f)
                .padding(4.dp)
        }

        DeliveryStatus.FAILED -> {
            Modifier
                .background(MaterialTheme.colorScheme.onError)
                .padding(4.dp)
        }
    }
