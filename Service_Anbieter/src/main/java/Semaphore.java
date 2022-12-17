public class Semaphore {
    private int value;
    public Semaphore(int init) {
        if( init < 0)
            init = 0;
        value = init;
    }
    public synchronized void p() {
        while ( value == 0) {
            try { wait (); }
catch ( InterruptedException e) {}
        }
        value--;
    }
    public synchronized void v() {
        value++;
        notify ();
    }
}