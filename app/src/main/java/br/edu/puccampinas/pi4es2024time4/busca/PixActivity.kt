package br.edu.puccampinas.pi4es2024time4.busca

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class PixActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pix)

        // Referência para a ImageView onde o QR Code será exibido
        val imageViewQRCode: ImageView = findViewById(R.id.imageViewQRCode)

        // Conteúdo do QR Code (exemplo de conteúdo para pagamento via Pix)
        val qrCodeContent = "Exemplo de conteúdo para pagamento via Pix"

        try {
            // Gerar o QR Code
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 200, 200)

            // Converter o bitMatrix para um Bitmap
            val qrCodeBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565)
            for (x in 0 until 200) {
                for (y in 0 until 200) {
                    qrCodeBitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }

            // Exibir o QR Code na ImageView
            imageViewQRCode.setImageBitmap(qrCodeBitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao gerar o QR Code", Toast.LENGTH_SHORT).show()
        }
    }
}
