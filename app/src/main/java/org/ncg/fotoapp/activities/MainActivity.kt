package org.ncg.fotoapp.activities

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.ncg.fotoapp.services.DbHelper
import org.ncg.fotoapp.services.LocationService
import org.ncg.fotoapp.services.PhotoStorageService
import java.io.File
import org.ncg.fotoapp.R

class MainActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var storage: PhotoStorageService
    private lateinit var gps: LocationService
    private lateinit var img: ImageView
    private var uid = -1L
    private var archivo: File? = null

    private val permisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { r ->
        if (r[Manifest.permission.CAMERA] == true) abrirCamara()
        else Toast.makeText(this, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
    }

    private val camara = registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) {
            val f = archivo ?: return@registerForActivityResult
            img.setImageBitmap(
                BitmapFactory.decodeFile(
                f.absolutePath, BitmapFactory.Options().apply { inSampleSize = 2 }))
            pedirComentario(f)
        } else archivo?.delete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DbHelper(this)
        storage = PhotoStorageService(this)
        gps = LocationService(this)
        uid = intent.getLongExtra("uid", -1)
        img = findViewById(R.id.imgFoto)

        findViewById<Button>(R.id.btnTomar).setOnClickListener {
            permisos.launch(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
        findViewById<Button>(R.id.btnVer).setOnClickListener {
            startActivity(Intent(this, PhotoListActivity::class.java).putExtra("uid", uid))
        }
        findViewById<Button>(R.id.btnSalir).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)); finish()
        }
    }

    private fun abrirCamara() {
        val f = storage.crearArchivoFoto(); archivo = f
        camara.launch(storage.uriPara(f))
    }

    private fun pedirComentario(f: File) {
        val et = EditText(this).apply { hint = "Comentario" }
        AlertDialog.Builder(this)
            .setTitle("Comentario de la foto")
            .setView(et)
            .setCancelable(false)
            .setPositiveButton("Guardar") { _, _ ->
                val loc = gps.ultimaUbicacion()
                db.guardarFoto(uid, f.absolutePath, et.text.toString(),
                    loc?.latitude ?: 0.0, loc?.longitude ?: 0.0)
                Toast.makeText(this,
                    if (loc == null) "Guardada sin ubicación GPS" else "Foto guardada",
                    Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}