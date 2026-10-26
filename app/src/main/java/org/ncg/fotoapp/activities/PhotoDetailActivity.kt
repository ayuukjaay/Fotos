package org.ncg.fotoapp.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.ncg.fotoapp.R
import org.ncg.fotoapp.services.DbHelper

class PhotoDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)
        val f = DbHelper(this).obtenerFoto(intent.getLongExtra("fotoId", -1))
        if (f == null) { finish(); return }

        findViewById<ImageView>(R.id.imgGrande).setImageBitmap(
            BitmapFactory.decodeFile(f.ruta, BitmapFactory.Options().apply { inSampleSize = 2 }))
        findViewById<TextView>(R.id.tvComentario).text =
            "Comentario: " + f.comentario.ifEmpty { "(sin comentario)" }
        findViewById<TextView>(R.id.tvGps).text =
            if (f.lat == 0.0 && f.lon == 0.0) "Ubicación GPS: no disponible"
            else "Ubicación GPS: %.6f, %.6f".format(f.lat, f.lon)
        findViewById<TextView>(R.id.tvFechaDet).text = "Fecha: ${f.fecha}"
    }
}