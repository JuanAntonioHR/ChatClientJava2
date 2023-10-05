/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lib;

import gui.GeneralChat;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author JuanA
 */
public class ChatClientJava {

    public static void main(String[] args) throws IOException {
        // Solicitar el hostname y el username al usuario mediante JOptionPane
        String serverIP = JOptionPane.showInputDialog(null, "Ingrese el hostname del servidor:");
        String username = JOptionPane.showInputDialog(null, "Ingrese su nombre de usuario:");

        // Verificar si se ingresaron valores
        if (serverIP != null && username != null && !serverIP.isEmpty() && !username.isEmpty()) {
            // Crear el cliente y pasar los valores del hostname y el username
            Cliente cliente = new Cliente(serverIP, username);
            
            // Mostrar la ventana de chat
            GeneralChat generalChatW = new GeneralChat(cliente);
            generalChatW.setVisible(true);
            generalChatW.getMessageTextField().requestFocus();
            
            cliente.notificarConexion();
            
            while (true) {
                generalChatW.mostrarMensajes();
                generalChatW.mostrarUsuarios();
            }

        } else {
            // Mostrar un mensaje de error y salir si no se ingresaron los valores
            JOptionPane.showMessageDialog(null, "Debe ingresar el hostname y el username.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}