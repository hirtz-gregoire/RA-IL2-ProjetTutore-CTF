package display;
import javafx.scene.media.*;
import javafx.util.Duration;

import java.io.File;

public class SongPlayer {
    private static MediaPlayer mediaPlayer;

    static String pathSongFiles = "ressources/songs/";

    public static MediaPlayer getMediaPlayer(String filename) {
        String path = pathSongFiles + filename + ".mp3";
        String file = new File(path).toURI().toString();
        Media media = new Media(file);
        return new MediaPlayer(media);
    }

    public static void playSuperposeSong(String filename) {
        MediaPlayer newMediaPlayer = getMediaPlayer(filename);
        newMediaPlayer.play();
    }

    public static void playSong(String filename) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = getMediaPlayer(filename);
        mediaPlayer.play();
    }

    public static void playRepeatSong(String filename) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = getMediaPlayer(filename);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            }
        });
        mediaPlayer.play();
    }

    public static void playRepeatWhenFinishSong(String filename) {
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                playRepeatSong(filename);
            }
        });
    }

    public static void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
