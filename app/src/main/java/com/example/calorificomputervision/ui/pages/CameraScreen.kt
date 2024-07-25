package com.example.calorificomputervision.ui.pages

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.calorificomputervision.model.DetectedObject
import com.example.calorificomputervision.viewmodel.CameraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

sealed class Result {
    data class Success(val bitmap: Bitmap, val objects: List<DetectedObject>) : Result()
    data class Error(val exception: Exception) : Result()
}
@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onError: (Exception) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var processedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var detectedObjects by remember { mutableStateOf<List<DetectedObject>?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (exc: Exception) {
                onError(exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { PreviewView(it) },
            modifier = Modifier.fillMaxSize()
        ) { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    onError(exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }

        when {
            isProcessing -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            processedImageBitmap != null -> {
                Image(
                    bitmap = processedImageBitmap!!.asImageBitmap(),
                    contentDescription = "Processed image",
                    modifier = Modifier.fillMaxSize()
                )
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Confirm")
                }
            }
            capturedImageUri != null -> {
                Image(
                    bitmap = remember(capturedImageUri) {
                        BitmapFactory.decodeFile(capturedImageUri?.path)
                    }.asImageBitmap(),
                    contentDescription = "Captured image",
                    modifier = Modifier.fillMaxSize()
                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isProcessing = true
                            try {
                                val result = processImageWithYOLOv8(context, capturedImageUri!!)
                                processedImageBitmap = result.first
                                detectedObjects = result.second
                            } catch (e: Exception) {
                                onError(e)
                            } finally {
                                isProcessing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Process Image")
                }
            }
            else -> {
                Button(
                    onClick = {
                        takePhoto(
                            imageCapture,
                            context.getOutputDirectory(),
                            ContextCompat.getMainExecutor(context),
                            { uri -> capturedImageUri = uri },
                            { exc -> onError(exc) }
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Take Photo")
                }
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Detected Objects") },
                text = {
                    Column {
                        detectedObjects?.forEach { obj ->
                            Text("${obj.name}: ${obj.calories.toInt()} calories")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                detectedObjects?.let { viewModel.saveObjects(it) }
                                showConfirmDialog = false
                                // Reset the screen state
                                processedImageBitmap = null
                                capturedImageUri = null
                                detectedObjects = null
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { showConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onImageCaptured(savedUri)
            }

            override fun onError(exc: ImageCaptureException) {
                onError(exc)
            }
        }
    )
}

private fun getCorrectedBitmap(context: Context, uri: Uri): Bitmap {
    val bitmap = BitmapFactory.decodeFile(uri.path)
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            val ei = try {
                ExifInterface(context.contentResolver.openInputStream(uri)!!)
            } catch (e: IOException) {
                e.printStackTrace()
                return bitmap
            }
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }
        }
        else -> bitmap
    }
}

private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height,
        matrix, true
    )
}

private fun Context.getOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, "CameraX-Images").apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
}

private suspend fun processImageWithYOLOv8(context: Context, uri: Uri): Pair<Bitmap, List<DetectedObject>> {
    val file = File(uri.path!!)
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
        .build()

    val request = Request.Builder()
        .url("https://e358-182-3-45-54.ngrok-free.app/process_image")
        .post(requestBody)
        .build()

    val client = OkHttpClient()

    return withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val jsonResponse = JSONObject(response.body?.string() ?: "")
            val imageUrl = jsonResponse.getString("image_url")
            val objectsArray = jsonResponse.getJSONArray("objects")
            val detectedObjects = mutableListOf<DetectedObject>()

            for (i in 0 until objectsArray.length()) {
                val objectJson = objectsArray.getJSONObject(i)
                detectedObjects.add(
                    DetectedObject(
                        name = objectJson.getString("name"),
                        volumeCm3 = objectJson.getDouble("volume_cm3"),
                        massGrams = objectJson.getDouble("mass_grams"),
                        calories = objectJson.getDouble("calories")
                    )
                )
            }

            // Download the processed image
            val processedImageRequest = Request.Builder().url(imageUrl).build()
            val processedImageResponse = client.newCall(processedImageRequest).execute()
            if (processedImageResponse.isSuccessful) {
                val processedBitmap = BitmapFactory.decodeStream(processedImageResponse.body?.byteStream())
                Pair(processedBitmap, detectedObjects)
            } else {
                throw IOException("Failed to download processed image")
            }
        } else {
            throw IOException("Error processing image: ${response.code}")
        }
    }
}