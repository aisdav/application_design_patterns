import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class LoggerSingletonDemo {

    public enum LogLevel {
        INFO, WARNING, ERROR
    }
    public static class Logger {
        private static volatile Logger instance;
        private static final ReentrantLock lock = new ReentrantLock();

        private LogLevel currentLevel;
        private String logFilePath;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private static final long MAX_FILE_SIZE = 1024 * 1024; 
        private int fileIndex = 0;

        private Logger() {
            loadConfiguration();
        }

        public static Logger getInstance() {
            if (instance == null) {
                lock.lock();
                try {
                    if (instance == null) {
                        instance = new Logger();
                    }
                } finally {
                    lock.unlock();
                }
            }
            return instance;
        }

        private void loadConfiguration() {
            Properties props = new Properties();
            try (InputStream input = new FileInputStream("logger.properties")) {
                props.load(input);
                String levelStr = props.getProperty("log.level", "INFO").toUpperCase();
                currentLevel = LogLevel.valueOf(levelStr);
                logFilePath = props.getProperty("log.file", "app.log");
            } catch (IOException e) {
                currentLevel = LogLevel.INFO;
                logFilePath = "app.log";
            }
        }

        public void setLogLevel(LogLevel level) {
            lock.lock();
            try {
                this.currentLevel = level;
            } finally {
                lock.unlock();
            }
        }

        public void log(String message, LogLevel level) {
            if (level.ordinal() < currentLevel.ordinal()) return;

            lock.lock();
            try {
                File logFile = new File(logFilePath);
                if (logFile.exists() && logFile.length() > MAX_FILE_SIZE) {
                    rotateLogFile();
                }

                String timestamp = LocalDateTime.now().format(formatter);
                String logEntry = String.format("[%s] [%s] %s%n", timestamp, level, message);
                Files.write(Paths.get(logFilePath), logEntry.getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Failed to write log: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        }

        private void rotateLogFile() {
            try {
                File oldFile = new File(logFilePath);
                String newName = logFilePath + "." + (++fileIndex);
                Files.move(oldFile.toPath(), Paths.get(newName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Log rotation failed: " + e.getMessage());
            }
        }

        public void logToAll(String message, LogLevel level) {
            log(message, level);
            System.out.printf("[%s] %s%n", level, message);
        }
    }

    public static class LogReader {
        private final String logFilePath;

        public LogReader(String logFilePath) {
            this.logFilePath = logFilePath;
        }

        public void readAndFilter(LogLevel minLevel) {
            try (Stream<String> lines = Files.lines(Paths.get(logFilePath))) {
                lines.forEach(line -> {
                    int levelStart = line.indexOf('[') + 1;
                    int levelEnd = line.indexOf(']', levelStart);
                    if (levelStart > 0 && levelEnd > levelStart) {
                        String levelStr = line.substring(levelStart, levelEnd);
                        try {
                            LogLevel level = LogLevel.valueOf(levelStr);
                            if (level.ordinal() >= minLevel.ordinal()) {
                                System.out.println(line);
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println(line);
                        }
                    } else {
                        System.out.println(line);
                    }
                });
            } catch (IOException e) {
                System.err.println("Error reading log file: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            new Thread(() -> {
                Logger logger = Logger.getInstance();
                logger.log("Thread " + threadId + " info", LogLevel.INFO);
                logger.log("Thread " + threadId + " warning", LogLevel.WARNING);
                logger.log("Thread " + threadId + " error", LogLevel.ERROR);
            }).start();
        }

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        LogReader reader = new LogReader("app.log");
        System.out.println("=== Logs with level >= WARNING ===");
        reader.readAndFilter(LogLevel.WARNING);
    }
}
