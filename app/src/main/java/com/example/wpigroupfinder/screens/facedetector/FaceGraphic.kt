
package com.example.wpigroupfinder.screens.facedetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import com.google.mlkit.vision.face.FaceLandmark.LandmarkType
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

/**
 * Graphic instance for rendering face position, contour, and landmarks within the associated
 * graphic overlay view.
 */
class FaceGraphic(private val face: Face, private val imageWidth: Int, private val canvasWidth: Float) {
  private val facePositionPaint: Paint
  private val numColors = COLORS.size
  private val idPaints = Array(numColors) { Paint() }
  private val boxPaints = Array(numColors) { Paint() }
  private val labelPaints = Array(numColors) { Paint() }
  private var drawBoolean = false

  init {
    val selectedColor = Color.WHITE
    facePositionPaint = Paint()
    facePositionPaint.color = selectedColor
    for (i in 0 until numColors) {
      idPaints[i] = Paint()
      idPaints[i].color = COLORS[i][0]
      idPaints[i].textSize = ID_TEXT_SIZE
      boxPaints[i] = Paint()
      boxPaints[i].color = COLORS[i][1]
      boxPaints[i].style = Paint.Style.STROKE
      boxPaints[i].strokeWidth = BOX_STROKE_WIDTH
      labelPaints[i] = Paint()
      labelPaints[i].color = COLORS[i][1]
      labelPaints[i].style = Paint.Style.FILL
    }
  }

  /** Draws the face annotations for position on the supplied canvas. */
  fun draw(canvas: Canvas) {
    // Draws a circle at the position of the detected face, with the face's track id below.

    // Draws a circle at the position of the detected face, with the face's track id below.
    val x = translateX(face.boundingBox.centerX().toFloat())
    val y = translateY(face.boundingBox.centerY().toFloat())
    canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)

    // Calculate positions.
    val left = x - scale(face.boundingBox.width() / 2.0f)
    val top = y - scale(face.boundingBox.height() / 2.0f)
    val right = x + scale(face.boundingBox.width() / 2.0f)
    val bottom = y + scale(face.boundingBox.height() / 2.0f)
    val lineHeight = ID_TEXT_SIZE + BOX_STROKE_WIDTH
    var yLabelOffset: Float = if (face.trackingId == null) 0f else -lineHeight

    // Decide color based on face ID
    val colorID = if (face.trackingId == null) 0 else abs(face.trackingId!! % NUM_COLORS)

    // Calculate width and height of label box
    var textWidth = idPaints[colorID].measureText("ID: " + face.trackingId)
    if (face.smilingProbability != null) {
      yLabelOffset -= lineHeight
      textWidth =
        max(
          textWidth,
          idPaints[colorID].measureText(
            String.format(Locale.US, "Happiness: %.2f", face.smilingProbability)
          )
        )
    }
    if (face.leftEyeOpenProbability != null) {
      yLabelOffset -= lineHeight
      textWidth =
        max(
          textWidth,
          idPaints[colorID].measureText(
            String.format(Locale.US, "Left eye open: %.2f", face.leftEyeOpenProbability)
          )
        )
    }
    if (face.rightEyeOpenProbability != null) {
      yLabelOffset -= lineHeight
      textWidth =
        max(
          textWidth,
          idPaints[colorID].measureText(
            String.format(Locale.US, "Right eye open: %.2f", face.rightEyeOpenProbability)
          )
        )
    }

    yLabelOffset = yLabelOffset - 3 * lineHeight
    textWidth =
      Math.max(
        textWidth,
        idPaints[colorID].measureText(
          String.format(Locale.US, "EulerX: %.2f", face.headEulerAngleX)
        )
      )
    textWidth =
      Math.max(
        textWidth,
        idPaints[colorID].measureText(
          String.format(Locale.US, "EulerY: %.2f", face.headEulerAngleY)
        )
      )
    textWidth =
      Math.max(
        textWidth,
        idPaints[colorID].measureText(
          String.format(Locale.US, "EulerZ: %.2f", face.headEulerAngleZ)
        )
      )

    // Draw labels
    canvas.drawRect(
      left - BOX_STROKE_WIDTH,
      top + yLabelOffset,
      left + textWidth + 2 * BOX_STROKE_WIDTH,
      top,
      labelPaints[colorID]
    )
    yLabelOffset += ID_TEXT_SIZE
    canvas.drawRect(left, top, right, bottom, boxPaints[colorID])
    if (face.trackingId != null) {
      canvas.drawText("ID: " + face.trackingId, left, top + yLabelOffset, idPaints[colorID])
      yLabelOffset += lineHeight
    }

    // Draws all face contours.
    if(drawBoolean == true) {
      for (contour in face.allContours) {
        for (point in contour.points) {
          canvas.drawCircle(
            translateX(point.x),
            translateY(point.y),
            FACE_POSITION_RADIUS,
            facePositionPaint
          )
        }
      }
    }

    // Draws smiling and left/right eye open probabilities.
    if (face.smilingProbability != null) {
      canvas.drawText(
        "Smiling: " + String.format(Locale.US, "%.2f", face.smilingProbability),
        left,
        top + yLabelOffset,
        idPaints[colorID]
      )
      yLabelOffset += lineHeight
    }

