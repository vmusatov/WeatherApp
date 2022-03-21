package com.example.weatherapp.ui.home

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

private const val STROKE_WIDTH = 4f
private const val PATH_CORNER_RADIUS_IN_DP = 30
private const val CHILD_HEADER_OR_FOOTER_HEIGHT_IN_DP = 12
private const val GRAPH_START_HEIGHT_DELTA = 95

class HourlyForecastItemDecorator(
    hours: List<Double>,
    private val context: Context
) : RecyclerView.ItemDecoration() {

    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = getDrawColor()
        pathEffect = CornerPathEffect(PATH_CORNER_RADIUS_IN_DP.dpToPx)
    }

    @ColorInt
    private fun getDrawColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondaryVariant,
            typedValue,
            true
        )
        return typedValue.data
    }

    private val normalizedHourTempValues = normalizeHourTempValues(hours)

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)

        val itemsCount = parent.adapter?.itemCount ?: -1

        val path = Path()
        var newPath = true

        for (childIndex in 0 until parent.childCount) {
            val childView = parent.getChildAt(childIndex)
            val dataIndex = parent.getChildAdapterPosition(childView)
            val childViewHeight = (childView.height / 2.55).toInt()
            val halfChildViewWidth = (childView.right.toFloat() - childView.left.toFloat()) / 2

            if (newPath) {
                val previousDataIndex = if (dataIndex > 0) (dataIndex - 1) else 0
                val moveToYPosition = calculateYValue(previousDataIndex, childViewHeight)
                path.moveTo(childView.left.toFloat() - halfChildViewWidth, moveToYPosition)
                newPath = false
            }

            val x = childView.right.toFloat() - halfChildViewWidth
            val y = calculateYValue(dataIndex, childViewHeight)

            if (dataIndex > 0) {
                path.lineTo(x, y)
            }
            path.addCircle(x, y, 5f, Path.Direction.CW)

            if (childIndex == parent.childCount - 1 && dataIndex != itemsCount - 1) {
                drawPathForNextChildView(
                    dataIndex + 1,
                    childView.right.toFloat(),
                    path,
                    halfChildViewWidth,
                    childViewHeight
                )
            }
        }

        canvas.drawPath(path, drawPaint)
    }

    private fun drawPathForNextChildView(
        nextChildViewDataIndex: Int,
        nextChildViewMiddleXValue: Float,
        path: Path,
        halfChildViewWidth: Float,
        childViewHeight: Int
    ) {
        if (nextChildViewDataIndex >= normalizedHourTempValues.size) {
            handleNextAfterLastChildView(nextChildViewMiddleXValue, path, childViewHeight)
        } else {
            val nextChildViewEndXValue = nextChildViewMiddleXValue + halfChildViewWidth
            path.lineTo(
                nextChildViewEndXValue,
                calculateYValue(nextChildViewDataIndex, childViewHeight)
            )
        }
    }

    private fun handleNextAfterLastChildView(
        lastXValue: Float,
        path: Path,
        childViewHeight: Int
    ) {
        path.lineTo(lastXValue, calculateYValue(normalizedHourTempValues.size - 1, childViewHeight))
    }

    private fun calculateYValue(dataIndex: Int, childViewHeight: Int): Float {
        val graphHeight = childViewHeight - (CHILD_HEADER_OR_FOOTER_HEIGHT_IN_DP * 2).dpToPx
        return ((1 - normalizedHourTempValues[dataIndex]) * graphHeight + GRAPH_START_HEIGHT_DELTA.dpToPx).toFloat()
    }

    private fun normalizeHourTempValues(hours: List<Double>): List<Double> {
        val minDayStock = hours.minOf { it }
        val maxDayStock = hours.maxOf { it }

        if (minDayStock >= maxDayStock) {
            return hours.map { 0.5 }
        }

        val range = maxDayStock - minDayStock
        return hours.map {
            val relativeValue = it - minDayStock
            return@map (relativeValue / range)
        }
    }
}

val Int.dpToPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density)