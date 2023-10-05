/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lib;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

/**
 *
 * @author JuanA
 */
public class Cliente {
    private String SERVER_IP;
    private static final int SERVER_PORT = 2099;
    private Vector<String> usuarios = new Vector<String>();
    private Socket socket;
    private DataInputStream netIn;
    private DataOutputStream netOut;
    private String username;

    public Cliente(String serverIP, String username) {
        this.SERVER_IP = serverIP;
        this.username = username;
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            netIn = new DataInputStream(socket.getInputStream());
            netOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // Mostrar un JOptionPane con el mensaje de error y cerrar la aplicación
            JOptionPane.showMessageDialog(null, "Error al conectar al servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Cerrar la aplicación
        }
    }

    public Vector<String> getUsuarios() {
        return usuarios;
    }

    public String getUsername() {
        return username;
    }

    public void notificarConexion() {
        try {
            // Enviar el username al servidor
            String msg = "j^" + username + "@" + socket.getInetAddress().getHostAddress() + "^-^-^";
            netOut.writeUTF(msg);
        } catch (IOException e) {
            // Mostrar un JOptionPane con el mensaje de error y cerrar la aplicación
            JOptionPane.showMessageDialog(null, "Error al notificar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Cerrar la aplicación
        }
    }

    public void enviarMensaje(String mensaje) {
        try {
            // Agregar el username al mensaje antes de enviarlo
            String msg = "m^" + username + "@" + socket.getInetAddress().getHostAddress() + "^-^" + mensaje;
            netOut.writeUTF(msg);
        } catch (IOException e) {
            // Mostrar un JOptionPane con el mensaje de error y cerrar la aplicación
            JOptionPane.showMessageDialog(null, "Error al enviar el mensaje: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Cerrar la aplicación
        }
    }

    public String recibirMensaje() throws IOException {
        try {
            String msg = netIn.readUTF();
            System.out.println("Mensaje recibido en chat general" + msg);

            if (msg.startsWith("l^")) {
                // Lista de usuarios conectados
                String usuarios = msg.substring(2);
                StringTokenizer st = new StringTokenizer(usuarios, "^");
                while (st.hasMoreTokens()) {
                    String usuario = st.nextToken();
                    if (!this.usuarios.contains(usuario)) {
                        this.usuarios.add(usuario);
                    }
                }
                return null;
            } else if (msg.startsWith("m^") || msg.startsWith("p^") || msg.startsWith("j^")) {
                StringTokenizer st = new StringTokenizer(msg, "^");
                String type = st.nextToken();
                String nombreIP = st.nextToken();
                String nombre = nombreIP.substring(0, nombreIP.indexOf("@"));
                String ipCliente = nombreIP.substring(nombreIP.indexOf("@") + 1);
                String target = st.nextToken();
                String mensaje = st.nextToken();

                if (type.equalsIgnoreCase("m")) {
                    // Mensaje del chat grupal
                    return nombre + ": " + mensaje;
                } else {
                    // Mensaje del servidor
                    return mensaje;
                }
            }

        } catch (IOException e) {
            // Manejar la excepción aquí
            throw new IOException("Error al recibir el mensaje.", e);
        }
        return null;
    }

    public void enviarMensajePrivado(String mensaje, String target) {
        try {
            // Agregar el username al mensaje antes de enviarlo
            String msg = "d^" + username + "@" + socket.getInetAddress().getHostAddress() + "^" + target + "^" + mensaje + "^";
            System.out.println(username + " envia " + msg);
            netOut.writeUTF(msg);
        } catch (IOException e) {
            // Mostrar un JOptionPane con el mensaje de error y cerrar la aplicación
            JOptionPane.showMessageDialog(null, "Error al enviar el mensaje: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Cerrar la aplicación
        }
    }

    public String recibirMensajePrivado(String nombre) throws IOException {
        try {
            String msg = netIn.readUTF();
            System.out.println("Si se recibe mensaje en funcion privada: " + msg);

            if (msg.startsWith("d")) {
                System.out.println("Se recibio un mensaje privado");
                // Mensaje privado
                StringTokenizer st = new StringTokenizer(msg, "^");
                String type = st.nextToken();
                String ipCliente = st.nextToken();
                String target = st.nextToken();
                String mensaje = st.nextToken();

                return nombre + ": " + mensaje;
            } else {
                System.out.println("Tecnicamente el mensaje no inicia con d:> " + msg);
                return null;
            }
        } catch (IOException e) {
            // Manejar la excepción aquí
            throw new IOException("Error al recibir el mensaje.", e);
        }
    }

    public void enviarArchivo(String path, String target) {
        try {
            // Agregar el username al mensaje antes de enviarlo
            String msg = "f^" + username + "@" + socket.getInetAddress().getHostAddress() + "^" + target + "^" + path + "^";
            netOut.writeUTF(msg);
        } catch (IOException e) {
            // Mostrar un JOptionPane con el mensaje de error y cerrar la aplicación
            JOptionPane.showMessageDialog(null, "Error al enviar el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Cerrar la aplicación
        }
    }

    public void cerrarConexion() {
        try {
            if (socket != null && !socket.isClosed()) {
                // Enviar el mensaje de salida al servidor
                String msg = "p^" + username + "@" + socket.getInetAddress().getHostAddress() + "^-^-^";
                netOut.writeUTF(msg);
                // Cerrar el socket
                socket.close();
            }
        } catch (IOException e) {
            // Mostrar un JOptionPane con el mensaje de error y cerrar la aplicación
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Cerrar la aplicación
        }
    }
}