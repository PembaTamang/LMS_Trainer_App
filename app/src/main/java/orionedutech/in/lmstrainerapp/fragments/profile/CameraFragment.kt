package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.R.attr.orientation
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.*
import android.view.View.*
import androidx.camera.core.*
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import kotlinx.android.synthetic.main.fragment_camera.view.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.CaptureInterface
import orionedutech.`in`.lmstrainerapp.interfaces.MoveNavBar

/**
 * A simple [Fragment] subclass.
 */
class CameraFragment : Fragment(), Executor, CaptureInterface.capture {
    private var lensFacing = CameraX.LensFacing.FRONT
    lateinit var texture: TextureView
    val aspectRatio = AspectRatio.RATIO_4_3
    lateinit var previewConfig: PreviewConfig
    lateinit var preview: Preview
    lateinit var imageCapture: ImageCapture
    var flashMode: FlashMode = FlashMode.OFF
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        texture = view.texture
        texture.post {
            startCamera()
        }
        // Every time the provided texture view changes, recompute layout
        texture.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }


        return view
    }
    fun toggleflash(){
        flashMode = if (flashMode == FlashMode.OFF) FlashMode.ON else FlashMode.OFF
        startCamera()

    }
    fun togglelens(){
        lensFacing =
            if (lensFacing == CameraX.LensFacing.FRONT) CameraX.LensFacing.BACK else CameraX.LensFacing.FRONT
        startCamera()
    }
    private fun startCamera() {
        CameraX.unbindAll()
        previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetAspectRatio(aspectRatio)
            setTargetRotation(activity!!.windowManager.defaultDisplay.rotation)
            setTargetRotation(texture.display.rotation)
        }.build()

        preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            texture.surfaceTexture = it.surfaceTexture
            updateTransform()
        }
        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setLensFacing(lensFacing)
                    .setFlashMode(flashMode)
                setTargetAspectRatio(aspectRatio)
                setTargetRotation(texture.display.rotation)
                setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
            }.build()

        // Build the image capture use case and attach button click listener
        imageCapture = ImageCapture(imageCaptureConfig)
       /* view!!.capture.setOnClickListener {

        }*/
        CaptureInterface.theRealInstance.setListener(this)
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }
    fun takePic(){
        val path = context!!.filesDir.path + "/profile_image.jpg"
        val file = File(path)
        file.parentFile!!.mkdirs()
        file.createNewFile()
        imageCapture.takePicture(file, Executors.newSingleThreadExecutor(),object : ImageCapture.OnImageSavedListener {

            override fun onImageSaved(file: File) {
                val msg = "Photo capture successfully: ${file.absolutePath}"

                var bitmap = BitmapFactory.decodeFile(file.absolutePath)
                var ei: ExifInterface? = null
                try {
                    ei = ExifInterface(file.absolutePath)
                        val eiAttributeInt: Int = ei.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )
                        bitmap = when (eiAttributeInt) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                            ExifInterface.ORIENTATION_NORMAL -> bitmap
                            else -> bitmap
                        }
                    
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                var os: OutputStream? = null
                try {
                    os = BufferedOutputStream(FileOutputStream(file))
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(
                imageCaptureError: ImageCapture.ImageCaptureError,
                message: String,
                cause: Throwable?
            ) {
            }


        })

    }
    override fun execute(p0: Runnable) {
        p0.run()
    }

    override fun onDetach() {
        super.onDetach()
    MoveNavBar.theRealInstance.refresh()

    }
    override fun onDestroy() {
        super.onDestroy()
        CameraX.unbindAll()
    }

    private fun updateTransform() {
        val matrix = Matrix()
        val centerX = texture.width / 2f
        val centerY = texture.height / 2f

        val rotationDegrees = when (texture.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        texture.setTransform(matrix)
    }

    override fun capturepic() {
    takePic()}

    override fun toggle1ens() {
   togglelens() }

    override fun flash1() {
    toggleflash()}
}
