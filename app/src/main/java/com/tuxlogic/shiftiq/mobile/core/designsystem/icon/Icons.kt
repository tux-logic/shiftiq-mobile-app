package com.tuxlogic.shiftiq.mobile.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
val arrowForward: ImageVector
    get() {
        if (_arrowForward != null) {
            return _arrowForward!!
        }
        _arrowForward = ImageVector.Builder(
            name = "arrowForward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16.18f, 13f)
                horizontalLineTo(4f)
                verticalLineTo(11f)
                horizontalLineTo(16.18f)
                lineTo(10.58f, 5.4f)
                lineTo(12f, 4f)
                lineToRelative(8f, 8f)
                lineToRelative(-8f, 8f)
                lineTo(10.58f, 18.6f)
                lineTo(16.18f, 13f)
                close()
            }
        }.build()
        return _arrowForward!!
    }

private var _arrowForward: ImageVector? = null

@Suppress("CheckReturnValue")
val checkCircle: ImageVector
    get() {
        if (_checkCircle != null) {
            return _checkCircle!!
        }
        _checkCircle = ImageVector.Builder(
            name = "checkCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
                reflectiveCurveTo(17.52f, 2f, 12f, 2f)
                close()
                moveToRelative(-2f, 15f)
                lineToRelative(-5f, -5f)
                lineToRelative(1.41f, -1.41f)
                lineTo(10f, 14.17f)
                lineToRelative(7.59f, -7.59f)
                lineTo(19f, 8f)
                lineToRelative(-9f, 9f)
                close()
            }
        }.build()
        return _checkCircle!!
    }

private var _checkCircle: ImageVector? = null

@Suppress("CheckReturnValue")
val smartphone: ImageVector
    get() {
        if (_smartphone != null) {
            return _smartphone!!
        }
        _smartphone = ImageVector.Builder(
            name = "smartphone",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(17f, 1.01f)
                lineTo(7f, 1f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(18f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(10f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                lineTo(19f, 3f)
                curveToRelative(0f, -1.1f, -0.9f, -1.99f, -2f, -1.99f)
                close()
                moveTo(17f, 19f)
                lineTo(7f, 19f)
                lineTo(7f, 5f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(14f)
                close()
            }
        }.build()
        return _smartphone!!
    }

private var _smartphone: ImageVector? = null

@Suppress("CheckReturnValue")
val storefront: ImageVector
    get() {
        if (_storefront != null) {
            return _storefront!!
        }
        _storefront = ImageVector.Builder(
            name = "storefront",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 4f)
                horizontalLineTo(4f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(16f)
                verticalLineTo(4f)
                close()
                moveToRelative(1f, 10f)
                verticalLineToRelative(-2f)
                lineToRelative(-1f, -5f)
                horizontalLineTo(4f)
                lineToRelative(-1f, 5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(-6f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-6f)
                horizontalLineToRelative(1f)
                close()
                moveTo(6f, 18f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(4f)
                horizontalLineTo(6f)
                close()
            }
        }.build()
        return _storefront!!
    }

private var _storefront: ImageVector? = null

@Suppress("CheckReturnValue")
val googleLogo: ImageVector
    get() {
        if (_googleLogo != null) {
            return _googleLogo!!
        }
        _googleLogo = ImageVector.Builder(
            name = "googleLogo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Google Red
            path(
                fill = SolidColor(Color(0xFFEA4335)),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 5f)
                curveToRelative(1.6f, 0f, 3f, 0.6f, 4.1f, 1.6f)
                lineToRelative(3.1f, -3.1f)
                curveTo(17.3f, 1.8f, 14.8f, 1f, 12f, 1f)
                curveTo(7.5f, 1f, 3.7f, 3.6f, 1.9f, 7.3f)
                lineToRelative(3.7f, 2.9f)
                curveTo(6.5f, 7.3f, 9f, 5f, 12f, 5f)
                close()
            }
            // Google Yellow
            path(
                fill = SolidColor(Color(0xFFFBBC05)),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(1.9f, 7.3f)
                curveTo(1.3f, 8.7f, 1f, 10.3f, 1f, 12f)
                reflectiveCurveToRelative(0.3f, 3.3f, 0.9f, 4.7f)
                lineToRelative(3.7f, -2.9f)
                curveTo(5.3f, 13f, 5.2f, 12.5f, 5.2f, 12f)
                reflectiveCurveToRelative(0.1f, -1f, 0.4f, -1.8f)
                lineTo(1.9f, 7.3f)
                close()
            }
            // Google Green
            path(
                fill = SolidColor(Color(0xFF34A853)),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 23f)
                curveToRelative(2.8f, 0f, 5.2f, -0.9f, 6.9f, -2.5f)
                lineToRelative(-3.4f, -2.8f)
                curveToRelative(-1f, 0.7f, -2.2f, 1.1f, -3.5f, 1.1f)
                curveToRelative(-3f, 0f, -5.5f, -2.3f, -6.4f, -5.2f)
                lineTo(1.9f, 16.7f)
                curveTo(3.7f, 20.4f, 7.5f, 23f, 12f, 23f)
                close()
            }
            // Google Blue
            path(
                fill = SolidColor(Color(0xFF4285F4)),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(23f, 12.2f)
                curveToRelative(0f, -0.8f, -0.1f, -1.6f, -0.2f, -2.2f)
                horizontalLineTo(12f)
                verticalLineToRelative(4.5f)
                horizontalLineToRelative(6.2f)
                curveToRelative(-0.3f, 1.4f, -1.1f, 2.6f, -2.3f, 3.4f)
                lineToRelative(3.4f, 2.8f)
                curveTo(21.3f, 18.8f, 23f, 15.8f, 23f, 12.2f)
                close()
            }
        }.build()
        return _googleLogo!!
    }

private var _googleLogo: ImageVector? = null
