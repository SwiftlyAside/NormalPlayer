import 'dart:async';
import 'dart:convert';

import 'package:just_audio/just_audio.dart';
import 'package:normal_player/models/playback.dart';
import 'package:normal_player/models/playerstatus.dart';
import 'package:normal_player/models/tune.dart';
import 'package:normal_player/services/arbiter_service.dart';
import 'package:rxdart/rxdart.dart';
import 'package:shared_preferences/shared_preferences.dart';

class MusicService {
  late BehaviorSubject<List<Tune>> _songs$;
  late BehaviorSubject<MapEntry<PlayerStatus, Tune>> _playerState$;
  late BehaviorSubject<MapEntry<List<Tune>, List<Tune>>>
      _playlist$; //key is normal, value is shuffle
  late BehaviorSubject<Duration> _position$;
  late BehaviorSubject<List<Playback>> _playback$;
  late BehaviorSubject<List<Tune>> _favorites$;
  late BehaviorSubject<bool> _isAudioSeeking$;
  late AudioPlayer _audioPlayer;
  late ArbiterService _arbiterService;
  late Tune _defaultSong;

  BehaviorSubject<List<Tune>> get songs$ => _songs$;

  BehaviorSubject<MapEntry<PlayerStatus, Tune>> get playerState$ =>
      _playerState$;

  BehaviorSubject<Duration> get position$ => _position$;

  BehaviorSubject<List<Playback>> get playback$ => _playback$;

  BehaviorSubject<List<Tune>> get favorites$ => _favorites$;

  BehaviorSubject<MapEntry<List<Tune>, List<Tune>>> get playlist$ => _playlist$;

  late StreamSubscription _audioPositionSub;
  late StreamSubscription _audioStateChangeSub;

  MusicService() {
    _defaultSong = Tune(null, " ", " ", " ", null, null, null, []);
    _initStreams();
    _initAudioPlayer();
  }

  Future<void> fetchSongs() async {
    await _arbiterService.fetchSongs().then(
      (data) {
        _songs$.add(data);
      },
    );
  }

  void playMusic(Tune song) {
    _audioPlayer.setUrl(song.uri!);
    _audioPlayer.play();
    _playerState$.add(MapEntry(PlayerStatus.playing, song));
  }

  void pauseMusic(Tune song) {
    _audioPlayer.pause();
    _playerState$.add(MapEntry(PlayerStatus.paused, song));
  }

  void stopMusic() {
    _audioPlayer.stop();
  }

  void updatePosition(Duration duration) {
    _position$.add(duration);
  }

  void updatePlaylist(List<Tune> normalPlaylist) {
    List<Tune> _shufflePlaylist = [...normalPlaylist];
    _shufflePlaylist.shuffle();
    _playlist$.add(MapEntry(normalPlaylist, _shufflePlaylist));
  }

  void playNextSong() {
    if (_playerState$.value.key == PlayerStatus.stopped) {
      return;
    }
    final Tune _currentSong = _playerState$.value.value;
    final bool _isShuffle = _playback$.value.contains(Playback.shuffle);
    final List<Tune> _playlist =
        _isShuffle ? _playlist$.value.value : _playlist$.value.key;
    int _index = _playlist.indexOf(_currentSong);
    if (_index == _playlist.length - 1) {
      _index = 0;
    } else {
      _index++;
    }
    stopMusic();
    playMusic(_playlist[_index]);
  }

  int getSongIndex(song) {
    final bool _isShuffle = _playback$.value.contains(Playback.shuffle);
    final List<Tune> _playlist =
        _isShuffle ? _playlist$.value.value : _playlist$.value.key;
    return _playlist.indexOf(song);
  }

  MapEntry<Tune, Tune> getNextPrevSong(Tune _currentSong) {
    print(_currentSong.id);
    final bool _isShuffle = _playback$.value.contains(Playback.shuffle);
    final List<Tune> _playlist =
        _isShuffle ? _playlist$.value.value : _playlist$.value.key;
    int _index = _playlist.indexOf(_currentSong);
    int nextSongIndex = _index + 1;
    int prevSongIndex = _index - 1;

    if (_index == _playlist.length - 1) {
      nextSongIndex = 0;
    }
    if (_index == 0) {
      prevSongIndex = _playlist.length - 1;
    }
    Tune nextSong = _playlist[nextSongIndex];
    Tune prevSong = _playlist[prevSongIndex];
    return MapEntry(nextSong, prevSong);
  }

  void playPreviousSong() {
    if (_playerState$.value.key == PlayerStatus.stopped) {
      return;
    }
    final Tune _currentSong = _playerState$.value.value;
    final bool _isShuffle = _playback$.value.contains(Playback.shuffle);
    final List<Tune> _playlist =
        _isShuffle ? _playlist$.value.value : _playlist$.value.key;
    int _index = _playlist.indexOf(_currentSong);
    if (_index == 0) {
      _index = _playlist.length - 1;
    } else {
      _index--;
    }
    stopMusic();
    playMusic(_playlist[_index]);
  }

  void _playSameSong() {
    final Tune _currentSong = _playerState$.value.value;
    stopMusic();
    playMusic(_currentSong);
  }

  void _onSongComplete() {
    final List<Playback> _playback = _playback$.value;
    if (_playback.contains(Playback.repeatSong)) {
      _playSameSong();
      return;
    }
    playNextSong();
  }

  void audioSeek(double seconds) {
    _audioPlayer.seek(Duration(seconds: seconds.toInt()));
  }

