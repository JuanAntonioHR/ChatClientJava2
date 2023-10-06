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
            String msg = "m^" + username + "@" + socket.getInetAddress().getHostAddress() + "^-^" + mensaje + "^";
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
                String users = msg.substring(2);
                StringTokenizer st = new StringTokenizer(users, "^");
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
            } else if (msg.startsWith("f")) {
                StringTokenizer st = new StringTokenizer(msg, "^");
                String type = st.nextToken();
                String ipCliente = st.nextToken();
                String target = st.nextToken();
                String nombreArchivo = st.nextToken();
                long tamañoArchivo = Long.parseLong(st.nextToken());

                // Crea un FileOutputStream para escribir los bytes del archivo en un nuevo archivo
                FileOutputStream fos = new FileOutputStream(nombreArchivo);

                // Lee los bytes del archivo y escríbelos en el archivo
                byte[] buffer = new byte[4096];
                int bytesRead;
                while (tamañoArchivo > 0 && (bytesRead = netIn.read(buffer, 0, (int) Math.min(buffer.length, tamañoArchivo))) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    tamañoArchivo -= bytesRead;
                }

                // Cierra el FileOutputStream después de recibir el archivo completo
                fos.close();

                // Notifica al usuario que el archivo ha sido recibido
                // Puedes mostrar un mensaje en la interfaz gráfica del usuario o realizar otra acción según sea necesario
                return "Has recibido el siguiente archivo: " + nombreArchivo;
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
            // Abre el archivo
            File archivo = new File(path);
            // Envía metadatos del archivo al receptor (nombre del archivo y tamaño)
            try (FileInputStream fis = new FileInputStream(archivo)) {
                netOut.writeUTF("f^" + username + "@" + socket.getInetAddress().getHostAddress() + "^" + target + "^" + archivo.getName() + "^" + archivo.length() + "^");
                // Envía el contenido del archivo al receptor
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    netOut.write(buffer, 0, bytesRead);
                }
                // Cierra el flujo de salida del archivo
                // Notifica al usuario que el archivo ha sido enviado
                // Puedes mostrar un mensaje en la interfaz gráfica del usuario o realizar otra acción según sea necesario
            }
        } catch (IOException e) {
            // Maneja las excepciones aquí
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