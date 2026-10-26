package org.ncg.fotoapp.services

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoStorageService(private val ctx: Context) {

    /** Crea un archivo vacío en filesDir/fotos para que la cámara escriba en él */
    fun crearArchivoFoto(): File {
        val dir = File(ctx.filesDir, "fotos").apply { mkdirs() }
        val nombre = "IMG_" +
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
        return File(dir, nombre)
    }

    /** Uri segura (FileProvider) que se entrega a la cámara */
    fun uriPara(archivo: File): Uri =
        FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", archivo)
}