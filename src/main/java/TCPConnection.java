import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));
    }

    public TCPConnection(final TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader( new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this); //TCPConnection.this -
                    // хитрая конструкция, в которой вызываем this того, что уровнем выше
                    // ниже пока поток не прерван (его можно прервать снаружи) мы получаем строчку и отдаем его eventlisteneru
                    while (!rxThread.isInterrupted()) {
                        String inReadLine = in.readLine();
                        //Читает строку тут
//                        if (inReadLine != null)
                        eventListener.onReceiveString(TCPConnection.this, inReadLine);


                    }

                } catch (IOException e) {
                   //происходит при выключении клиента
                  //  e.printStackTrace();
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();

    }

    //синхронизируем,чтобы безопасно обращаться можно было из разных потоков
    public synchronized void sendString(String value) {
        try {
//            System.out.println(value);
            out.write(value);
            out.flush();
            out.close();
//            System.out.println("Отправили сообщение - T56");
            disconnect();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
