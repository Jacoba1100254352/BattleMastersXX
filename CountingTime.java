// Main.java
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


class CountingTime {
    private AtomicInteger secondsPassed;
  
    // Constructors
    public CountingTime(int initialSeconds) {
      secondsPassed = new AtomicInteger(initialSeconds);
    }
    // Default constructor
    public CountingTime() {
      secondsPassed = new AtomicInteger();
    }
    
  
  
    public int getSeconds() {
        return secondsPassed.intValue();
    }
    public void setSeconds(int seconds) {
        secondsPassed.set(seconds);
    }
    
    public void timerfunc() {
      	Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // All we care about is actually updating secondsPassed
                int current = secondsPassed.getAndIncrement();
            }
        }, 2000, 1000); // delay = 2s, repeat every 1s
    }
}