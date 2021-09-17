import 'package:flutter/material.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:provider/provider.dart';

class NowPlayingControls extends StatelessWidget {
  final List<int> colors;
  const NowPlayingControls({Key? key, required this.colors}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var musicService = Provider.of<MusicService>(context);
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 0),
      width: double.infinity,
      child: Container(), // TODO  implement streambuilder
    );
  }
}
