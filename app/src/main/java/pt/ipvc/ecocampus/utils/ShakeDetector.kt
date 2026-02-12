package pt.ipvc.ecocampus.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(context: Context) : SensorEventListener {

    // Acesso direto ao hardware. Escolhi o Acelerómetro por ser universal em todos os telemóveis.
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Callback (Lambda) para notificar a UI quando o evento ocorrer.
    private var onShakeListener: (() -> Unit)? = null

    // --- CALIBRAÇÃO ---
    // Defini 2.7g como threshold por testes práticos: filtra caminhadas, mas deteta abanões intencionais.
    private val shakeThreshold = 2.7f
    private val shakeCooldown = 1000 // Debounce de 1s para evitar múltiplos disparos no mesmo movimento.
    private var lastShakeTime: Long = 0

    // Registo o listener. Importante usar SENSOR_DELAY_UI para equilibrar latência e bateria.
    fun start(onShake: () -> Unit) {
        this.onShakeListener = onShake
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    // Crítico: Desligar o sensor quando o ecrã fecha (onDispose) para não drenar a bateria.
    fun stop() {
        sensorManager.unregisterListener(this)
        onShakeListener = null
    }

    // Ocorre em tempo real (várias vezes por segundo).
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        //

        // Cálculo da magnitude do vetor 3D (Teorema de Pitágoras no espaço).
        // Normalizo dividindo pela gravidade da Terra para obter valor em Força G.
        val gForce = sqrt((x * x + y * y + z * z).toDouble()) / SensorManager.GRAVITY_EARTH

        if (gForce > shakeThreshold) {
            val now = System.currentTimeMillis()

            // Verificação de cooldown (Debounce Logic)
            if (lastShakeTime + shakeCooldown > now) return

            lastShakeTime = now
            onShakeListener?.invoke() // Disparo o evento para a UI
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não necessito de tratar mudanças de precisão para esta funcionalidade.
    }
}

