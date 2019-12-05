/*
package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.*
import android.view.View.*
import android.widget.ImageView
import androidx.camera.core.*
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_camera.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.activities.MainActivity
import orionedutech.`in`.lmstrainerapp.getOrientation
import orionedutech.`in`.lmstrainerapp.interfaces.*
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import java.io.*
import java.util.*
import java.util.concurrent.Executors

*/
/**
 * A simple [Fragment] subclass.
 *//*

class CameraFragment : Fragment(), CaptureInterface.capture,UploadImage {
    override fun upload() {

    }

    private var lensFacing = LensFacing.FRONT
    lateinit var texture: TextureView
    val aspectRatio = AspectRatio.RATIO_16_9
    lateinit var previewConfig: PreviewConfig
    lateinit var preview: Preview
    lateinit var imageCapture: ImageCapture
    var flashMode: FlashMode = FlashMode.AUTO.
    lateinit var previewImage : ImageView
    lateinit var  okbtn : ImageView
    lateinit var cancelbtn : ImageView
    lateinit var img: File
    lateinit var img1: File
    var busy = false
    lateinit var animation : LottieAnimationView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        animation = view.lottie
        texture = view.texture
        texture.post {
            startCamera()
        }
        // Every time the provided texture view changes, recompute layout
        texture.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
        view.close.setOnClickListener {
            MoveNavBar.theRealInstance.refresh()
            activity!!.supportFragmentManager.popBackStack()
        }
        view.help.setOnClickListener {
            val barpreference: SharedPreferences = activity!!.getSharedPreferences("bar", Context.MODE_PRIVATE)
            MaterialAlertDialogBuilder(context)
                .setTitle("Alert")
                .setMessage("-You can switch to the normal camera if the in-app cam is not working. \n \n-You can also long press on the camera icon in the previous screen to choose between the two camera modes.")
                .setPositiveButton("Switch to phone cam") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    MoveNavBar.theRealInstance.refresh()
                    barpreference.edit().putBoolean("move", false).apply()
                    activity!!.supportFragmentManager.popBackStack()
                    mToast.showToast(context,"Phone cam selected")


                }.setNegativeButton("cancel") { dialogInterface, i ->
                    dialogInterface.dismiss()

                }.create().show()
        }


        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(object  : OnKeyListener{
            override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if( keyCode == KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_UP) {
                    MoveNavBar.theRealInstance.refresh()
                    activity!!.supportFragmentManager.popBackStack()

                    return true
                }
                return false
            }

        })

        return view
    }

    fun toggleflash() {
        flashMode = if (flashMode == FlashMode.OFF) FlashMode.ON else FlashMode.OFF
        startCamera()

    }

    fun togglelens() {
        lensFacing =
            if (lensFacing == CameraX.LensFacing.FRONT) CameraX.LensFacing.BACK else CameraX.LensFacing.FRONT
        startCamera()
    }

    private fun startCamera() {
        CameraX.unbindAll()
         val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenSize = Size(displayMetrics.widthPixels,displayMetrics.heightPixels)
        mLog.i(TAG,"width ${displayMetrics.widthPixels} height ${displayMetrics.heightPixels} dpi ${displayMetrics.densityDpi}" +
                "density ${displayMetrics.density} dpi ${displayMetrics.densityDpi}"
        )
        val screenAspectRatio = Rational(displayMetrics.widthPixels, displayMetrics.heightPixels)
        previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetResolution(screenSize)
          // setTargetAspectRatio(aspectRatio)
            setTargetRotation(activity!!.windowManager.defaultDisplay.rotation)
            setTargetRotation(texture.display.rotation)
        }.build()

        preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            val parent = texture.parent as ViewGroup
            parent.removeView(texture)
            texture.surfaceTexture = it.surfaceTexture
            parent.addView(texture, 0)
            updateTransform()
        }
        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setLensFacing(lensFacing)
                setFlashMode(flashMode)
                setTargetAspectRatio(aspectRatio)
                setTargetRotation(texture.display.rotation)
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        imageCapture = ImageCapture(imageCaptureConfig)
        */
/* view!!.capture.setOnClickListener {

         }*//*

        CaptureInterface.theRealInstance.setListener(this)
        CameraX.bindToLifecycle(this, preview, imageCapture)


    }

    fun takePic() {
        animation.visibility = VISIBLE
        animation.playAnimation()
        busy = true
        val path = context!!.filesDir.path + "/profile.jpg"
        img = File(path)
        img.parentFile!!.mkdirs()
        img.createNewFile()
        imageCapture.takePicture(
            img,
            Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedListener {

                override fun onImageSaved(file: File) {

                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    var ei: ExifInterface? = null
                    try {
                        ei = ExifInterface(file.absolutePath)
                        val orientation = ei.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )
                        rotateBitmap(bitmap, orientation)!!

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    //  bitmap =  rotateBitmap(context!!, getFileUri(context!!,img),bitmap)

                    */
