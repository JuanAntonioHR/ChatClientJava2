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
    private Cliente cliente;
    private PrivateChat privateChat;

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
                // El ipClienteTarget es el ip del cliente que envió el mensaje
                if (mensaje != null && privateChat.isVisible()) {

                    System.out.println("Va a ejecutar la función para mostrar mensajes");
                    privateChat.mostrarMensaje(mensaje);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Manejar la excepción si es necesario
            }
        }
    }
}
