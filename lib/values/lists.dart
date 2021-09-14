import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

final Map<int, List<MapEntry<String, GlobalKey>>> headerItems = {
  0: [
    MapEntry(
      "Tracks",
      GlobalKey(),
    ),
    MapEntry(
      "Artists",
      GlobalKey(),
    ),
    MapEntry(
      "Albums",
      GlobalKey(),
    ),
  ],
  1: [
    MapEntry("Playlists", GlobalKey()),
    MapEntry("Favorites", GlobalKey()),
  ],
};

final List<MapEntry<String, Icon>> bottomNavBarItems = [
  const MapEntry("Library", Icon(CupertinoIcons.rectangle_stack)),
  const MapEntry("Playlists", Icon(CupertinoIcons.music_note_list)),
  const MapEntry("Search", Icon(CupertinoIcons.search)),
  const MapEntry("Equalizer", Icon(CupertinoIcons.waveform)),
  const MapEntry("Settings", Icon(CupertinoIcons.gear_alt_fill)),
];
