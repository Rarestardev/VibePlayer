package com.rarestardev.videovibe.VideoPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.media.AudioManagerCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.rarestardev.videovibe.Listener.SubtitleFilesSaveState;
import com.rarestardev.videovibe.R;
import com.rarestardev.videovibe.Utilities.FormatViews;
import com.rarestardev.videovibe.databinding.ActivityVideoPlayerBinding;
import com.rarestardev.videovibe.databinding.CustomPlaybackViewBinding;
import com.rarestardev.videovibe.databinding.SwipeZoomDesignBinding;

import java.io.File;
import java.util.Collections;
import java.util.List;

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
    private List<SRTParser.Subtitle> subtitles;

    private enum ControlsMode {LOCK, FULLSCREEN}

    private float speed;
    private float startBrightness = -1.0f;
    private float startVolumePercent = -1.0f;
    private float scale_factor = 1.0f;
    private int startVideoTime = -1;
    private static final int MAX_VIDEO_STEP_TIME = 60 * 1000;
    private static final int MAX_BRIGHTNESS = 100;
    private String videoPath;
    private int targetTime;
    private int totalTime;
    private long currentDurationPlayer;
    private boolean playWhenReady = true;

    private static final String LOG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player);
        playbackViewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.custom_playback_view, binding.parent, true);
        swipeZoomDesignBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.swipe_zoom_design, binding.parent, true);

        checkAndPauseMusic();

        if (savedInstanceState != null) {
            currentDurationPlayer = savedInstanceState.getLong("playback_position");
            playWhenReady = savedInstanceState.getBoolean("play_when_ready");
        }
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
        }else {
            doInitialize();
        }
    }

    private void doInitialize() {
        videoPath = getIntent().getStringExtra("VideoPath");
        playbackViewBinding.videoName.setText(getIntent().getStringExtra("VideoName"));

        if (videoPath == null) {
            Log.e(LOG,"Video Path is empty");
            return;
        }
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleDetector());


        initializePlayer(false,null);

        SwipeTouchListener();

        onClickPlayerViews();
    }

    private void initializePlayer(boolean isSubtitle,List<SRTParser.Subtitle> subtitles) {
        if (player == null) {
            player = new ExoPlayer.Builder(this).build();
            binding.exoplayerView.setPlayer(player);
        }

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,"user-agent");
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoPath));
        if (!isSubtitle){
            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        long duration = player.getDuration();
                        playbackViewBinding.exoProgress.setDuration(duration);
                        playbackViewBinding.exoPosition.setText(FormatViews.formatTime(duration));
                    }
                }

                @Override
                public void onPlayerError(@NonNull PlaybackException error) {
                    Toast.makeText(VideoPlayerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(LOG,error.getMessage());
                }

            });

            player.prepare(videoSource);
            player.setPlayWhenReady(true);
        }else {

            if (subtitles != null){
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == Player.STATE_READY) {
                            long duration = player.getDuration();
                            playbackViewBinding.exoProgress.setDuration(duration);
                            playbackViewBinding.exoPosition.setText(FormatViews.formatTime(duration));
                        }
                    }

                    @Override
                    public void onPlayerError(@NonNull PlaybackException error) {
                        Toast.makeText(VideoPlayerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(LOG,error.getMessage());
                    }

                    @Override
                    public void onCues(@NonNull CueGroup cueGroup) {
                        List<Cue> cues = cueGroup.cues;

                        if (!cues.isEmpty()) {
                            for (Cue cue : cues) {
                                Log.d(LOG, "Subtitle: " + cue.text + " at position: " + cue.position);
                            }
                        } else {
                            Log.d(LOG, "No subtitles available.");
                        }
                        Player.Listener.super.onCues(cueGroup);
                    }

                    @Override
                    public void onCues(@NonNull List<Cue> cues) {
                        Log.d(LOG, "onCues called with " + cues.size() + " cues");

                        if (!cues.isEmpty()) {
                            Cue cue = cues.get(0);
                            binding.exoSubtitles.setText(cue.text);
                            binding.exoSubtitles.setVisibility(View.VISIBLE);
                        } else {
                            binding.exoSubtitles.setVisibility(View.GONE);
                            Log.d(LOG, "No subtitles available or they are not loaded.");
                            binding.exoSubtitles.setText("");
                        }
                    }

                });

                player.prepare(videoSource);
                player.setPlayWhenReady(true);

                new Thread(() -> {
                    while (true){
                        long currentPosition = player.getCurrentPosition();

                        for (SRTParser.Subtitle subtitle:subtitles){
                            if (currentPosition >= subtitle.startTime && currentPosition <= subtitle.endTime){
                                runOnUiThread(() -> binding.exoSubtitles.setText(subtitle.text));
                            }
                        }

                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }else {
                Log.e(LOG,"ERROR");
            }


        }



        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                long currentPosition = player.getCurrentPosition();
                playbackViewBinding.exoDuration.setText(FormatViews.formatTime(currentPosition));
                playbackViewBinding.exoProgress.setPosition(currentPosition);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);

        ExoTimeBarListener();

        playbackViewBinding.exoPlayPause.setImageResource(R.drawable.baseline_pause_24);
        playbackViewBinding.nightMode.setVisibility(View.GONE);
        playbackViewBinding.icMute.setImageResource(R.drawable.baseline_volume_down_24);
    }

    @Override
    public void subtitlePath(String path) {
        if (!path.isEmpty()){

            SRTParser parser = new SRTParser();
            List<SRTParser.Subtitle> subtitles = parser.parseSRT(path);
            initializePlayer(true,subtitles);

        }else {
            Log.e(LOG,"Path isEmpty");
        }
    }

    private void ExoTimeBarListener() {
        playbackViewBinding.exoProgress.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long l) {}

            @Override
            public void onScrubMove(TimeBar timeBar, long l) {
                player.seekTo(l);
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long l, boolean b) {
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
                    playbackViewBinding.toolbar.setVisibility(View.GONE);
                    playbackViewBinding.progress.setVisibility(View.GONE);
                    playbackViewBinding.menuItems.setVisibility(View.GONE);
                    playbackViewBinding.bottomIcons.setVisibility(View.GONE);
                    // fullscreen mode
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                } else {
                    playbackViewBinding.toolbar.setVisibility(View.VISIBLE);
                    playbackViewBinding.menuItems.setVisibility(View.VISIBLE);
                    playbackViewBinding.progress.setVisibility(View.VISIBLE);
                    playbackViewBinding.bottomIcons.setVisibility(View.VISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
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
                    totalTime = (int) player.getDuration();
                }
                if (targetTime < 0) {
                    targetTime = totalTime;
                }
                String targetTimeString = FormatViews.formatDuration(targetTime / 1000);

                if (forwardDirection) {
                    playbackViewBinding.indicatorImageView.setImageResource(R.drawable.baseline_fast_forward_24);
                } else {
                    playbackViewBinding.indicatorImageView.setImageResource(R.drawable.baseline_fast_rewind_24);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("playback_position", currentDurationPlayer);
        outState.putBoolean("play_when_ready", playWhenReady);
    }

    @SuppressLint("Range")
    private void onClickPlayerViews() {
        playbackViewBinding.videoMore.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(VideoPlayerActivity.this, playbackViewBinding.videoMore);
            popupMenu.getMenuInflater().inflate(R.menu.settings_video_player_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.subtitle) {
                    SubtitleDialog subtitleDialog = new SubtitleDialog(VideoPlayerActivity.this, this);
                    subtitleDialog.getWindow().setGravity(Gravity.BOTTOM);
                    subtitleDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    subtitleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    subtitleDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    subtitleDialog.setCancelable(false);
                    subtitleDialog.show();
                }
                return true;
            });
            popupMenu.show();
        });
        playbackViewBinding.videoBack.setOnClickListener(view -> {
            if (player != null) {
                player.release();
            }
            finish();
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
                playbackViewBinding.exoPlayPause.setImageResource(R.drawable.baseline_play_arrow_24);
            } else {
                player.play();
                playbackViewBinding.exoPlayPause.setImageResource(R.drawable.baseline_pause_24);
            }
        });

        playbackViewBinding.icNight.setOnClickListener(view -> {
            if (playbackViewBinding.nightMode.getVisibility() == View.VISIBLE) {
                playbackViewBinding.nightMode.setVisibility(View.GONE);
            } else {
                playbackViewBinding.nightMode.setVisibility(View.VISIBLE);
            }
        });

        playbackViewBinding.icMute.setOnClickListener(view -> {
            if (player.getVolume() == 100) {
                player.setVolume(0);
                playbackViewBinding.icMute.setImageResource(R.drawable.baseline_volume_mute_24);
            } else {
                player.setVolume(100);
                playbackViewBinding.icMute.setImageResource(R.drawable.baseline_volume_down_24);
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
            playbackViewBinding.scaling.setImageResource(R.drawable.baseline_zoom_out_map_24);
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
        currentDurationPlayer = player.getCurrentPosition();
        playWhenReady = player.getPlayWhenReady();
        player.pause();
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        player.setPlayWhenReady(true);
        player.release();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentDurationPlayer);
        player.play();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
        player.release();
        player = null;
    }
}