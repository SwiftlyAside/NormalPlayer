import 'dart:io';

import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';
import 'package:normal_player/models/playerstatus.dart';
import 'package:normal_player/models/tune.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:normal_player/services/theme_service.dart';
import 'package:provider/provider.dart';

import 'now_playing_controls.dart';
import 'now_playing_slider.dart';

class NowPlaying extends StatefulWidget {
  const NowPlaying({Key? key}) : super(key: key);

  @override
  _NowPlayingState createState() => _NowPlayingState();
}

class _NowPlayingState extends State<NowPlaying> {
  getPlayingLayout(Tune _currentSong, List<int> colors, double _screenHeight,
      MusicService musicService) {
    MapEntry<Tune, Tune> songs = musicService.getNextPrevSong(_currentSong);
    if (_currentSong == null || songs == null) {
      return Container();
    }

    String image = songs.value.albumArt!;
    String image2 = songs.key.albumArt!;
    return Column(
      mainAxisSize: MainAxisSize.max,
      children: <Widget>[
        Container(
            constraints: BoxConstraints(
                maxHeight: _screenHeight / 2, minHeight: _screenHeight / 2),
            padding: const EdgeInsets.all(10),
            child: Dismissible(
              key: UniqueKey(),
              background: image == null
                  ? Image.asset("images/cover.png")
                  : Image.file(File(image)),
              secondaryBackground: image2 == null
                  ? Image.asset("images/cover.png")
                  : Image.file(File(image2)),
              movementDuration: const Duration(milliseconds: 500),
              resizeDuration: const Duration(milliseconds: 2),
              dismissThresholds: const {
                DismissDirection.endToStart: 0.3,
                DismissDirection.startToEnd: 0.3
              },
              direction: DismissDirection.horizontal,
              onDismissed: (DismissDirection direction) {
                if (direction == DismissDirection.startToEnd) {
                  musicService.playPreviousSong();
                } else {
                  musicService.playNextSong();
                }
              },
              child: _currentSong.albumArt == null
                  ? Image.asset("images/cover.png")
                  : Image.file(File(_currentSong.albumArt!)),
            )),
        Expanded(
          child: Container(
            decoration: BoxDecoration(
              boxShadow: [
                BoxShadow(
                    color: Color(colors[0]),
                    blurRadius: 50,
                    spreadRadius: 50,
                    offset: const Offset(0, -20)),
              ],
            ),
            padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 8),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Expanded(
                      child: Column(
                        children: [
                          Text(
                            _currentSong.title,
                            maxLines: 1,
                            textAlign: TextAlign.center,
                            overflow: TextOverflow.ellipsis,
                            style: TextStyle(
                              color: Color(colors[1]).withOpacity(.7),
                              fontSize: 18,
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.only(top: 10),
                            child: Text(
                              NormalUtils.getArtists(_currentSong.artist),
                              textAlign: TextAlign.center,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              style: TextStyle(
                                color: Color(colors[1]).withOpacity(.7),
                                fontSize: 15,
                              ),
                            ),
                          )
                        ],
                      ),
                    )
                  ],
                ),
                NowPlayingSlider(colors: colors),
                NowPlayingControls(colors: colors),
              ],
            ),
          ),
        ),
      ],
    );
  }

  String getDuration(Tune _song) {
    final double _temp = _song.duration! / 1000;
    final int _minutes = (_temp / 60).floor();
    final int _seconds = (((_temp / 60) - _minutes) * 60).round();
    if (_seconds.toString().length != 1) {
      return _minutes.toString() + ":" + _seconds.toString();
    } else {
      return _minutes.toString() + ":0" + _seconds.toString();
    }
  }

  @override
  Widget build(BuildContext context) {
    final _screenHeight = MediaQuery.of(context).size.height;
    var musicService = Provider.of<MusicService>(context);
    var themeService = Provider.of<ThemeService>(context);
    return StreamBuilder<MapEntry<PlayerStatus, Tune>>(
      stream: musicService.playerState$,
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return const Scaffold(
            backgroundColor: NormalTheme.kBackgroundColor,
          );
        }

        final Tune _currentSong = snapshot.data!.value;

        if (_currentSong.id == null) {
          return const Scaffold(
            backgroundColor: NormalTheme.kBackgroundColor,
          );
        }

        return Scaffold(
            body: StreamBuilder<List<int>>(
                stream: themeService.color$,
                builder: (context, snapshot) {
                  if (!snapshot.hasData) {
                    return Container();
                  }
                  final List<int> colors = snapshot.data!;
                  return AnimatedContainer(
                    padding: MediaQuery.of(context).padding,
                    duration: const Duration(milliseconds: 500),
                    curve: Curves.decelerate,
                    color: Color(colors[0]),
                    child: getPlayingLayout(
                        _currentSong, colors, _screenHeight, musicService),
                  );
                }));
      },
    );
  }
}
