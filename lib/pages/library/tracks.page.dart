import 'package:flutter/material.dart';
import 'package:normal_player/constants.dart';
import 'package:normal_player/models/playerstatus.dart';
import 'package:normal_player/models/tune.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:provider/provider.dart';

class TracksPage extends StatefulWidget {
  const TracksPage({Key? key}) : super(key: key);

  @override
  _TracksPageState createState() => _TracksPageState();
}

class _TracksPageState extends State<TracksPage>
    with AutomaticKeepAliveClientMixin<TracksPage> {
  late MusicService musicService;
  late ScrollController controller;

  @override
  void initState() {
    controller = ScrollController();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    musicService = Provider.of<MusicService>(context);
    super.build(context);
    return Container(
      alignment: Alignment.center,
      color: NormalTheme.kNormalThemeData.backgroundColor,
      child: Row(
        mainAxisSize: MainAxisSize.max,
        children: [
          Flexible(
              child: StreamBuilder(
            stream: musicService.songs$,
            builder: (context, AsyncSnapshot<List<Tune>> snapshot) {
              if (!snapshot.hasData) {
                return Container();
              }

              final _songs = snapshot.data;
              _songs!.sort((a, b) {
                return a.title.toLowerCase().compareTo(b.title.toLowerCase());
              });

              return ListView.builder(
                padding: const EdgeInsets.all(0),
                controller: controller,
                shrinkWrap: true,
                itemExtent: 62,
                physics: const AlwaysScrollableScrollPhysics(),
                itemCount: _songs.length + 1,
                itemBuilder: (context, index) {
                  return StreamBuilder<MapEntry<PlayerStatus, Tune>>(
                    stream: musicService.playerState$,
                    builder: (context, snapshot) {
                      if (!snapshot.hasData) {
                        return Container();
                      }
                      if (index == 0) {
                        return Container(); // TODO PageHeader
                      }

                      int newIndex = index - 1;
                      final PlayerStatus _state = snapshot.data!.key;
                      final Tune _currentSong = snapshot.data!.value;
                      final bool _isSelectedSong =
                          _currentSong == _songs[newIndex];

                      return InkWell(
                        enableFeedback: false,
                        onTap: () {
                          musicService.updatePlaylist(_songs);
                          switch (_state) {
                            case PlayerStatus.playing:
                              if (_isSelectedSong) {
                                musicService.pauseMusic(_currentSong);
                              } else {
                                musicService.stopMusic();
                                musicService.playMusic(_songs[newIndex]);
                              }
                              break;
                            case PlayerStatus.paused:
                              if (_isSelectedSong) {
                                musicService.playMusic(_songs[newIndex]);
                              } else {
                                musicService.stopMusic();
                                musicService.playMusic(_songs[newIndex]);
                              }
                              break;
                            case PlayerStatus.stopped:
                              musicService.playMusic(_songs[newIndex]);
                              break;
                          }
                        },
                        child: Container(), // TODO MyCard
                      );
                    },
                  );
                },
              );
            },
          ))
        ],
      ),
    );
  }

  @override
  bool get wantKeepAlive => true;
}
