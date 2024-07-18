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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
    data class Success(val bitmap: Bitmap) : Result()
    data class Error(val exception: Exception) : Result()
}
@Composable
fun CameraScreen(
    onError: (Exception) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var processedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var currentRotation by remember { mutableStateOf(Surface.ROTATION_0) }

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
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.post {
                    currentRotation = view.display?.rotation ?: Surface.ROTATION_0
                    imageCapture.targetRotation = currentRotation
                }
            }
        )

        when {
            isProcessing -> {
                Log.d(TAG, "Showing processing indicator")
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            processedImageBitmap != null -> {
                Log.d(TAG, "Displaying processed image")
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            bitmap = processedImageBitmap!!.asImageBitmap(),
                            contentDescription = "Processed image",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        )
                        Button(
                            onClick = {
                                Log.d(TAG, "Take Another Photo button clicked")
                                processedImageBitmap = null
                                capturedImageUri = null
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp)
                        ) {
                            Text("Take Another Photo")
                        }
                    }
                }
            }
            capturedImageUri != null -> {
                Log.d(TAG, "Displaying captured image")
                capturedImageUri?.let { uri ->
                    val bitmap = remember(uri) {
                        getCorrectedBitmap(context, uri)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Captured image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                            )
                            Button(
                                onClick = {
                                    Log.d(TAG, "Process Image button clicked")
                                    coroutineScope.launch {
                                        try {
                                            isProcessing = true
                                            val result = withContext(Dispatchers.IO) {
                                                processImageWithYOLOv8(context, uri)
                                            }
                                            withContext(Dispatchers.Main) {
                                                isProcessing = false
                                                when (result) {
                                                    is Result.Success -> {
                                                        Log.d(TAG, "Image processed successfully")
                                                        processedImageBitmap = result.bitmap
                                                    }
                                                    is Result.Error -> {
                                                        Log.e(TAG, "Error processing image", result.exception)
                                                        onError(result.exception)
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Unexpected error during image processing", e)
                                            withContext(Dispatchers.Main) {
                                                isProcessing = false
                                                onError(e)
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 20.dp)
                            ) {
                                Text("Process Image")
                            }
                        }
                    }
                }
            }
            else -> {
                Log.d(TAG, "Showing Take Photo button")
                Button(
                    onClick = {
                        Log.d(TAG, "Take Photo button clicked")
                        takePhoto(
                            imageCapture,
                            context.getOutputDirectory(),
                            ContextCompat.getMainExecutor(context),
                            { uri ->
                                Log.d(TAG, "Photo captured: $uri")
                                capturedImageUri = uri
                            },
                            { e ->
                                Log.e(TAG, "Error taking photo", e)
                                onError(e)
                            }
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                ) {
                    Text("Take Photo")
                }
            }
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

private suspend fun processImageWithYOLOv8(context: Context, uri: Uri): Result {
    val file = File(uri.path!!)
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
        .build()

    val request = Request.Builder()
        .url("https://df9a-182-3-37-38.ngrok-free.app/process_image")
        .post(requestBody)
        .build()

    val client = OkHttpClient()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val jsonResponse = JSONObject(response.body?.string() ?: "")
            val imageUrl = jsonResponse.getString("image_url")

            // Download the processed image
            val processedImageRequest = Request.Builder().url(imageUrl).build()
            val processedImageResponse = client.newCall(processedImageRequest).execute()
            if (processedImageResponse.isSuccessful) {
                val processedBitmap = BitmapFactory.decodeStream(processedImageResponse.body?.byteStream())
                Result.Success(processedBitmap)
            } else {
                Result.Error(IOException("Failed to download processed image"))
            }
        } else {
            Result.Error(IOException("Error processing image: ${response.code}"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}