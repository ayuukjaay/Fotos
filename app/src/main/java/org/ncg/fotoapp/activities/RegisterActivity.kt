package org.ncg.fotoapp.activities

import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.ncg.fotoapp.R
import org.ncg.fotoapp.services.DbHelper

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val db = DbHelper(this)
        val nombre = findViewById<EditText>(R.id.etNombre)
        val apellidos = findViewById<EditText>(R.id.etApellidos)
        val correo = findViewById<EditText>(R.id.etCorreo)
        val pass = findViewById<EditText>(R.id.etPass)

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val n = nombre.text.toString().trim()
            val a = apellidos.text.toString().trim()
            val c = correo.text.toString().trim()
            val p = pass.text.toString()
            when {
                n.isEmpty() || a.isEmpty() || c.isEmpty() || p.isEmpty() ->
                    toast("Completa todos los campos")
                !Patterns.EMAIL_ADDRESS.matcher(c).matches() -> toast("Correo no válido")
                p.length < 6 -> toast("La contraseña debe tener al menos 6 caracteres")
                db.registrar(n, a, c, p) -> { toast("Usuario registrado"); finish() }
                else -> toast("Ese correo ya está registrado")
            }
        }
    }
    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
}