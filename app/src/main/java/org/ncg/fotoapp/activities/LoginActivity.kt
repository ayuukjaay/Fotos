package org.ncg.fotoapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.ncg.fotoapp.R
import org.ncg.fotoapp.services.DbHelper

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val db = DbHelper(this)
        val correo = findViewById<EditText>(R.id.etCorreo)
        val pass = findViewById<EditText>(R.id.etPass)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val uid = db.login(correo.text.toString().trim(), pass.text.toString())
            if (uid > 0) {
                startActivity(Intent(this, MainActivity::class.java).putExtra("uid", uid))
                finish()
            } else Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnIrRegistro).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}