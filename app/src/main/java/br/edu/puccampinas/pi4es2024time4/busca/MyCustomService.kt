package br.edu.puccampinas.pi4es2024time4.busca

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyCustomService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        // Este método deve ser implementado, mas pode retornar null se o serviço não for de ligação
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Lógica que será executada quando o serviço for iniciado
        Log.d("MyCustomService", "Serviço iniciado")
        return START_STICKY // Pode escolher START_NOT_STICKY ou START_REDELIVER_INTENT dependendo do comportamento desejado
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyCustomService", "Serviço encerrado")
    }
}