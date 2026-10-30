package br.edu.ifpe.jsreboques

import android.app.Application

/** Cria o "container" de dependências uma única vez, quando o app abre. */
class JsReboquesApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
