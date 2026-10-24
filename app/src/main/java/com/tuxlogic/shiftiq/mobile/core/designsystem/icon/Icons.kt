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
val arrowBack: ImageVector
    get() {
        if (_arrowBack != null) {
            return _arrowBack!!
        }
        _arrowBack = ImageVector.Builder(
            name = "arrowBack",
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
                moveTo(7.83f, 13f)
                lineToRelative(5.6f, 5.6f)
                lineTo(12f, 20f)
                lineTo(4f, 12f)
                lineTo(12f, 4f)
                lineToRelative(1.43f, 1.4f)
                lineTo(7.83f, 11f)
                horizontalLineTo(20f)
                verticalLineToRelative(2f)
                horizontalLineTo(7.83f)
                close()
            }
        }.build()
        return _arrowBack!!
    }

private var _arrowBack: ImageVector? = null

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
val mailOutline: ImageVector
    get() {
        if (_mailOutline != null) {
            return _mailOutline!!
        }
        _mailOutline = ImageVector.Builder(
            name = "mailOutline",
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
                lineTo(4f, 4f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(2f, 18f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(16f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                lineTo(22f, 6f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveToRelative(0f, 14f)
                lineTo(4f, 14f)
                lineTo(4f, 8f)
                lineToRelative(8f, 5f)
                lineToRelative(8f, -5f)
                verticalLineToRelative(10f)
                close()
                moveToRelative(-8f, -7f)
                lineTo(4f, 6f)
                horizontalLineToRelative(16f)
                lineToRelative(-8f, 5f)
                close()
            }
        }.build()
        return _mailOutline!!
    }

private var _mailOutline: ImageVector? = null

@Suppress("CheckReturnValue")
val lockOutline: ImageVector
    get() {
        if (_lockOutline != null) {
            return _lockOutline!!
        }
        _lockOutline = ImageVector.Builder(
            name = "lockOutline",
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
                moveTo(18f, 8f)
                horizontalLineToRelative(-1f)
                lineTo(17f, 6f)
                curveToRelative(0f, -2.76f, -2.24f, -5f, -5f, -5f)
                reflectiveCurveTo(7f, 3.24f, 7f, 6f)
                verticalLineToRelative(2f)
                lineTo(6f, 8f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(10f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(12f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                lineTo(20f, 10f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(9f, 6f)
                curveToRelative(0f, -1.66f, 1.34f, -3f, 3f, -3f)
                reflectiveCurveToRelative(3f, 1.34f, 3f, 3f)
                verticalLineToRelative(2f)
                lineTo(9f, 8f)
                lineTo(9f, 6f)
                close()
                moveToRelative(9f, 14f)
                lineTo(6f, 20f)
                lineTo(6f, 10f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(10f)
                close()
                moveToRelative(-6f, -3f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                close()
            }
        }.build()
        return _lockOutline!!
    }

private var _lockOutline: ImageVector? = null

@Suppress("CheckReturnValue")
val visibility: ImageVector
    get() {
        if (_visibility != null) {
            return _visibility!!
        }
        _visibility = ImageVector.Builder(
            name = "visibility",
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
                moveTo(15.19f, 14.69f)
                quadTo(16.5f, 13.38f, 16.5f, 11.5f)
                reflectiveQuadTo(15.19f, 8.31f)
                reflectiveQuadTo(12f, 7f)
                reflectiveQuadTo(8.81f, 8.31f)
                reflectiveQuadTo(7.5f, 11.5f)
                reflectiveQuadToRelative(1.31f, 3.19f)
                reflectiveQuadTo(12f, 16f)
                reflectiveQuadToRelative(3.19f, -1.31f)
                close()
                moveToRelative(-5.1f, -1.28f)
                quadTo(9.3f, 12.63f, 9.3f, 11.5f)
                reflectiveQuadTo(10.09f, 9.59f)
                reflectiveQuadTo(12f, 8.8f)
                reflectiveQuadToRelative(1.91f, 0.79f)
                quadToRelative(0.79f, 0.79f, 0.79f, 1.91f)
                reflectiveQuadToRelative(-0.79f, 1.91f)
                reflectiveQuadTo(12f, 14.2f)
                reflectiveQuadTo(10.09f, 13.41f)
                close()
                moveTo(5.35f, 16.96f)
                quadTo(2.35f, 14.93f, 1f, 11.5f)
                quadTo(2.35f, 8.07f, 5.35f, 6.04f)
                reflectiveQuadTo(12f, 4f)
                reflectiveQuadToRelative(6.65f, 2.04f)
                reflectiveQuadTo(23f, 11.5f)
                quadToRelative(-1.35f, 3.42f, -4.35f, 5.46f)
                reflectiveQuadTo(12f, 19f)
                reflectiveQuadTo(5.35f, 16.96f)
                close()
                moveTo(12f, 11.5f)
                close()
                moveToRelative(5.19f, 4.01f)
                quadTo(19.55f, 14.02f, 20.8f, 11.5f)
                quadTo(19.55f, 8.98f, 17.19f, 7.49f)
                reflectiveQuadTo(12f, 6f)
                quadTo(9.18f, 6f, 6.81f, 7.49f)
                reflectiveQuadTo(3.2f, 11.5f)
                quadToRelative(1.25f, 2.52f, 3.61f, 4.01f)
                reflectiveQuadTo(12f, 17f)
                reflectiveQuadToRelative(5.19f, -1.49f)
                close()
            }
        }.build()
        return _visibility!!
    }

private var _visibility: ImageVector? = null

@Suppress("CheckReturnValue")
val visibilityOff: ImageVector
    get() {
        if (_visibilityOff != null) {
            return _visibilityOff!!
        }
        _visibilityOff = ImageVector.Builder(
            name = "visibilityOff",
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
                moveTo(16.1f, 13.3f)
                lineTo(14.65f, 11.85f)
                quadToRelative(0.22f, -1.18f, -0.67f, -2.2f)
                quadTo(13.08f, 8.63f, 11.65f, 8.85f)
                lineTo(10.2f, 7.4f)
                quadTo(10.63f, 7.2f, 11.06f, 7.1f)
                reflectiveQuadTo(12f, 7f)
                quadToRelative(1.88f, 0f, 3.19f, 1.31f)
                reflectiveQuadTo(16.5f, 11.5f)
                quadToRelative(0f, 0.5f, -0.1f, 0.94f)
                reflectiveQuadTo(16.1f, 13.3f)
                close()
                moveToRelative(3.2f, 3.15f)
                lineToRelative(-1.45f, -1.4f)
                quadToRelative(0.95f, -0.72f, 1.69f, -1.59f)
                reflectiveQuadTo(20.8f, 11.5f)
                quadTo(19.55f, 8.98f, 17.21f, 7.49f)
                reflectiveQuadTo(12f, 6f)
                quadTo(11.28f, 6f, 10.58f, 6.1f)
                reflectiveQuadTo(9.2f, 6.4f)
                lineTo(7.65f, 4.85f)
                quadTo(8.68f, 4.42f, 9.75f, 4.21f)
                reflectiveQuadTo(12f, 4f)
                quadToRelative(3.78f, 0f, 6.73f, 2.09f)
                reflectiveQuadTo(23f, 11.5f)
                quadToRelative(-0.57f, 1.47f, -1.51f, 2.74f)
                reflectiveQuadTo(19.3f, 16.45f)
                close()
                moveToRelative(0.5f, 6.15f)
                lineTo(15.6f, 18.45f)
                quadToRelative(-0.88f, 0.28f, -1.76f, 0.41f)
                reflectiveQuadTo(12f, 19f)
                quadTo(8.23f, 19f, 5.28f, 16.91f)
                reflectiveQuadTo(1f, 11.5f)
                quadTo(1.53f, 10.17f, 2.33f, 9.04f)
                reflectiveQuadTo(4.15f, 7f)
                lineTo(1.4f, 4.2f)
                lineTo(2.8f, 2.8f)
                lineTo(21.2f, 21.2f)
                lineToRelative(-1.4f, 1.4f)
                close()
                moveTo(5.55f, 8.4f)
                quadTo(4.83f, 9.05f, 4.23f, 9.82f)
                reflectiveQuadTo(3.2f, 11.5f)
                quadToRelative(1.25f, 2.52f, 3.59f, 4.01f)
                reflectiveQuadTo(12f, 17f)
                quadToRelative(0.5f, 0f, 0.98f, -0.06f)
                reflectiveQuadTo(13.95f, 16.8f)
                lineToRelative(-0.9f, -0.95f)
                quadToRelative(-0.28f, 0.07f, -0.53f, 0.11f)
                reflectiveQuadTo(12f, 16f)
                quadTo(10.13f, 16f, 8.81f, 14.69f)
                reflectiveQuadTo(7.5f, 11.5f)
                quadToRelative(0f, -0.28f, 0.04f, -0.53f)
                reflectiveQuadTo(7.65f, 10.45f)
                lineTo(5.55f, 8.4f)
                close()
            }
        }.build()
        return _visibilityOff!!
    }

private var _visibilityOff: ImageVector? = null

@Suppress("CheckReturnValue")
val directionsCar: ImageVector
    get() {
        if (_directionsCar != null) {
            return _directionsCar!!
        }
        _directionsCar = ImageVector.Builder(
            name = "directionsCar",
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
                moveTo(18.92f, 6.01f)
                curveTo(18.72f, 5.42f, 18.16f, 5f, 17.5f, 5f)
                horizontalLineToRelative(-11f)
                curveToRelative(-0.66f, 0f, -1.21f, 0.42f, -1.42f, 1.01f)
                lineTo(3f, 12f)
                verticalLineToRelative(8f)
                curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
                horizontalLineToRelative(1f)
                curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
                verticalLineToRelative(-1f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(1f)
                curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
                horizontalLineToRelative(1f)
                curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
                verticalLineToRelative(-8f)
                lineToRelative(-2.08f, -5.99f)
                close()
                moveTo(6.85f, 7f)
                horizontalLineToRelative(10.29f)
                lineToRelative(1.08f, 3.11f)
                horizontalLineTo(5.77f)
                lineTo(6.85f, 7f)
                close()
                moveTo(19f, 17f)
                horizontalLineTo(5f)
                verticalLineToRelative(-5f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(5f)
                close()
                moveTo(7.5f, 16f)
                curveToRelative(0.83f, 0f, 1.5f, -0.67f, 1.5f, -1.5f)
                reflectiveCurveTo(8.33f, 13f, 7.5f, 13f)
                reflectiveCurveTo(6f, 13.67f, 6f, 14.5f)
                reflectiveCurveTo(6.67f, 16f, 7.5f, 16f)
                close()
                moveToRelative(9f, 0f)
                curveToRelative(0.83f, 0f, 1.5f, -0.67f, 1.5f, -1.5f)
                reflectiveCurveToRelative(-0.67f, -1.5f, -1.5f, -1.5f)
                reflectiveCurveToRelative(-1.5f, 0.67f, -1.5f, 1.5f)
                reflectiveCurveToRelative(0.67f, 1.5f, 1.5f, 1.5f)
                close()
            }
        }.build()
        return _directionsCar!!
    }

private var _directionsCar: ImageVector? = null

@Suppress("CheckReturnValue")
val appleLogo: ImageVector
    get() {
        if (_appleLogo != null) {
            return _appleLogo!!
        }
        _appleLogo = ImageVector.Builder(
            name = "appleLogo",
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
                moveTo(18.71f, 19.5f)
                curveToRelative(-0.83f, 1.24f, -1.71f, 2.45f, -3.05f, 2.47f)
                curveToRelative(-1.34f, 0.03f, -1.77f, -0.79f, -3.29f, -0.79f)
                curveToRelative(-1.53f, 0f, -2f, 0.77f, -3.27f, 0.82f)
                curveToRelative(-1.31f, 0.05f, -2.3f, -1.32f, -3.14f, -2.53f)
                curveTo(4.25f, 17f, 2.94f, 12.45f, 4.7f, 9.39f)
                curveToRelative(0.87f, -1.52f, 2.43f, -2.48f, 4.12f, -2.51f)
                curveToRelative(1.28f, -0.02f, 2.5f, 0.87f, 3.29f, 0.87f)
                curveToRelative(0.78f, 0f, 2.26f, -1.07f, 3.81f, -0.91f)
                curveToRelative(0.65f, 0.03f, 2.47f, 0.26f, 3.64f, 1.98f)
                curveToRelative(-0.09f, 0.06f, -2.17f, 1.28f, -2.15f, 3.81f)
                curveToRelative(0.03f, 3.02f, 2.65f, 4.03f, 2.68f, 4.04f)
                curveToRelative(-0.03f, 0.07f, -0.42f, 1.44f, -1.38f, 2.83f)
                close()
                moveTo(15.97f, 4.71f)
                curveToRelative(0.66f, -0.8f, 1.11f, -1.92f, 0.99f, -3.04f)
                curveToRelative(-0.96f, 0.04f, -2.13f, 0.64f, -2.82f, 1.45f)
                curveToRelative(-0.61f, 0.7f, -1.14f, 1.83f, -1f, 2.92f)
                curveToRelative(1.07f, 0.08f, 2.17f, -0.53f, 2.83f, -1.33f)
                close()
            }
        }.build()
        return _appleLogo!!
    }

private var _appleLogo: ImageVector? = null

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
val shieldIcon: ImageVector
    get() {
        if (_shieldIcon != null) {
            return _shieldIcon!!
        }
        _shieldIcon = ImageVector.Builder(
            name = "shieldIcon",
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
                moveTo(12f, 1f)
                lineTo(3f, 5f)
                verticalLineToRelative(6f)
                curveToRelative(0f, 5.55f, 3.84f, 10.74f, 9f, 12f)
                curveToRelative(5.16f, -1.26f, 9f, -6.45f, 9f, -12f)
                verticalLineTo(5f)
                lineToRelative(-9f, -4f)
                close()
                moveToRelative(-2f, 16f)
                lineToRelative(-4f, -4f)
                lineToRelative(1.41f, -1.41f)
                lineTo(10f, 14.17f)
                lineToRelative(6.59f, -6.59f)
                lineTo(18f, 9f)
                lineToRelative(-8f, 8f)
                close()
            }
        }.build()
        return _shieldIcon!!
    }

private var _shieldIcon: ImageVector? = null

@Suppress("CheckReturnValue")
val accessTime: ImageVector
    get() {
        if (_accessTime != null) {
            return _accessTime!!
        }
        _accessTime = ImageVector.Builder(
            name = "accessTime",
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
                moveTo(11.99f, 2f)
                curveTo(6.47f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.47f, 10f, 9.99f, 10f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                reflectiveCurveTo(17.52f, 2f, 11.99f, 2f)
                close()
                moveTo(12f, 20f)
                curveToRelative(-4.42f, 0f, -8f, -3.58f, -8f, -8f)
                reflectiveCurveToRelative(3.58f, -8f, 8f, -8f)
                reflectiveCurveToRelative(8f, 3.58f, 8f, 8f)
                reflectiveCurveToRelative(-3.58f, 8f, -8f, 8f)
                close()
                moveToRelative(0.5f, -13f)
                horizontalLineTo(11f)
                verticalLineToRelative(6f)
                lineToRelative(5.25f, 3.15f)
                lineToRelative(0.75f, -1.23f)
                lineToRelative(-4.5f, -2.67f)
                close()
            }
        }.build()
        return _accessTime!!
    }

private var _accessTime: ImageVector? = null

@Suppress("CheckReturnValue")
val hourglassEmpty: ImageVector
    get() {
        if (_hourglassEmpty != null) {
            return _hourglassEmpty!!
        }
        _hourglassEmpty = ImageVector.Builder(
            name = "hourglassEmpty",
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
                moveTo(6f, 2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(0.01f)
                lineTo(6f, 8.01f)
                lineTo(10f, 12f)
                lineToRelative(-4f, 4f)
                lineToRelative(0.01f, 0.01f)
                horizontalLineTo(6f)
                verticalLineTo(22f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(-5.99f)
                horizontalLineToRelative(-0.01f)
                lineTo(18f, 16f)
                lineToRelative(-4f, -4f)
                lineToRelative(4f, -3.99f)
                lineToRelative(-0.01f, -0.01f)
                horizontalLineTo(18f)
                verticalLineTo(2f)
                horizontalLineTo(6f)
                close()
                moveToRelative(10f, 14.5f)
                verticalLineTo(20f)
                horizontalLineTo(8f)
                verticalLineToRelative(-3.5f)
                lineToRelative(4f, -4f)
                lineToRelative(4f, 4f)
                close()
                moveToRelative(0f, -9f)
                lineToRelative(-4f, 4f)
                lineToRelative(-4f, -4f)
                verticalLineTo(4f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(3.5f)
                close()
            }
        }.build()
        return _hourglassEmpty!!
    }

private var _hourglassEmpty: ImageVector? = null

@Suppress("CheckReturnValue")
val syncIcon: ImageVector
    get() {
        if (_syncIcon != null) {
            return _syncIcon!!
        }
        _syncIcon = ImageVector.Builder(
            name = "syncIcon",
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
                moveTo(12f, 4f)
                verticalLineTo(1f)
                lineTo(8f, 5f)
                lineToRelative(4f, 4f)
                verticalLineTo(6f)
                curveToRelative(3.31f, 0f, 6f, 2.69f, 6f, 6f)
                curveToRelative(0f, 1.01f, -0.25f, 1.97f, -0.7f, 2.8f)
                lineToRelative(1.46f, 1.46f)
                curveTo(19.54f, 15.03f, 20f, 13.57f, 20f, 12f)
                curveToRelative(0f, -4.42f, -3.58f, -8f, -8f, -8f)
                close()
                moveToRelative(0f, 14f)
                curveToRelative(-3.31f, 0f, -6f, -2.69f, -6f, -6f)
                curveToRelative(0f, -1.01f, 0.25f, -1.97f, 0.7f, -2.8f)
                lineTo(5.24f, 7.74f)
                curveTo(4.46f, 8.97f, 4f, 10.43f, 4f, 12f)
                curveToRelative(0f, 4.42f, 3.58f, 8f, 8f, 8f)
                verticalLineToRelative(3f)
                lineToRelative(4f, -4f)
                lineToRelative(-4f, -4f)
                verticalLineToRelative(3f)
                close()
            }
        }.build()
        return _syncIcon!!
    }

private var _syncIcon: ImageVector? = null

@Suppress("CheckReturnValue")
val closeIcon: ImageVector
    get() {
        if (_closeIcon != null) {
            return _closeIcon!!
        }
        _closeIcon = ImageVector.Builder(
            name = "closeIcon",
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
                moveTo(19f, 6.41f)
                lineTo(17.59f, 5f)
                lineTo(12f, 10.59f)
                lineTo(6.41f, 5f)
                lineTo(5f, 6.41f)
                lineTo(10.59f, 12f)
                lineTo(5f, 17.59f)
                lineTo(6.41f, 19f)
                lineTo(12f, 13.41f)
                lineTo(17.59f, 19f)
                lineTo(19f, 17.59f)
                lineTo(13.41f, 12f)
                close()
            }
        }.build()
        return _closeIcon!!
    }

private var _closeIcon: ImageVector? = null

@Suppress("CheckReturnValue")
val errorOutline: ImageVector
    get() {
        if (_errorOutline != null) {
            return _errorOutline!!
        }
        _errorOutline = ImageVector.Builder(
            name = "errorOutline",
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
                moveTo(11f, 15f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-2f)
                close()
                moveToRelative(0f, -8f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(-2f)
                close()
                moveToRelative(0.99f, -5f)
                curveTo(6.47f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.47f, 10f, 9.99f, 10f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                reflectiveCurveTo(17.52f, 2f, 11.99f, 2f)
                close()
                moveTo(12f, 20f)
                curveToRelative(-4.42f, 0f, -8f, -3.58f, -8f, -8f)
                reflectiveCurveToRelative(3.58f, -8f, 8f, -8f)
                reflectiveCurveToRelative(8f, 3.58f, 8f, 8f)
                reflectiveCurveToRelative(-3.58f, 8f, -8f, 8f)
                close()
            }
        }.build()
        return _errorOutline!!
    }

private var _errorOutline: ImageVector? = null

@Suppress("CheckReturnValue")
val person: ImageVector
    get() {
        if (_person != null) {
            return _person!!
        }
        _person = ImageVector.Builder(
            name = "person",
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
                moveTo(12f, 12f)
                curveToRelative(2.21f, 0f, 4f, -1.79f, 4f, -4f)
                reflectiveCurveToRelative(-1.79f, -4f, -4f, -4f)
                reflectiveCurveToRelative(-4f, 1.79f, -4f, 4f)
                reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
                close()
                moveToRelative(0f, 2f)
                curveToRelative(-2.67f, 0f, -8f, 1.34f, -8f, 4f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(16f)
                verticalLineToRelative(-2f)
                curveToRelative(0f, -2.66f, -5.33f, -4f, -8f, -4f)
                close()
            }
        }.build()
        return _person!!
    }

private var _person: ImageVector? = null

@Suppress("CheckReturnValue")
val bluetooth: ImageVector
    get() {
        if (_bluetooth != null) {
            return _bluetooth!!
        }
        _bluetooth = ImageVector.Builder(
            name = "bluetooth",
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
                moveTo(17.71f, 7.71f)
                lineTo(12f, 2f)
                horizontalLineToRelative(-1f)
                verticalLineToRelative(7.59f)
                lineTo(6.41f, 5f)
                lineTo(5f, 6.41f)
                lineTo(10.59f, 12f)
                lineTo(5f, 17.59f)
                lineTo(6.41f, 19f)
                lineTo(11f, 14.41f)
                verticalLineTo(22f)
                horizontalLineToRelative(1f)
                lineToRelative(5.71f, -5.71f)
                lineToRelative(-4.3f, -4.29f)
                lineToRelative(4.3f, -4.29f)
                close()
                moveTo(13f, 5.83f)
                lineToRelative(1.88f, 1.88f)
                lineTo(13f, 9.59f)
                verticalLineTo(5.83f)
                close()
                moveToRelative(1.88f, 10.46f)
                lineTo(13f, 18.17f)
                verticalLineToRelative(-3.76f)
                lineToRelative(1.88f, 1.88f)
                close()
            }
        }.build()
        return _bluetooth!!
    }

private var _bluetooth: ImageVector? = null

@Suppress("CheckReturnValue")
val photoCamera: ImageVector
    get() {
        if (_photoCamera != null) {
            return _photoCamera!!
        }
        _photoCamera = ImageVector.Builder(
            name = "photoCamera",
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
                moveTo(12f, 15f)
                curveToRelative(1.66f, 0f, 3f, -1.34f, 3f, -3f)
                reflectiveCurveToRelative(-1.34f, -3f, -3f, -3f)
                reflectiveCurveToRelative(-3f, 1.34f, -3f, 3f)
                reflectiveCurveToRelative(1.34f, 3f, 3f, 3f)
                close()
                moveToRelative(9f, -8f)
                horizontalLineToRelative(-3.17f)
                lineTo(16f, 4.83f)
                horizontalLineTo(8f)
                lineTo(6.17f, 7f)
                horizontalLineTo(3f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(12f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(18f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(9f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveToRelative(0f, 14f)
                horizontalLineTo(3f)
                verticalLineTo(9f)
                horizontalLineToRelative(3.91f)
                lineToRelative(1.83f, -2f)
                horizontalLineToRelative(6.52f)
                lineToRelative(1.83f, 2f)
                horizontalLineTo(21f)
                verticalLineToRelative(12f)
                close()
            }
        }.build()
        return _photoCamera!!
    }

private var _photoCamera: ImageVector? = null

@Suppress("CheckReturnValue")
val badgeIcon: ImageVector
    get() {
        if (_badgeIcon != null) {
            return _badgeIcon!!
        }
        _badgeIcon = ImageVector.Builder(
            name = "badgeIcon",
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
                moveTo(20f, 7f)
                horizontalLineToRelative(-5f)
                verticalLineTo(4f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                horizontalLineToRelative(-2f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(3f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(11f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(16f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(9f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(11f, 4f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(-2f)
                verticalLineTo(4f)
                close()
                moveToRelative(9f, 16f)
                horizontalLineTo(4f)
                verticalLineTo(9f)
                horizontalLineToRelative(5f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(2f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(11f)
                close()
            }
        }.build()
        return _badgeIcon!!
    }

private var _badgeIcon: ImageVector? = null
