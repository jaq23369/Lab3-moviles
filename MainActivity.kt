package edu.uvg.MiPrimeraAppLab3

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    var tvRes:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // Después de esta llamada ya puedo usar los elementos

        tvRes=findViewById(R.id.tvRes)

    }
    fun calcular(view : View){
        var boton=view as Button
        var textoBoton=boton.text.toString()
        var concatenar=tvRes?.text.toString()+textoBoton
        var concatenarSinCeros=quitarCerosIzquierda(concatenar)
        if(textoBoton=="="){
           var resultado=0.0
            try {
                resultado=eval(tvRes?.text.toString())
                tvRes?.text=resultado.toString()
            }catch (e:Exception){
                tvRes?.text=e.toString()
            }

        }else if(textoBoton=="RESET") {
            tvRes?.text = "0"
        }else{
            tvRes?.text =concatenarSinCeros
        }
    }

    fun quitarCerosIzquierda(str : String): String{
        var i=0
        while(i<str.length && str[i]=='0')i++
        val sb=StringBuffer(str)
        sb.replace(0,i,"")
        return sb.toString()

    }

    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.toInt()) -> x += parseTerm()
                        eat('-'.toInt()) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.toInt()) -> x *= parseFactor()
                        eat('/'.toInt()) -> {
                            val denom = parseFactor()
                            if (denom == 0.0) throw RuntimeException("Division by zero")
                            x /= denom
                        }
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.toInt())) return parseFactor()  // unary plus
                if (eat('-'.toInt())) return -parseFactor()  // unary minus

                var x = 0.0
                val startPos = pos
                if (eat('('.toInt())) {  // parentheses
                    x = parseExpression()
                    if (!eat(')'.toInt())) throw RuntimeException("Closing parenthesis expected")
                } else if ((ch >= '0'.toInt() && ch <= '9'.toInt()) || ch == '.'.toInt()) {  // numbers
                    while ((ch >= '0'.toInt() && ch <= '9'.toInt()) || ch == '.'.toInt()) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                }

                if (eat('^'.toInt())) {  // After reading a number, check for exponentiation
                    x = Math.pow(x, parseFactor())
                } else if (eat('e'.toInt())) {  // Check for exponential function 'e^'
                    if (eat('^'.toInt())) {  // Only apply exp if '^' follows 'e'
                        x = Math.exp(parseFactor())
                    } else {  // Otherwise, treat 'e' as a number (Euler's number)
                        x = Math.E
                    }
                } else if (eat('√'.toInt())) {  // square root
                    x = Math.sqrt(parseFactor())
                }

                return x
            }


            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + (ch.toChar()))
                return x
            }
        }.parse()
    }

}



