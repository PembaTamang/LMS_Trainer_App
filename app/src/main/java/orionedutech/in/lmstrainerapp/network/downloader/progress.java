package orionedutech.in.lmstrainerapp.network.downloader;

public interface progress {
    void progress(double progress, float speed,long secs);
    void failed();
    void completed(long totalbytes);
}