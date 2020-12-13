package com.example.camera

import android.graphics.Bitmap
import androidx.annotation.experimental.UseExperimental
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.PredefinedCategory

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
            // Live detection and tracking
            val options = ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                .enableClassification()  // Optional
                .build()
            val objectDetector = ObjectDetection.getClient(options)

            val image = InputImage.fromBitmap(
                Bitmap.createBitmap(IntArray(100 * 100), 100, 100,
                    Bitmap.Config.ARGB_8888), 0)

            objectDetector.process(image)
                .addOnSuccessListener { results ->
                    // Task completed successfully
                    // ...
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }

            val results = listOf<DetectedObject>()
            // [START read_results_default]
            for (detectedObject in results) {
                val boundingBox = detectedObject.boundingBox
                val trackingId = detectedObject.trackingId
                for (label in detectedObject.labels) {
                    val text = label.text
                    if (PredefinedCategory.FOOD == text) {
                        // ...
                    }
                    val index = label.index
                    if (PredefinedCategory.FOOD_INDEX == index) {
                        // ...
                    }
                    val confidence = label.confidence
                }
            }


            /*

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
                }*/



        }
    }

}