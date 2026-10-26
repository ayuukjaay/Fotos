package org.ncg.fotoapp.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.ncg.fotoapp.R
import org.ncg.fotoapp.services.DbHelper

class PhotoListActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private var uid = -1L
    private lateinit var lista: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)
        db = DbHelper(this)
        uid = intent.getLongExtra("uid", -1)
        lista = findViewById(R.id.lvFotos)
    }

    override fun onResume() {
        super.onResume()
        val fotos = db.listarFotos(uid)
        findViewById<TextView>(R.id.tvVacio).visibility =
            if (fotos.isEmpty()) View.VISIBLE else View.GONE

        lista.adapter = object : BaseAdapter() {
            override fun getCount() = fotos.size
            override fun getItem(p: Int) = fotos[p]
            override fun getItemId(p: Int) = fotos[p].id
            override fun getView(p: Int, v: View?, parent: ViewGroup): View {
                val row = v ?: layoutInflater.inflate(R.layout.item_foto, parent, false)
                val f = fotos[p]
                row.findViewById<ImageView>(R.id.imgMini).setImageBitmap(
                    BitmapFactory.decodeFile(f.ruta, BitmapFactory.Options().apply { inSampleSize = 8 }))
                row.findViewById<TextView>(R.id.tvFecha).text = f.fecha
                row.findViewById<TextView>(R.id.tvCom).text = f.comentario
                return row
            }
        }
        lista.setOnItemClickListener { _, _, _, id ->
            startActivity(Intent(this, PhotoDetailActivity::class.java).putExtra("fotoId", id))
        }
    }
}