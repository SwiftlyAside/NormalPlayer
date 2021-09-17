import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:normal_player/models/tune.dart';
import 'package:palette_generator/palette_generator.dart';
import 'package:rxdart/rxdart.dart';

class ThemeService {
  late BehaviorSubject<List<int>> _color$;

  BehaviorSubject<List<int>> get color$ => _color$;
  late Map<String, List<int>> _savedColors;

  ThemeService() {
    _initStreams();
    _savedColors = {};
  }

  void _initStreams() {
    _color$ = BehaviorSubject<List<int>>.seeded([0xff111111, 0xffffffff]);
  }

  void updateTheme(Tune song) async {
    if (_savedColors.containsKey(song.id)) {
      _color$.add(_savedColors[song.id]!);
      return;
    }

    var path = song.albumArt;
    if (path == null) {
      _color$.add([0xff111111, 0xffffffff]);
      return;
    }

    print("Getting colors..");
    final PaletteGenerator paletteGenerator =
        await PaletteGenerator.fromImageProvider(FileImage(File(path)));

    List<int> colors = List.empty(growable: true);
    var dominantColor = paletteGenerator.dominantColor!;
    colors.add(dominantColor.color.value);
    colors.add(dominantColor.bodyTextColor.value);
    colors.add(dominantColor.titleTextColor.value);

    _color$.add(colors);
    _savedColors[song.id!] = colors;
  }
}
