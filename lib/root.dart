import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:provider/provider.dart';

enum StartupState { busy, success, error }

class Root extends StatefulWidget {
  const Root({Key? key}) : super(key: key);

  @override
  _RootState createState() => _RootState();
}

class _RootState extends State<Root> with TickerProviderStateMixin {
  late final musicService;
  final _androidAppRetain = const MethodChannel("android_app_retain");

  final StreamController<StartupState> _startupStatus =
      StreamController<StartupState>();

  @override
  void initState() {
    loadFiles();
    super.initState();
  }

  @override
  void dispose() {
    _startupStatus.close();
    super.dispose();
  }

  Future loadFiles() async {
    _startupStatus.add(StartupState.busy);
    musicService = Provider.of<MusicService>(context);
  }

  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
