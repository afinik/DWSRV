package ru.finik;/*version server s0.001*/

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static ru.finik.Log.logd;

public class DWServer implements TCPConnectionListener {

    //if id doesn't exist - create a new session


    boolean isExist = false;
    private static int currentSesId = 0;
    private String clientId;
    private List<String> clientIds = new ArrayList<>();
    long startTime;
    private Map<String, TCPConnection> clientsConnections;
    private List<Integer> currentTimeOfSessions = new ArrayList<>();
    private static List<Session> sessions = new ArrayList<>();
    public String hcClient;
    public HashMap<String, Integer> clientHashNumber;
    public ArrayList<Integer> clientNumber;
    private int lastNumber;
    private String stringNewClient;


    public static void main(String[] args) {
        new DWServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private DWServer() {
        clientHashNumber = new HashMap<>();
        logd("С39 Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {

                    logd("С46 ru.finik.TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        //logd("connections.size())=" + connections.size());
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
        logd("C80 Smth. closed - onException");
        logd("С81 ru.finik.TCPConnection exception: " + e);
    }

    private void sentAllConnection(String value, TCPConnection tcpConnection) {
        //и тут нашу строку получаем
        //первые 5 символов
//        ConcurrentHashMap concurrentHashMap
//        log.info("Строка84");
        if (value != null && value.length() > 6)
//        clientsConnections.put (value, connections.get(connections.size() - 1));
            switch (value.substring(0, 5)) {
                //инициализация листа клиентов
                //посылается с клиента до нажатия на кпонку "выбрать файл"
                case "hcode":
                    //номер клиента еще не сформирован - получаем только хэш - отправляем номер
                    hcClient = value.substring(8, 42);
//                    clientHashNumber.put("22", "22");
                    int tempNum = clientNumber(hcClient);
                    int duration = Integer.parseInt(value.substring(value.lastIndexOf("/") + 1));
                    logd("С101 " + stringNewClient + "номер " + tempNum);

                    isExist = false;
//                    clientIds.add(hcClient);
                    //надо добавить в мэп это и коннекшн - не обязательно пока - надо понять надо ли
//                    if (sessions != null)
                        for (Session s : sessions) {
                        //Сравниваем длину композиции в сессии с тем, что пришло с клиента
                        //TODO В будущем сравниваем не только длину, но и специальный код, который участники
                        //TODO узнают непосредственно перед мероприятием (во избежании фальстарта)
                        if (s.getDuration() == duration) {
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) addSession(duration);
                    tcpConnection.sendString("clNum" + "/" + tempNum);
                    isExist = false;
                    break;
                case "Start":
                    String tempStr = value.substring(8, value.lastIndexOf("/"));
                    //сюда отправляется "Start = " + duration
//                    logd("Start!!!! clnum=" + tempStr);
                    int dur1 = Integer.parseInt(value.substring(value.lastIndexOf("/") + 1));
                    logd(tempStr + "","С125 Запрос на старт композиции с длиной (мс)" + dur1);

//                    if (sessions != null)
                        for (Session s : sessions) {

                           // logd(clientNumber(hcClient) + "","Сессия не существует");
                            isExist = false;
                            if (s.isExist(dur1)) {
                                //we found the session and isExist (boolean but not void!
                                // isExist(long duration) - there is big difference!

                                long deltaTime;
                                isExist = true;
                                //if session is not active (not started before)
                                if(!s.isActive()) {
                                    s.setActive(true);
                                    logd(clientNumber(hcClient) + "","C151: " + System.currentTimeMillis());
                                    logd("C142: " + s.getStartTime());


                                    //we are calculate a difference between startTime and currentTime
                                    startTime = s.getStartTime();

                                    //                        timer(3000);


                                }
                                deltaTime = System.currentTimeMillis() - startTime;
                                logd("C152: " + deltaTime);
                                //if the difference between their is smaller than duration of music file
                                // we send deltaTime to the client
                                if (deltaTime < dur1) tcpConnection.sendString("curtime" + "/" + deltaTime);
                                else{
                                    //TODO else - remove session from sessions
//                                    s.setActive(false);
//                                TODO обработать команду Stop на клиенте, поступающую с сервера на клиент
                                    tcpConnection.sendString("Stop");
                                    logd("C162 Клиент видимо вылетел. Номер клиента "  + clientNumber);
                                    sessions.remove(s);
                                }
                                break;
                            }
                    }
                    //
                    if (!isExist) tcpConnection.sendString("Error: Сессия не найдена");
                    break;

                default:
                    //обнаружен запуск только вначале
                    if (value.length() > 8)
                        logd("С188 Отладка: " + value);
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

    private Integer clientNumber(String hcClient){
//        logd("хэш: " + hcClient);
        for (Map.Entry entry: clientHashNumber.entrySet()) {
            //Если находим такой хэш, то
//            logd("(for _ lastNumber = " + lastNumber);
//            logd("хэш " + hcClient + " уже есть");
            stringNewClient = "Клиент подсоединился вновь - ";
            if (entry.getKey().equals(hcClient)){
                return (Integer) entry.getValue();

            }
        }
//        logd("хэша " + hcClient + " нет");
        stringNewClient = "Новый клиент подсоединился. Хэш " + hcClient + ". Клиенту выдан ";
        lastNumber = lastNumber + 1;
        clientHashNumber.put(hcClient, lastNumber);
//        logd("lastNumber = " + lastNumber);
        return lastNumber;
    }

}
