package com.tech.camh

import android.Manifest
import android.R
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.tech.camh.databinding.ActivityCameraBinding
import com.tech.camh.databinding.ActivityMainBinding
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.tan

class CameraActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    lateinit var binding: ActivityCameraBinding

    var camera: Camera? = null

    lateinit var mSensorManager: SensorManager

    lateinit var mAccelerometer: Sensor
    lateinit var mMagnetometer: Sensor

    lateinit var threeDForm: DecimalFormat

    private val units = arrayOf("meters", "cms", "feet", "inches")

    var unit = 0

    private var mGravity: FloatArray? = null
    private var mMagnetic: FloatArray? = null

    var pressure: Float? = null



    var h1 = 0.0
    var d: Double = 0.0



    var value = FloatArray(3)

    private var AngleA = 0.0
    private var AngleB = 0.0
    private var X1 = 0.0
    private var X2 = 0.0

    private val un = units[unit]
    var test = "Results:\nObj Distance = $d$un\nCam height = $h1$un"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFormat(PixelFormat.TRANSLUCENT)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            1
        )

        binding =  ActivityCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        setupCamera()

        setupSensor()

        threeDForm = DecimalFormat("#.###")

        binding.editText1.setText("0.0")
        binding.textView1.text = test
        unit = 0
        binding.distanceButton.isEnabled = false


        addListenerOnSpinnerItemSelection()

        binding.adjustHeightButton.setOnClickListener {

            adjustHeight()

        }


        binding.distanceButton.setOnClickListener {

            distance()

        }

        binding.resetButton.setOnClickListener {
            reset()
        }



    }

    private fun distance() {

        AngleA = getDirection().toDouble()
        AngleA = Math.toRadians(90.0)-AngleA;
        d = threeDForm.format(abs(h1*(tan((AngleA))))).toDouble()

        val un = units[unit]
        test = "Results:\nObj Distance = $d$un\nCam height = $h1$un"

        showMessage("Object distance calculated!")

        binding.textView1.text = test
    }


    private fun adjustHeight() {

        h1 = binding.editText1.text.toString().toDouble()

        if(h1.equals(0.0)){

            showMessage("Camera Height must be more than 0")

        }else{

            val df = "Camera height = "+h1+units[unit]
            binding.textView1.text = df

            binding.distanceButton.isEnabled = true


            showMessage("Phone Height adjusted")

        }
    }

    private fun reset() {

        AngleA = 0.0
        AngleB = 0.0
        X1 = 0.0
        X2 = 0.0
        d = 0.0


        showMessage("Value Reset")
        val un = units[unit]
        test = "Results:\nObj Distance = $d$un\nCam height = $h1$un"
        binding.textView1.text = test
    }

    private fun showMessage(s: String) {

        Toast.makeText(applicationContext,s,Toast.LENGTH_LONG).show()

    }

    fun addListenerOnSpinnerItemSelection() {


        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item,units)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner1.adapter = adapter

        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, arg2: Int, arg3: Long) {

                when (arg2) {

                    0 -> {

                        //convert to meters

                        when (unit) {

                            1 -> {

                                h1 = threeDForm.format(h1*0.01).toDouble()
                                d = threeDForm.format(d*0.01).toDouble()


                            }

                            2 -> {

                                h1 = threeDForm.format(h1*0.3048).toDouble()
                                d = threeDForm.format(d*0.3048).toDouble()


                            }

                            3 -> {

                                h1 = threeDForm.format(h1*0.0254).toDouble()
                                d = threeDForm.format(d*0.0254).toDouble()


                            }


                        }

                        unit = 0
                        val un = units[unit]
                        test = "Results:\nObj Distance = $d$un\nCam height = $h1$un"
                        binding.textView1.text = test

                    }

                    1 -> {

                        //convert to centimetres

                        when (unit) {

                            0 -> {

                                h1 = threeDForm.format(h1*100).toDouble()
                                d = threeDForm.format(d*100).toDouble()


                            }

                            2 -> {

                                h1 = threeDForm.format(h1*30.48).toDouble()
                                d = threeDForm.format(d*30.48).toDouble()


                            }

                            3 -> {

                                h1 = threeDForm.format(h1*2.54).toDouble()
                                d = threeDForm.format(d*2.54).toDouble()


                            }
                        }

                        unit = 1
                        val un = units[unit]
                        test = "Results:\nObj Distance = $d$un\nCam height = $h1$un"
                        binding.textView1.text = test


                    }

                    2 -> {

                        //convert to feet

                        when (unit) {

                            0 -> {

                                h1 = threeDForm.format(h1*3.28084).toDouble()
                                d = threeDForm.format(d*3.28084).toDouble()


                            }

                            1 -> {

                                h1 = threeDForm.format(h1*0.0328084).toDouble()
                                d = threeDForm.format(d*0.0328084).toDouble()


                            }

                            3 -> {

                                h1 = threeDForm.format(h1*0.0833333).toDouble()
                                d = threeDForm.format(d*0.0833333).toDouble()


                            }
                        }

                        unit = 2
                        val un = units[unit]
                        test = "Results:\nObj Distance = $d$un\nCam height = $h1$un"
                        binding.textView1.text = test


                    }

                    3 -> {

                        when (unit) {

                            0 -> {

                                h1 = threeDForm.format(h1*39.3701).toDouble()
                                d = threeDForm.format(d*39.3701).toDouble()


                            }

                            1 -> {

                                h1 = threeDForm.format(h1*0.393701).toDouble()
                                d = threeDForm.format(d*0.393701).toDouble()

                            }

                            2 -> {

                                h1 = threeDForm.format(h1*12).toDouble()
                                d = threeDForm.format(d*12).toDouble()

                            }
                        }

                        unit = 3
                        val un = units[unit]
                        test = "Results:\nObj Distance = $d$un\nCam height = $h1$un"
                        binding.textView1.text = test

                    }

                }

            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {

            }
        }

    }

    override fun onResume() {
        super.onResume()

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    override fun onPause() {
        super.onPause()

        mSensorManager.unregisterListener(this);
    }



    private fun setupSensor() {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {

        val preview : Preview = Preview.Builder()
            .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when(event!!.sensor.type){

            Sensor.TYPE_ACCELEROMETER -> {

                mGravity = event.values.clone()

            }

            Sensor.TYPE_MAGNETIC_FIELD -> {

                mMagnetic = event.values.clone()

            }

            Sensor.TYPE_PRESSURE -> {

                pressure = event.values[0];
                pressure = pressure!!*100;

            }
        }

        if(mGravity != null && mMagnetic != null)
        {
            getDirection();
        }
    }

    private fun getDirection(): Float {
        val temp = FloatArray(9)
        val r = FloatArray(9)

        //Load rotation matrix into R
        SensorManager.getRotationMatrix(temp, null, mGravity, mMagnetic)

        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_X, SensorManager.AXIS_Z, r)

        //Return the orientation values
        SensorManager.getOrientation(r, value)


        //value[0] - Z, value[1]-X, value[2]-Y in radians
        return value[1] //return x

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}