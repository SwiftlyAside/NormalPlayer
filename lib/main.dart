import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:normal_player/components/now_playing.dart';
import 'package:normal_player/constants.dart';
import 'package:normal_player/root.dart';
import 'package:normal_player/services/layout_service.dart';
import 'package:normal_player/services/music_service.dart';
import 'package:normal_player/services/theme_service.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:provider/provider.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

import 'components/bottom_panel.dart';

void main() {
  runApp(const MyApp());
  SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
      statusBarColor: NormalTheme.kNormalThemeData.primaryColor));
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Permission.storage
        .request()
        .then((value) => {if (!value.isGranted) print('not granted!')});

    return MultiProvider(
      providers: [
        Provider<MusicService>(create: (_) => MusicService()),
        Provider<ThemeService>(create: (_) => ThemeService()),
        Provider<LayoutService>(create: (_) => LayoutService()),
      ],
      child: MaterialApp(
        theme: NormalTheme.kNormalThemeData,
        debugShowCheckedModeBanner: false,
        home: Wrapper(
          child: Column(
            mainAxisSize: MainAxisSize.max,
            children: [
              const Expanded(
                child: Root(),
              ),
              Container(
                height: 60,
                color: NormalTheme.kNormalThemeData.primaryColor,
              )
            ],
          ),
        ),
      ),
    );
  }
}

class Wrapper extends StatelessWidget {
  final Widget child;

  const Wrapper({Key? key, required this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var layoutService = Provider.of<LayoutService>(context);

    return SlidingUpPanel(
      panel: const NowPlaying(),
      controller: layoutService.globalPanelController,
      minHeight: 60,
      maxHeight: MediaQuery.of(context).size.height,
      backdropEnabled: true,
      backdropOpacity: 0.5,
      parallaxEnabled: true,
      collapsed: const Material(
        child: BottomPanel(),
      ),
      body: MaterialApp(
        debugShowCheckedModeBanner: false,
        home: child,
      ),
    );
  }
}
