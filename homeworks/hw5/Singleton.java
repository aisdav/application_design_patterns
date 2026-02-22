import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigurationManagerSingleton {
    public static class ConfigurationManager {
        private static volatile ConfigurationManager instance;
        private static final ReentrantLock lock = new ReentrantLock();

        private Map<String, String> config;
        private String configFile;

        private ConfigurationManager() {
            config = new ConcurrentHashMap<>();
        }

        public static ConfigurationManager getInstance() {
            if (instance == null) {
                lock.lock();
                try {
                    if (instance == null) {
                        instance = new ConfigurationManager();
                    }
                } finally {
                    lock.unlock();
                }
            }
            return instance;
        }

        public void loadFromFile(String filename) throws IOException {
            this.configFile = filename;
            Properties props = new Properties();
            try (InputStream input = new FileInputStream(filename)) {
                props.load(input);
                for (String key : props.stringPropertyNames()) {
                    config.put(key, props.getProperty(key));
                }
            }
        }

        public void saveToFile() throws IOException {
            if (configFile == null) {
                throw new IllegalStateException("No config file specified");
            }
            Properties props = new Properties();
            props.putAll(config);
            try (OutputStream output = new FileOutputStream(configFile)) {
                props.store(output, "Configuration");
            }
        }

        public String get(String key) throws NoSuchElementException {
            String value = config.get(key);
            if (value == null) {
                throw new NoSuchElementException("Key '" + key + "' not found");
            }
            return value;
        }

        public void set(String key, String value) {
            config.put(key, value);
        }

        public void loadFromDatabase(String connectionString) {

            config.put("db.url", connectionString);
            config.put("db.user", "admin");
            config.put("db.password", "secret");
        }

        public boolean containsKey(String key) {
            return config.containsKey(key);
        }

        @Override
        public String toString() {
            return "ConfigurationManager" + config;
        }
    }

    public static void main(String[] args) {

        Runnable task = () -> {
            ConfigurationManager cm = ConfigurationManager.getInstance();
            System.out.println(Thread.currentThread().getName() + " -> " + cm.hashCode());
        };

        for (int i = 0; i < 5; i++) {
            new Thread(task, "Thread-" + i).start();
        }

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        ConfigurationManager cm = ConfigurationManager.getInstance();
        try {
            cm.loadFromFile("config.properties");
            System.out.println("Loaded: " + cm.get("app.name"));
        } catch (IOException e) {
            System.err.println("File not found, creating default config.");
            cm.set("app.name", "MyApp");
            cm.set("app.version", "1.0");
            try {
                cm.saveToFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
        }

        try {
            System.out.println(cm.get("nonexistent.key"));
        } catch (NoSuchElementException e) {
            System.out.println("Expected exception: " + e.getMessage());
        }

        cm.loadFromDatabase("jdbc:mysql://localhost/test");
        System.out.println("After DB load: " + cm);
    }
}
