import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';
import 'package:normal_player/models/playerstatus.dart';
import 'package:normal_player/models/tune.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:provider/provider.dart';
import 'package:rxdart/rxdart.dart';

class NowPlayingSlider extends StatelessWidget {
  final List<int> colors;

  const NowPlayingSlider({Key? key, required this.colors}) : super(key: key);

  String parseDuration(x) {
    final double _temp = x / 1000;
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
    var musicService = Provider.of<MusicService>(context);
    return StreamBuilder<MapEntry<Duration, MapEntry<PlayerStatus, Tune>>>(
      stream: Rx.combineLatest2(
          musicService.position$,
          musicService.playerState$,
          (Duration a, MapEntry<PlayerStatus, Tune> b) => MapEntry(a, b)),
      builder: (context, snapshot) {
        if (!snapshot.hasData ||
            snapshot.data!.value.key == PlayerStatus.stopped) {
          return Slider(
            value: 0,
            onChanged: (value) {},
            activeColor: NormalTheme.kNormalThemeData.primaryColor,
            inactiveColor: NormalTheme.kNormalThemeData.disabledColor,
          );
        }

        final Duration _currentDuration = snapshot.data!.key;
        final Tune _currentSong = snapshot.data!.value.value;
        final int _milliseconds = _currentDuration.inMilliseconds;
        final int _songDurationInMilliseconds = _currentSong.duration!;

        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(
            mainAxisSize: MainAxisSize.max,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              Text(
                parseDuration(_currentDuration.inMilliseconds),
                style: TextStyle(
                    color: Color(colors[1]).withOpacity(.7),
                    fontSize: 12,
                    fontWeight: FontWeight.w600),
              ),
              Expanded(
                child: Slider(
                  min: 0,
                  max: _songDurationInMilliseconds.toDouble(),
                  value: _songDurationInMilliseconds > _milliseconds
                      ? _milliseconds.toDouble()
                      : _songDurationInMilliseconds.toDouble(),
                  onChangeStart: (double value) =>
                      musicService.invertSeekingState(),
                  onChanged: (double value) {
                    final Duration _duration = Duration(
                      milliseconds: value.toInt(),
                    );
                    musicService.updatePosition(_duration);
                  },
                  onChangeEnd: (double value) {
                    musicService.invertSeekingState();
                    musicService.audioSeek(value / 1000);
                  },
                  activeColor: Color(colors[1]).withOpacity(.7),
                  inactiveColor: Color(colors[1]).withOpacity(.2),
                ),
              ),
              Text(
                parseDuration(_songDurationInMilliseconds),
                style: TextStyle(
                    color: Color(colors[1]).withOpacity(.7),
                    fontSize: 12,
                    fontWeight: FontWeight.w600),
              ),
            ],
          ),
        );
      },
    );
  }
}
