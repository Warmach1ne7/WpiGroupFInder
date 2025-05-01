package com.example.wpigroupfinder.screens.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.FaceDetector
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.wpigroupfinder.screens.facedetector.FaceDetectorProcessor
import com.example.wpigroupfinder.screens.facedetector.FaceGraphic
import com.example.wpigroupfinder.screens.facedetector.FaceMeshDetectorProcessor
import com.example.wpigroupfinder.screens.facedetector.FaceMeshGraphic
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.facemesh.FaceMesh


@Composable
fun FaceRecogScreenDesign(navController: NavController) {
    var (selected, setSelected) = remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }
    // This controls the lens (front vs back)
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }

    // Watch lensFacing and rebind camera
    LaunchedEffect(lensFacing) {
        controller.cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        controller.bindToLifecycle(lifecycleOwner)

    }

    val capturedImage = remember { mutableStateOf<Bitmap?>(null) }
    var rememberBool by remember { mutableStateOf(false) }

    var detectedFaces by remember { mutableStateOf(emptyList<Face>()) }
    var detectedFacesMesh by remember { mutableStateOf(emptyList<FaceMesh>()) }

    val faceDetectorProcessor = remember { FaceDetectorProcessor() }
    val faceMeshProcessor = remember { FaceMeshDetectorProcessor() }

    var loading by remember { mutableStateOf(false) }

    val CAMERA_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA
    )
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    //when an image is captured, launch detection code
    LaunchedEffect(capturedImage.value) {

        capturedImage.value?.let { bitmap ->
            detectedFaces = faceDetectorProcessor.detectInImage(bitmap)
        }

        capturedImage.value?.let { bitmap ->
            detectedFacesMesh = faceMeshProcessor.detectInImageMesh(bitmap)
        }
        loading = false

    }

    Column(
        modifier = Modifier
            .padding(PaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            contentAlignment = Alignment.Center
        ){

            if(capturedImage.value != null){
                Log.d("Test", "Hello!!!!")
                Image(
                    bitmap = capturedImage.value!!.asImageBitmap(),
                    contentDescription = "Captured Photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(2.dp)),
                    contentScale = ContentScale.Crop
                )

                //if a picture has been taken
                if(detectedFaces.isNotEmpty() && detectedFacesMesh.isNotEmpty()){
                    Log.d("Test", "Attempted to Draw Graphic")

                    //draws specific face graphic on canvas
                    //TODO: fix incorrect scaling/misplacement
                    Canvas(modifier = Modifier.fillMaxSize()) {

                        if(selected != "none" || selected != "") {
                            if (selected == "cd") {

                            } else {

                            }
                            val f = FaceGraphic(
                                detectedFaces[0],
                                capturedImage.value!!.width,
                                size.width
                            )
                            f.setDrawBool(rememberBool)
                            f.draw(drawContext.canvas.nativeCanvas)

                            if(selected == "md") {
                                val g = FaceMeshGraphic(detectedFacesMesh[0], capturedImage.value!!.width,size.width)
                                g.draw(drawContext.canvas.nativeCanvas)
                            }
                        }
                    }
                }

            }else{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                ) {
                    CameraPreview(
                        controller = controller,
                        modifier = Modifier
                            .fillMaxSize()
                    )

                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Flip Camera"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier
            .height(50.dp)
        )

        if(loading) {
            Text("Loading...", fontSize = 20.sp)
        }
        if(capturedImage.value == null) {
            Button(
                modifier = Modifier
                    .width(400.dp)
                    .padding(vertical = 8.dp),
                onClick = {
                    loading = true

                    setSelected("fd")
                    takePhoto(context = context, controller = controller, onPhotoTaken = { bitmap ->
                        capturedImage.value = bitmap
                    })

                },
                shape = RoundedCornerShape(1.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff9910e3),
                    contentColor = Color.White
                )
            ) {
                Text("Verify Humanity", fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier
            .height(10.dp)
        )


        if(detectedFaces.isNotEmpty()){
            Button(
                modifier = Modifier
                    .width(400.dp)
                    .padding(vertical = 8.dp),
                onClick = {
                    navController.navigate("signup")
                },
                shape = RoundedCornerShape(1.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff9910e3),
                    contentColor = Color.White
                )
            ){
                Text("Continue", fontSize = 20.sp)
            }
        } else if (capturedImage.value != null && detectedFaces.isEmpty() && !loading){
            Button(
                modifier = Modifier
                    .width(400.dp)
                    .padding(vertical = 8.dp),
                onClick = {
                    capturedImage.value = null
                },
                shape = RoundedCornerShape(1.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff9910e3),
                    contentColor = Color.White
                )
            ){
                Text("Try Again", fontSize = 20.sp)
            }
        }
//        Row(
//            modifier = Modifier
//                .height(50.dp)
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            RadioButton(
//                selected = ("none" == selected),
//                onClick = { detectedFaces = emptyList(); setSelected("none");},
//                modifier = Modifier.scale(1.5f)
//            )
//            Text(
//                modifier = Modifier.padding(start = 16.dp),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                text = "None")
//        }
//
//        Spacer(modifier = Modifier
//            .height(5.dp)
//        )
//        Row(
//            modifier = Modifier
//                .height(50.dp)
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            RadioButton(
//                selected = ("fd" == selected),
//                onClick = {setSelected("fd"); },
//                modifier = Modifier.scale(1.5f)
//            )
//            Text(
//                modifier = Modifier.padding(start = 16.dp),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                text = "Face detection")
//            Text(
//                modifier = Modifier.padding(start = 16.dp),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                text = "${detectedFaces.size} face detected")
//        }
//        Spacer(modifier = Modifier
//            .height(5.dp)
//        )
//        Row(
//            modifier = Modifier
//                .height(50.dp)
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            RadioButton(
//                selected = ("cd" == selected),
//                onClick = {setSelected("cd")},
//                modifier = Modifier.scale(1.5f)
//            )
//            Text(
//                modifier = Modifier.padding(start = 16.dp),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                text = "Contour detection")
//        }
//
//        Spacer(modifier = Modifier
//            .height(5.dp)
//        )
//        Row(
//            modifier = Modifier
//                .height(50.dp)
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            RadioButton(
//                selected = ("md" == selected),
//                onClick = {setSelected("md")},
//                modifier = Modifier.scale(1.5f)
//            )
//            Text(
//                modifier = Modifier.padding(start = 16.dp),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                text = "Mesh detection")
//        }
//
//        Spacer(modifier = Modifier
//            .height(5.dp)
//        )
//        Row(
//            modifier = Modifier
//                .height(50.dp)
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            RadioButton(
//                selected = ("ss" == selected),
//                onClick = {setSelected("ss")},
//                modifier = Modifier.scale(1.5f)
//            )
//            Text(
//                modifier = Modifier.padding(start = 16.dp),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                text = "Selfie segmentation")
//        }
    }


}





private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit
){
    controller.takePicture(
        ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val rotationDegrees = image.imageInfo.rotationDegrees

                val originalBitmap = image.toBitmap()

                val matrix = android.graphics.Matrix().apply{
                    postRotate(rotationDegrees.toFloat())
                }

                val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)

                image.close()
                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera: ", "Couldn't take picture", exception)
            }

        }
    )
}



@Composable
fun CameraPreview(controller: LifecycleCameraController, modifier: Modifier){
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}