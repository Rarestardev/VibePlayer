package com.rarestardev.vibeplayer.VideoPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.media.AudioManagerCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.common.collect.ImmutableList;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SettingsDao;
import com.rarestardev.vibeplayer.Database.entity.SettingsEntity;
import com.rarestardev.vibeplayer.Listener.SubtitleFilesSaveState;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.Utilities.FileFormater;
import com.rarestardev.vibeplayer.Views.settings.SubtitleSettingsActivity;
import com.rarestardev.vibeplayer.databinding.ActivityVideoPlayerBinding;
import com.rarestardev.vibeplayer.databinding.CustomPlaybackViewBinding;
import com.rarestardev.vibeplayer.databinding.SwipeZoomDesignBinding;

import java.util.List;
import java.util.concurrent.Executors;

public class VideoPlayerActivity extends AppCompatActivity implements SubtitleFilesSaveState {

    private ActivityVideoPlayerBinding binding;
    private CustomPlaybackViewBinding playbackViewBinding;
    private SwipeZoomDesignBinding swipeZoomDesignBinding;

    private ExoPlayer player;
    private PlaybackParameters parameters;
    private ScaleGestureDetector scaleGestureDetector;
    private Handler handler;
    private Runnable runnable;
    ControlsMode controlsMode;
    private MediaItem mediaItem;
    private CountDownTimer countDownTimer;
    private SettingsDao settingsDao;
    private SharedPreferences sharedPreferences;

    private enum ControlsMode {LOCK, FULLSCREEN}

    private float speed;
    private float startBrightness = -1.0f;
    private float startVolumePercent = -1.0f;
    private float scale_factor = 1.0f;
    private int startVideoTime = -1;
    private static final int MAX_VIDEO_STEP_TIME = 60 * 1000;
    private static final int MAX_BRIGHTNESS = 100;
    private String videoPath;
    private String subtitlePath;
    private int targetTime;
    private int totalTime;
    private long currentDurationPlayer;
    private boolean playWhenReady = true;
    private float previousVolume;

