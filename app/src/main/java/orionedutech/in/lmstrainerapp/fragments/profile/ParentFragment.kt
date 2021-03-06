package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragmentCallback
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_parent.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment
import orionedutech.`in`.lmstrainerapp.interfaces.SetProfileImage
import orionedutech.`in`.lmstrainerapp.interfaces.flashtoggle
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */


class ParentFragment : BaseFragment(), UCropFragmentCallback, flashtoggle.capture {


    override fun flash(path : String) {
        mLog.i(TAG,"path $path")
        profilePref.edit().putString("image",path).apply()
        mToast.showToast(context, "photo saved")
        checkProfileImage()
    }

    override fun onCropFinish(result: UCropFragment.UCropResult?) {
        when (result!!.mResultCode) {
            RESULT_OK -> {
                mLog.i(TAG, "result ok")
            }
            UCrop.RESULT_ERROR -> {
                mLog.i(TAG, "result ok")
            }
            else -> {
                mLog.i(TAG, " u error")
            }
        }
    }

    override fun loadingProgress(showLoader: Boolean) {
    }

    private var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    lateinit var img: File
    lateinit var img1: File
    lateinit var name: TextView
    lateinit var profilePref: SharedPreferences
    lateinit var barPreferences: SharedPreferences
    private val REQUEST_CAPTURE_IMAGE = 100
    lateinit var profileImage : CircleImageView
    lateinit var animation : LottieAnimationView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_parent, container, false)
        tabLayout = view.tabLayout
        viewPager = view.viewPager
        barPreferences = activity!!.getSharedPreferences("bar", Context.MODE_PRIVATE)
        animation = view.anim
        profileImage = view.circularImageView
        view.camera.setOnClickListener {
            takePhoto()
           /* //start camera intent
            if (barPreferences.getBoolean("move", true)) {
              //  moveToFragment(())
                MoveNavBar.theRealInstance.refresh()
                profilebooleantoggle.theRealInstance.toggle(false)
            } else {
                takePhoto()
            }*/
        }

        view.camera.setOnLongClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Alert")
                .setMessage("Choose the default camera mode.")
                .setPositiveButton("In-App Cam") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    barPreferences.edit().putBoolean("move", true).apply()
                    mToast.showToast(context, "In-App Cam selected")
                }
                .setNegativeButton("Phone Cam") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    barPreferences.edit().putBoolean("move", false).apply()
                    mToast.showToast(context, "Phone Cam selected")
                }.create().show()
            return@setOnLongClickListener true
        }
        name = view.name

        flashtoggle.theRealInstance.setListener(this)
        launch {
            context?.let {
                val dao = MDatabase(it).getUserDao()
                val naam = dao.getadminName()
                withContext(Main) {
                    name.text = naam
                }
            }
        }

        profilePref = activity!!.getSharedPreferences("profile", Context.MODE_PRIVATE)
        checkProfileImage()
        animation.playAnimation()
        Handler().postDelayed({
            CoroutineScope(IO).launch{
                val adapter = ViewPagerAdapter(childFragmentManager)
                withContext(Main){
                    viewPager!!.adapter = adapter
                    tabLayout!!.setupWithViewPager(viewPager)
                    animation.cancelAnimation()
                    animation.visibility = GONE
                }

            }
        },1000)


        return view
    }

    private fun checkProfileImage() {
        val imageURL = profilePref.getString("image", "")
        mLog.i(TAG,"imageurl $imageURL")
        if (imageURL != "") {
            Glide.with(context!!).load(imageURL).skipMemoryCache(true).diskCacheStrategy(
                DiskCacheStrategy.NONE).into(profileImage)
        }
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageuri = getOutputMediaFileUri(context!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri)
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            val path = context!!.filesDir.path + "/profile.jpg"
            img = File(path)
            if (!img.exists()) {
                try {
                    val mkdirs = File(Objects.requireNonNull<String>(img.parent)).mkdirs()
                    val a = img.createNewFile()
                    mLog.i(TAG, "file created: $a directory created: $mkdirs")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            val imageUri =
                FileProvider.getUriForFile(
                    context!!,
                    "orionedutech.in.lmstrainerapp.fileprovider",
                    img
                )
            mLog.i(TAG, "imagepath " + img.absolutePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_CAPTURE_IMAGE)
        }
    }

    override fun onResume() {
        mLog.i(TAG,"on resume called")
        super.onResume()
    }
    private fun getOutputMediaFileUri(context: Context): Uri {
        val mediaStorageDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Camera"
        )
        //If File is not present create directory
        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdir())
                Log.e("Create Directory", "Main Directory Created : $mediaStorageDir")
        }
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())//Get Current timestamp
        val mediaFile = File(
            mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".jpg"
        )//create image path with system mill and image format
        return Uri.fromFile(mediaFile)

    }



    private fun moveToFragment(fragment: Fragment) {
        val ft = activity?.supportFragmentManager!!.beginTransaction()
        ft.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        ft.add(R.id.mainContainer, fragment)
        ft.addToBackStack(null)
        ft.commit()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            val path = img.absolutePath
            val bitmap = BitmapFactory.decodeFile(path)
            val exif: ExifInterface
            try {
                exif = ExifInterface(img.absolutePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                val bmRotated = rotateBitmap(bitmap, orientation)!!
                val file = File(path)

                bmRotated.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(file))
                //send file to crop activity
                val path1 = context!!.filesDir.path + "/profile1.jpg"
                img1 = File(path1)
                if (!img1.exists()) {
                    try {
                        val mkdirs = File(Objects.requireNonNull<String>(img1.parent)).mkdirs()
                        val a = img1.createNewFile()
                        mLog.i(TAG, "file created: $a directory created: $mkdirs")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                val sourceuri = Uri.fromFile(img)
                val destinationuri = Uri.fromFile(img1)
                mLog.i(TAG, "s $sourceuri ")
                mLog.i(TAG, "d $destinationuri")
                UCrop.of(sourceuri, destinationuri)
                    .withAspectRatio(4f, 4f)
                    .start(context!!, this@ParentFragment, UCrop.REQUEST_CROP)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            mLog.i(TAG, "crop success")
            Glide.with(context!!).load(img1).skipMemoryCache(true).diskCacheStrategy(
                DiskCacheStrategy.NONE).into(profileImage)
            //send img one to server
            profilePref.edit().putString("image",img1.absolutePath).apply()
            SetProfileImage.classInstance!!.set()
            mToast.showToast(context,"photo saved")

        } else {
            mLog.i(TAG, "crop error")
        }
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
                Log.i(TAG, "UNKNOWN orientation")
                return bitmap
            }
        }
        try {
            val bmRotated =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            return bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }

    }

}