    val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
    if (face.leftEyeOpenProbability != null) {
      canvas.drawText(
        "Left eye open: " + String.format(Locale.US, "%.2f", face.leftEyeOpenProbability),
        left,
        top + yLabelOffset,
        idPaints[colorID]
      )
      yLabelOffset += lineHeight
    }
    if (leftEye != null) {
      val leftEyeLeft =
        translateX(leftEye.position.x) - idPaints[colorID].measureText("Left Eye") / 2.0f
      canvas.drawRect(
        leftEyeLeft - BOX_STROKE_WIDTH,
        translateY(leftEye.position.y) + ID_Y_OFFSET - ID_TEXT_SIZE,
        leftEyeLeft + idPaints[colorID].measureText("Left Eye") + BOX_STROKE_WIDTH,
        translateY(leftEye.position.y) + ID_Y_OFFSET + BOX_STROKE_WIDTH,
        labelPaints[colorID]
      )
      canvas.drawText(
        "Left Eye",
        leftEyeLeft,
        translateY(leftEye.position.y) + ID_Y_OFFSET,
        idPaints[colorID]
      )
    }

    val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
    if (face.rightEyeOpenProbability != null) {
      canvas.drawText(
        "Right eye open: " + String.format(Locale.US, "%.2f", face.rightEyeOpenProbability),
        left,
        top + yLabelOffset,
        idPaints[colorID]
      )
      yLabelOffset += lineHeight
    }
    if (rightEye != null) {
      val rightEyeLeft =
        translateX(rightEye.position.x) - idPaints[colorID].measureText("Right Eye") / 2.0f
      canvas.drawRect(
        rightEyeLeft - BOX_STROKE_WIDTH,
        translateY(rightEye.position.y) + ID_Y_OFFSET - ID_TEXT_SIZE,
        rightEyeLeft + idPaints[colorID].measureText("Right Eye") + BOX_STROKE_WIDTH,
        translateY(rightEye.position.y) + ID_Y_OFFSET + BOX_STROKE_WIDTH,
        labelPaints[colorID]
      )
      canvas.drawText(
        "Right Eye",
        rightEyeLeft,
        translateY(rightEye.position.y) + ID_Y_OFFSET,
        idPaints[colorID]
      )
    }

    canvas.drawText("EulerX: " + face.headEulerAngleX, left, top + yLabelOffset, idPaints[colorID])
    yLabelOffset += lineHeight
    canvas.drawText("EulerY: " + face.headEulerAngleY, left, top + yLabelOffset, idPaints[colorID])
    yLabelOffset += lineHeight
    canvas.drawText("EulerZ: " + face.headEulerAngleZ, left, top + yLabelOffset, idPaints[colorID])

    if(drawBoolean == true){
      // Draw facial landmarks
      drawFaceLandmark(canvas, FaceLandmark.LEFT_EYE)
      drawFaceLandmark(canvas, FaceLandmark.RIGHT_EYE)
      drawFaceLandmark(canvas, FaceLandmark.LEFT_CHEEK)
      drawFaceLandmark(canvas, FaceLandmark.RIGHT_CHEEK)
    }
  }

  public fun setDrawBool(boolean: Boolean): Unit{
    drawBoolean = boolean
  }

  private fun drawFaceLandmark(canvas: Canvas, @LandmarkType landmarkType: Int) {
    val faceLandmark = face.getLandmark(landmarkType)
    if (faceLandmark != null) {
      canvas.drawCircle(
        translateX(faceLandmark.position.x),
        translateY(faceLandmark.position.y),
        FACE_POSITION_RADIUS,
        facePositionPaint
      )
    }
  }

  // 1. Scale function - Adjusts the size from image to view coordinates.
  private fun scale(value: Float): Float {
    // Scale value relative to the width of the image and the width of the canvas
    return value * (canvasWidth / imageWidth)
  }

  // 2. Translate X - Converts the X coordinate from image to canvas.
  private fun translateX(x: Float): Float {
    // Scale the X value based on the canvas size
    return scale(x)
  }

  // 3. Translate Y - Converts the Y coordinate from image to canvas.
  private fun translateY(y: Float): Float {
    // Scale the Y value based on the canvas size
    return scale(y) - 100
  }


  companion object {
    private const val FACE_POSITION_RADIUS = 8.0f
    private const val ID_TEXT_SIZE = 30.0f
    private const val ID_Y_OFFSET = 40.0f
    private const val BOX_STROKE_WIDTH = 5.0f
    private const val NUM_COLORS = 10
    private val COLORS =
      arrayOf(
        intArrayOf(Color.BLACK, Color.WHITE),
        intArrayOf(Color.WHITE, Color.MAGENTA),
        intArrayOf(Color.BLACK, Color.LTGRAY),
        intArrayOf(Color.WHITE, Color.RED),
        intArrayOf(Color.WHITE, Color.BLUE),
        intArrayOf(Color.WHITE, Color.DKGRAY),
        intArrayOf(Color.BLACK, Color.CYAN),
        intArrayOf(Color.BLACK, Color.YELLOW),
        intArrayOf(Color.WHITE, Color.BLACK),
        intArrayOf(Color.BLACK, Color.GREEN)
      )
  }
}