  void addToFavorites(Tune song) async {
    List<Tune> _favorites = _favorites$.value;
    _favorites.add(song);
    _favorites$.add(_favorites);
    await saveFavorites();
  }

  void removeFromFavorites(Tune _song) async {
    List<Tune> _favorites = _favorites$.value;
    final int index = _favorites.indexWhere((song) => song.id == _song.id);
    _favorites.removeAt(index);
    _favorites$.add(_favorites);
    await saveFavorites();
  }

  void invertSeekingState() {
    final _value = _isAudioSeeking$.value;
    _isAudioSeeking$.add(!_value);
  }

  void updatePlayback(Playback playback) {
    List<Playback> _value = playback$.value;
    if (playback == Playback.shuffle) {
      final List<Tune> _normalPlaylist = _playlist$.value.key;
      updatePlaylist(_normalPlaylist);
    }
    _value.add(playback);
    _playback$.add(_value);
  }

  void removePlayback(Playback playback) {
    List<Playback> _value = playback$.value;
    _value.remove(playback);
    _playback$.add(_value);
  }

  Future<void> saveFavorites() async {
    SharedPreferences _prefs = await SharedPreferences.getInstance();
    final List<Tune> _favorites = _favorites$.value;
    List<String> _encodedStrings = [];
    for (Tune song in _favorites) {
      _encodedStrings.add(_encodeSongToJson(song));
    }
    _prefs.setStringList("favoritetunes", _encodedStrings);
  }

  Future<void> saveFiles() async {
    SharedPreferences _prefs = await SharedPreferences.getInstance();
    final List<Tune> _songs = _songs$.value;
    List<String> _encodedStrings = [];
    for (Tune song in _songs) {
      _encodedStrings.add(_encodeSongToJson(song));
    }
    _prefs.setStringList("tunes", _encodedStrings);
  }

  Future<List<Tune>> retrieveFiles() async {
    SharedPreferences _prefs = await SharedPreferences.getInstance();
    List<String> _savedStrings = _prefs.getStringList("tunes") ?? [];
    List<Tune> _songs = [];
    print(_savedStrings.length);
    for (String data in _savedStrings) {
      final Tune song = _decodeSongFromJson(data);
      _songs.add(song);
    }
    _songs$.add(_songs);
    return _songs$.value;
  }

  void retrieveFavorites() async {
    SharedPreferences _prefs = await SharedPreferences.getInstance();
    final List<Tune> _fetchedSongs = _songs$.value;
    List<String> _savedStrings = _prefs.getStringList("favoritetunes") ?? [];
    List<Tune> _favorites = [];
    for (String data in _savedStrings) {
      final Tune song = _decodeSongPlusFromJson(data);
      for (var fetchedSong in _fetchedSongs) {
        if (song.id == fetchedSong.id) {
          _favorites.add(song);
        }
      }
    }
    _favorites$.add(_favorites);
  }

  String _encodeSongToJson(Tune song) {
    final _songMap = songToMap(song);
    final data = json.encode(_songMap);
    return data;
  }

  Tune _decodeSongFromJson(String ecodedSong) {
    final _songMap = json.decode(ecodedSong);
    final Tune _song = Tune.fromMap(_songMap);
    return _song;
  }

  Tune _decodeSongPlusFromJson(String ecodedSong) {
    final _songMap = json.decode(ecodedSong);
    final Tune _song = Tune.fromMap(_songMap);
    return _song;
  }

  Map<String, dynamic> songToMap(Tune song) {
    Map<String, dynamic> _map = {};
    _map["album"] = song.album;
    _map["id"] = song.id;
    _map["artist"] = song.artist;
    _map["title"] = song.title;
    _map["duration"] = song.duration;
    _map["uri"] = song.uri;
    _map["albumArt"] = song.albumArt;
    _map["colors"] = song.colors;
    return _map;
  }

  void _initStreams() {
    _arbiterService = ArbiterService();
    _isAudioSeeking$ = BehaviorSubject<bool>.seeded(false);
    _songs$ = BehaviorSubject<List<Tune>>();
    _position$ = BehaviorSubject<Duration>();
    _playlist$ = BehaviorSubject<MapEntry<List<Tune>, List<Tune>>>();
    _playback$ = BehaviorSubject<List<Playback>>.seeded([]);
    _favorites$ = BehaviorSubject<List<Tune>>.seeded([]);
    _playerState$ = BehaviorSubject<MapEntry<PlayerStatus, Tune>>.seeded(
      MapEntry(
        PlayerStatus.stopped,
        _defaultSong,
      ),
    );
  }

  void _initAudioPlayer() {
    _audioPlayer = AudioPlayer();

    _audioPositionSub = _audioPlayer.positionStream.listen((Duration duration) {
      final bool _isAudioSeeking = _isAudioSeeking$.value;
      if (!_isAudioSeeking) {
        updatePosition(duration);
      }
    });

    _audioStateChangeSub =
        _audioPlayer.processingStateStream.listen((ProcessingState state) {
      if (state == ProcessingState.completed) {
        _onSongComplete();
      }
    });
  }

  void dispose() {
    stopMusic();
    _isAudioSeeking$.close();
    _songs$.close();
    _playerState$.close();
    _playlist$.close();
    _position$.close();
    _playback$.close();
    _favorites$.close();
    _audioPositionSub.cancel();
    _audioStateChangeSub.cancel();
  }
}