    private static final String LOG = Constants.LOG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player);
        playbackViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.custom_playback_view, binding.parent, true);
        swipeZoomDesignBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.swipe_zoom_design, binding.parent, true);

        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        settingsDao = db.settingsDao();

        sharedPreferences = getSharedPreferences("PlayerManager", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        currentDurationPlayer = sharedPreferences.getLong("Duration", 0);
        playWhenReady = sharedPreferences.getBoolean("WhenReady", false);
        subtitlePath = sharedPreferences.getString("PlayerSubtitle", "");

        checkAndPauseMusic();
    }

    private void checkAndPauseMusic() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isMusicActive()) {
            audioManager.requestAudioFocus(i -> {
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            doInitialize();

            Intent i = new Intent("com.android.music.musicservicecommand");
            i.putExtra("command", "pause");
            sendBroadcast(i);
        } else {
            doInitialize();
        }
    }

    private void doInitialize() {
        videoPath = getIntent().getStringExtra("VideoPath");
        playbackViewBinding.videoName.setText(getIntent().getStringExtra("VideoName"));

        if (videoPath == null) {
            Log.e(LOG, "Video Path is empty");
            Toast.makeText(this, "Wrong video !", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        player = new ExoPlayer.Builder(this).build();
        binding.exoplayerView.setPlayer(player);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleDetector());

        onClickPlayerViews();

        initSubtitleSettings();

        SwipeTouchListener();

        HideViewsOnVideosWithTimer();
    }

    private void initSubtitleSettings() {
        getSubtitleColor(subtitleColor -> {
            if (subtitleColor != 0) {
                playbackViewBinding.tvSubtitle.setTextColor(subtitleColor);
            }
        });

        getSubtitleTextSize(subtitleTextSize -> {
            if (subtitleTextSize != 0) {
                playbackViewBinding.tvSubtitle.setTextSize(subtitleTextSize);
            }
        });

        getSubtitleTextStyles(s -> {
            if (!s.isEmpty()) {
                switch (s) {
                    case "normal":
                        playbackViewBinding.tvSubtitle.setTypeface(Typeface.DEFAULT);
                        break;
                    case "bold":
                        playbackViewBinding.tvSubtitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                        break;
                    case "italic":
                        playbackViewBinding.tvSubtitle.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                        break;
                }
            }
        });

        getSubtitleTextFont(s -> {
            if (!s.isEmpty()) {
                Typeface typeface = Typeface.create(s, Typeface.NORMAL);
                playbackViewBinding.tvSubtitle.setTypeface(typeface);
            }
        });

    }

    private void getSubtitleTextSize(Consumer<Integer> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int textSize = settingsDao.getSubtitleTextSize();
            runOnUiThread(() -> callback.accept(textSize));
        });
    }

    private void getSubtitleColor(Consumer<Integer> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int color = settingsDao.getSubtitleColor();
            runOnUiThread(() -> callback.accept(color));
        });
    }

    private void getSubtitleTextStyles(Consumer<String> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String style = settingsDao.getSubStyle();
            runOnUiThread(() -> callback.accept(style));
        });
    }

    private void getSubtitleTextFont(Consumer<String> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String font = settingsDao.getSubFont();
            runOnUiThread(() -> callback.accept(font));
        });
    }

    private void initializePlayer() {
        mediaItem = new MediaItem.Builder().setUri(videoPath).build();
        player.addMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    playbackViewBinding.exoDuration.setText(FileFormater.formatTime(player.getCurrentPosition()));
                    playbackViewBinding.exoProgress.setPosition(player.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);

        HandlePlayerListener();
    }

    private void addSubtitle() {
        MediaItem.SubtitleConfiguration subtitle = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitlePath))
                .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                .setLanguage("en")
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build();

        MediaItem updatedMediaItem = mediaItem.buildUpon()
                .setSubtitleConfigurations(ImmutableList.of(subtitle))
                .build();

        player.setMediaItem(updatedMediaItem, player.getCurrentPosition());
        player.prepare();
        HandlePlayerListener();
    }

    private void HandlePlayerListener() {
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    binding.exoplayerView.setKeepScreenOn(true);

                    long duration = player.getDuration();
                    playbackViewBinding.exoProgress.setDuration(duration);
                    playbackViewBinding.exoPosition.setText(FileFormater.formatTime(duration));


                    playbackViewBinding.nightMode.setVisibility(View.GONE);
                    playbackViewBinding.icMute.setImageResource(R.drawable.icon_volume_down);
                    playbackViewBinding.exoPlayPause.setImageResource(R.drawable.icon_pause);

                    ExoTimeBarListener();
                    HideViewsOnVideosWithTimer();
                } else {
                    playbackViewBinding.exoPlayPause.setImageResource(R.drawable.icon_play_arrow);
                    ShowViewsOnVideos();
                    binding.exoplayerView.setKeepScreenOn(false);
                }

                if (playbackState == Player.STATE_READY && playWhenReady) {
                    Log.d(Constants.LOG, "Player is ready and playing.");
                } else if (playbackState == Player.STATE_READY) {
                    Log.d(Constants.LOG, "Player is ready but not playing.");
                } else if (playbackState == Player.STATE_BUFFERING) {
                    Log.d(Constants.LOG, "Player is buffering.");
                } else if (playbackState == Player.STATE_IDLE) {
                    Log.d(Constants.LOG, "Player is idle.");
                } else if (playbackState == Player.STATE_ENDED) {
                    Log.d(Constants.LOG, "Player has ended playback.");
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Toast.makeText(VideoPlayerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(LOG, error.getMessage());
            }

            @Override
            public void onCues(@NonNull List<Cue> cues) {
                if (!cues.isEmpty()) {
                    playbackViewBinding.tvSubtitle.setVisibility(View.VISIBLE);
                    for (Cue cue : cues) {
                        playbackViewBinding.tvSubtitle.setText(cue.text);
                    }

                } else {
                    playbackViewBinding.tvSubtitle.setVisibility(View.GONE);
                    playbackViewBinding.tvSubtitle.setText("");
                }
            }
        });
    }

    @Override
    public void subtitlePath(String path) {
        if (!path.isEmpty()) {
            Log.d(LOG, "SubtitlePath : " + path);
            subtitlePath = path;
            addSubtitle();
            Toast.makeText(this, "Subtitle added", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(LOG, "Path isEmpty");
        }
    }

    private void ExoTimeBarListener() {
        playbackViewBinding.exoProgress.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(@NonNull TimeBar timeBar, long l) {

            }

            @Override
            public void onScrubMove(@NonNull TimeBar timeBar, long l) {
                player.seekTo(l);
            }

            @Override
            public void onScrubStop(@NonNull TimeBar timeBar, long l, boolean b) {
                player.seekTo(l);
            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "Range", "QueryPermissionsNeeded", "UseCompatLoadingForDrawables"})
    private void SwipeTouchListener() {
        playbackViewBinding.voidViewId.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return super.onTouch(v, event);
            }

            @Override
            public boolean onSwipeLeft() {
                return true;
            }

            @Override
            public boolean onSingleTap() {
                super.onSingleTap();
                if (playbackViewBinding.toolbar.getVisibility() == View.VISIBLE) {
                    HideViewsOnVideos();
                } else {
                    ShowViewsOnVideos();
                    HideViewsOnVideosWithTimer();
                }
                return true;
            }

            @Override
            public boolean onDoubleTap() {
                super.onDoubleTap();
                if (!player.getPlayWhenReady()) {
                    player.setPlayWhenReady(true);
                    swipeZoomDesignBinding.doubleTapPlayPause.setVisibility(View.GONE);
                } else {
                    player.setPlayWhenReady(false);
                    swipeZoomDesignBinding.doubleTapPlayPause.setVisibility(View.VISIBLE);
                }
                return true;
            }

            @Override
            public boolean onSwipeTop() {
                return true;
            }

            @Override
            public void onGestureDone() {
                startBrightness = -1.0f;
                startVolumePercent = -1.0f;
                startVideoTime = -1;
                playbackViewBinding.indicatorView.setVisibility(View.GONE);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void adjustBrightness(double adjustPercent) {
                if (adjustPercent < -1.0f) {
                    adjustPercent = -1.0f;
                } else if (adjustPercent > 1.0f) {
                    adjustPercent = 1.0f;
                }
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                if (startBrightness < 0) {
                    startBrightness = lp.screenBrightness;
                }
                float targetBrightness = (float) (startBrightness + adjustPercent * 1.0f);
                if (targetBrightness <= 0.0f) {
                    targetBrightness = 0.0f;
                } else if (targetBrightness >= 1.0f) {
                    targetBrightness = 1.0f;
                }
                lp.screenBrightness = targetBrightness;
                getWindow().setAttributes(lp);
                playbackViewBinding.indicatorImageView.setImageResource(R.drawable.icon_brightness_high);
                playbackViewBinding.indicatorTextView.setText((int) (targetBrightness * MAX_BRIGHTNESS) + "%");
                playbackViewBinding.indicatorView.setVisibility(View.VISIBLE);
            }

            @Override
            public void adjustVolumeLevel(double adjustPercent) {
                if (adjustPercent < -1.0f) {
                    adjustPercent = -1.0f;
                } else if (adjustPercent > 1.0f) {
                    adjustPercent = 1.0f;
                }
                AudioManager audioManager = ContextCompat.getSystemService(VideoPlayerActivity.this, AudioManager.class);
                final int STREAM = AudioManager.STREAM_MUSIC;
                int maxVolume = AudioManagerCompat.getStreamMaxVolume(audioManager, STREAM);
                if (maxVolume == 0) return;
                if (startVolumePercent < 0) {
                    int curVolume = audioManager.getStreamVolume(STREAM);
                    startVolumePercent = curVolume * 1.0f / maxVolume;
                }
                double targetPercent = startVolumePercent + adjustPercent;
                if (targetPercent > 1.0f) {
                    targetPercent = 1.0f;
                } else if (targetPercent < 0) {
                    targetPercent = 0;
                }
                int index = (int) (maxVolume * targetPercent);
                if (index > maxVolume) {
                    index = maxVolume;
                } else if (index < 0) {
                    index = 0;
                }
                audioManager.setStreamVolume(STREAM, index, 0);
                playbackViewBinding.indicatorImageView.setImageResource(R.drawable.icon_volume_up);
                playbackViewBinding.indicatorTextView.setText(index * 100 / maxVolume + "%");
                playbackViewBinding.indicatorView.setVisibility(View.VISIBLE);
            }

            @Override
            public void adjustVideoPosition(double adjustPercent, boolean forwardDirection) {
                if (adjustPercent < -1.0f) {
                    adjustPercent = -1.0f;
                } else if (adjustPercent > 1.0f) {
                    adjustPercent = 1.0f;
                }
                totalTime = (int) player.getDuration();

                if (startVideoTime < 0) {
                    startVideoTime = (int) player.getContentPosition();
                }
                double positiveAdjustPercent = Math.max(adjustPercent, -adjustPercent);
                targetTime = startVideoTime + (int) (MAX_VIDEO_STEP_TIME * adjustPercent * (positiveAdjustPercent / 0.1));

                if (targetTime > totalTime) {
                    targetTime = (int) player.getDuration();
                }
                if (targetTime < 0) {
                    targetTime = totalTime;
                }
                String targetTimeString = FileFormater.formatDuration(targetTime / 1000);

                if (forwardDirection) {
                    playbackViewBinding.indicatorImageView.setImageResource(R.drawable.icon_fast_forward);
                } else {
                    playbackViewBinding.indicatorImageView.setImageResource(R.drawable.icon_fast_rewind);
                }
                playbackViewBinding.indicatorTextView.setText(targetTimeString);
                playbackViewBinding.indicatorView.setVisibility(View.VISIBLE);
                player.seekTo(targetTime);
            }

            @Override
            public Rect viewRect() {
                return new Rect(playbackViewBinding.videoView.getLeft(), playbackViewBinding.videoView.getTop(),
                        playbackViewBinding.videoView.getRight(), playbackViewBinding.videoView.getBottom());
            }
        });
    }

    private void HideViewsOnVideosWithTimer() {
        countDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long l) {
                playbackViewBinding.voidViewId.setOnClickListener(view -> {
                    if (playbackViewBinding.toolbar.getVisibility() == View.VISIBLE) {
                        HideViewsOnVideos();
                    } else {
                        ShowViewsOnVideos();
                        this.start();
                    }
                });
            }

            @Override
            public void onFinish() {
                if (player != null && player.isPlaying()) {
                    HideViewsOnVideos();
                } else {
                    ShowViewsOnVideos();
                }
                this.cancel();
            }
        }.start();
    }

    private void HideViewsOnVideos() {
        playbackViewBinding.toolbar.setVisibility(View.GONE);
        playbackViewBinding.progress.setVisibility(View.GONE);
        playbackViewBinding.menuItems.setVisibility(View.GONE);
        playbackViewBinding.bottomIcons.setVisibility(View.GONE);
        // fullscreen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        countDownTimer.cancel();
    }

    private void ShowViewsOnVideos() {
        playbackViewBinding.toolbar.setVisibility(View.VISIBLE);
        playbackViewBinding.menuItems.setVisibility(View.VISIBLE);
        playbackViewBinding.progress.setVisibility(View.VISIBLE);
        playbackViewBinding.bottomIcons.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @SuppressLint("Range")
    private void onClickPlayerViews() {
        playbackViewBinding.videoMore.setOnClickListener(view -> {
            if (player.isPlaying()) {
                player.pause();
                HandlePlayerListener();
            }
            PopupMenu popupMenu = new PopupMenu(this, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.subtitles_menu, popupMenu.getMenu());

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.subtitlesMenu) {
                    SubtitleDialog subtitleDialog = new SubtitleDialog(VideoPlayerActivity.this, VideoPlayerActivity.this);
                    subtitleDialog.getWindow().setGravity(Gravity.BOTTOM);
                    subtitleDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    subtitleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    subtitleDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    subtitleDialog.setCancelable(false);
                    subtitleDialog.show();
                } else if (menuItem.getItemId() == R.id.subtitleSettingsMenu) {
                    getAllSettings(settingsEntity -> {
                        Intent subSettingIntent = new Intent(VideoPlayerActivity.this, SubtitleSettingsActivity.class);
                        subSettingIntent.putExtra("subColor", settingsEntity.getSubtitleColor());
                        subSettingIntent.putExtra("subTextSize", settingsEntity.getSubtitleTextSize());
                        subSettingIntent.putExtra("subTextStyle", settingsEntity.getSubTextStyle());
                        subSettingIntent.putExtra("subTextFont", settingsEntity.getSubTextFont());
                        startActivity(subSettingIntent);
                    });
                }
                return true;
            });
        });

        playbackViewBinding.videoBack.setOnClickListener(view -> {
            if (player != null) {
                player.stop();
                player.release();
                player = null;
                finish();
            }
        });

        playbackViewBinding.icRotate.setOnClickListener(LandscapeListener);

        playbackViewBinding.scaling.setOnClickListener(firstListener);

        playbackViewBinding.lock.setOnClickListener(view -> {
            controlsMode = ControlsMode.FULLSCREEN;
            playbackViewBinding.rootLayout.setVisibility(View.VISIBLE);
            playbackViewBinding.lock.setVisibility(View.INVISIBLE);
        });

        playbackViewBinding.unlock.setOnClickListener(view -> {
            controlsMode = ControlsMode.LOCK;
            playbackViewBinding.rootLayout.setVisibility(View.INVISIBLE);
            playbackViewBinding.lock.setVisibility(View.VISIBLE);
        });

        playbackViewBinding.exoPlayPause.setOnClickListener(view -> {
            if (player.isPlaying()) {
                player.pause();
                playbackViewBinding.exoPlayPause.setImageResource(R.drawable.icon_play_arrow);
            } else {
                player.play();
                playbackViewBinding.exoPlayPause.setImageResource(R.drawable.icon_pause);
            }
            HandlePlayerListener();
        });

        playbackViewBinding.icNight.setOnClickListener(view -> {
            if (playbackViewBinding.nightMode.getVisibility() == View.VISIBLE) {
                playbackViewBinding.nightMode.setVisibility(View.GONE);
                rotateAndChangeIcon(playbackViewBinding.icNight, R.drawable.icon_light_mode);
            } else {
                playbackViewBinding.nightMode.setVisibility(View.VISIBLE);
                rotateAndChangeIcon(playbackViewBinding.icNight, R.drawable.icon_nights_stay);
            }
        });

        playbackViewBinding.icMute.setOnClickListener(view -> {
            if (player.getVolume() > 0f) {
                previousVolume = player.getVolume();
                player.setVolume(0f);
                rotateAndChangeIcon(playbackViewBinding.icMute, R.drawable.icon_volume_mute);
            } else {
                player.setVolume(previousVolume);
                rotateAndChangeIcon(playbackViewBinding.icMute, R.drawable.icon_volume_down);
            }
        });

        playbackViewBinding.icBrightness.setOnClickListener(view -> {
            BrightnessDialog brightnessDialog = new BrightnessDialog();
            brightnessDialog.show(getSupportFragmentManager(), "dialog");
        });

        playbackViewBinding.exoFfwd.setOnClickListener(view -> {
            long currentPosition = player.getCurrentPosition();
            player.seekTo(currentPosition + 10000);
        });

        playbackViewBinding.exoRew.setOnClickListener(view -> {
            long currentPosition = player.getCurrentPosition();
            player.seekTo(currentPosition - 10000);
        });

        playbackViewBinding.icPlaySpeed.setOnClickListener(view -> ShowDialogSpeedControl());
    }

    private void getAllSettings(Consumer<SettingsEntity> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            SettingsEntity settingsEntities = settingsDao.allSettings();
            runOnUiThread(() -> callback.accept(settingsEntities));
        });
    }

    private void ShowDialogSpeedControl() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Select play Speed")
                .setPositiveButton("Done", null);
        String[] items = {"0.5x", "1x" + " " + "(Normal)", "1.25x", "1.5x", "2x"};
        int checkedItem = -1;
        alertDialog.setSingleChoiceItems(items, checkedItem, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    speed = 0.5f;
                    parameters = new PlaybackParameters(speed);
                    player.setPlaybackParameters(parameters);
                    break;
                case 1:
                    speed = 1f;
                    parameters = new PlaybackParameters(speed);
                    player.setPlaybackParameters(parameters);
                    break;
                case 2:
                    speed = 1.25f;
                    parameters = new PlaybackParameters(speed);
                    player.setPlaybackParameters(parameters);
                    break;
                case 3:
                    speed = 1.5f;
                    parameters = new PlaybackParameters(speed);
                    player.setPlaybackParameters(parameters);
                    break;
                case 4:
                    speed = 2f;
                    parameters = new PlaybackParameters(speed);
                    player.setPlaybackParameters(parameters);
                    break;
                default:
                    break;
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    View.OnClickListener LandscapeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Uri uri = Uri.parse(videoPath);
                retriever.setDataSource(VideoPlayerActivity.this, uri);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                playbackViewBinding.icRotate.setOnClickListener(PortraitListener);
            } catch (Exception e) {
                Log.e("MediaMetadataRetriever", "screenOrientation");
            }
        }
    };
    View.OnClickListener PortraitListener = new View.OnClickListener() {
        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        public void onClick(View view) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Uri uri = Uri.parse(videoPath);
                retriever.setDataSource(VideoPlayerActivity.this, uri);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                playbackViewBinding.icRotate.setOnClickListener(LandscapeListener);
            } catch (Exception e) {
                Log.e("MediaMetadataRetriever", "screenOrientation");
            }
        }
    };
    View.OnClickListener firstListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            playbackViewBinding.scaling.setImageResource(R.drawable.fullscreen);

            playbackViewBinding.scaling.setOnClickListener(secondListener);
        }
    };
    View.OnClickListener secondListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            playbackViewBinding.scaling.setImageResource(R.drawable.icon_zoom_out_map);
            playbackViewBinding.scaling.setOnClickListener(thirdListener);
        }
    };
    View.OnClickListener thirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            playbackViewBinding.scaling.setImageResource(R.drawable.fit);
            playbackViewBinding.scaling.setOnClickListener(firstListener);
        }
    };

    private void rotateAndChangeIcon(ImageView icon, int drawable) {
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        icon.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                icon.setImageResource(drawable);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private class ScaleDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            scale_factor *= detector.getScaleFactor();
            scale_factor = Math.max(0.5f, Math.min(scale_factor, 6.0f));
            swipeZoomDesignBinding.zoomContainer.setScaleX(scale_factor);
            swipeZoomDesignBinding.zoomContainer.setScaleY(scale_factor);
            int percentage = (int) (scale_factor * 100);
            swipeZoomDesignBinding.zoomPercentage.setText(" " + percentage + "%");
            swipeZoomDesignBinding.zoomContainer.setVisibility(View.VISIBLE);
            return super.onScale(detector);
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
            swipeZoomDesignBinding.zoomContainer.setVisibility(View.GONE);
            super.onScaleEnd(detector);
        }
    }

    @Override
    protected void onPause() {
        Log.d(Constants.LOG, "onPause");
        super.onPause();

        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            currentDurationPlayer = player.getCurrentPosition();

            onStopPlayer(currentDurationPlayer, subtitlePath, playWhenReady);
        }
    }

    @Override
    protected void onResume() {
        Log.d(Constants.LOG, "onResume");
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(playWhenReady);
            doInitialize();
            initializePlayer();
            player.seekTo(currentDurationPlayer);
            addSubtitle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
            countDownTimer.cancel();
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    private void onStopPlayer(long CurrentPosition, String path, boolean playWhenReady) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("PlayerDuration", CurrentPosition);
        editor.putString("PlayerSubtitle", path);
        editor.putBoolean("WhenReady", playWhenReady);
        editor.apply();
    }
}