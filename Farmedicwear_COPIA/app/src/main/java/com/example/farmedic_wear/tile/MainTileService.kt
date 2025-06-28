package com.example.farmedic_wear.tile

import android.content.Context
import android.util.Log
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService

private var RESOURCES_VERSION = 0L

@OptIn(ExperimentalHorologistApi::class)
class MainTileService : SuspendingTileService() {

    private val viewModel = FarmaMedicViewModel(applicationContext)

    override fun onCreate() {
        super.onCreate()
        Log.d("MainTileService", "Servicio de tile creado")
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        Log.d("MainTileService", "Solicitud de recursos recibida")
        return ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION.toString())
            .build()
    }

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        Log.d("MainTileService", "Solicitud de tile recibida")
        return tile(requestParams, this@MainTileService)
    }

    private suspend fun tile(
        requestParams: RequestBuilders.TileRequest,
        context: Context,
    ): TileBuilders.Tile {
        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout(requestParams, context))
                            .build()
                    )
                    .build()
            )
            .build()

        return TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION.toString())
            .setTileTimeline(singleTileTimeline)
            .setFreshnessIntervalMillis(1000L) // Actualizar cada segundo
            .build()
    }

    private suspend fun tileLayout(
        requestParams: RequestBuilders.TileRequest,
        context: Context,
    ): LayoutElementBuilders.LayoutElement {
        val stepsText = "Pasos: ${viewModel.stepCount}"
        Log.d("MainTileService", "Renderizando tile con: $stepsText")
        RESOURCES_VERSION++

        val defaultDeviceParams = DeviceParametersBuilders.DeviceParameters.Builder()
            .setScreenWidthDp(192)
            .setScreenHeightDp(192)
            .setScreenDensity(1.0f)
            .setFontScale(1.0f)
            .build()

        return PrimaryLayout.Builder(defaultDeviceParams)
            .setResponsiveContentInsetEnabled(true)
            .setContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        Text.Builder(context, stepsText)
                            .setColor(argb(Colors.DEFAULT.onSurface))
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .build()
                    )
                    .build()
            )
            .build()
    }
}