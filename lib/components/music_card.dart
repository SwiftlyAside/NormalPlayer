import 'dart:io';

import 'package:flutter/material.dart';
import 'package:normal_player/models/playerstatus.dart';
import 'package:normal_player/models/tune.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:provider/provider.dart';

class MusicCard extends StatelessWidget {
  final Tune _song;

  const MusicCard({Key? key, required Tune song})
      : _song = song,
        super(key: key);

  @override
  Widget build(BuildContext context) {
    var musicService = Provider.of<MusicService>(context);
    return StreamBuilder<MapEntry<PlayerStatus, Tune>>(
      stream: musicService.playerState$,
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return Container();
        }

        final Tune _currentSong = snapshot.data!.value;
        final bool _isSelectedSong = _song == _currentSong;
        final _textColor = _isSelectedSong ? Colors.black : Colors.black54;
        final _fontWeight = _isSelectedSong ? FontWeight.w900 : FontWeight.w400;
        final _image = _song.albumArt != null
            ? FileImage(File(_song.albumArt!))
            : const AssetImage('images/track.png') as ImageProvider;

        return Container(
          color: Colors.transparent,
          padding: const EdgeInsets.symmetric(vertical: 5),
          child: Row(
            children: <Widget>[
              Expanded(
                child: Row(
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.only(right: 15),
                      child: SizedBox(
                        height: 62,
                        width: 62,
                        child: FadeInImage(
                          placeholder: const AssetImage('images/track.png'),
                          fadeInDuration: const Duration(milliseconds: 200),
                          fadeOutDuration: const Duration(milliseconds: 100),
                          image: _image,
                        ),
                      ),
                    ),
                    Flexible(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: <Widget>[
                          Padding(
                            padding: const EdgeInsets.only(bottom: 8),
                            child: Text(
                              (_song.title == null)
                                  ? "Unknown Title"
                                  : _song.title,
                              overflow: TextOverflow.ellipsis,
                              style: TextStyle(
                                fontSize: 13.5,
                                fontWeight: _fontWeight,
                                color: Colors.black,
                              ),
                            ),
                          ),
                          Text(
                            (_song.artist == null)
                                ? "Unknown Artist"
                                : _song.artist,
                            overflow: TextOverflow.ellipsis,
                            style: TextStyle(
                              fontSize: 12.5,
                              fontWeight: _fontWeight,
                              color: _textColor,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              Padding(
                  padding: const EdgeInsets.only(right: 10.0),
                  child: IconButton(
                    icon: const Icon(
                      Icons.more_vert,
                      size: 22,
                    ),
                    onPressed: () {},
                    color: Colors.black26,
                  )),
            ],
          ),
        );
      },
    );
  }
}