/*  var os: OutputStream? = null
                      try {
                          os = BufferedOutputStream(FileOutputStream(file))
                         roatatedbitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, os)
                          os.close()
                      } catch (e: Exception) {
                          e.printStackTrace()
                      }*//*


                    val path1 = context!!.filesDir.path + "/profile1.jpg"
                    img1 = File(path1)
                    if (!img1.exists()) {
                        try {
                            val mkdirs =
                                File(Objects.requireNonNull<String>(img1.parent)).mkdirs()
                            val a = img1.createNewFile()
                            mLog.i(TAG, "file created: $a directory created: $mkdirs")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }

                    //send to crop activity
                    val sourceuri = Uri.fromFile(img)
                    val destinationuri = Uri.fromFile(img1)
                    mLog.i(TAG, "s $sourceuri ")
                    mLog.i(TAG, "d $destinationuri")


                    Handler(Looper.getMainLooper()).run {
                        activity!!.runOnUiThread {


                             previewImage = view!!.preview
                             okbtn = view!!.okImage
                             cancelbtn = view!!.cancelImage
                            previewImage.visibility = VISIBLE
                            okbtn.visibility = VISIBLE
                            cancelbtn.visibility = VISIBLE
                            texture.visibility = INVISIBLE
                            Glide.with(context!!).load(img)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(previewImage)

                            okbtn.setOnClickListener {
                                UCrop.of(sourceuri, destinationuri)
                                    .withAspectRatio(4f, 4f)
                                    .start(context!!, this@CameraFragment, UCrop.REQUEST_CROP)
                            }
                            cancelbtn.setOnClickListener {
                                previewImage.visibility = INVISIBLE
                                okbtn.visibility = INVISIBLE
                                cancelbtn.visibility = INVISIBLE
                                texture.visibility = VISIBLE
                                busy = false
                            }
                            animation.cancelAnimation()
                            animation.visibility = GONE
                        }
                        */
/* activity!!.runOnUiThread {


                         val preview =
                             LayoutInflater.from(context).inflate(R.layout.imagepreview, null, false)
                         val builder = MaterialAlertDialogBuilder(context, R.style.AlertDialog)
                             .setView(preview)
                         val dialogue = builder.create()

                         val image = preview.image
                         Glide.with(context!!).load(img).into(image)
                         val okbtn = preview.okImage
                         okbtn.setOnClickListener {
                             dialogue.dismiss()
                             UCrop.of(sourceuri, destinationuri)
                                 .withAspectRatio(16f, 9f)
                                 .start(context!!, this@CameraFragment, UCrop.REQUEST_CROP)

                         }
                         val cancelbtn = preview.cancelImage
                         cancelbtn.setOnClickListener {
                             dialogue.dismiss()
                         }


                         dialogue.show()
                     }*//*

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

    private fun rotateBitmap(context: Context, photoUri: Uri, bitmap: Bitmap): Bitmap {
        val orientation = getOrientation(context, photoUri)
        if (orientation <= 0) {
            return bitmap
        }
        val matrix = Matrix()
        matrix.postRotate(orientation.toFloat())
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight(),
            matrix,
            false
        );
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> {
                Log.i(TAG, "normal")
                return bitmap
            }
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                Log.i(TAG, "ORIENTATION_FLIP_HORIZONTAL")
                matrix.setScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                Log.i(TAG, "ORIENTATION_ROTATE_180")
                matrix.setRotate(180f)
            }
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                Log.i(TAG, "ORIENTATION_FLIP_VERTICAL")
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
                Log.i(TAG, "ORIENTATION_TRANSPOSE")
            }
            ExifInterface.ORIENTATION_ROTATE_90 ->

                Log.i(TAG, "ORIENTATION_ROTATE_90")
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                Log.i(TAG, "ORIENTATION_TRANSVERSE")
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.setRotate(-90f)
                Log.i(TAG, "ORIENTATION_ROTATE_270")
            }

            else -> {
                Log.i(TAG, "UNKNOWN")
                return bitmap
            }
        }
        try {
            val bmRotated =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            return bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }

    }


    override fun onDetach() {
        super.onDetach()

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
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        matrix.setScale(1f,1.050f)

        texture.setTransform(matrix)
    }

    override fun capturepic() {
        takePic()
    }

    override fun toggle1ens() {
        togglelens()
    }

    override fun flash1() {
        toggleflash()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            mLog.i(TAG, "crop success")
            previewImage.visibility = INVISIBLE
            okbtn.visibility = INVISIBLE
            cancelbtn.visibility = INVISIBLE
            texture.visibility = VISIBLE
            busy = false
            flashtoggle.theRealInstance.flashtoggle(img1.absolutePath)
            MoveNavBar.theRealInstance.refresh()
            activity!!.supportFragmentManager.popBackStack()

        }
    }

}
*/
