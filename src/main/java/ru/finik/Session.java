/* 24.04.2020 - начало
 * Сервер:
 * Скорее всего выделенный Сервер.
 * Содержит переменные:
 * Идентификаторы Сессий. Идентификатор формируется после чтения Сервером информации о плейлисте с Клиента.
 * Сессия содержит следующие параметры:
 *
 * Количество Подключившихся (Подключение Приложения проверяется каждый трек. Если ответа от Приложения
 * Сервер не получает, Приложение считается отключившимся и количество подключившихся уменьшается на 1)
 * Статус Сессии (активирован Сессия или нет, то есть танцуют ли сейчас с помощью Приложения)
 * Длину Плейлиста Сессии (в единицах времени - милли/секундах)
 * Длину всех треков Сессии (в единицах времени - милли/секундах)
 * Интервал между треками в текущей Сессии (по дефолту 1-3 секунды - устанавливается)
 * Положение Бегунка в текущей Сессии (Сколько единиц времени прошло с начала плейлиста)
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Session implements GeneralNames {

    //номер сессии
    private int sesId;

    //количество подключений данной сессии
    private int numOfConns;

    //активна ли сессия?
    private boolean isActive = false;




    //в будущем. В версии 0.01 не реализовано
    //равен длине всех треков + длине интервала * на количество треков - 1
    //

    //список треков - версия 0.01
    private List<MusicTrack> musicTracks;

    private List<PlayList> playLists;
    //список плейлистов
    private int lengthOfPlaylist;
    //длина паузы между треками
    private int lengthOfInterval;
    //положение бегунка
    //формируется так. Вначале равен нулю, но после старта (т.е. когда сессия активна
    // начинает увеличиваться каждую секунду на 1 единицу
    private int timeFromStart = 0;
    //ТОЛЬКО ДЛЯ НАЧАЛЬНЫХ ВЕРСИЙ
    private int duration;
    //время старта сессии по часам сервера
    private long startTime;

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Session(int sesId, int numOfConns, boolean isActive, long startTime, int duration) {
        this.sesId = sesId;
        this.numOfConns = numOfConns;
        this.startTime = startTime;
        this.duration = duration;
        this.isActive = isActive;
    }

    public int getDuration() {
        return duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setSesId(int sesId) {
        this.sesId = sesId;
    }

    public int getNumOfConns() {
        return numOfConns;
    }

    public void setNumOfConns(int numOfConns) {
        this.numOfConns = numOfConns;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<MusicTrack> getMusicTracks() {
        return musicTracks;
    }

    public void setMusicTracks(List<MusicTrack> musicTracks) {
        this.musicTracks = musicTracks;
    }

    public int getTimeFromStart() {
        return timeFromStart;
    }

    public void setTimeFromStart(int timeFromStartOfSession) {
        this.timeFromStart = timeFromStartOfSession;
    }

//    public int getLenghOfMusicFile() {
//        return lenghOfMusicFile;
//    }

    public void setLenghOfMusicFile(int duration) {
        this.duration = duration;
    }

/*    //таймер - задержка в миллисекундах
    public void timer(long t) {
        System.out.println("Осталось " +  t/1000 + " секунд");
        while (t >= 1000) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
            t -= 1000;
            System.out.println("Осталось " + t/1000 + " секунд");
        }
        while (t > 0 && t < 1000) {
            try {
                TimeUnit.MILLISECONDS.sleep(t);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }
    }

 */

    //    //in new Thread
    public void plusTimer(int duration) throws InterruptedException {
        for (int timeFromStart = 0; timeFromStart <= duration; timeFromStart++) {
            TimeUnit.MILLISECONDS.sleep(1);
            setTimeFromStart(timeFromStart);
        }
    }

    public boolean isExist(long duration) {
        //if duration of session == duration which sent from client
        // then return true. Else return false
        if (this.getDuration() == duration) {
            long deltaTime;
//            startTime = System.currentTimeMillis();
            this.setStartTime(System.currentTimeMillis());
            System.out.println("S158: " + this.getStartTime());
            return true;
        }
        return false;
    }
}
