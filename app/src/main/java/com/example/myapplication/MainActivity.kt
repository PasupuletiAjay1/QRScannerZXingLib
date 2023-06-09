package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var launcher: ActivityResultLauncher<Unit>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setOnClickListener()

        launcher = registerForActivityResult(ZXingActivityResultContract()){ scannedData:String? ->
            if (scannedData!=null){
                try {
                    parseQRData(scannedData)
                }
                catch (e: JSONException) {
                    // Data not in the expected format. So, whole object as toast message.
                    Toast.makeText(this, scannedData, Toast.LENGTH_LONG).show()
                }

            }else{
                messageErrorScanningQR()
            }
        }

    }

    private fun parseQRData(scannedData: String) {
        val data = JSONObject(scannedData)
        val deeplinkUrl = data.getString("deeplinkUrl")
        val UUID = data.getString("UUID")
        binding.deeplinkUrl.text = deeplinkUrl
        binding.UUID.text = UUID
    }

    private fun messageErrorScanningQR() {
        Toast.makeText(this, "Error while scanning QR", Toast.LENGTH_SHORT).show()
    }

    private fun setOnClickListener() {
        binding.btnScan.setOnClickListener{
            performAction()
        }
    }

    private fun performAction() {
        // Code to perform action when button is clicked.
        launcher.launch(Unit)
    }

    inner class ZXingActivityResultContract: ActivityResultContract<Unit, String?>(){
        override fun createIntent(context: Context, input: Unit): Intent {
            val qrScanIntegrator: IntentIntegrator = IntentIntegrator(this@MainActivity)
            qrScanIntegrator.setOrientationLocked(true)
            return qrScanIntegrator.createScanIntent()
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            val result: String? = if (resultCode == RESULT_OK && intent!=null){
                val result = IntentIntegrator.parseActivityResult(resultCode, intent)
                result.contents
            } else null
            return result
        }
    }
}