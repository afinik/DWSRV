/*version server s0.001*/
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChatServer implements TCPConnectionListener {

    //if id doesn't exist - create a new session


    boolean isExist = false;
    private static int currentSesId = 0;
    private String clientId;
    private List<String> clientIds = new ArrayList<>();
    long startTime;
    private Map<String, TCPConnection> clientsConnections;
    private List<Integer> currentTimeOfSessions = new ArrayList<>();
    private static List<Session> sessions = new ArrayList<>();


    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("С31 Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {

                    System.out.println("С38 TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        //System.out.println("connections.size())=" + connections.size());
        // sentAllConnection("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
//        if (value != null)
        //и тут нашу строку получает
        sentAllConnection(value, tcpConnection);
//        tcpConnection.sendString("test - 52");
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        if (connections.size() != 0)
            connections.remove(tcpConnection);
        // sentAllConnection("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("C72 Smth. closed - onException");
        System.out.println("С73 TCPConnection exception: " + e);
    }

    private void sentAllConnection(String value, TCPConnection tcpConnection) {
        //и тут нашу строку получаем
        //первые 5 символов
//        ConcurrentHashMap concurrentHashMap
        if (value != null && value.length() > 6)
//        clientsConnections.put (value, connections.get(connections.size() - 1));
            switch (value.substring(0, 5)) {
                //инициализация листа клиентов
                //посылается с клиента до нажатия на кпонку "выбрать файл"
                case "hcode":
                    String hcClient = value.substring(8, 42);
                    int duration = Integer.parseInt(value.substring(value.lastIndexOf("/") + 1));
                    System.out.println("С88 Новый клиент id = " + hcClient);
                    isExist = false;
//                    clientIds.add(hcClient);
                    //надо добавить в мэп это и коннекшн - не обязательно пока - надо понять надо ли
//                    if (sessions != null)
                        for (Session s : sessions) {
                        //если сессия не существует
                        // Тут пропишем еще и sesid в будущем
                        if (s.getDuration() == duration) {
                            isExist = true;
                            //Здесь будем вычислять номер сессии и посылать ее клиенту
                            // будет мэп (номер сессии, экземпляр сессии
                            // в дальнейшем клиент будет посылать номер сессии, а не размер
                            // плейлиста/мелодии в миллисекундах
                            break;
                        }
                    }
                    if (!isExist) addSession(duration);
                    tcpConnection.sendString("num" + "/" + duration);
                    isExist = false;

                    //TODO если в листе такого id нет
                    // то добавляем такой id. Если же
                    // есть, прибавляем 1 к количеству подключившихся
                    // а вопрос, как сделать чтобы два раза одного не считало.
                    // Как идентифицировать пользователя и знать, что он не отключился
                    break;
                case "Start":
                    //сюда отправляется "Start = " + duration
                    int dur1 = Integer.parseInt(value.substring(8));
                    System.out.println("С117 Запрос на старт композиции с длиной (мс)" + dur1);

//                    if (sessions != null)
                        for (Session s : sessions) {
                        //если сессия не существует
                        // TODO Тут пропишем еще и sesid в будущем
                        //если существует - стартует время
                        //reset boolean isExist

                            isExist = false;
                            if (s.isExist(dur1)) {
                                //we found the session and isExist (boolean but not void!
                                // isExist(long duration) - there is big difference!

                                long deltaTime;
                                isExist = true;
                                //if session is not active (not started before)
                                if(!s.isActive()) {
                                    s.setActive(true);
                                    System.out.println("C136: " + System.currentTimeMillis());
                                    System.out.println("C137: " + s.getStartTime());


                                    //we are calculate a difference between startTime and currentTime
                                    startTime = s.getStartTime();

                                    //                        timer(3000);


                                }
                                deltaTime = System.currentTimeMillis() - startTime;
                                System.out.println("C148: " + deltaTime);
                                //if the difference between their is smaller than duration of music file
                                // we send deltaTime to the client
                                if (deltaTime < dur1) tcpConnection.sendString("curtime" + "/" + deltaTime);
                                else{
                                    //TODO else - remove session from sessions
//                                    s.setActive(false);
//                                TODO обработать команду Stop на клиенте, поступающую с сервера на клиент
                                    tcpConnection.sendString("Stop");
                                    sessions.remove(s);
                                }
                                break;
                            }
                    }
                    //
                    if (!isExist) tcpConnection.sendString("Error: Сессия не найдена");
                    break;

                default:
                    if (value.length() > 8)
                        System.out.println("С180 Отладка:" + value);
                    break;
            }
    }

    private void addSession(int duration) {
        sessions.add(new Session(0, 1, false, 0, duration));
    }

    private static void timer(long t) {

        try {
            TimeUnit.MILLISECONDS.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
