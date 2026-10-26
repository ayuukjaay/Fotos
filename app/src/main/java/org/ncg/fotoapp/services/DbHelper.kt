package org.ncg.fotoapp.services

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.ncg.fotoapp.models.Foto
import java.security.MessageDigest

class DbHelper(ctx: Context) : SQLiteOpenHelper(ctx, "fotoapp.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE usuarios(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL, apellidos TEXT NOT NULL,
            correo TEXT NOT NULL UNIQUE, password TEXT NOT NULL)""")
        db.execSQL("""CREATE TABLE fotos(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            usuario_id INTEGER NOT NULL, ruta TEXT NOT NULL,
            comentario TEXT, latitud REAL, longitud REAL,
            fecha TEXT DEFAULT CURRENT_TIMESTAMP)""")
    }

    override fun onUpgrade(db: SQLiteDatabase, o: Int, n: Int) {}

    private fun hash(s: String) = MessageDigest.getInstance("SHA-256")
        .digest(s.toByteArray()).joinToString("") { "%02x".format(it) }

    private fun Cursor.toFoto() = Foto(
        getLong(0), getString(1), getString(2) ?: "",
        getDouble(3), getDouble(4), getString(5) ?: ""
    )

    // ---------- Usuarios ----------

    fun registrar(nombre: String, apellidos: String, correo: String, pass: String): Boolean {
        val v = ContentValues().apply {
            put("nombre", nombre); put("apellidos", apellidos)
            put("correo", correo.lowercase()); put("password", hash(pass))
        }
        return writableDatabase.insert("usuarios", null, v) != -1L
    }

    /** Devuelve el id del usuario o -1 si las credenciales no coinciden */
    fun login(correo: String, pass: String): Long {
        readableDatabase.rawQuery(
            "SELECT id FROM usuarios WHERE correo=? AND password=?",
            arrayOf(correo.lowercase(), hash(pass))
        ).use { return if (it.moveToFirst()) it.getLong(0) else -1L }
    }

    // ---------- Fotos ----------

    fun guardarFoto(uid: Long, ruta: String, comentario: String, lat: Double, lon: Double): Long {
        val v = ContentValues().apply {
            put("usuario_id", uid); put("ruta", ruta); put("comentario", comentario)
            put("latitud", lat); put("longitud", lon)
        }
        return writableDatabase.insert("fotos", null, v)
    }

    fun listarFotos(uid: Long): List<Foto> {
        val lista = mutableListOf<Foto>()
        readableDatabase.rawQuery(
            "SELECT id,ruta,comentario,latitud,longitud,fecha FROM fotos WHERE usuario_id=? ORDER BY id DESC",
            arrayOf(uid.toString())
        ).use { while (it.moveToNext()) lista.add(it.toFoto()) }
        return lista
    }

    fun obtenerFoto(id: Long): Foto? = readableDatabase.rawQuery(
        "SELECT id,ruta,comentario,latitud,longitud,fecha FROM fotos WHERE id=?",
        arrayOf(id.toString())
    ).use { if (it.moveToFirst()) it.toFoto() else null }
}