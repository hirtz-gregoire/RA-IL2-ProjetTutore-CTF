package display;
import javafx.scene.media.*;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongPlayer {
    private final static int MAX_NUMBER_OF_SUPERPOSE_SONGS = 5;

    private static List<MediaPlayer> mediaPlayers = new ArrayList<MediaPlayer>(MAX_NUMBER_OF_SUPERPOSE_SONGS);

    static String pathSongFiles = "ressources/songs/";

    public static MediaPlayer getMediaPlayer(String filename) {
        String path = pathSongFiles + filename + ".mp3";
        String file = new File(path).toURI().toString();
        Media media = new Media(file);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayers.add(mediaPlayer);
        return mediaPlayer;
    }

    public static void playSuperposeSong(String filename) {
        if (mediaPlayers.size() < MAX_NUMBER_OF_SUPERPOSE_SONGS) {
            MediaPlayer newMediaPlayer = getMediaPlayer(filename);
            newMediaPlayer.play();
            newMediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    mediaPlayers.remove(newMediaPlayer);
                }
            });
        }
    }

    public static void playSong(String filename) {
        stopAllSongs();
        MediaPlayer mediaPlayer = getMediaPlayer(filename);
        mediaPlayer.play();
    }

    public static void playRepeatSong(String filename) {
        stopAllSongs();
        MediaPlayer mediaPlayer = getMediaPlayer(filename);
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            }
        });
    }

    public static void stopAllSongs() {
        mediaPlayers.forEach(MediaPlayer::stop);
        mediaPlayers.clear();
    }
}
