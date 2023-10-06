/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lib;

import gui.PrivateChat;
import java.io.IOException;

/**
 *
 * @author JuanA
 */
public class MensajePrivadoHandler implements Runnable {
    //Variables locales
    private final Cliente cliente;
    private final PrivateChat privateChat;

    //Constructor
    public MensajePrivadoHandler(Cliente cliente, PrivateChat privateChat) {
        this.cliente = cliente;
        this.privateChat = privateChat;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Si se ejecuta el hilo");
            try {
                String mensaje = cliente.recibirMensajePrivado(privateChat.getUsernameTarget());
                System.out.println("Si sale después de que le llegue un mensaje privado: " + mensaje);
                // Mostrar el mensaje si la pantalla está activa
                if (mensaje != null && privateChat.isVisible()) {
                    System.out.println("Va a ejecutar la función para mostrar mensajes");
                    privateChat.mostrarMensaje(mensaje);
                }
            } catch (IOException e) {
                // Manejar la excepción si es necesario
            }
        }
    }
}
