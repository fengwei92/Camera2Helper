package com.stanic.camerahelper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import com.android.volley.VolleyError
import com.stanic.sjty_pda.util.VolleyUtil
import java.lang.Exception
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    var screenInfo = "屏幕分辨率："
    var cameraInfo = ""
    var picInfo = "图片分辨率："
    var chooseBestInfo = "预览最佳分辨率："
    var picBestInfo = "照片最佳分辨率"
    var errorInfo = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val b = checkPremission()
        if (b) {
            checkSupportHighResolvingPower()
            getScreenInfo()
            isSupportCamera2()
            setUpCameraOutputs()
            setMessage()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listOf<String>(Manifest.permission.CAMERA).toTypedArray(), 100)
            }
        }


    }

    private fun checkPremission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun checkSupportHighResolvingPower() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val characteristics = manager.getCameraCharacteristics("0")
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val picSize = map?.getOutputSizes(ImageFormat.JPEG)
            for (i in 0 until picSize?.size!!) {
                val size = "${picSize[i].width} x ${picSize[i].height} \r\n"
                picInfo += size
            }

        } catch (e: Exception) {
            errorInfo += e.toString()
            e.printStackTrace()
        }


    }


    private fun isSupportCamera2() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val characteristics = manager.getCameraCharacteristics("0")
            val deviceLevel =
                characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
            if (deviceLevel == null) {
                cameraInfo += "can not get INFO_SUPPORTED_HARDWARE_LEVEL"

            }
            when (deviceLevel) {
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> cameraInfo += "LEVEL FULL"
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> cameraInfo += "LEVEL LEGACY"
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> cameraInfo += "LEVEL 3"
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> cameraInfo += "LEVEL LIMITED"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun getScreenInfo() {
        val dm = resources.displayMetrics
        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels
        screenInfo += "$screenWidth x $screenHeight"
    }

    private fun setUpCameraOutputs() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (i in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(i)
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val picSize = map?.getOutputSizes(ImageFormat.JPEG)
                val previewSize =
                    chooseOptimalSize(map?.getOutputSizes(SurfaceTexture::class.java), i)
                chooseBestSize(picSize!!, previewSize, i)


            }
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }


    }

    private fun chooseOptimalSize(choices: Array<Size>?, id: String): Size {
        val bigEnough = ArrayList<Size>()
        val bestBigEnough = ArrayList<Size>()
        val notBigEnough = ArrayList<Size>()
        val bestSize = ArrayList<Size>()
        for (i in 0 until choices?.size!!) {
            val w = choices[i].width
            val h = choices[i].height
            val rate: Double = (w / h).toDouble()
            val rateStr = rate.toString()
            if (w > 1920 && h > 1080) {
                bigEnough.add(choices[i])
                if (rateStr.startsWith("1.7")) {
                    bestBigEnough.add(choices[i])
                }

            } else {
                if (rateStr.startsWith("1.7")) {
                    bestSize.add(choices[i])
                }
                notBigEnough.add(choices[i])
            }
        }
        val size: Size
        if (bestSize.size > 0) {
            size = Collections.max(bestSize, CompareSizesByArea())
            chooseBestInfo += "id= $id : + ${size.width} + x + ${size.height} "
        } else if (notBigEnough.size > 0) {
            size = Collections.max(notBigEnough, CompareSizesByArea())
            chooseBestInfo += "id= $id : + ${size.width} + x + ${size.height}"
        } else if (bestBigEnough.size > 0) {
            size = Collections.max(bestBigEnough, CompareSizesByArea())
            chooseBestInfo += "id= $id : + ${size.width} + x + ${size.height}"
        } else if (bigEnough.size > 0) {
            size = Collections.max(bigEnough, CompareSizesByArea())
            chooseBestInfo += "id= $id : + ${size.width} + x + ${size.height}"
        } else {
            size = choices[0]
            chooseBestInfo += "id= $id : + ${choices[0].width} + x + ${choices[0].height}"
        }

        return size
    }

    private fun chooseBestSize(pic: Array<Size>, previewSize: Size, id: String) {
        val width = previewSize.width
        val height = previewSize.height
        var rate: Double = (width / height).toDouble()
        rate = m1(rate)
        val sameRateSize = ArrayList<Size>()
        val bigPixel = ArrayList<Size>()
        val bigSameSize = ArrayList<Size>()
        for (i in 0 until pic.size) {
            var rate1 = (pic[i].width / pic[i].height).toDouble()
            rate1 = m1(rate1)
            if (pic[i].width > 3000 && pic[i].height > 1500) {
                bigPixel.add(pic[i])
            }
            if (rate == rate1) {
                sameRateSize.add(pic[i])
            }
            if (rate == rate1 && pic[i].width > 3000 && pic[i].height > 1500) {
                bigSameSize.add(pic[i])
            }
        }
        if (bigSameSize.size == 0) {
            if (bigPixel.size == 0) {
                if (sameRateSize.size == 0) {
                    picBestInfo += "id= $id : + ${previewSize.width} x ${previewSize.height}"
                } else {
                    val size = getMaxSize(sameRateSize)
                    picBestInfo += "id= $id : +${size.width} x ${size.height}"
                }
            } else {
                val size = getMinSize(bigPixel)
                picBestInfo += "id= $id : +${size.width} x ${size.height}"
            }
        } else {
            val size = getMinSize(bigSameSize)
            picBestInfo += "id= $id : +${size.width} x ${size.height}"
        }

    }

    private fun m1(d: Double): Double {
        var str = d.toString()
        if (str.length > 4) {
            str = str.substring(0, 4)
        } else {
            str = str.substring(0, str.length - 1)
        }
        return str.toDouble()
    }


    private fun getMaxSize(list: ArrayList<Size>): Size {
        val length = list.size
        for (i in 0 until length - 1) {
            for (j in 0 until length - 1) {
                if (list[j].height > list[j + 1].height) {
                    val temp = list[i]
                    list.set(j, list.get(j + 1))
                    list.set(j + 1, temp)
                }
            }
        }
        return list.get(length - 1)

    }

    private fun getMinSize(list: ArrayList<Size>): Size {
        val length = list.size
        for (i in 0 until length - 1) {
            for (j in 0 until length - 1) {
                if (list[j].height > list[j + 1].height) {
                    val temp = list[i]
                    list.set(j, list.get(j + 1))
                    list.set(j + 1, temp)
                }
            }
        }
        return list.get(0)
    }

    private fun setMessage() {
        val url = "http://jb.stanic.com.cn:10045/jbtest/jbsave/savedata"
        VolleyUtil.get(url, object : VolleyUtil.OnResponse<String> {
            override fun onMap(map: HashMap<String, String>) {
                val message = screenInfo + picInfo + cameraInfo + chooseBestInfo + picBestInfo
                map["message"] = message
            }

            override fun onSuccess(response: String) {
                Toast.makeText(this@MainActivity, response, Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: VolleyError) {
                Toast.makeText(this@MainActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }

        })

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            var isGranted = true
            for (i in grantResults.iterator()) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    break
                }
            }
            if (isGranted) {
                checkSupportHighResolvingPower()
                getScreenInfo()
                isSupportCamera2()
                setUpCameraOutputs()
                setMessage()
            }
        }

    }

}
