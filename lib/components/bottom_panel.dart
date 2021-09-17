import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';
import 'package:normal_player/models/playerstatus.dart';
import 'package:normal_player/models/tune.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:normal_player/services/theme_service.dart';
import 'package:provider/provider.dart';

class BottomPanel extends StatelessWidget {
  const BottomPanel({Key? key}) : super(key: key);

  String getArtists(Tune song) {
    if (song.artist == null) return "Unknown Artist";
    return song.artist.split(";").reduce((String a, String b) {
      return a + " & " + b;
    });
  }

  Widget getBottomPanelLayout(
      musicService, _state, _currentSong, _artists, colors) {
    return Row(
      children: [
        Expanded(
          child: Row(
            children: [
              Padding(
                padding: const EdgeInsets.only(right: 20, left: 5),
                child: _currentSong.albumArt != null
                    ? Image.file(File(_currentSong.albumArt))
                    : Image.asset("images/track.png"),
              ),
              Flexible(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.only(bottom: 8),
                      child: Text(
                        _currentSong.title,
                        overflow: TextOverflow.ellipsis,
                        style: TextStyle(
                          fontSize: 15,
                          color: Color(colors[1]).withOpacity(.7),
                        ),
                      ),
                    ),
                    Text(
                      _artists,
                      overflow: TextOverflow.ellipsis,
                      style: TextStyle(
                        fontSize: 11,
                        color: Color(colors[1]).withOpacity(.7),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        Material(
          color: Colors.transparent,
          child: InkWell(
            onTap: () {
              if (_currentSong.uri == null) {
                return;
              }
              if (PlayerStatus.paused == _state) {
                musicService.playMusic(_currentSong);
              } else {
                musicService.pauseMusic(_currentSong);
              }
            },
            child: Column(
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 15.0),
                  child: _state == PlayerStatus.playing
                      ? Icon(
                          Icons.pause,
                          color: Color(colors[1]).withOpacity(.7),
                        )
                      : Icon(
                          Icons.play_arrow,
                          color: Color(colors[1]).withOpacity(.7),
                        ),
                ),
              ],
            ),
          ),
        )
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    var musicService = Provider.of<MusicService>(context);
    var themeService = Provider.of<ThemeService>(context);
    return StreamBuilder<MapEntry<PlayerStatus, Tune>>(
      stream: musicService.playerState$,
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return Container(
            color: NormalTheme.kNormalThemeData.bottomAppBarColor,
            height: double.infinity,
            width: double.infinity,
            alignment: Alignment.bottomCenter,
          );
        }

        final Tune _currentSong = snapshot.data!.value;

        if (_currentSong.id == null) {
          return Container(
            color: NormalTheme.kNormalThemeData.bottomAppBarColor,
            height: double.infinity,
            width: double.infinity,
            alignment: Alignment.bottomCenter,
          );
        }

        final PlayerStatus _state = snapshot.data!.key;
        final String _artists = getArtists(_currentSong);

        return StreamBuilder<List<int>>(
            stream: themeService.color$,
            builder: (context, AsyncSnapshot<List<int>> snapshot) {
              if (!snapshot.hasData) {
                return Container(
                  color: NormalTheme.kNormalThemeData.bottomAppBarColor,
                  height: double.infinity,
                  width: double.infinity,
                  alignment: Alignment.bottomCenter,
                );
              }

              final List<int> colors = snapshot.data!;

              return AnimatedContainer(
                  duration: const Duration(milliseconds: 500),
                  curve: Curves.decelerate,
                  color: Color(colors[0]),
                  height: double.infinity,
                  width: double.infinity,
                  alignment: Alignment.bottomCenter,
                  child: getBottomPanelLayout(
                      musicService, _state, _currentSong, _artists, colors));
            });
      },
    );
  }
}
