import java.util.*;

class TV {
    private boolean isOn = false;
    private int channel = 1;

    public void on() {
        isOn = true;
        System.out.println("Телевизор включён.");
    }

    public void off() {
        isOn = false;
        System.out.println("Телевизор выключен.");
    }

    public void setChannel(int channel) {
        if (isOn) {
            this.channel = channel;
            System.out.println("Телевизор переключён на канал " + channel);
        } else {
            System.out.println("Телевизор выключен, канал не выбран.");
        }
    }

    public void setInput(String input) {
        if (isOn) {
            System.out.println("Телевизор переключён на вход " + input);
        } else {
            System.out.println("Телевизор выключен, вход не изменится.");
        }
    }
}

class AudioSystem {
    private boolean isOn = false;
    private int volume = 10;

    public void on() {
        isOn = true;
        System.out.println("Аудиосистема включена.");
    }

    public void off() {
        isOn = false;
        System.out.println("Аудиосистема выключена.");
    }

    public void setVolume(int volume) {
        if (isOn) {
            this.volume = Math.max(0, Math.min(volume, 100));
            System.out.println("Громкость установлена на " + this.volume);
        } else {
            System.out.println("Аудиосистема выключена, громкость не изменена.");
        }
    }

    public void setMode(String mode) {
        if (isOn) {
            System.out.println("Аудиосистема переключена в режим " + mode);
        } else {
            System.out.println("Аудиосистема выключена, режим не изменён.");
        }
    }
}

class DVDPlayer {
    private boolean isOn = false;
    private boolean isPlaying = false;

    public void on() {
        isOn = true;
        System.out.println("DVD-проигрыватель включён.");
    }

    public void off() {
        isOn = false;
        isPlaying = false;
        System.out.println("DVD-проигрыватель выключен.");
    }

    public void play() {
        if (isOn) {
            isPlaying = true;
            System.out.println("DVD проигрывается.");
        } else {
            System.out.println("DVD-проигрыватель выключен, невозможно воспроизвести.");
        }
    }

    public void pause() {
        if (isOn && isPlaying) {
            System.out.println("DVD на паузе.");
        } else {
            System.out.println("Невозможно поставить на паузу.");
        }
    }

    public void stop() {
        if (isOn && isPlaying) {
            isPlaying = false;
            System.out.println("DVD остановлен.");
        } else {
            System.out.println("Невозможно остановить.");
        }
    }
}

class GameConsole {
    private boolean isOn = false;

    public void on() {
        isOn = true;
        System.out.println("Игровая консоль включена.");
    }

    public void off() {
        isOn = false;
        System.out.println("Игровая консоль выключена.");
    }

    public void startGame(String game) {
        if (isOn) {
            System.out.println("Запущена игра: " + game);
        } else {
            System.out.println("Консоль выключена, запуск игры невозможен.");
        }
    }
}

class HomeTheaterFacade {
    private TV tv;
    private AudioSystem audio;
    private DVDPlayer dvd;
    private GameConsole console;

    public HomeTheaterFacade() {
        tv = new TV();
        audio = new AudioSystem();
        dvd = new DVDPlayer();
        console = new GameConsole();
    }

    public void watchMovie(String movieName, int volume) {
        System.out.println("\n=== Начинаем просмотр фильма: " + movieName + " ===");
        tv.on();
        tv.setInput("HDMI");
        audio.on();
        audio.setVolume(volume);
        dvd.on();
        dvd.play();
        System.out.println("Фильм запущен. Наслаждайтесь!\n");
    }

    public void endMovie() {
        System.out.println("\n=== Выключаем систему ===");
        dvd.off();
        audio.off();
        tv.off();
        System.out.println("Система выключена.\n");
    }

    public void startGame(String gameName) {
        System.out.println("\n=== Запускаем игру: " + gameName + " ===");
        tv.on();
        tv.setInput("HDMI");
        audio.on();
        audio.setVolume(50);
        console.on();
        console.startGame(gameName);
        System.out.println("Игра запущена.\n");
    }

    public void listenMusic() {
        System.out.println("\n=== Начинаем прослушивание музыки ===");
        tv.on();
        tv.setInput("AUX");
        audio.on();
        audio.setVolume(40);
        audio.setMode("Stereo");
        System.out.println("Музыка играет.\n");
    }

    public void setVolume(int volume) {
        System.out.println("\n=== Регулировка громкости ===");
        audio.setVolume(volume);
    }
}

public class HomeTheaterDemo {
    public static void main(String[] args) {
        HomeTheaterFacade homeTheater = new HomeTheaterFacade();

        homeTheater.watchMovie("Интерстеллар", 70);
        homeTheater.setVolume(55);
        homeTheater.endMovie();

        homeTheater.startGame("The Witcher 3");
        homeTheater.setVolume(45);
        homeTheater.endMovie(); 

        homeTheater.listenMusic();
        homeTheater.setVolume(30);
        homeTheater.endMovie();
    }
}
