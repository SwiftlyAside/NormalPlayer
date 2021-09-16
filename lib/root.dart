import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:normal_player/pages/library/library.page.dart';
import 'package:normal_player/services/layout_service.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:provider/provider.dart';

enum StartupState { busy, success, error }

class Root extends StatefulWidget {
  const Root({Key? key}) : super(key: key);

  @override
  _RootState createState() => _RootState();
}

class _RootState extends State<Root> with TickerProviderStateMixin {
  late MusicService musicService;
  late LayoutService layoutService;
  final _androidAppRetain = const MethodChannel("android_app_retain");

  final StreamController<StartupState> _startupStatus =
      StreamController<StartupState>();

  @override
  void dispose() {
    _startupStatus.close();
    super.dispose();
  }

  Future loadFiles() async {
    _startupStatus.add(StartupState.busy);
    final data = await musicService.retrieveFiles();
    if (data.isEmpty) {
      await musicService.fetchSongs();
      musicService.saveFiles();
      musicService.retrieveFavorites();
      _startupStatus.add(StartupState.success);
    } else {
      musicService.retrieveFavorites();
      _startupStatus.add(StartupState.success);
    }
  }

  @override
  Widget build(BuildContext context) {
    musicService = Provider.of<MusicService>(context);
    layoutService = Provider.of<LayoutService>(context);
    loadFiles();
    return WillPopScope(
      child: Scaffold(
        body: StreamBuilder<StartupState>(
          stream: _startupStatus.stream,
          builder: (context, snapshot) {
            if (!snapshot.hasData) {
              return Container();
            }
            if (snapshot.data == StartupState.busy) {
              return Container();
            }

            return Padding(
              padding: MediaQuery.of(context).padding,
              child: Column(
                children: [
                  Expanded(
                      child: Stack(
                    fit: StackFit.expand,
                    children: [
                      Column(
                        mainAxisSize: MainAxisSize.max,
                        children: [
                          Expanded(
                            child: PageView(
                              controller: layoutService.globalPageController,
                              physics: const NeverScrollableScrollPhysics(),
                              scrollDirection: Axis.horizontal,
                              children: [
                                const LibraryPage(),
                                Container(), // TODO CollectionPage
                              ],
                            ),
                          ),
                        ],
                      )
                    ],
                  ))
                ],
              ),
            );
          },
        ),
      ),
      onWillPop: () {
        if (!layoutService.globalPanelController.isPanelClosed) {
          layoutService.globalPanelController.close();
          return Future.value(true);
        } else {
          _androidAppRetain.invokeMethod("sendToBackground");
          return Future.value(false);
        }
      },
    );
  }
}
