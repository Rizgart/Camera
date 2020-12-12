package com.example.camera

import androidx.annotation.experimental.UseExperimental
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

@UseExperimental(markerClass = ExperimentalGetImage::class)
class ObjectDetection(
    private val onObjectDetection: (ObjectDetection: List<DetectedObject>? ) -> Unit
) : ImageAnalysis.Analyzer {

    private fun rotationDegreesToFirebaseRotation(rotationDegrees: Int): Int {
        return when (rotationDegrees) {
            0 -> 0
            90 -> 1
            180 -> 2
            270 -> 3
            else -> throw IllegalArgumentException("Not supported")
        }
    }

    override fun analyze(image: ImageProxy) {
        val rotation = rotationDegreesToFirebaseRotation(image.imageInfo.rotationDegrees)
        image.image?.let {
            val imageValue = InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)
            val options = ObjectDetectorOptions.Builder().setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                .build()
            val Option = ObjectDetectorOptions.Builder().enableMultipleObjects().build()
            val scanner = ObjectDetection.getClient(options)

            scanner.process(imageValue)
                .addOnCompleteListener { detectedObjects ->
                    detectedObjects.result?.forEach { detectedObjects ->
                        val bounds = detectedObjects.boundingBox
                        val corners = detectedObjects.labels
                        val rawValue = detectedObjects.trackingId
                    }
                    onObjectDetection(detectedObjects.result)
                    image.image?.close()
                    image.close()
                }
                .addOnFailureListener { failure ->
                    failure.printStackTrace()
                    image.image?.close()
                    image.close()
                }
        }
    }

}